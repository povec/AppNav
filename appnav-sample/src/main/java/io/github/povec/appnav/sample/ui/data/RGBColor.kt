package io.github.povec.appnav.sample.ui.data

import kotlinx.serialization.Serializable

object RGBColor{

    @Serializable
    data class Item(
        val name: String,
        val description: String,
        val red: Boolean,
        val green: Boolean,
        val blue: Boolean,
    ){
        val r: Float get() = if(red) 1f else 0f
        val g: Float get() = if(green) 1f else 0f
        val b: Float get() = if(blue) 1f else 0f
    }

    val Red = Item(
        name = "Red",
        description = "情熱的で力強い、光の三原色のひとつ。",
        red = true,
        green = false,
        blue = false
    )

    val Green = Item(
        name = "Green",
        description = "自然や調和を感じさせる、安らぎの色。",
        red = false,
        green = true,
        blue = false
    )

    val Blue = Item(
        name = "Blue",
        description = "空や海を象徴する、深く落ち着いた色。",
        red = false,
        green = false,
        blue = true,
    )

    val Yellow = Item(
        name = "Yellow",
        description = "赤と緑を混ぜて生まれる、明るく希望に満ちた色。",
        red = true,
        green = true,
        blue = false
    )

    val Magenta = Item(
        name = "Magenta",
        description = "赤と青を混ぜて生まれる、華やかで創造的な色。",
        red = true,
        green = false,
        blue = true
    )

    val Cyan = Item(
        name = "Cyan",
        description = "緑と青を混ぜて生まれる、清涼感のある鮮やかな水色。",
        red = false,
        green = true,
        blue = true
    )

    val Black = Item(
        name = "Black",
        description = "光が全くない状態。すべての色を吸収する。",
        red = false,
        green = false,
        blue = false
    )

    val White = Item(
        name = "White",
        description = "すべての光が重なり合った状態。純粋さと明るさの象徴。",
        red = true,
        green = true,
        blue = true
    )

    val entries = listOf(
        Red,
        Green,
        Blue,
        Yellow,
        Magenta,
        Cyan,
        Black,
        White,
    )

    fun find(
        red: Boolean,
        green: Boolean,
        blue: Boolean,
    ) = entries.find { it.red == red && it.green == green && it.blue == blue }

}