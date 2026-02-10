package io.github.povec.appnav.key

import io.github.povec.appnav.constraint.AppNavConstraint
import io.github.povec.appnav.constraint.findTree
import kotlinx.serialization.Serializable

/**
 * [AppNavRole]
 * 画面がアプリの構造の中で果たす「役割」。
 * インデックスのリスト（priorityPath）によって、木構造上の位置を特定する。
 */
@Serializable
sealed interface AppNavRole {
    val priority: Int
    val priorityChain: List<Int>
    val priorityPath: List<Int> get() = priorityChain + priority
    val level: Int

    @Serializable
    data object Base : AppNavRole {
        override val priority: Int = 0
        override val priorityChain: List<Int> = emptyList()
        override val level: Int = 0
    }

    @Serializable
    data class Pane(
        override val priority: Int,
        override val priorityChain: List<Int>,
    ) : AppNavRole {
        override val level: Int get() = priorityChain.size
    }

    @Serializable
    data object Overlay : AppNavRole {
        override val priority: Int = 0
        override val priorityChain: List<Int> = listOf(0)

        override val level: Int = Int.MAX_VALUE
    }
}

/**
 * 現在のRoleが、指定された骨格(Constraint)において
 * 特定のPriorityに拡張可能かどうかを判定する。
 */
fun AppNavRole.expand(constraint: AppNavConstraint, priority: Int): AppNavRole? {
    // 現在の Role に対応するツリーノードを取得
    val tree = constraint.findTree(this) ?: return null
    if (tree.children.isEmpty()) return null

    // 指定された priority 以下で、最も大きい既存の priority を探す
    val bestChild = tree.children
        .filter { it.priority <= priority }
        .maxByOrNull { it.priority } ?: return null

    // priorityを付け足し新しい Role を作る
    return AppNavRole.Pane(
        priority = bestChild.priority,
        priorityChain = priorityPath
    )
}

/**
 * [AppNavRole] から論理的なペイン名を解決する。
 */
fun AppNavRole.toConstraintName(constraint: AppNavConstraint): String {
    return when (this) {
        is AppNavRole.Overlay -> constraint.overlay
        else -> constraint.findTree(this)?.name
            ?: error("Role not found in constraint tree")
    }
}

/**
 * 自身、または自身の子孫 Role であるかを判定する。
 */
fun AppNavRole.isSelfOrDescendantOf(other: AppNavRole): Boolean {
    return this == other || this.isDescendantOf(other)
}

/**
 * 指定された [other] が自身の先祖である（＝自分がその子孫である）かを判定する。
 */
fun AppNavRole.isDescendantOf(other: AppNavRole): Boolean {
    if (other is AppNavRole.Overlay) return false
    val otherPath = other.priorityPath
    return priorityPath.size > otherPath.size && priorityPath.take(otherPath.size) == otherPath
}

/**
 * 自身、または自身の先祖 Role であるかを判定する。
 */
fun AppNavRole.isSelfOrAncestorOf(other: AppNavRole): Boolean {
    return this == other || this.isAncestorOf(other)
}

/**
 * 指定された [other] が自身の子孫である（＝自分がその先祖である）かを判定する。
 */
fun AppNavRole.isAncestorOf(other: AppNavRole): Boolean {
    // Overlayは家系図の外なので、自分がOverlayなら他人の先祖にはなれない
    if (this is AppNavRole.Overlay) return false

    val myPath = this.priorityPath
    val otherPath = other.priorityPath

    // 相手のパスが自分より長く、かつ相手のパスの開始部分が自分のパスと一致していれば、自分は先祖。
    return otherPath.size > myPath.size && otherPath.take(myPath.size) == myPath
}

/**
 * [sortByVisibleOrder]
 * 視覚的な重なり順（Zオーダー）でソートする。
 * 階層が深いほど、また同一階層なら Priority が高いほど「手前」に来る。
 */
fun List<AppNavRole>.sortByVisibleOrder(): List<AppNavRole> {
    return sortedWith(
        comparator = compareBy<AppNavRole> { it.level }
            .then { r1, r2 ->
                val path1 = r1.priorityPath
                val path2 = r2.priorityPath
                // 共通の先祖をスキップ
                val commonCount = path1.zip(path2).takeWhile { (a, b) -> a == b }.count()

                // 分岐点の優先度を取得
                val p1 = path1.getOrNull(commonCount) ?: 0
                val p2 = path2.getOrNull(commonCount) ?: 0

                p1.compareTo(p2)
            }
    )
}

/**
 * [sortBySeekOrder]
 * 探索・解決順序でソートする（VisibleOrder の逆）。
 * 外側のレイアウトから順に解決していく際などに使用。
 */
fun List<AppNavRole>.sortBySeekOrder(): List<AppNavRole> {
    return sortedWith(
        comparator = Comparator<AppNavRole> { r1, r2 ->
            val path1 = r1.priorityPath
            val path2 = r2.priorityPath
            val commonCount = path1.zip(path2).takeWhile { (a, b) -> a == b }.count()

            val p1 = path1.getOrNull(commonCount) ?: 0
            val p2 = path2.getOrNull(commonCount) ?: 0

            p2.compareTo(p1)
        }
            .thenByDescending { it.level }
    )
}