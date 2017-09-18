package info.jukov.vkstoryteller.util.message

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.support.v4.content.ContextCompat
import info.jukov.vkstoryteller.R
import info.jukov.vkstoryteller.createpost.CustomCursorEditText
import java.security.AccessControlContext

/**
 * User: jukov
 * Date: 16.09.2017
 * Time: 10:48
 */

public fun getMessageStyles(context: Context): List<MessageStyle> {

    //TODO background из ресурсов + alpha внутри color
    val defaultStyle = MessageStyle(
            Color.TRANSPARENT,
            0x0,
            ContextCompat.getColor(context, R.color.black),
            ContextCompat.getColor(context, R.color.cornflowerBlueOpacity72)
    )

    val whiteBackgroundStyle = MessageStyle(
            Color.WHITE,
            0xff,
            ContextCompat.getColor(context, R.color.black),
            ContextCompat.getColor(context, R.color.cornflowerBlueOpacity72)
    )

    val transparentBackgroundStyle = MessageStyle(
            Color.WHITE,
            0x1e,
            ContextCompat.getColor(context, R.color.trueWhite),
            ContextCompat.getColor(context, R.color.trueWhiteOpacity72)
    )

    return listOf(defaultStyle, whiteBackgroundStyle, transparentBackgroundStyle)
}

public class MessageStyle(private val backgroundColor: Int,
                               private val backgroundAlpha: Int,
                               private val fontColor: Int,
                               private val cursorColor: Int) {

    fun apply(backgroundAroundLineSpan: BackgroundAroundLineSpan, editText: CustomCursorEditText) {
        backgroundAroundLineSpan.color = backgroundColor
        backgroundAroundLineSpan.alpha = backgroundAlpha
        editText.setTextColor(fontColor)
        editText.setCursorColor(cursorColor)
    }

}