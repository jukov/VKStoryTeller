package info.jukov.vkstoryteller.surface

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.constraint.ConstraintLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.View
import android.view.animation.AnimationUtils
import info.jukov.vkstoryteller.R
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

    private enum class FabState {
        HIDDEN,
        VISIBLE,
        SELECTED
    }

    private var uiThreadHandler = Handler(this)

    @Volatile private var fabState = FabState.HIDDEN

    private val fabRect = Rect(0, 0, 0, 0)

    init {
        View.inflate(context, R.layout.view_create_post, this)

        postEditView.onStickerMoveListener = object : PostEditView.OnStickerMoveListner {

            override fun onStickerMoveByOnePointer(pointer: FloatArray) {
                if (fabState != FabState.HIDDEN) {
                    val bundle = Bundle()
                    bundle.putFloatArray(KEY_POINTER, pointer)
                    val message = uiThreadHandler.obtainMessage(MESSAGE_POINTER_MOVE)
                    message.data = bundle
                    uiThreadHandler.sendMessage(message)

                } else {
                    uiThreadHandler.sendEmptyMessageDelayed(MESSAGE_SHOW_FAB, SHOW_FAB_DELAY)
                }
            }

            override fun onStickerStopMove() {
                uiThreadHandler.removeMessages(MESSAGE_SHOW_FAB)
                when (fabState) {
                    FabState.VISIBLE -> uiThreadHandler.sendEmptyMessage(MESSAGE_HIDE_FAB)
                    FabState.SELECTED -> {
                        uiThreadHandler.sendEmptyMessage(MESSAGE_HIDE_FAB)
                        postEditView.deleteCurrentSticker()
                    }
                    FabState.HIDDEN -> {}
                }
            }
        }
    }

    public fun addSticker(dragableImage: DragableImage) {
        postEditView.addSticker(dragableImage)
    }

    override fun handleMessage(msg: Message?): Boolean {
        if (msg != null) {
            when (msg.what) {
                MESSAGE_SHOW_FAB -> {
                    val animation = AnimationUtils.loadAnimation(context, R.anim.appear_from_bottom)
                    fabDelete.startAnimation(animation)
                    fabDelete.visibility = View.VISIBLE
                    fabState = FabState.VISIBLE
                }
                MESSAGE_HIDE_FAB -> {
                    val animation = AnimationUtils.loadAnimation(context, R.anim.disappear_to_bottom)
                    fabDelete.startAnimation(animation)
                    fabDelete.visibility = View.GONE
                    fabState = FabState.HIDDEN
                }
                MESSAGE_POINTER_MOVE -> {
                    if (fabRect.isEmpty) {//TODO придумать способ получще для вытаскивания rect
                        fabDelete.getHitRect(fabRect)
                        setRectToFab(
                                fabContainer.left,
                                fabContainer.top,
                                fabContainer.width,
                                fabContainer.height,
                                fabRect)
                    }

                    val pointer = msg.data.getFloatArray(KEY_POINTER)

                    val layoutParams = fabDelete.layoutParams

                    if (fabRect.contains(pointer[0].toInt(), pointer[1].toInt())) {
                        layoutParams.width = context.resources.getDimensionPixelSize(R.dimen.fabSurfaceViewSelectedSize)
                        layoutParams.height = context.resources.getDimensionPixelSize(R.dimen.fabSurfaceViewSelectedSize)
                        fabDelete.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_fab_trash_released))
                        fabState = FabState.SELECTED
                    } else {
                        layoutParams.width = context.resources.getDimensionPixelSize(R.dimen.fabSurfaceViewDefaultSize)
                        layoutParams.height = context.resources.getDimensionPixelSize(R.dimen.fabSurfaceViewDefaultSize)
                        fabDelete.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_fab_trash))
                        fabState = FabState.VISIBLE
                    }
                    fabDelete.requestLayout()
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