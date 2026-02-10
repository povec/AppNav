package io.github.povec.appnav.key

import io.github.povec.appnav.constraint.AppNavConstraintResolver
import kotlinx.serialization.Serializable

/**
 * [AppNavContext]
 * 画面の「出自」と「立ち位置」を証明するコンテキスト。
 * セッション情報、役割（Role）、どの骨格（Constraint）に従っているか、そして遷移元情報を保持する。
 */
@Serializable
data class AppNavContext(
    val session: AppNavSession,
    val role: AppNavRole,
    val constraintId: String,
    val previous: Int,
    val caller: AppNavCaller,
) {
    /** 自身のRole内での最初の画面（Root）かどうか。*/
    val isRoot: Boolean get() = previous == role.hashCode()
    /** 自身がその Role の起点である場合、その Role を返す。 */
    val isRoleRoot: AppNavRole? get() = if (isRoot) role else null

    /**
     * 次の画面のための新しいコンテキストを生成する。
     */
    fun next(
        role: AppNavRole? = null,
        caller: AppNavCaller? = null,
    ): AppNavContext = copy(
        role = role ?: this.role,
        previous = role?.hashCode() ?: hashCode(),
        caller = caller ?: AppNavCaller.EMPTY
    )

    companion object {

        // --- 各種セッション開始用のファクトリメソッド ---

        fun createSpecific(name: String, constraintId: String) =
            AppNavContext(
                session = AppNavSession.Specific(name),
                role = AppNavRole.Base,
                constraintId = constraintId,
                previous = AppNavRole.Base.hashCode(),
                caller = AppNavCaller.EMPTY
            )

        fun createManaged(name: String, constraintId: String) =
            AppNavContext(
                session = AppNavSession.Managed(name),
                role = AppNavRole.Base,
                constraintId = constraintId,
                previous = AppNavRole.Base.hashCode(),
                caller = AppNavCaller.EMPTY
            )

        fun createGeneral(constraintId: String, caller: AppNavCaller? = null) =
            AppNavContext(
                session = AppNavSession.General(),
                role = AppNavRole.Base,
                constraintId = constraintId,
                previous = AppNavRole.Base.hashCode(),
                caller = caller ?: AppNavCaller.EMPTY
            )
    }
}

/**
 * [AppNavAction]
 * 遷移の「意図」を表す。
 */
sealed interface AppNavAction {
    data object Stack : AppNavAction
    data class Expand(val priority: Int = 0) : AppNavAction
    data object Overlay : AppNavAction
    data object Replace : AppNavAction
}

/**
 * [AppNavConnect]
 * 遷移元と遷移先の「繋がり」を表す。
 */
data class AppNavConnect(
    val payload: String? = null
)

/**
 * [action]
 * ユーザーの「意図（Action）」を具体的な「次のコンテキスト（Context）」に翻訳するメインエンジン。
 */
fun AppNavContext.action(
    action: AppNavAction,
    resolver: AppNavConstraintResolver,
    connect: AppNavConnect?
): AppNavContext = action(
    action = action,
    resolver = resolver,
    caller = connect?.let { AppNavCaller(hashCode(), it.payload) }?: AppNavCaller.EMPTY
)

/**
 * [action]
 * ユーザーの「意図（Action）」を具体的な「次のコンテキスト（Context）」に翻訳するメインエンジン。
 */
fun AppNavContext.action(
    action: AppNavAction,
    resolver: AppNavConstraintResolver,
    caller: AppNavCaller,
): AppNavContext {
    return when (action) {
        AppNavAction.Stack -> {
            next(role = null, caller = caller)
        }

        is AppNavAction.Expand -> {

            val expandRole = if(isRoot) role.expand(resolver.getConstraint(constraintId), action.priority) else null

            if (expandRole != null) {
                next(role = expandRole, caller = caller)
            } else {
                next(role = null, caller = caller)
            }
        }

        AppNavAction.Overlay -> {
            if (role is AppNavRole.Overlay) {
                next(role = null, caller = caller)
            } else {
                next(role = AppNavRole.Overlay, caller = caller)
            }
        }

        AppNavAction.Replace -> this

    }
}