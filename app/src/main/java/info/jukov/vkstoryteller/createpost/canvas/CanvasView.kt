package info.jukov.vkstoryteller.createpost.canvas

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import info.jukov.vkstoryteller.createpost.*
import info.jukov.vkstoryteller.util.moveToEnd
import java.util.*

/**
 * User: jukov
 * Date: 11.09.2017
 * Time: 19:02
 */

private const val SCALE_TRESHOLD_MIN = 0.3f
private const val SCALE_TRESHOLD_MAX = 2.0f

private const val MAX_STICKERS = 50

private const val ANIMATE_STICKER_ALPHA_FOR_ITERATION = 256 / 10
private const val ANIMATE_STICKER_SCALE_START = 0.02f
private const val ANIMATE_STICKER_SCALE_MULTIPLER_FOR_ITERATION = 2

private const val MESSAGE_ANIMATE_STICKER = 2053
private const val MESSAGE_DELETE_STICKER = 2054
private const val MESSAGE_ADD_STICKER = 2055

class CanvasView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    var onStickerMoveListener: OnStickerMoveListner? = null

    private val random = Random()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val stickerMatrix = Matrix()

    private var stickerList: MutableList<DragableImage> = ArrayList()

    private var previousCentroid: FloatArray? = null
    private var previousDistanceSumFromPointsToCentroid: Float? = null
    private var previousPointers: FloatArray? = null
    private var currentSticker: DragableImage? = null

    init {

        setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_MOVE -> {

                    val pointers = FloatArray(event.pointerCount * 2)

                    for (i in 0..event.pointerCount - 1) {
                        pointers[i * 2] = event.getX(i)
                        pointers[i * 2 + 1] = event.getY(i)
                    }

                    onPointerMove(pointers)
                }
                MotionEvent.ACTION_DOWN,
                MotionEvent.ACTION_POINTER_DOWN,
                MotionEvent.ACTION_POINTER_UP,
                MotionEvent.ACTION_UP -> onPointerCountChange()
                else -> {
                }
            }

            true
        }

    }

    public fun addSticker(dragableImage: DragableImage): Boolean {//TODO incapsulate dragable image
        if (stickerList.size < MAX_STICKERS) {
            addStickerWithAnimation(dragableImage)
            return true
        }

        return false
    }

    fun deleteCurrentSticker() {
        deleteStickerWithAnimation()
    }

    private fun deleteStickerWithAnimation() {

        val currentSticker = requireNotNull(this.currentSticker)

        val MESSAGE_ANIMATE_STICKER = 2053
        val MESSAGE_DELETE_STICKER = 2054

        var scalePerFrame = ANIMATE_STICKER_SCALE_START

        val deleteHandler = Handler(Handler.Callback {//TODO check leaks
            if (it == null) {
                return@Callback true
            }

            if (it.what == MESSAGE_ANIMATE_STICKER) {
                currentSticker.alpha -= ANIMATE_STICKER_ALPHA_FOR_ITERATION
                currentSticker.scale = Math.max(currentSticker.scale - scalePerFrame, 0f)

                scalePerFrame *= ANIMATE_STICKER_SCALE_MULTIPLER_FOR_ITERATION

            } else if (it.what == MESSAGE_DELETE_STICKER) {
                stickerList.remove(currentSticker)
            }

            invalidate()
            true
        })

        for (i in 0..10 * 16 step 16) {
            deleteHandler.sendEmptyMessageDelayed(MESSAGE_ANIMATE_STICKER, i.toLong())
        }
        deleteHandler.sendEmptyMessageDelayed(MESSAGE_DELETE_STICKER, 10 * 16);
    }

    private fun addStickerWithAnimation(newSticker: DragableImage) {

        stickerList.add(newSticker)

        newSticker.angle = (random.nextInt(60) - 30).toFloat() //From -30* to 30*
        newSticker.x = (random.nextInt(width - newSticker.width) + newSticker.width / 2).toFloat()
        val randomYposition = random.nextBoolean();

        if (randomYposition) {
            newSticker.y = newSticker.heightCenter//To top of text
        } else {
            newSticker.y = height - newSticker.heightCenter//To bottom of text
        }

        val scale = (random.nextInt(20) + 90) / 100.0f //From 0.9 to 1.1

        newSticker.scale = 0f
        newSticker.alpha = 0

        var scalePerFrame = ANIMATE_STICKER_SCALE_START

        val appearHandler = Handler(Handler.Callback {//TODO check leaks
            if (it == null) {
                return@Callback true
            }

            if (it.what == MESSAGE_ANIMATE_STICKER) {
                newSticker.alpha += Math.min(ANIMATE_STICKER_ALPHA_FOR_ITERATION * 2, 255)
                newSticker.scale = Math.min(scalePerFrame, scale)

                 scalePerFrame *= ANIMATE_STICKER_SCALE_MULTIPLER_FOR_ITERATION

            } else if (it.what == MESSAGE_ADD_STICKER) {
                newSticker.scale = scale
                newSticker.alpha = 255
            }

            invalidate()
            true
        })

        for (i in 0..10 * 16 step 16) {
            appearHandler.sendEmptyMessageDelayed(MESSAGE_ANIMATE_STICKER, i.toLong())
        }
        appearHandler.sendEmptyMessageDelayed(MESSAGE_ADD_STICKER, 10 * 16);
    }

    override fun onDraw(c: Canvas?) {
        val canvas = requireNotNull(c)

        canvas.drawColor(Color.WHITE)

        stickerList.forEach {
            stickerMatrix.setScale(it.scale, it.scale)
            stickerMatrix.preRotate(it.angle, it.widthCenter, it.heightCenter)
            stickerMatrix.postTranslate(it.x - it.widthCenter * it.scale, it.y - it.heightCenter * it.scale)

            paint.alpha = it.alpha

            canvas.drawBitmap(it.bitmap, stickerMatrix, paint)
        }

        paint.alpha = 255
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measuredWidth
        setMeasuredDimension(width, width)
    }

    private fun onRedrawNeeded() {
        invalidate()
    }

    private fun onPointerMove(currentPointers: FloatArray) {
        if (stickerList.size == 0) {
            return
        }

        if (previousCentroid == null) {
            previousCentroid = getCentroid(currentPointers)
            previousDistanceSumFromPointsToCentroid = getAverageDistanceFromPointsToCentroid(currentPointers)
            previousPointers = currentPointers
            currentSticker = getMostCloseSticker(currentPointers, stickerList)
            stickerList.moveToEnd(currentSticker!!)
            return
        }

        val currentSticker: DragableImage = this.currentSticker!!

        //Calc translate
        val currentCentroid = getCentroid(currentPointers)

        val newX = currentSticker.x - (previousCentroid!![0] - currentCentroid[0])
        val newY = currentSticker.y - (previousCentroid!![1] - currentCentroid[1])

        //Calc scale
        val currentDistanceSumFromPointsToCentroid = getAverageDistanceFromPointsToCentroid(currentPointers)
        val distanceDiff = (previousDistanceSumFromPointsToCentroid!! - currentDistanceSumFromPointsToCentroid) / 600
        var newScale = currentSticker.scale - distanceDiff
        newScale = Math.max(SCALE_TRESHOLD_MIN, newScale)
        newScale = Math.min(SCALE_TRESHOLD_MAX, newScale)

        //Calc rotate
        val averageAngle = getAverageAngleBetweenPointsPairsAndCentroid(previousPointers!!, currentPointers)

        var newAngle = currentSticker.angle

        if (!averageAngle.equals(Float.NaN)) {
            newAngle += (averageAngle * 1.5).toFloat()
        }

        //Set previous values
        previousCentroid = currentCentroid
        previousDistanceSumFromPointsToCentroid = currentDistanceSumFromPointsToCentroid
        previousPointers = currentPointers

        //Modify sticker location parameters
        currentSticker.angle = newAngle
        currentSticker.scale = newScale
        currentSticker.x = newX
        currentSticker.y = newY

        invalidate()

        if (currentPointers.size == 2) {
            onStickerMoveListener?.onStickerMoveByOnePointer(currentPointers)
        }
    }

    private fun onPointerCountChange() {
        onStickerMoveListener?.onPointerCountChange()

        previousCentroid = null
        previousDistanceSumFromPointsToCentroid = null
        previousPointers = null
        currentSticker = null
    }

    interface OnStickerMoveListner {
        fun onStickerMoveByOnePointer(pointer: FloatArray)
        fun onPointerCountChange()
    }
}