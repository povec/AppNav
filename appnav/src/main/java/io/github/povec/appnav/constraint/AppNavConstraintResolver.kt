package io.github.povec.appnav.constraint

import io.github.povec.appnav.key.AppNavArg
import kotlin.reflect.KClass

/**
 * [AppNavConstraintResolver]
 * 引数（Arg）の型に基づいて、適用すべき制約（Constraint）を特定する「アプリの羅針盤」。
 * * どの画面データ（Arg）がどの論理構造（Constraint）に属しているかを一括管理し、
 * ナビゲーション実行時に動的に構造を解決するために使用される。
 */
data class AppNavConstraintResolver(
    private val argToIdMap: Map<KClass<out AppNavArg>, String>,
    private val idToConstraintMap: Map<String, AppNavConstraint>,
    private val defaultConstraint: AppNavConstraint? = null
) {

    /**
     * [resolveId]
     * 指定された [AppNavArg] の型から、対応する Constraint の ID を解決する。
     * バインドされていない型の場合は、otherwise で設定されたデフォルトを返す。
     */
    fun resolveId(arg: AppNavArg): String =
        argToIdMap[arg::class]
            ?: defaultConstraint?.id
            ?: error("No ConstraintId bound for ${arg::class} and no default provided.")

    /**
     * [getConstraint]
     * ID から実際の [AppNavConstraint] オブジェクトを取得する。
     */
    fun getConstraint(id: String): AppNavConstraint =
        idToConstraintMap[id] ?: error("No Constraint found for ID: $id")
}

/**
 * [AppNavConstraintResolverBuilder]
 * [AppNavConstraintResolver] を型安全な DSL で構築するためのビルダー。
 */
class AppNavConstraintResolverBuilder {
    private val argToIdMap = mutableMapOf<KClass<out AppNavArg>, String>()
    private val idToConstraintMap = mutableMapOf<String, AppNavConstraint>()
    private var defaultConstraint: AppNavConstraint? = null

    /**
     * [otherwise]
     * 明示的にバインドされていない [AppNavArg] が渡された際に、
     * フォールバックとして使用されるデフォルトの制約を設定する。
     */
    @AppNavConstraintDsl
    fun otherwise(constraint: AppNavConstraint) {
        idToConstraintMap[constraint.id] = constraint
        defaultConstraint = constraint
    }

    /**
     * [bind]
     * 特定の [AppNavArg] の型と、使用する制約（Constraint）を紐付ける。
     */
    @AppNavConstraintDsl
    inline fun <reified T : AppNavArg> bind(constraint: AppNavConstraint) =
        bind(T::class, constraint)

    /**
     * [bind]
     * KClass を直接指定して制約を紐付ける（内部用、またはリフレクション用）。
     */
    @AppNavConstraintDsl
    fun bind(klass: KClass<out AppNavArg>, constraint: AppNavConstraint) {
        idToConstraintMap[constraint.id] = constraint
        argToIdMap[klass] = constraint.id
    }

    /**
     * ビルドを行い、不変（Immutable）な [AppNavConstraintResolver] インスタンスを生成する。
     */
    fun build() = AppNavConstraintResolver(
        argToIdMap = argToIdMap.toMap(),
        idToConstraintMap = idToConstraintMap.toMap(),
        defaultConstraint = defaultConstraint
    )
}

/**
 * [constraintResolver]
 * アプリの制約解決ルールを定義するためのエントリーポイント DSL。
 */
fun constraintResolver(block: AppNavConstraintResolverBuilder.() -> Unit): AppNavConstraintResolver {
    return AppNavConstraintResolverBuilder().apply(block).build()
}