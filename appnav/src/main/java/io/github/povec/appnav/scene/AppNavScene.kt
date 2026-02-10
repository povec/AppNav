package io.github.povec.appnav.scene

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Transition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.Scene
import androidx.navigationevent.NavigationEvent
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.NavigationEventTransitionState.InProgress
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import io.github.povec.appnav.constraint.AppNavConstraint
import io.github.povec.appnav.constraint.flatRoles
import io.github.povec.appnav.core.context
import io.github.povec.appnav.key.AppNavKey
import io.github.povec.appnav.key.AppNavRole
import io.github.povec.appnav.key.AppNavSession
import io.github.povec.appnav.key.sortBySeekOrder
import io.github.povec.appnav.key.sortByVisibleOrder

/**
* ペイン内の AnimatedContentScope を取得するための Local。
* 画面遷移アニメーションの詳細な制御に使用します。
*/
val LocalNavPaneAnimatedContentScope: ProvidableCompositionLocal<AnimatedContentScope> =
    staticCompositionLocalOf {
        throw IllegalStateException(
            "LocalNavPaneAnimatedContentScope が見つかりません。 " +
                    "この Scope は AppNavScene の Pane (EntryHolder) 内部でのみアクセス可能です。"
        )
    }

/**
 * [AppNavScene]
 * Navigation3 の [Scene] を拡張し、マルチペインレイアウトと高度な遷移アニメーションを実現するクラス。
 * * 現在のバックスタック（entries）を解析し、[AppNavConstraint] に基づいて
 * どの画面をどのペインに配置するか（[AppNavTransitionValue]）を計算します。
 */
class AppNavScene(
    override val key: Any,
    val onBack: () -> Unit,
    val activeSession: AppNavSession,
    val sessionEntries: List<NavEntry<AppNavKey>>,
    val constraint: AppNavConstraint,
    val layout: AppNavSceneLayout,
    val allEntries: List<NavEntry<AppNavKey>>,
    val sizeTransform: SizeTransform?,
    val enterTransitionSpec: AnimatedContentTransitionScope<AppNavTransitionValue>.() -> ContentTransform,
    val exitTransitionSpec: AnimatedContentTransitionScope<AppNavTransitionValue>.() -> ContentTransform,
    val transitionSpec: AnimatedContentTransitionScope<AppNavTransitionValue>.() -> ContentTransform,
    val popTransitionSpec: AnimatedContentTransitionScope<AppNavTransitionValue>.() -> ContentTransform,
    val predictivePopTransitionSpec: AnimatedContentTransitionScope<AppNavTransitionValue>.(@NavigationEvent.SwipeEdge Int) -> ContentTransform,
) : Scene<AppNavKey> {

    override val entries: List<NavEntry<AppNavKey>> get() = this@AppNavScene.sessionEntries
    override val previousEntries: List<NavEntry<AppNavKey>> get() = allEntries.dropLast(1)

    /** 定義されたレイアウトの中から最大のペイン数を特定 */
    val resolveMaxPaneCount: Int = layout.entryPaneCount

    /** 現在のスタック状態から計算された、描画・遷移用のスナップショット値 */
    val currentSessionValue: AppNavTransitionValue
        get() = calculateSessionValue(
            snapShotSessionEntries = this@AppNavScene.sessionEntries,
            constraint = constraint,
            resolveMaxPaneCount = resolveMaxPaneCount,
        )

    /** 戻る操作を行った際の遷移先情報を保持するクラス */
    class OnBackResult(

        val previousSessionValue: AppNavTransitionValue?,

        val previousEntries: List<NavEntry<AppNavKey>>,

        )

    val onBackResult: OnBackResult = calculateOnBackResult()

    /**
     * 1つ前の状態（戻り先）のペイン構成をあらかじめ計算します。
     * これにより、戻るジェスチャー中のリアルタイムなプレビューが可能になります。
     */
    private fun calculateOnBackResult(): OnBackResult {

        val previousSessionEntries = this@AppNavScene.sessionEntries.dropLast(1)
        val previousAllEntries = allEntries.dropLast(1)

        return OnBackResult(
            previousSessionValue = if(previousSessionEntries.isEmpty()) AppNavTransitionValue.EMPTY else calculateSessionValue(previousSessionEntries, constraint, resolveMaxPaneCount),
            previousEntries = previousAllEntries
        )
    }

    /**
     * 【コアロジック】
     * 「論理スタック」を「物理ペイン」に変換するマッピング計算。
     * Role の優先順位を計算して
     * どのエントリがどの枠（Pane）に座るべきかを決定します。
     */
    private fun calculateSessionValue(
        snapShotSessionEntries: List<NavEntry<AppNavKey>>,
        constraint: AppNavConstraint,
        resolveMaxPaneCount: Int,
    ): AppNavTransitionValue {

        val visibleOrder = constraint.flatRoles(AppNavRole.Base).sortByVisibleOrder()

        val paneOrder = visibleOrder.take(resolveMaxPaneCount)

        val last = paneOrder.last()

        val dropsSeekRoles = visibleOrder.drop(resolveMaxPaneCount).filter { it.priorityChain == last.priorityChain }

        val seekRoles = (listOf(last) + dropsSeekRoles).toSet()

        val parentPaneIndex = paneOrder.indexOfLast { it.priorityPath == last.priorityChain }

        val panesRoles = paneOrder.mapIndexed { index, role -> if(parentPaneIndex == index) dropsSeekRoles + listOf(role) else listOf(role) }

        val groupedSession = snapShotSessionEntries
            .groupBy { it.context.role }
            .mapValues { (_, value) -> value.last() }

        val paneEntryKeys = panesRoles
            .map { paneRoles ->
                paneRoles.firstNotNullOfOrNull { role ->
                    if(role in seekRoles){
                        constraint
                            .flatRoles(role)
                            .sortBySeekOrder()
                            .firstNotNullOfOrNull {
                                groupedSession[it]?.contentKey
                            }
                    } else {
                        groupedSession[role]?.contentKey
                    }
                }
            }

        val overlayEntryKey = groupedSession[AppNavRole.Overlay]?.contentKey

        return AppNavTransitionValue(
            paneEntryKeys = paneEntryKeys,
            overlayEntryKey = overlayEntryKey,
            size = snapShotSessionEntries.size
        )

    }

    /**
     * シーンのメインコンテンツ。
     * 状態管理、ジェスチャー連携、レイアウトの選択を行います。
     */
    override val content: @Composable () -> Unit = {

        val sessionValue = currentSessionValue
        val sessionState = remember { AppNavTransitionState(sessionValue) }

        // 値が更新されたらアニメーション開始
        LaunchedEffect(sessionValue) { sessionState.animateTo(sessionValue) }

        val previousSessionValue = onBackResult.previousSessionValue

        // --- 予測型戻るジェスチャーのハンドリング ---
        val gestureInfo = remember(key, entries) { AppNavSceneInfo(key, entries) }
        val gestureState = rememberNavigationEventState(currentInfo = gestureInfo)
        NavigationBackHandler(
            state = gestureState,
            isBackEnabled = previousSessionValue != AppNavTransitionValue.EMPTY,
            onBackCompleted = {
                repeat(allEntries.size - onBackResult.previousEntries.size) { onBack() }
            },
        )

        val transitionState = gestureState.transitionState
        LaunchedEffect(transitionState) {
            if (transitionState is InProgress) {
                // ジェスチャーの進行度（0.0 ~ 1.0）に合わせて画面をリアルタイムに動かす
                val latestEvent = transitionState.latestEvent

                sessionState.seekTo(
                    fraction = latestEvent.progress,
                    targetState = previousSessionValue!!,
                    isPredictiveBackState = latestEvent.swipeEdge,
                )
            } else {
                // ジェスチャーが中断または確定されたら安定した状態へ戻す
                sessionState.animateTo(targetState = sessionValue)
            }
        }

        val transition = sessionState.rememberTransition()

        // 子ペインへ提供する各種情報（Scope）の構築
        val scope = remember(constraint, sessionEntries, sessionState, transition) {

            object : AppNavSceneScope {
                override val activeSession: AppNavSession get() = this@AppNavScene.activeSession
                override val sessionEntries get() = this@AppNavScene.sessionEntries
                override val currentSessionValue get() = this@AppNavScene.currentSessionValue
                override val constraint get() = this@AppNavScene.constraint

                override val transitionFlags: AppNavTransitionFlags get() = sessionState
                override val transition: Transition<AppNavTransitionValue> get() = transition

                override val sizeTransform: SizeTransform?
                    get() = this@AppNavScene.sizeTransform
                override val enterTransitionSpec: AnimatedContentTransitionScope<AppNavTransitionValue>.() -> ContentTransform
                    get() = this@AppNavScene.enterTransitionSpec
                override val exitTransitionSpec: AnimatedContentTransitionScope<AppNavTransitionValue>.() -> ContentTransform
                    get() = this@AppNavScene.exitTransitionSpec
                override val transitionSpec: AnimatedContentTransitionScope<AppNavTransitionValue>.() -> ContentTransform
                    get() = this@AppNavScene.transitionSpec
                override val popTransitionSpec: AnimatedContentTransitionScope<AppNavTransitionValue>.() -> ContentTransform
                    get() = this@AppNavScene.popTransitionSpec
                override val predictivePopTransitionSpec: AnimatedContentTransitionScope<AppNavTransitionValue>.(@NavigationEvent.SwipeEdge Int) -> ContentTransform
                    get() = this@AppNavScene.predictivePopTransitionSpec
            }
        }

        layout.content?.invoke(scope)

    }
}

private data class AppNavSceneInfo(
    val key: Any, val entries: List<NavEntry<AppNavKey>>
) : NavigationEventInfo()