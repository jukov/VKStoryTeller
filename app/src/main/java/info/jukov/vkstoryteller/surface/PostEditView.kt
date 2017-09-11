package info.jukov.vkstoryteller.surface

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import info.jukov.vkstoryteller.util.moveToEnd

/**
 * User: jukov
 * Date: 11.09.2017
 * Time: 19:02
 */

private const val SCALE_TRESHOLD_MIN = 0.3f
private const val SCALE_TRESHOLD_MAX = 2.0f//TODO to res?

class PostEditView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    var onStickerMoveListener: OnStickerMoveListner? = null

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)//TODO Check flags
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

    public fun addSticker(dragableImage: DragableImage) {
        dragableImage.x = width.toFloat() / 2
        dragableImage.y = height.toFloat() / 2

        stickerList.add(dragableImage)

        invalidate()
    }

    fun deleteCurrentSticker() {
        stickerList.remove(currentSticker)
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas == null) {
            return
        }

        canvas.drawColor(Color.BLACK)

        stickerList.forEach {
            stickerMatrix.setScale(it.scale, it.scale)
            stickerMatrix.preRotate(it.angle, it.widthCenter, it.heightCenter)
            stickerMatrix.postTranslate(it.x - it.widthCenter * it.scale, it.y - it.heightCenter * it.scale)

            canvas.drawBitmap(it.bitmap, stickerMatrix, paint)
        }
    }

    private fun onRedrawNeeded() {
        invalidate()
    }

    private fun onPointerMove(currentPointers: FloatArray) {
        if (stickerList.size == 0) {
            return
        }

        if (previousCentroid == null) {//TODO нормальное условие
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

        if (currentCentroid.size == 2) {
            onStickerMoveListener?.onStickerMoveByOnePointer(currentPointers)
        }
    }

    private fun onPointerCountChange() {
        onStickerMoveListener?.onStickerStopMove()

        previousCentroid = null
        previousDistanceSumFromPointsToCentroid = null
        previousPointers = null
        currentSticker = null
    }

    interface OnStickerMoveListner {
        fun onStickerMoveByOnePointer(pointer: FloatArray)
        fun onStickerStopMove()
    }
}