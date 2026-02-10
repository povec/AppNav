package io.github.povec.appnav.scene

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import androidx.navigationevent.NavigationEvent
import io.github.povec.appnav.constraint.AppNavConstraintResolver
import io.github.povec.appnav.key.AppNavKey
import io.github.povec.appnav.core.context

/**
 * [AppNavSceneStrategy]
 * 現在のバックスタックに対して、どのレイアウトと遷移ルールを適用すべきかを決定する抽象基盤。
 * * * 役割：
 * 1. スタックのトップ画面から現在のセッションと制約（Constraint）を特定する。
 * 2. 適合するレイアウト（Layout）があるかを検証する。
 * 3. 条件を満たせば、アニメーション設定やコンテキストを統合した [AppNavScene] を生成する。
 */
abstract class AppNavSceneStrategy() : SceneStrategy<AppNavKey> {

    protected abstract val resolver: AppNavConstraintResolver
    protected abstract val layouts: List<AppNavSceneLayout>
    /** この戦略の論理名*/
    protected abstract val name: String

    // 各種アニメーションの定義
    protected abstract val sizeTransform: SizeTransform?
    protected abstract val enterTransitionSpec: AnimatedContentTransitionScope<AppNavTransitionValue>.() -> ContentTransform
    protected abstract val exitTransitionSpec: AnimatedContentTransitionScope<AppNavTransitionValue>.() -> ContentTransform
    protected abstract val transitionSpec: AnimatedContentTransitionScope<AppNavTransitionValue>.() -> ContentTransform
    protected abstract val popTransitionSpec: AnimatedContentTransitionScope<AppNavTransitionValue>.() -> ContentTransform
    protected abstract val predictivePopTransitionSpec: AnimatedContentTransitionScope<AppNavTransitionValue>.(@NavigationEvent.SwipeEdge Int) -> ContentTransform

    /**
     * 現在のスタックに対して、この戦略が適用可能かどうかを判定するカスタムロジック。
     */
    protected abstract fun calculateResolve(
        entries: List<NavEntry<AppNavKey>>
    ): Boolean

    /**
     * Navigation3 のコアメソッド。スタックが変更されるたびに呼ばれ、
     * 適切な Scene を返すか、適用外なら null を返します。
     */
    override fun SceneStrategyScope<AppNavKey>.calculateScene(
        entries: List<NavEntry<AppNavKey>>
    ): Scene<AppNavKey>? {

        if (entries.isEmpty()) return null

        // 1. サブクラスによる適用可否の判定
        if (!calculateResolve(entries)) return null

        val topEntry = entries.last()
        val topContext = topEntry.context
        val constraintId = topContext.constraintId

        // 2. 現在の物理制約（ID）と論理名（name）に合致するレイアウトを抽出
        val layout = layouts
            .find { it.isResolve(constraintId, name) }
            ?: return null

        // 3. 現在アクティブなセッションを特定
        val activeSession = topContext.session

        // 4. 同じセッションに属するエントリのみを抽出（これがこのシーンの「スタック」になる）
        val sessionEntries = entries.filter { it.context.session == activeSession }

        if(sessionEntries.isEmpty()) return null

        // 5. 画面の骨格（Constraint）を取得
        val constraint = resolver.getConstraint(topContext.constraintId)

        // 全てが揃ったら AppNavScene を生成して「舞台」を構築
        return AppNavScene(
            key = "$constraintId of $name - ${activeSession.identifier}",
            constraint = constraint,
            activeSession = activeSession,
            sessionEntries = sessionEntries,
            allEntries = entries,
            onBack = onBack,
            layout = layout,
            sizeTransform = sizeTransform,
            enterTransitionSpec = enterTransitionSpec,
            exitTransitionSpec = exitTransitionSpec,
            transitionSpec = transitionSpec,
            popTransitionSpec = popTransitionSpec,
            predictivePopTransitionSpec = predictivePopTransitionSpec,
        )
    }
}