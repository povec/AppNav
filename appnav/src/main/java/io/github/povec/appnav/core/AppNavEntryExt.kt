package io.github.povec.appnav.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavEntry
import io.github.povec.appnav.key.AppNavContext
import io.github.povec.appnav.key.AppNavKey

inline fun <reified K : AppNavKey> EntryProviderScope<AppNavKey>.entryWithController(
    noinline clazzContentKey: (K) -> Any = { defaultContentKey(it) },
    metadata: Map<String, Any> = emptyMap(),
    noinline content: @Composable (K) -> Unit,
) {
    entry<K>(
        clazzContentKey = clazzContentKey,
        metadata = metadata
    ) { key ->
        CompositionLocalProvider(LocalNavController provides rememberAppNavController(key)) {
            content.invoke(key)
        }
    }
}

fun defaultContentKey(key: Any): Any = key

val NavEntry<AppNavKey>.context: AppNavContext
    get() = (contentKey as? AppNavKey)?.context ?: error(
        "NavEntry must have AppNavContext"
    )