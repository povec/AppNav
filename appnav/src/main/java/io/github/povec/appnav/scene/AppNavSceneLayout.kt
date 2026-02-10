package io.github.povec.appnav.scene

import androidx.compose.runtime.Composable
import kotlin.reflect.KClass

/**
 * [AppNavSceneLayoutHolder]
 * 特定の制約（[constraintId]）下での具体的な UI 構成を保持するコンテナ。
 * レイアウト内部で発生するイベント [E] を [AppNavEmitter] を介して最上位へ飛ばすことができます。
 */
class AppNavSceneLayoutHolder<E : Any>(
    val constraintId: String,
    val strategyNames: Set<String>,
    val entryPaneCount: Int,
    val layout: @Composable AppNavSceneScope.(AppNavEmitter<E>) -> Unit,
) {

    /** 現在の環境（制約 ID と戦略名）にこのレイアウトが適合するか判定します。 */
    fun isResolve(constraintId: String, strategyName: String): Boolean =
        this.constraintId == constraintId && strategyName in strategyNames

    /**
     * イベントハンドラを注入し、Compose のコンテンツとして実行可能な形に変換します。
     */
    fun content(
        onEvent: (Any) -> Unit
    ): @Composable (AppNavSceneScope.() -> Unit){

        val emitter = object : AppNavEmitter<E> {
            override fun send(event: E){ onEvent(event) }
        }

        return { layout(emitter) }
    }

}

/**
 * [AppNavEmitter]
 * レイアウト内部（UI）から発生したイベントを、型安全に外の世界へ送り出すための窓口。
 */
interface AppNavEmitter<in E> {
    fun send(event: E)
}

/**
 * [AppNavEventResolver]
 * 受け取ったイベントの型（[KClass]）に基づいて、適切な処理（ハンドラ）を呼び出す解決器。
 * これにより、複数のレイアウトから飛んでくる多様なイベントを一箇所で整理して処理できます。
 */
class AppNavEventResolver {
    private val handlers = mutableMapOf<KClass<*>, (Any) -> Unit>()

    inline fun <reified E : Any> on(noinline handler: (E) -> Unit) =
        on(E::class, handler)

    @Suppress("UNCHECKED_CAST")
    fun <E : Any> on(klass: KClass<E>, handler: (E) -> Unit) {
        handlers[klass] = { handler(it as E) }
    }

    fun handle(event: Any) {
        val key = handlers.keys.find { it.isInstance(event) }?: return
        handlers[key]?.invoke(event)
    }
}

/**
 * [AppNavConstraintLayoutBuilder]
 * 特定の [constraintId] に紐づく複数のレイアウト構成を構築する DSL スコープ。
 */
class AppNavConstraintLayoutBuilder<E : Any>(private val constraintId: String) {
    private val holders = mutableListOf<AppNavSceneLayoutHolder<E>>()

    /**
     * [presentation]
     * 特定の論理名（[strategyName]）とペイン数に対応する UI ルーティングを定義します。
     */
    fun presentation(
        paneCount: Int,
        vararg strategyName: String,
        router: @Composable AppNavSceneScope.(AppNavEmitter<E>) -> Unit
    ) {
        holders.add(
            AppNavSceneLayoutHolder(
                constraintId = constraintId,
                strategyNames = strategyName.toSet(),
                entryPaneCount = paneCount,
                layout = router
            )
        )
    }

    fun build(): List<AppNavSceneLayoutHolder<E>> = holders
}

/**
 * 物理制約に基づいたレイアウト定義を開始する DSL エントリーポイント。
 */
fun <E : Any> appNavConstraintLayout(
    constraintId: String,
    block: AppNavConstraintLayoutBuilder<E>.() -> Unit
): List<AppNavSceneLayoutHolder<E>> {
    return AppNavConstraintLayoutBuilder<E>(constraintId).apply(block).build()
}

/**
 * [AppNavSceneLayout]
 * [AppNavScene] が最終的に参照する、解決済みのレイアウトインターフェース。
 */
interface AppNavSceneLayout {

    val constraintId: String

    val strategyNames: Set<String>

    val entryPaneCount: Int

    fun isResolve(constraintId: String, strategyName: String): Boolean

    val content: (@Composable AppNavSceneScope.() -> Unit)?
}

/**
 * [AppNavSceneLayoutHolder] のリストにイベント処理ロジックを注入（バインド）し、
 * [AppNavSceneLayout] のリストへ変換します。
 */
fun List<AppNavSceneLayoutHolder<*>>.injectEvent(
    eventResolverBlock: AppNavEventResolver.() -> Unit
): List<AppNavSceneLayout> = map { it.injectEvent(eventResolverBlock) }

/**
 * イベント処理を一括定義（バインド）し、具体的な実体へと変換します。
 */
fun AppNavSceneLayoutHolder<*>.injectEvent(
    eventResolverBlock: AppNavEventResolver.() -> Unit
): AppNavSceneLayout {
    val origin = this
    val resolver = AppNavEventResolver().apply(eventResolverBlock)

    return object : AppNavSceneLayout {

        override val constraintId get() = origin.constraintId
        override val strategyNames get() = origin.strategyNames
        override val entryPaneCount get() = origin.entryPaneCount

        override fun isResolve(constraintId: String, strategyName: String): Boolean {
            return origin.isResolve(constraintId, strategyName)
        }

        override val content get() = origin.content(resolver::handle)

    }
}