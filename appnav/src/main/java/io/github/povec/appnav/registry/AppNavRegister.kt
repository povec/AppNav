package io.github.povec.appnav.registry

import io.github.povec.appnav.constraint.AppNavConstraintResolver
import io.github.povec.appnav.key.AppNavArg
import io.github.povec.appnav.key.AppNavContext
import io.github.povec.appnav.key.AppNavKey
import io.github.povec.appnav.key.AppNavSession
import io.github.povec.appnav.key.plus

/**
 * AppNavRegistry 構築用の DSL マーカー
 */
@DslMarker
annotation class AppNavRegistryDsl

/**
 * [AppNavRegistry]
 * 文字列の識別子（ID）と、それに対応する画面の引数（Arg）を紐付けて管理するレジストリ。
 * 主にボトムナビゲーションやサイドメニューなど、名前ベースで画面を呼び出すために使用されます。
 */
class AppNavRegistry(
    private val definitions: Map<String, AppNavArg>
) {
    /**
     * [AppNavConstraintResolver] を注入し、ID文字列を [AppNavKey] へ変換するための解決関数を生成します。
     * * @param resolver 引数（Arg）からアプリの制約（Constraint）を導き出す解決器
     * @return 識別子を受け取り、コンテキストが付与された [AppNavKey] を返す関数
     */
    fun resolve(resolver: AppNavConstraintResolver): (String) -> AppNavKey {
        return { identifier ->
            val arg = definitions[identifier]
                ?: throw IllegalStateException("Unknown identifier: $identifier")


            val constraintId = resolver.resolveId(arg)


            val parts = identifier.split(AppNavSession.Type.SEPARATOR, limit = 2)
            val type = parts[0].lowercase()
            val name = parts.getOrNull(1) ?: ""

            when (type) {
                AppNavSession.Type.SPECIFIC.value -> arg + AppNavContext.createSpecific(name, constraintId)
                AppNavSession.Type.MANAGED.value -> arg + AppNavContext.createManaged(name, constraintId)
                AppNavSession.Type.GENERAL.value -> arg + AppNavContext.createGeneral(constraintId)
                else -> throw IllegalArgumentException("Invalid session type: $identifier")
            }
        }
    }
}

/**
 * [AppNavRegistry] を構築するための DSL スコープクラス。
 */
@AppNavRegistryDsl
class AppNavRegistryScope {
    private val definitions = mutableMapOf<String, AppNavArg>()

    fun register(identifier: String, arg: AppNavArg) {
        definitions[identifier] = arg
    }

    fun build() = AppNavRegistry(definitions)
}

/**
 * アプリの画面レジストリを宣言的に定義するエントリーポイント。
 */
inline fun appNavRegistry(builder: AppNavRegistryScope.() -> Unit): AppNavRegistry =
    AppNavRegistryScope().apply(builder).build()