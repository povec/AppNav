package io.github.povec.appnav.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import io.github.povec.appnav.key.AppNavKey

/**
 * [rememberAppNavEntries]
 * 論理的な履歴（AppNavKey のリスト）を、Navigation3 が解釈可能な [NavEntry] のリストに変換します。
 * * 1. セッションごとに履歴をグルーピングし、セッション間の状態を分離します。
 * 2. 削除保留中のスタック（Pending）を管理し、roleが確実に入れ替わったタイミングで破棄します。
 */
@Composable
fun rememberAppNavEntries(
    backStack: List<AppNavKey>,
    inActiveBackStack: List<AppNavKey>,
    generateDecorators: @Composable () -> List<NavEntryDecorator<AppNavKey>>,
    entryProvider: (AppNavKey) -> NavEntry<AppNavKey>
): List<NavEntry<AppNavKey>> {


    val grouped = backStack.groupBy { it.context.session }
    val inactiveGrouped = inActiveBackStack.groupBy { it.context.session }

    val activeSessions = grouped.keys


    return (inactiveGrouped + grouped)
        .mapValues { (session, logicBackStack) ->
            key(session) {
                rememberAppNavLogicEntries(
                    logicBackStack = logicBackStack,
                    generateDecorators = generateDecorators,
                    entryProvider = entryProvider
                )
            }
        }
        .filterKeys { it in activeSessions }
        .flatMap { (_, entries) -> entries }
}

/**
 * logicBackStack を役割ごとに分配し、それぞれの物理スタックで独立して状態を管理します。
 * 状態が干渉せず、個別に ViewModel や保存済み状態が維持されます。
 */
@Composable
private fun rememberAppNavLogicEntries(
    logicBackStack: List<AppNavKey>,
    generateDecorators: @Composable () -> List<NavEntryDecorator<AppNavKey>>,
    entryProvider: (AppNavKey) -> NavEntry<AppNavKey>
): List<NavEntry<AppNavKey>> {

    val stack = rememberAppNavKeyChain()
    val pending = rememberAppNavKeyChain()

    val logicExistRoles = logicBackStack.map { it.context.role }.toSet()
    val existRoles = stack.map { it.context.role }.toSet()

    val rolesToRemove = existRoles - logicExistRoles
    val rolesToAdd = logicExistRoles - existRoles

    pending.removeAll { it.context.role in rolesToAdd }
    pending.addAll(logicBackStack.filter { it.context.role in rolesToRemove })

    stack.clear()
    stack.addAll(logicBackStack)

    val allKeys = (pending + stack).distinct()

    val entries = rememberDecoratedNavEntries(allKeys, generateDecorators.invoke(), entryProvider)

    return remember(logicBackStack, entries) {
        logicBackStack.mapNotNull { key ->
            entries.find { it.contentKey == key }
        }
    }
}