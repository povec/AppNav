package io.github.povec.appnav

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.defaultPopTransitionSpec
import androidx.navigation3.ui.defaultPredictivePopTransitionSpec
import androidx.navigation3.ui.defaultTransitionSpec
import androidx.navigationevent.NavigationEvent
import io.github.povec.appnav.constraint.AppNavConstraintResolver
import io.github.povec.appnav.core.AppNavBackStack
import io.github.povec.appnav.core.LocalAppNavDispatcher
import io.github.povec.appnav.core.rememberAppNavDispatcher
import io.github.povec.appnav.core.rememberAppNavEntries
import io.github.povec.appnav.core.rememberAppNavSaveableStateHolderNavEntryDecorator
import io.github.povec.appnav.key.AppNavKey

/**
 * [AppNavDisplay]
 * AppNav ライブラリのメインエントリーポイント。
 * * 履歴（[backStack]）、画面解決（[constraintResolver]）、表示戦略（[sceneStrategy]）を統合し、
 * ナビゲーションの状態に応じた適切な UI を描画します。
 */
@Composable
fun AppNavDisplay(
    modifier: Modifier = Modifier,
    /** アプリ全体の画面履歴を管理するスタック */
    backStack: AppNavBackStack,
    /** 戻る操作で履歴が空になった際のコールバック */
    onFinish: () -> Unit = {},
    contentAlignment: Alignment = Alignment.TopStart,
    /** 識別子から Key を生成するプロバイダー */
    registerKeyProvider: (String) -> AppNavKey,
    /** 画面引数から物理制約を解決するリゾルバー */
    constraintResolver: AppNavConstraintResolver,
    /** Key から具体的な NavEntry を生成するプロバイダー */
    entryProvider: (AppNavKey) -> NavEntry<AppNavKey>,
    /** 画面（Entry）に対して状態保存などの付加機能を適用するデコレータ */
    generateDecorators: @Composable () -> List<NavEntryDecorator<AppNavKey>> = {
        listOf(
            rememberAppNavSaveableStateHolderNavEntryDecorator()
        )
    },
    /** 現在の状況に最適な Scene を選択する戦略 */
    sceneStrategy: SceneStrategy<AppNavKey>,
    // シーン間の遷移アニメーション設定
    sizeTransform: SizeTransform? = null,
    transitionSpec: AnimatedContentTransitionScope<Scene<AppNavKey>>.() -> ContentTransform =
        defaultTransitionSpec(),
    popTransitionSpec: AnimatedContentTransitionScope<Scene<AppNavKey>>.() -> ContentTransform =
        defaultPopTransitionSpec(),
    predictivePopTransitionSpec: AnimatedContentTransitionScope<Scene<AppNavKey>>.(@NavigationEvent.SwipeEdge Int) -> ContentTransform =
        defaultPredictivePopTransitionSpec(),
) {

    // 1. Dispatcher の提供
    // 画面遷移のコマンドを全画面で利用可能にする
    CompositionLocalProvider(
        LocalAppNavDispatcher provides rememberAppNavDispatcher(
            backStack,
            registerKeyProvider,
            constraintResolver
        )
    ) {

        // 2. NavEntry 郡の構築
        // バックスタック内の Key を Compose が扱えるエントリ（コンテンツ）に変換し、
        // 状態保存（SaveableState）などの装飾を施します。
        val entries = rememberAppNavEntries(
            backStack = backStack.backStack,
            inActiveBackStack = backStack.inActiveBackStack,
            generateDecorators = generateDecorators,
            entryProvider = entryProvider,
        )

        // 3. 描画
        // Navigation3 の標準コンポーネントを使い、計算されたエントリと戦略に基づいて描画。
        // ここで AppNavScene が生成され、マルチペインや遷移アニメーションが実行されます。
        NavDisplay(
            entries = entries,
            modifier = modifier,
            contentAlignment = contentAlignment,
            sceneStrategy = sceneStrategy,
            sizeTransform = sizeTransform,
            transitionSpec = transitionSpec,
            popTransitionSpec = popTransitionSpec,
            predictivePopTransitionSpec = predictivePopTransitionSpec,
            onBack = {
                if (backStack.back() == false) {
                    onFinish()
                }
            }
        )

    }

}