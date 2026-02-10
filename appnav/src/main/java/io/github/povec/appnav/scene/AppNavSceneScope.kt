package io.github.povec.appnav.scene

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Transition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.rememberLifecycleOwner
import androidx.navigation3.runtime.NavEntry
import androidx.navigationevent.NavigationEvent
import io.github.povec.appnav.constraint.AppNavConstraint
import io.github.povec.appnav.key.AppNavKey
import io.github.povec.appnav.key.AppNavRole
import io.github.povec.appnav.key.AppNavSession
import io.github.povec.appnav.core.context
import io.github.povec.appnav.constraint.findRole

/**
 * [AppNavSceneScope]
 * レイアウト構築中に、現在のセッション情報やメタデータ、アニメーション状態にアクセスするためのスコープ。
 */
interface AppNavSceneScope {
    val activeSession: AppNavSession
    val sessionEntries: List<NavEntry<AppNavKey>>
    val currentSessionValue: AppNavTransitionValue
    val constraint: AppNavConstraint

    /** アニメーションの進行状況や種類を判定するフラグ */
    val transitionFlags: AppNavTransitionFlags
    /** ペイン間の遷移を司る Compose Transition */
    val transition: Transition<AppNavTransitionValue>

    // 遷移アニメーションのカスタマイズスペック群
    val sizeTransform: SizeTransform?
    val enterTransitionSpec: AnimatedContentTransitionScope<AppNavTransitionValue>.() -> ContentTransform
    val exitTransitionSpec: AnimatedContentTransitionScope<AppNavTransitionValue>.() -> ContentTransform
    val transitionSpec: AnimatedContentTransitionScope<AppNavTransitionValue>.() -> ContentTransform
    val popTransitionSpec: AnimatedContentTransitionScope<AppNavTransitionValue>.() -> ContentTransform
    val predictivePopTransitionSpec: AnimatedContentTransitionScope<AppNavTransitionValue>.(@NavigationEvent.SwipeEdge Int) -> ContentTransform

    /** 特定の Role に属するトップのメタデータを名前で取得 */
    fun roleRootMetadata(roleName: String): Map<String, Any> = roleRootMetadata(constraint.findRole(roleName))
    /** 特定の Role に属する先頭のメタデータを取得 */
    fun roleRootMetadata(role: AppNavRole): Map<String, Any> = sessionEntries.firstOrNull { it.context.role == role }?.metadata?: emptyMap()

    /** 特定の Role に属するルートのメタデータを名前で取得 */
    fun roleTopMetadata(roleName: String): Map<String, Any> = roleTopMetadata(constraint.findRole(roleName))
    /** 特定の Role に属するルートのメタデータを取得 */
    fun roleTopMetadata(role: AppNavRole): Map<String, Any> = sessionEntries.lastOrNull { it.context.role == role }?.metadata?: emptyMap()

}

/**
 * [EntryHolder]
 * 実際の [AnimatedContent] をラップし、特定のペイン内での画面切り替えを実現します。
 * ZIndex の制御や、遷移中の Lifecycle 制御（STARTED/RESUMED）を自動化します。
 */
@Composable
fun AppNavSceneScope.EntryHolder(
    modifier: Modifier,
    panePriority: Int,
    placeholder: @Composable (() -> Unit)?,
    content: @Composable (NavEntry<AppNavKey>.() -> Unit)
){

    val (initialZIndex, targetZIndex) = remember(transition.currentState, transition.targetState, sessionEntries) {

            val currentKey = transition.currentState[panePriority]
            val initialIdx = sessionEntries.indexOfLast { it.contentKey == currentKey }
            val targetKey = transition.targetState[panePriority]
            val targetIdx = sessionEntries.indexOfLast { it.contentKey == targetKey }
            if (initialIdx <= targetIdx) 0f to 1f else 1f to 0f
        }

    transition.AnimatedContent(
            modifier = modifier,
            contentKey = { it[panePriority]?: "empty - $panePriority" },
            transitionSpec = scope@{

                val spec = when(transitionFlags.transitionType(panePriority)){
                    AppNavTransitionType.None -> ContentTransform(EnterTransition.None, ExitTransition.None)
                    AppNavTransitionType.Enter -> enterTransitionSpec(this)
                    AppNavTransitionType.Exit -> exitTransitionSpec(this)
                    AppNavTransitionType.Nav -> transitionSpec(this)
                    AppNavTransitionType.Pop -> popTransitionSpec(this)
                    AppNavTransitionType.PredictivePop -> predictivePopTransitionSpec(this, transitionFlags.predictiveBackSwipEdge)
                }

                ContentTransform(
                    targetContentEnter = spec.targetContentEnter,
                    initialContentExit = spec.initialContentExit,
                    targetContentZIndex = targetZIndex,
                    sizeTransform = sizeTransform
                )
            }
        ) { sessionValue ->

        val entry = sessionValue[panePriority]?.let { key -> sessionEntries.find { it.contentKey == key } }

        if(entry == null && placeholder == null) return@AnimatedContent

        val isSettled = transition.currentState == transition.targetState
        val lifecycleOwner = rememberLifecycleOwner(
            maxLifecycle = if (isSettled) Lifecycle.State.RESUMED else Lifecycle.State.STARTED
        )

        CompositionLocalProvider(
            LocalLifecycleOwner provides lifecycleOwner,
            LocalNavPaneAnimatedContentScope provides this
        ) {
            entry?.content()?: placeholder?.invoke()
        }
    }
}

/**
 * [EntryPane]
 * レイアウト上の「枠（ペイン）」を定義します。
 * 画面が存在する場合、またはプレースホルダーが必要な場合のみ [EntryHolder] を配置します
 */
@Composable
fun AppNavSceneScope.EntryPane(
    modifier: Modifier,
    panePriority: Int,
    placeholder: @Composable (() -> Unit)?,
    content: @Composable NavEntry<AppNavKey>.() -> Unit
){
    if(currentSessionValue[panePriority] != null || placeholder != null){
        EntryHolder(
            modifier = modifier,
            panePriority = panePriority,
            placeholder = placeholder,
            content = content
        )
    }
}

/**
 * [OverlayEntryPane]
 * ダイアログやフルスクリーンオーバーレイなど、スタックの最前面に表示される特別なペイン。
 */
@Composable
fun AppNavSceneScope.OverlayEntryPane(
    modifier: Modifier,
    overlayContainer: @Composable (content: @Composable () -> Unit) -> Unit,
    content: @Composable NavEntry<AppNavKey>.() -> Unit
){
    if(currentSessionValue[Int.MAX_VALUE] != null) {
        overlayContainer {
            EntryPane(
                modifier = modifier,
                panePriority = Int.MAX_VALUE,
                placeholder = null,
                content = content
            )
        }
    }
}