package io.github.povec.appnav.sample.ui.navigation.stage.component

import androidx.compose.ui.graphics.vector.ImageVector

object NavLocation {
    const val NONE = 0
    const val BOTTOM_BAR = 1 shl 0
    const val RAIL = 1 shl 1
    const val DRAWER = 1 shl 2

    //優先度
    const val FIRST = BOTTOM_BAR or RAIL or DRAWER
    const val SECOND = RAIL or DRAWER
    const val THIRD = DRAWER

}

data class NavigationItem(
    val identifier: String,
    val label: String,
    val icon: ImageVector,
    val locationFlag: Int, // 配置場所のビットフラグ
    val actionFlag: Boolean, // セッション中の有効性 ＋ 強調表示（FAB等）のトリガー
) {
    // 判定ヘルパー
    fun exist(location: Int) = (locationFlag and location) != 0
    fun visible(location: Int) = !actionFlag && exist(location)
}