package io.github.povec.appnav.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import io.github.povec.appnav.constraint.AppNavConstraintResolver
import io.github.povec.appnav.message.AppNavMessenger
import io.github.povec.appnav.message.rememberAppNavMessenger
import io.github.povec.appnav.key.AppNavKey

/**
 * [AppNavDispatcher] を生成し、ライフサイクル内で保持するための Composable。
 * [AppNavMessenger] の初期化もここで行い、各コンポーネントを統合します。
 */
@Composable
internal fun rememberAppNavDispatcher(
    backStack: AppNavBackStack,
    registerKeyProvider: (String) -> AppNavKey,
    constraintResolver: AppNavConstraintResolver,
): AppNavDispatcher {

    val messenger = rememberAppNavMessenger(backStack)

    return remember(backStack, messenger, registerKeyProvider, constraintResolver) {
        AppNavDispatcher(backStack, messenger, registerKeyProvider, constraintResolver)
    }

}

/**
 * [AppNavDispatcher]
 * AppNav のコアエンジンを構成する主要コンポーネントを集約したデータクラス。
 *
 * @property backStack 画面履歴の管理本体。
 * @property messenger 画面間通信（Result送信/Pub-Sub）の基盤。
 * @property registerKeyProvider セッションIDから固定の [AppNavKey] を取得するためのプロバイダ。
 * @property constraintResolver 引数（Arg）からアプリの骨格（Constraint）を導き出す解決器。
 */
internal data class AppNavDispatcher(
    val backStack: AppNavBackStack,
    val messenger: AppNavMessenger,
    val registerKeyProvider: (String) -> AppNavKey,
    val constraintResolver: AppNavConstraintResolver,
)

/**
 * 下位の Composable（主に [AppNavController]）が Dispatcher にアクセスするための CompositionLocal。
 * [io.github.povec.appnav.AppNavDisplay] 内で提供されることが保証されている必要があります。
 */
internal val LocalAppNavDispatcher = staticCompositionLocalOf<AppNavDispatcher> {
    error("AppNavDisplay not found in parent hierarchy. Ensure AppNavDisplay is at the root of your navigation.")
}