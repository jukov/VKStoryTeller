package info.jukov.vkstoryteller.surface

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.constraint.ConstraintLayout
import android.text.Spannable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import info.jukov.vkstoryteller.R
import info.jukov.vkstoryteller.util.span.BackgroundAroundLineSpan
import info.jukov.vkstoryteller.util.ItemCarousel
import info.jukov.vkstoryteller.util.span.MessageStyle
import kotlinx.android.synthetic.main.view_create_post.view.*


/**
 * User: jukov
 * Date: 10.09.2017
 * Time: 16:05
 */

private const val MESSAGE_SHOW_FAB = 1645;
private const val MESSAGE_HIDE_FAB = 1646;
private const val MESSAGE_POINTER_MOVE = 1647;

private const val KEY_POINTER = "KEY_POINTER";

private const val ON_POINTER_ON_FAB_MULTIPLER = 1.2F;

private const val SHOW_FAB_DELAY = 300L;

class CreatePostView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr), Handler.Callback {

    private var uiThreadHandler = Handler(this)

    private val fabRect = Rect(0, 0, 0, 0)

    private val styleCarousel = ItemCarousel<MessageStyle>(MessageStyle.values())
    private val messageSpan = BackgroundAroundLineSpan()

    init {
        View.inflate(context, R.layout.view_create_post, this)

        postEditView.onStickerMoveListener = object : PostEditView.OnStickerMoveListner {

            override fun onStickerMoveByOnePointer(pointer: FloatArray) {
                if (fabDelete.state != DeleteFloatingActionButton.State.HIDDEN) {
                    val bundle = Bundle()
                    bundle.putFloatArray(KEY_POINTER, pointer)
                    val message = uiThreadHandler.obtainMessage(MESSAGE_POINTER_MOVE)
                    message.data = bundle
                    uiThreadHandler.sendMessage(message)

                } else {
                    uiThreadHandler.sendEmptyMessageDelayed(MESSAGE_SHOW_FAB, SHOW_FAB_DELAY)
                }
            }

            override fun onPointerCountChange() {
                uiThreadHandler.removeMessages(MESSAGE_SHOW_FAB)
                when (fabDelete.state) {
                    DeleteFloatingActionButton.State.VISIBLE ->
                        uiThreadHandler.sendEmptyMessage(MESSAGE_HIDE_FAB)
                    DeleteFloatingActionButton.State.SELECTED -> {
                        uiThreadHandler.sendEmptyMessage(MESSAGE_HIDE_FAB)
                        postEditView.deleteCurrentSticker()
                    }
                    DeleteFloatingActionButton.State.HIDDEN -> {
                    }
                }
            }
        }

        styleCarousel.next().apply(messageSpan)

        editTextMessage.text.setSpan(messageSpan, 0, editTextMessage.text.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        editTextMessage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public fun addSticker(dragableImage: DragableImage) {
        if (!postEditView.addSticker(dragableImage)) {
            Toast.makeText(context, R.string.too_many_stickers, Toast.LENGTH_LONG).show()
        }
    }

    public fun changeMessageStyle() {
        styleCarousel.next().apply(messageSpan)
        editTextMessage.invalidate()
    }

    override fun handleMessage(msg: Message?): Boolean {
        when (msg?.what) {
            MESSAGE_SHOW_FAB -> {
                fabDelete.show()
            }
            MESSAGE_HIDE_FAB -> {
                fabDelete.hide()
            }
            MESSAGE_POINTER_MOVE -> {
                if (fabRect.isEmpty) {
                    fabDelete.getHitRect(fabRect)
                    setRectToFab(
                            fabContainer.left,
                            fabContainer.top,
                            fabContainer.width,
                            fabContainer.height,
                            fabRect)
                }

                val pointer = msg.data.getFloatArray(KEY_POINTER)

                if (fabRect.contains(pointer[0].toInt(), pointer[1].toInt())) {
                    fabDelete.select()
                } else {
                    fabDelete.deselect()
                }
            }
        }

        return true
    }

    private fun setRectToFab(containerLeft: Int,
                             containerTop: Int,
                             containerWidth: Int,
                             containerHeight: Int,
                             fabRect: Rect) {

        val trueLeft = containerLeft + (containerWidth - fabRect.width()) / 2
        val trueTop = containerTop + (containerHeight - fabRect.height()) / 2

        fabRect.offsetTo(trueLeft, trueTop)
    }
}