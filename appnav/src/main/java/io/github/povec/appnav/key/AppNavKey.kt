package io.github.povec.appnav.key

import androidx.navigation3.runtime.NavKey

/**
 * [AppNavKey]
 * AppNav における履歴管理の最小単位です。
 * * * [arg]: 画面に渡される具体的な引数（データ）。
 * * [context]: その画面が置かれている論理的な状況（セッション、ロール、ハッシュなど）。
 *
 * この 2 つが組み合わさることで、マルチペインにおける「左側の詳細画面」と「右側の詳細画面」
 * のように、同じデータ（Arg）を表示していても異なる状態を持つ画面を区別できます。
 */
interface AppNavKey : NavKey {
    /**画面に渡されるデータ本体*/
    val arg: AppNavArg
    /**このkeyの文脈*/
    val context: AppNavContext
}

/**
 * 引数（Arg）とコンテキスト（Context）を結合して [AppNavKey] を生成するためのユーティリティ演算子。
 * * 使用例:
 * `val key = UserDetailArg(id = 1) + AppNavContext.createGeneral(id)`
 */
operator fun AppNavArg.plus(context: AppNavContext): AppNavKey = createKey(context)