package io.github.povec.appnav.key

import kotlinx.serialization.Serializable

/**
 * [AppNavCaller]
 * どの画面（あるいはどの地点）から現在の画面が呼び出されたかを記録します。
 * * 主に `startSessionForResult` などで利用され、この [hash] を宛先として
 * メッセージやリザルトを送り返すための「返信先住所」として機能します。
 *
 * @property hash 呼び出し元の [AppNavContext] のハッシュ値。
 * @property payload 呼び出し元から渡される任意の識別情報やメタデータ。
 * (例: どのボタンから呼ばれたか、どのリクエストIDに関連するか等)
 */
@Serializable
data class AppNavCaller(
    val hash: Int,
    val payload: String? = null,
) {
    companion object {
        /**
         * 呼び出し元が存在しない（ルート画面など）場合に使用される空の定義。
         */
        val EMPTY = AppNavCaller(0)
    }
}