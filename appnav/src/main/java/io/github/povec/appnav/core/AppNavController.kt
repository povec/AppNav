package io.github.povec.appnav.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import io.github.povec.appnav.constraint.AppNavConstraintResolver
import io.github.povec.appnav.message.AppNavMessage
import io.github.povec.appnav.message.AppNavResult
import io.github.povec.appnav.key.AppNavAction
import io.github.povec.appnav.key.AppNavArg
import io.github.povec.appnav.key.AppNavCaller
import io.github.povec.appnav.key.AppNavConnect
import io.github.povec.appnav.key.AppNavContext
import io.github.povec.appnav.key.AppNavKey
import io.github.povec.appnav.key.action
import io.github.povec.appnav.key.plus
import kotlin.reflect.KClass

/**
 * [AppNavController] を画面内の Composable から取得するための Local。
 */
val LocalNavController = staticCompositionLocalOf<AppNavController> {
    error("navController not provided")
}

/**
 * 現在の [AppNavKey] に基づいて [AppNavController] の実体を生成・保持します。
 * Dispatcher を介して BackStack や Messenger 操作を仲介します。
 */
@Composable
fun rememberAppNavController(key: AppNavKey): AppNavController {

    val dispatcher = LocalAppNavDispatcher.current

    return remember(key, dispatcher) {

        object : AppNavController() {
            override val key: AppNavKey = key

            override fun rebase(sessionId: String) = with(dispatcher) {
                backStack.rebase(registerKeyProvider(sessionId))
            }

            override fun reset() = dispatcher.backStack.navigate(key)

            override fun startConstSession(sessionId: String) = with(dispatcher) {
                backStack.navigate(registerKeyProvider(sessionId))
            }

            override fun startSession(arg: AppNavArg, connect: AppNavConnect?) = with(dispatcher) {
                backStack.navigate(
                    newKey = arg + AppNavContext.createGeneral(
                        constraintId = constraintResolver.resolveId(arg),
                        caller = connect?.let { AppNavCaller(key.context.hashCode(), it.payload) }
                    )
                )
            }

            override fun navigate(arg: AppNavArg, action: AppNavAction, connect: AppNavConnect?) = with(dispatcher) {
                backStack.navigate(arg + action(constraintResolver, connect))
            }

            override fun pop() = dispatcher.backStack.pop(key.context)

            override fun send(result: AppNavResult) = dispatcher.messenger.send(key.context.caller, result)

            @Composable
            override fun <M : AppNavMessage> Receive(kClass: KClass<M>, onResult: (M) -> Boolean) {
                val myHash = key.context.hashCode()

                LaunchedEffect(myHash, dispatcher.messenger) {
                    dispatcher.messenger
                        .receive(myHash, kClass, onResult)
                }
            }

            override fun publish(state: Any) {
                dispatcher.messenger.publish(key.context.hashCode(), state)
            }

            @Composable
            override fun <T : Any> Subscribe(kClass: KClass<T>, onState: (T) -> Unit) {
                val myHash = key.context.caller.hash

                LaunchedEffect(myHash, dispatcher.messenger) {
                    dispatcher.messenger
                        .subscribe(myHash, kClass, onState)
                }
            }

        }
    }
}

/**
 * [AppNavController]
 * 開発者が画面遷移や通信を行うためのインターフェース。
 * 物理的な座標ではなく、「何を表示したいか（Arg）」と「どう扱いたいか（Action）」に集中できます。
 */
abstract class AppNavController() {

    protected abstract val key: AppNavKey

    abstract fun rebase(sessionId: String)

    abstract fun startConstSession(sessionId: String)

    abstract fun startSession(arg: AppNavArg, connect: AppNavConnect? = null)

    abstract fun navigate(arg: AppNavArg, action: AppNavAction, connect: AppNavConnect? = null)

    abstract fun reset()

    abstract fun pop(): Boolean?

    abstract fun send(result: AppNavResult)

    @Composable
    inline fun <reified M : AppNavMessage> Receive(noinline onResult: (M) -> Boolean) =
        Receive(M::class, onResult)

    @Composable
    abstract fun <M : AppNavMessage> Receive(kClass: KClass<M>, onResult: (M) -> Boolean)

    abstract fun publish(state: Any)

    @Composable
    inline fun <reified T : Any> Subscribe(noinline onResult: (T) -> Unit) =
        Subscribe(T::class, onResult)

    @Composable
    abstract fun <T : Any> Subscribe(kClass: KClass<T>, onState: (T) -> Unit)

    protected operator fun AppNavAction.invoke(
        resolver: AppNavConstraintResolver,
        connect: AppNavConnect? = null
    ): AppNavContext =
        key.context.action(
            action = this,
            resolver = resolver,
            connect = connect,
        )
}