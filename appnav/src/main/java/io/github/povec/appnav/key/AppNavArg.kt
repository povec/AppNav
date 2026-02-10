package io.github.povec.appnav.key

/**
 * [AppNavArg]
 * 画面遷移に必要な引数を表すインターフェース。
 * * 開発者が定義する各画面のデータクラス（例：UserDetailArg）に実装します。
 * これ自体は純粋なデータであり、[createKey] を通じて実行時のコンテキスト（[AppNavContext]）
 * と結びつくことで、一意の識別子である [AppNavKey] へと変換されます。
 */
interface AppNavArg {
    /**
     * 指定されたコンテキストを用いて、この引数に対応する [AppNavKey] を生成します。
     * 通常は `AppNavKey(this, context)` を返す実装になります。
     */
    fun createKey(context: AppNavContext): AppNavKey
}