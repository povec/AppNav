package io.github.povec.appnav.constraint

import io.github.povec.appnav.key.AppNavRole
import kotlinx.serialization.Serializable

@DslMarker
annotation class AppNavConstraintDsl

/**
 * [AppNavConstraint]
 * アプリの論理的な骨格（DNA）を保持するデータクラス。
 * 画面の親子関係や優先順位を木構造で管理し、物理的なレイアウトから独立させる。
 */
@Serializable
data class AppNavConstraint(
    val id: String,
    val tree: AppNavConstraintTree,
    val overlay: String,
)

/**
 * [AppNavConstraintTree]
 * 木構造の各ノード。children が空であれば末端のペインを意味する。
 */
@Serializable
data class AppNavConstraintTree(
    val name: String,
    val priority: Int,
    val children: List<AppNavConstraintTree> = emptyList()
)

// --- DSL Builder ---

/**
 * DSL を通じて AppNavConstraintTree を構築する。
 * 宣言順に priority を自動付与することで、コードの並び順がそのまま論理的な優先順位になる。
 */
class AppNavConstraintBuilder(private val name: String, private val priority: Int) {
    private val children = mutableListOf<AppNavConstraintTree>()

    @AppNavConstraintDsl
    fun pane(id: String, block: AppNavConstraintBuilder.() -> Unit = {}) {

        val nextPriority = children.size
        val builder = AppNavConstraintBuilder(id, nextPriority)
        builder.block()
        children.add(builder.build())
    }

    fun build(): AppNavConstraintTree = AppNavConstraintTree(name, priority, children)
}

/**
 * [appNavConstraint]
 * 制約を定義するためのエントリーポイント DSL。
 */
@AppNavConstraintDsl
fun appNavConstraint(
    id: String,
    base: String,
    overlay: String,
    block: AppNavConstraintBuilder.() -> Unit
): AppNavConstraint {
    return AppNavConstraint(
        id = id,
        tree = AppNavConstraintBuilder(base, 0).apply(block).build(),
        overlay = overlay
    )
}

/**
 * 指定されたペイン名（String）から、その制約内での [AppNavRole] を特定する。
 */
fun AppNavConstraint.findRole(name: String): AppNavRole {
    if (name == overlay) return AppNavRole.Overlay
    if (name == tree.name) return AppNavRole.Base

    return tree.findRole { this.name == name }?: error("Pane name '$name' not found in constraint $id")
}

/**
 * Role に基づいて、木構造内の該当するサブツリーノードを特定する。
 */
fun AppNavConstraint.findTree(role: AppNavRole): AppNavConstraintTree? {
    if (role == AppNavRole.Overlay) return null
    if (role == AppNavRole.Base) return tree

    // path の 2番目以降を使って、順に子ノードへ潜っていく
    return role.priorityPath.drop(1).fold(tree) { current, priority ->
        current.children.find { it.priority == priority }?: return null
    }
}

/**
 * Role に基づいて、木構造内の該当するサブツリーノードを特定する。
 */
fun AppNavConstraint.flatRoles(role: AppNavRole): List<AppNavRole> =
    flatRolesBuilder(role) { it }

fun <T> AppNavConstraint.flatRolesBuilder(role: AppNavRole, builder: AppNavConstraintTree.(role: AppNavRole) -> T): List<T> {
    val tree = findTree(role)?: return emptyList()

    return tree.flatRolesBuilder(role.priorityChain, builder)
}

/**
 * 指定された条件に一致した、その制約内での [AppNavRole] を特定する。
 */
private fun AppNavConstraintTree.findRole(finder: AppNavConstraintTree.() -> Boolean): AppNavRole? {
    // Treeから対象のパスを探す
    val path = findPath(emptyList(), finder)?: return null

    return AppNavRole.Pane(
        priority = path.last(),
        priorityChain = path.dropLast(1)
    )
}

/**
 * 特定の条件を満たすノードを探索し、ルートからの Priority パスを返す。
 */
private fun AppNavConstraintTree.findPath(currentChain: List<Int>, finder: AppNavConstraintTree.() -> Boolean): List<Int>? {
    val newChain = currentChain + this.priority
    if (this.finder()) return newChain

    for (child in children) {
        val found = child.findPath(newChain, finder)
        if (found != null) return found
    }

    return null
}

private fun <T> AppNavConstraintTree.flatRolesBuilder(currentChain: List<Int>, builder: AppNavConstraintTree.(role: AppNavRole) -> T): List<T> = buildList {
    val newChain = currentChain + this@flatRolesBuilder.priority

    val currentRole = if(newChain.size == 1) {
        AppNavRole.Base
    } else {
        AppNavRole.Pane(
            priority = newChain.last(),
            priorityChain = newChain.dropLast(1)
        )
    }

    add(builder(currentRole))

    val roles = children.flatMap { it.flatRolesBuilder(newChain, builder) }

    addAll(roles)

}