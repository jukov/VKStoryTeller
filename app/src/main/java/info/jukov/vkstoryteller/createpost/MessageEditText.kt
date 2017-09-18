package info.jukov.vkstoryteller.createpost

import android.content.Context
import android.os.Parcelable
import android.text.Spannable
import android.util.AttributeSet
import android.view.View
import info.jukov.vkstoryteller.util.message.BackgroundAroundLineSpan
import kotlinx.android.synthetic.main.view_create_post.view.*

/**
 * User: jukov
 * Date: 18.09.2017
 * Time: 23:14
 */

class MessageEditText : CustomCursorEditText {

    val messageSpan = BackgroundAroundLineSpan()

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        text.setSpan(messageSpan, 0, editTextMessage.text.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

        //Рисуем без аппаратного ускорения, так как оно не поддерживает transparency
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    override fun onSaveInstanceState(): Parcelable {
        return MessageState(
                super.onSaveInstanceState(),
                messageSpan.color,
                messageSpan.alpha,
                currentTextColor,
                getCursorColor())
    }

    override fun onRestoreInstanceState(state: Parcelable?) {

        val messageState = state as MessageState

        super.onRestoreInstanceState(messageState.viewState)

        messageSpan.color = messageState.backgroundColor
        messageSpan.alpha = messageState.backgroundAlpha
        setTextColor(messageState.textColor)
        setCursorColor(messageState.cursorColor)

        text.clearSpans()
        text.setSpan(messageSpan, 0, editTextMessage.text.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
    }
}
