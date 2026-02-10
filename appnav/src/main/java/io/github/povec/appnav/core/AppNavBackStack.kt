package io.github.povec.appnav.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.serialization.NavBackStackSerializer
import androidx.navigation3.runtime.serialization.NavKeySerializer
import io.github.povec.appnav.key.AppNavContext
import io.github.povec.appnav.key.AppNavKey
import io.github.povec.appnav.key.AppNavRole
import io.github.povec.appnav.key.AppNavSession
import io.github.povec.appnav.key.isSelfOrDescendantOf


/**
 * AppNavBackStack を Composition 内で保持・復元するための Composable。
 * アクティブなスタックと、一時的に退避されたインアクティブなスタックの両方を管理する。
*/
@Composable
fun rememberAppNavBackStack(root: AppNavKey): AppNavBackStack {
    val inner = rememberAppNavKeyChain(root)
    val inActiveInner = rememberAppNavKeyChain()

    return remember(inner, inActiveInner) {
        AppNavBackStack(inner, inActiveInner)
    }
}

/**
 * Navigation3 の NavBackStack をシリアライズ可能な状態で生成する。
 */
@Composable
fun rememberAppNavKeyChain(vararg elements: AppNavKey): NavBackStack<AppNavKey> =
    rememberSerializable(
        serializer = NavBackStackSerializer(elementSerializer = NavKeySerializer())
    ) {
        NavBackStack(*elements)
    }

/**
 * アプリの「論理的な履歴」を管理するクラス。
 * * 画面配置（物理レイアウト）とは切り離されており、
 * 各画面が属するセッションや役割（Role）に基づいてスタック操作を行う。
 */
class AppNavBackStack(
    private val innerStack: NavBackStack<AppNavKey>,
    private val inActiveInnerStack: NavBackStack<AppNavKey>,
) {

    // 現在スタックの最上位にあるセッションを動的に解決
    private val _activeSession = derivedStateOf {
        innerStack.lastOrNull()?.context?.session
    }


    val activeSession by _activeSession

    //現在のバックスタック
    internal val backStack: List<AppNavKey> get() = innerStack
    //退避されたバックスタック
    internal val inActiveBackStack: List<AppNavKey> get() = inActiveInnerStack

    /**
     * 特定のキーを唯一の起点（Base）としてスタックを強制リセットする。
     * 主にアプリの根本的なモード切り替えや、ディープリンクからの復帰などで使用。
     */
    fun rebase(newKey: AppNavKey) {

        if (newKey.context.isRoleRoot != AppNavRole.Base) return

        innerStack.clear()
        inActiveInnerStack.clear()
        innerStack.add(newKey)
    }

    /**
     * 指定された AppNavKey に基づいて論理スタックを更新する。
     * セッションの再開、Role（役割）に基づいた挿入位置の特定、履歴の入れ替えを行う。
     */
    fun navigate(newKey: AppNavKey) {

        val context = newKey.context
        val session = context.session

        //specificとmanegedのbaseRootは、bring to frontと、restoreを試みる
        if ((session is AppNavSession.Specific || session is AppNavSession.Managed) && context.isRoleRoot == AppNavRole.Base) {

            if (session is AppNavSession.Specific) {
                val inActiveMatches = inActiveInnerStack.filter { it.context.session == session }
                if (inActiveMatches.isNotEmpty()) {
                    inActiveInnerStack.removeAll(inActiveMatches)
                    innerStack.addAll(inActiveMatches)
                    //restoreしたので、早期リターン
                    return
                }
            }

            val activeMatches = innerStack.filter { it.context.session == session }
            if (activeMatches.isNotEmpty()) {
                innerStack.removeAll(activeMatches)
                innerStack.addAll(activeMatches)
                //bring to frontしたので、早期リターン
                return
            }
        }

        //最後のkey
        val last = innerStack.lastOrNull()

        //最後のkeyが存在していて、現在のsessionが、追加されるsessionと一緒の場合
        if (last != null && innerStack.last().context.session == session) {

            //現在のsessionでfilter
            val sessionStack = innerStack.filter { it.context.session == session }

            //その内のroleの最後
            val roleLast = sessionStack.lastOrNull { it.context.role == context.role }

            //roleが散財しない場合
            if (roleLast == null) {

                //新規のroleRootである必要がある。
                if (!context.isRoot) return

                //role毎に分類
                when (val role = context.role) {
                    is AppNavRole.Overlay -> {
                        //最後に追加される
                        innerStack.add(newKey)
                    }

                    is AppNavRole.Pane -> {
                        val priorityChain = role.priorityChain

                        val previousRole =
                            sessionStack
                                //自分より優先度の高い兄弟を探す
                                .filter {
                                    it.context.role.priorityChain == priorityChain
                                            && it.context.role.priority < role.priority
                                }
                                .run {
                                    //その内、一番後ろの優先度を探す、isEmpty時は、親を探す処理に回す
                                    val max = maxByOrNull { it.context.role.priority } ?: return@run null
                                    //そのroleで、最後のkeyを探す
                                    lastOrNull { it.context.role.priority == max.context.role.priority }?.context?.role
                                }
                                ?: sessionStack.find { it.context.role.priorityPath == priorityChain }?.context?.role
                                ?: return

                        val previousIndex =
                            innerStack.indexOfLast { it.context.role.isSelfOrDescendantOf(previousRole) }

                        if (previousIndex != -1) {
                            innerStack.add(previousIndex + 1, newKey)
                        } else return
                    }

                    is AppNavRole.Base -> {
                        innerStack.add(newKey)
                    }
                }
            } else {
                val sessionLastIndex = innerStack.indexOf(roleLast)

                if(sessionLastIndex == -1) return

                if (context.isRoot) {
                    innerStack.removeAll {
                        it.context.session == session && it.context.role.isSelfOrDescendantOf(
                            context.role
                        )
                    }
                    innerStack.add(newKey)
                } else if (roleLast.context.hashCode() == context.previous) {
                    innerStack.add(sessionLastIndex + 1, newKey)
                }
            }
        } else {
            if (context.isRoleRoot == AppNavRole.Base) {
                innerStack.add(newKey)
            }
        }
    }

    /**
     * 指定されたコンテキストに基づいて履歴を削除する。
     * 下位roleも共に、popする。
    */
    fun pop(context: AppNavContext): Boolean? {
        if (innerStack.isEmpty()) return false

        val session = context.session
        val sessionId = session.identifier


        if (innerStack.lastOrNull()?.context?.session?.identifier == sessionId) {

            if (context.isRoot) {


                if (session is AppNavSession.Specific && context.isRoleRoot == AppNavRole.Base) {
                    val activeSessionChain =
                        innerStack.filter { it.context.session.identifier == sessionId }
                    if (activeSessionChain.isNotEmpty()) {
                        innerStack.removeAll(activeSessionChain)
                        inActiveInnerStack.addAll(activeSessionChain)
                        return null
                    }
                }


                innerStack.removeAll {
                    it.context.session.identifier == sessionId && it.context.role.isSelfOrDescendantOf(
                        context.role
                    )
                }
            } else {

                val targetIndex = innerStack.indexOfLast { it.context == context }
                if (targetIndex == -1) return null


                val nextKey = innerStack.getOrNull(targetIndex + 1)
                if (nextKey?.context?.previous == context.hashCode()) return null

                innerStack.removeAt(targetIndex)
            }
        }
        return true
    }

    /**
     * スタックの最上位にある画面を戻す。
     */
    fun back(): Boolean? {
        val last = innerStack.lastOrNull() ?: return false
        return pop(last.context)
    }

    /**
     * 特定の Role を、その子孫ごと強制排除する
     */
    fun excludeRole(session: AppNavSession, role: AppNavRole): Boolean {

        if (role == AppNavRole.Base) return false

        val last = innerStack.lastOrNull() ?: return false
        if (last.context.session != session) return false

        val sessionId = session.identifier


        return innerStack.removeAll {
            it.context.session.identifier == sessionId && it.context.role.isSelfOrDescendantOf(
                role
            )
        }
    }
}