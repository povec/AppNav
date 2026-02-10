package io.github.povec.appnav.scene

import androidx.annotation.FloatRange
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.SeekableTransitionState
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.rememberTransition
import androidx.compose.foundation.MutatorMutex
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigationevent.NavigationEvent

/**
 * [AppNavTransitionValue]
 * 特定の瞬間における「全ペインの配置状態」を保持するデータクラス。
 * アニメーションの InitialState / TargetState として機能します。
 */
data class AppNavTransitionValue(
    /** 各ペインのインデックス（優先度）ごとのコンテンツキー */
    val paneEntryKeys: List<Any?>,
    /** 最前面に表示されるオーバーレイのキー */
    val overlayEntryKey: Any?,
    /** 論理的なスタックの深さ */
    val size: Int,
){
    /** 指定された優先度 [index] のキーを取得。Int.MAX_VALUE はオーバーレイを指す。 */
    operator fun get(index: Int) =
        if(index == Int.MAX_VALUE) overlayEntryKey else paneEntryKeys[index]

    companion object {
        val EMPTY = AppNavTransitionValue(
            paneEntryKeys = emptyList(),
            overlayEntryKey = null,
            size = 0
        )
    }
}

/**
 * [AppNavTransitionType]
 * 個別のペインで発生している遷移の種類。
 * これに基づいて「横スライド」「下からフェード」などの Spec が選択されます。
 */
enum class AppNavTransitionType{
    None, // null -> null
    Enter, // null -> state
    Exit, // state -> null
    Nav, // state -> state (!isPop && !isPredictivePop)
    Pop, // state -> state (isPop)
    PredictivePop, // state -> state (isPredictivePop)
}

/**
 * [AppNavTransitionFlags]
 * UIレイヤーが現在のアニメーション状況（進捗率や種類）を把握するための読み取り専用インターフェース。
 */
interface AppNavTransitionFlags {

    val isPredictivePop: Boolean
    val predictiveBackSwipEdge: @NavigationEvent.SwipeEdge Int
    val isPop: Boolean
    @get:FloatRange(from = 0.0, to = 1.0)
    val progressFraction: Float

    /** 特定のペイン優先度における遷移タイプを判定 */
    fun transitionType(panePriority: Int): AppNavTransitionType

}

/**
 * [AppNavTransitionState] (Internal)
 * [SeekableTransitionState] をラップし、AppNav 専用のマルチペイン遷移ロジックを管理。
 * [MutatorMutex] により、ジェスチャー中の割り込みやアニメーションの競合を安全に処理します。
 */
internal class AppNavTransitionState(initialSessionValue: AppNavTransitionValue): AppNavTransitionFlags {

    // SeekableTransitionState を使って SessionValue 間の遷移を管理
    private val transitionState = SeekableTransitionState(initialSessionValue)

    /** 現在のセッション状態（アニメーションの開始点） */
    internal val currentState: AppNavTransitionValue
        get() = transitionState.currentState

    /** 目標とするセッション状態（アニメーションの終着点） */
    internal val targetState: AppNavTransitionValue
        get() = transitionState.targetState

    /** 遷移の進捗率 (0.0 〜 1.0) */
    @get:FloatRange(from = 0.0, to = 1.0)
    override val progressFraction: Float
        get() = transitionState.fraction

    /** 予測型戻りジェスチャーが進行中かどうか */
    override val isPredictivePop: Boolean
        get() = _predictiveBackSwipeState != null

    override val predictiveBackSwipEdge: @NavigationEvent.SwipeEdge Int
        get() = _predictiveBackSwipeState?: NavigationEvent.EDGE_NONE

    /** 予測型戻りジェスチャーの状態 */
    private var _predictiveBackSwipeState: @NavigationEvent.SwipeEdge Int? by mutableStateOf(null)

    /**
     * currentState と targetState から Pop（戻る）かどうかを判定する
     */
    override val isPop: Boolean
        get() = currentState.size > targetState.size

    override fun transitionType(panePriority: Int): AppNavTransitionType {
        val current = currentState[panePriority]
        val target = targetState[panePriority]

        return when{
            current == null && target == null -> AppNavTransitionType.None
            current != null && target == null -> AppNavTransitionType.Exit
            current == null && target != null -> AppNavTransitionType.Enter
            isPredictivePop -> AppNavTransitionType.PredictivePop
            isPop -> AppNavTransitionType.Pop
            else -> AppNavTransitionType.Nav
        }
    }

    // 同時実行されるアニメーションの競合を防ぐ
    private val mutatorMutex = MutatorMutex()

    @Composable
    fun rememberTransition(): Transition<AppNavTransitionValue> =
        rememberTransition(
            transitionState,
            label = "AppNavTransition"
        )

    suspend fun snapTo(targetState: AppNavTransitionValue) {
        mutatorMutex.mutate {
            this._predictiveBackSwipeState = null
            transitionState.snapTo(targetState)
        }
    }

    suspend fun seekTo(
        @FloatRange(from = 0.0, to = 1.0) fraction: Float,
        targetState: AppNavTransitionValue,
        isPredictiveBackState: @NavigationEvent.SwipeEdge Int?,
    ) {
        mutatorMutex.mutate {
            this._predictiveBackSwipeState = isPredictiveBackState
            transitionState.seekTo(fraction, targetState)
        }
    }

    suspend fun animateTo(
        targetState: AppNavTransitionValue,
        animationSpec: FiniteAnimationSpec<Float>? = null,
        isPredictiveBackState: @NavigationEvent.SwipeEdge Int? = null
    ) {
        mutatorMutex.mutate {
            try {
                this._predictiveBackSwipeState = isPredictiveBackState
                transitionState.animateTo(targetState, animationSpec)
            } finally {
                // アニメーション完了時にフラグをリセット
                this._predictiveBackSwipeState = null
            }
        }
    }
}