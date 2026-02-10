package io.github.povec.appnav.message

/**
 * [AppNavMessage]
 * 呼び出し元（Caller）へ送信されるメッセージの実体。
 * 「何が起きたか（Result）」と「付随するデータ（Payload）」をセットで保持します。
 */
interface AppNavMessage {
    val result: AppNavResult
    val payload: String?
}

/**
 * [AppNavResult]
 * メッセージの種類を定義するインターフェース。
 * 通常、特定の画面ごとに `sealed interface` や `enum` として実装されます。
 * [createMessage] を通じて、詳細なペイロードを含んだ [AppNavMessage] を生成します。
 */
interface AppNavResult {
    fun createMessage(payload: String?): AppNavMessage
}

/**
 * 結果（Result）とデータ（Payload）を結合して [AppNavMessage] を生成するためのユーティリティ演算子。
 *
 * 使用例:
 * `val message = SelectionResult.Success + "selected_item_id"`
 */
operator fun AppNavResult.plus(payload: String?): AppNavMessage = createMessage(payload)