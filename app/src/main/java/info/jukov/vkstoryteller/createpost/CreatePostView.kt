package info.jukov.vkstoryteller.createpost

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.constraint.ConstraintLayout
import android.text.Spannable
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import info.jukov.vkstoryteller.R
import info.jukov.vkstoryteller.createpost.canvas.CanvasView
import info.jukov.vkstoryteller.createpost.canvas.DragableImage
import info.jukov.vkstoryteller.util.ItemCarousel
import info.jukov.vkstoryteller.util.message.BackgroundAroundLineSpan
import info.jukov.vkstoryteller.util.message.MessageStyle
import info.jukov.vkstoryteller.util.message.WidthWrapperInputFilter
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

private const val MESSAGE_TEXT_PADDING_DP = 48;

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

        canvasView.onStickerMoveListener = object : CanvasView.OnStickerMoveListner {

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
                        canvasView.deleteCurrentSticker()
                    }
                    DeleteFloatingActionButton.State.HIDDEN -> {
                    }
                }
            }
        }

        fabContainer.addOnLayoutChangeListener({ _, left, top, right, bottom, _, _, _, _ ->
            if (!fabRect.isEmpty) {
                setRectToFab(
                        left,
                        top,
                        right - left,
                        bottom - top,
                        fabRect)
            }
        })

        fabDelete.addOnLayoutChangeListener({ _, _, _, _, _, _, _, _, _ ->
            fabDelete.getHitRect(fabRect)
        })

        styleCarousel.next().apply(messageSpan)

        editTextMessage.text.setSpan(messageSpan, 0, editTextMessage.text.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        //Рисуем без аппаратного ускорения, так как оно не поддерживает transparency
        editTextMessage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {

        editTextMessage.filters = arrayOf(WidthWrapperInputFilter(editTextMessage.paint, calcMessageTextWidth(w)))

    }

    private fun calcMessageTextWidth(width: Int): Int {

        val displayMetrics = context.resources.displayMetrics

        return width - (MESSAGE_TEXT_PADDING_DP * displayMetrics.density + 0.5).toInt()

    }

    public fun addSticker(dragableImage: DragableImage) {
        if (!canvasView.addSticker(dragableImage)) {
            Toast.makeText(context, R.string.tooManyStickers, Toast.LENGTH_LONG).show()
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