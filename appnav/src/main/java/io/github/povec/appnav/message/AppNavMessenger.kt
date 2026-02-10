package io.github.povec.appnav.message

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import io.github.povec.appnav.core.AppNavBackStack
import io.github.povec.appnav.key.AppNavCaller
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlin.reflect.KClass

/**
 * [AppNavMessenger] を生成し、履歴の状態に合わせて自動的にメモリを管理（掃除）します。
 */
@Composable
fun rememberAppNavMessenger(backStack: AppNavBackStack): AppNavMessenger {
    val messenger = remember { AppNavMessenger() }

    LaunchedEffect(backStack.backStack, backStack.inActiveBackStack) {
        val activeHashes = (backStack.backStack + backStack.inActiveBackStack)
            .map { it.context.hashCode() }
            .toSet()

        messenger.sync(activeHashes)
    }

    return messenger
}

/**
 * [AppNavMessenger]
 * 画面間通信のハブとなるクラス。
 * * 1. **通知 (Push/Receive)**: 呼び出し元への結果返却など、1回限りのイベント。
 * 2. **購読 (Publish/Subscribe)**: 特定の画面が掲示する「状態」を、他の画面がリアルタイムに観測する。
 */
class AppNavMessenger {
    /**
     * 【通知用】特定の住所（ハッシュ）に届く「手紙（通知）」を管理。
     * 画面が戻った瞬間に未読があれば 1 件受け取れるバッファ設計。
     */
    private val pushMap = mutableMapOf<Int, MutableSharedFlow<AppNavMessage>>()

    /**
     * 【購読用】特定の住所（ハッシュ）にある「掲示板（状態）」を管理。
     * replay = 1 により、いつ誰が見に行っても「現在の最新状態」が見える設計。
     */
    private val pullBoardMap = mutableMapOf<Int, MutableMap<KClass<*>, MutableSharedFlow<Any>>>()

    // --- 通知 (Event-based Messaging) ---

    private fun getPushFlow(targetHash: Int): MutableSharedFlow<AppNavMessage> {
        return pushMap.getOrPut(targetHash) {
            MutableSharedFlow(
                replay = 1,
                onBufferOverflow = BufferOverflow.DROP_OLDEST
            )
        }
    }

    /**
     * 指定された [caller] のポストへメッセージを送信します。
     */
    fun send(caller: AppNavCaller, result: AppNavResult) {
        if (caller != AppNavCaller.EMPTY) {
            getPushFlow(caller.hash).tryEmit(result + caller.payload)
        }
    }

    /**
     * 自分の [myHash] 宛に届いたメッセージを、指定した型 [klass] で待ち受けます。
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun <M : AppNavMessage> receive(myHash: Int, klass: KClass<M>, onResult: (M) -> Boolean) {

        val flow = getPushFlow(myHash)

        flow
            .filterIsInstance(klass)
            .collect { message ->
                if (onResult(message)) {
                    flow.resetReplayCache()
                }
            }
    }

    // --- 購読 (State-based Messaging) ---

    private fun getPullFlow(providerHash: Int, klass: KClass<*>): MutableSharedFlow<Any> {
        val board = pullBoardMap.getOrPut(providerHash) { mutableMapOf() }
        return board.getOrPut(klass) {
            MutableSharedFlow(
                replay = 1,
                onBufferOverflow = BufferOverflow.DROP_OLDEST
            )
        }
    }

    /**
     * 自分の [myHash] 空間に、指定された [state] を「現在の状態」として掲示します。
     */
    fun <T : Any> publish(myHash: Int, state: T) {
        getPullFlow(myHash, state::class).tryEmit(state)
    }

    /**
     * 指定された画面 [targetHash] が掲示している、型 [klass] の状態を観測します。
     */
    @Suppress("UNCHECKED_CAST")
    suspend fun <T : Any> subscribe(targetHash: Int, klass: KClass<T>, onState: (T) -> Unit) {

        val flow = getPullFlow(targetHash, klass) as Flow<T>

        flow
            .distinctUntilChanged()
            .collect { state ->
                onState(state)
            }
    }


    /**
     * 無効になったハッシュに関連するリソースをすべて破棄します。
     */
    fun sync(activeContextHashes: Set<Int>) {
        pushMap.keys.retainAll(activeContextHashes)
        pullBoardMap.keys.retainAll(activeContextHashes)
    }
}