package info.jukov.vkstoryteller.util.message

import android.graphics.Color

/**
 * User: jukov
 * Date: 16.09.2017
 * Time: 10:48
 */

public enum class MessageStyle(private val color: Int, private val alpha: Int) {

    NONE(Color.TRANSPARENT, 0x0),
    WHITE(Color.WHITE, 0xFF),
    WHITE_TRANSPARENT(Color.WHITE, 0x5C);

    fun apply(backgroundAroundLineSpan: BackgroundAroundLineSpan) {
        backgroundAroundLineSpan.color = color
        backgroundAroundLineSpan.alpha = alpha
    }

}