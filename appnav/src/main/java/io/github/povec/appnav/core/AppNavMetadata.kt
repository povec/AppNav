package io.github.povec.appnav.core

import io.github.povec.appnav.constraint.AppNavConstraint
import io.github.povec.appnav.key.AppNavRole
import io.github.povec.appnav.key.toConstraintName

/**
 * メタデータ解決のための予約語定数。
 * [GLOBAL] はアプリ全体、[DEFAULT] は制約内でのデフォルトを指します。
 */
object AppNavMetadataConst {
    const val GLOBAL = "global"
    const val DEFAULT = "default"
    const val SEPARATOR = ":"
}

/**
 * 階層構造を表現するためのキーを生成します。
 * 形式: "constraintId:roleName:identifier"
 */
fun createKey(constraint: String, role: String, identifier: String): String {
    return "$constraint${AppNavMetadataConst.SEPARATOR}$role${AppNavMetadataConst.SEPARATOR}$identifier"
}

/**
 * [AppNavMetadataBuilder]
 * DSL を用いて階層的なメタデータを定義するビルダー。
 * 設定の継承（imp）をサポートし、DRY（Don't Repeat Yourself）な定義を可能にします。
 */
class AppNavMetadataBuilder {
    private val data = mutableMapOf<String, Any>()

    private val global = AppNavMetadataConst.GLOBAL
    private val default = AppNavMetadataConst.DEFAULT

    /**
     * アプリ全体の共通設定
     */
    fun default(block: RoleScope.() -> Unit) {
        RoleScope(global, default).apply(block)
    }

    inner class ConstraintScope(private val constraintId: String) {

        /** Global Default を引き継いでこの制約のデフォルトを作る */
        fun default(block: RoleScope.() -> Unit) {
            val scope = RoleScope(constraintId, default)
            scope.implementFrom(global, default)
            scope.apply(block)
        }

        /** Global を引き継がず、この制約独自のデフォルトを作る */
        fun resetDefault(block: RoleScope.() -> Unit) {
            RoleScope(constraintId, default).apply(block)
        }

        /** 継承 infix */
        infix fun String.imp(sourceName: String): Pair<String, String> = this to sourceName
        infix fun List<String>.imp(sourceName: String): List<Pair<String, String>> =
            this.map { it to sourceName }

        /** 通常の Role 定義 (暗黙的にこの制約の default を継承) */
        fun role(vararg names: String, block: RoleScope.() -> Unit) {
            names.forEach { internalRole(it, default, block) }
        }

        /** 継承付き Role 定義 (単体/複数) */
        fun role(inheritance: Pair<String, String>, block: RoleScope.() -> Unit) =
            internalRole(inheritance.first, inheritance.second, block)

        fun role(inheritances: List<Pair<String, String>>, block: RoleScope.() -> Unit) =
            inheritances.forEach { internalRole(it.first, it.second, block) }

        private fun internalRole(newName: String, sourceName: String, block: RoleScope.() -> Unit) {
            val scope = RoleScope(constraintId, newName)

            if (sourceName == default) {
                if (hasConstraintDefault()) scope.implementFrom(constraintId, default)
                else scope.implementFrom(global, default)
            } else {
                scope.implementFrom(constraintId, sourceName)
            }
            scope.apply(block)
        }

        private fun hasConstraintDefault() =
            data.keys.any { it.startsWith("$constraintId${AppNavMetadataConst.SEPARATOR}$default") }
    }

    /**
     * 実際にキーと値のペアを登録するスコープ。
     */
    inner class RoleScope(private val constraintId: String, private val roleName: String) {
        operator fun set(identifier: String, value: Any) {
            data[createKey(constraintId, roleName, identifier)] = value
        }

        /** 他の Role スコープから既存の設定値をすべてコピーします。 */
        fun implementFrom(srcConstraint: String, srcRole: String) {
            val fromPrefix =
                "$srcConstraint${AppNavMetadataConst.SEPARATOR}$srcRole${AppNavMetadataConst.SEPARATOR}"
            val toPrefix =
                "$constraintId${AppNavMetadataConst.SEPARATOR}$roleName${AppNavMetadataConst.SEPARATOR}"
            data.filter { it.key.startsWith(fromPrefix) }.forEach { (k, v) ->
                data[k.replaceFirst(fromPrefix, toPrefix)] = v
            }
        }
    }

    /** 制約（Constraint）ごとの設定定義を開始します。 */
    fun constraint(constraintId: String, block: ConstraintScope.() -> Unit) =
        ConstraintScope(constraintId).apply(block)

    fun build() = data.toMap()
}

/**
 * メタデータを定義するためのエントリーポイント DSL。
 */
fun appNavMetadata(block: AppNavMetadataBuilder.() -> Unit): Map<String, Any> =
    AppNavMetadataBuilder().apply(block).build()

/**
 * 階層的なメタデータ解決を行う拡張関数。
 * * 優先順位:
 * 1. 現在の制約の現在の Role 専用設定
 * 2. 現在の制約のデフォルト設定
 * 3. アプリ全体のデフォルト設定
 * 4. identifier そのものをキーとした設定
 */
inline fun <reified T : Any> Map<String, Any>.find(
    identifier: String,
    role: AppNavRole,
    constraint: AppNavConstraint,
): T? = find(identifier, role.toConstraintName(constraint), constraint.id)

/**
 * 階層的なメタデータ解決を行う拡張関数。
 * * 優先順位:
 * 1. 現在の制約の現在の Role 専用設定
 * 2. 現在の制約のデフォルト設定
 * 3. アプリ全体のデフォルト設定
 * 4. identifier そのものをキーとした設定
 */
inline fun <reified T : Any> Map<String, Any>.find(
    identifier: String,
    roleName: String,
    constraintId: String,
): T? {
    val g = AppNavMetadataConst.GLOBAL
    val d = AppNavMetadataConst.DEFAULT

    val candidates = listOf(
        createKey(constraintId, roleName, identifier),
        createKey(constraintId, d, identifier),
        createKey(g, d, identifier),
        identifier
    )

    for (key in candidates) {
        val value = this[key] ?: continue
        if (value is T) return value
    }
    return null
}