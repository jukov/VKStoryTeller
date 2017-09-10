package info.jukov.vkstoryteller.surface

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.view.SurfaceHolder
import info.jukov.vkstoryteller.util.moveToEnd
import java.util.*

/**
 * User: jukov
 * Date: 07.09.2017
 * Time: 22:09
 */
private const val RENDER_THREAD_NAME = "RenderThread"
private const val TAG = "RenderThread"

private const val MESSAGE_NEW_DRAGABLE = 2109
private const val MESSAGE_REDRAW_DRAGABLE = 2110
private const val MESSAGE_POINTER_MOVE = 2111
private const val MESSAGE_POINTER_COUNT_CHANGE = 2112

private const val KEY_POINTERS = "KEY_POINTERS"

private const val SCALE_TRESHOLD_MIN = 0.3f
private const val SCALE_TRESHOLD_MAX = 2.0f

internal class RenderThread(private val surfaceHolder: SurfaceHolder) : HandlerThread(RENDER_THREAD_NAME), Handler.Callback {

    var onStickerMoveListener : OnStickerMoveListner? = null

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val matrix = Matrix()

    private var surfaceWidth: Int = 0
    private var surfaceHeight: Int = 0

    private var handler: Handler? = null

    private var previousCentroid: FloatArray? = null
    private var previousDistanceSumFromPointsToCentroid: Float? = null
    private var previousPointers: FloatArray? = null
    private var currentSticker: DragableImage? = null

    private val stickerList: MutableList<DragableImage> = Collections.synchronizedList(ArrayList<DragableImage>())

    override fun onLooperPrepared() {
        handler = Handler(looper, this)
    }

    override fun quit(): Boolean {
        handler!!.removeCallbacksAndMessages(null)
        handler = null
        return super.quit()
    }

    override fun handleMessage(message: Message): Boolean {
        when (message.what) {
            MESSAGE_NEW_DRAGABLE -> {
                val newSticker = stickerList.last()

                newSticker.x = surfaceWidth.toFloat() / 2
                newSticker.y = surfaceHeight.toFloat() / 2

                surfaceHolder.unlockCanvasAndPost(
                        drawStickers(
                                surfaceHolder.lockCanvas()))
            }
            MESSAGE_REDRAW_DRAGABLE -> {
                surfaceHolder.unlockCanvasAndPost(
                        drawStickers(
                                surfaceHolder.lockCanvas()))
            }
            MESSAGE_POINTER_MOVE -> {
                if (stickerList.size == 0) {
                    return true
                }

                val currentPointers = message.data.getFloatArray(KEY_POINTERS) ?: throw IllegalStateException()

                if (previousCentroid == null) {//TODO нормальное условие
                    previousCentroid = getCentroid(currentPointers)
                    previousDistanceSumFromPointsToCentroid = getAverageDistanceFromPointsToCentroid(currentPointers)
                    previousPointers = currentPointers
                    currentSticker = getMostCloseSticker(currentPointers, stickerList)
                    stickerList.moveToEnd(currentSticker!!)
                    return true
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

                //Draw stickers
                surfaceHolder.unlockCanvasAndPost(
                        drawStickers(
                                surfaceHolder.lockCanvas()))

                if (currentCentroid.size == 2) {
                    onStickerMoveListener?.onStickerMoveByOnePointer(currentPointers)
                }
            }
            MESSAGE_POINTER_COUNT_CHANGE -> {
                previousCentroid = null
                previousDistanceSumFromPointsToCentroid = null
                previousPointers = null
                currentSticker = null

                onStickerMoveListener?.onStickerStopMove()
            }
            else -> {
            }
        }
        return true
    }

    fun updateSize(width: Int, height: Int) {
        surfaceWidth = width
        surfaceHeight = height
    }

    fun onPointerMoveEvent(pointers: FloatArray) {
        val bundle = Bundle()
        bundle.putFloatArray(KEY_POINTERS, pointers)

        val message = handler!!.obtainMessage(MESSAGE_POINTER_MOVE)
        message.data = bundle
        handler!!.sendMessage(message)
    }

    fun onPointerCountChangeEvent() {
        handler!!.sendEmptyMessage(MESSAGE_POINTER_COUNT_CHANGE)
    }

    fun onAddNewStickerEvent(sticker: DragableImage) {
        stickerList.add(sticker);
        handler!!.sendEmptyMessage(MESSAGE_NEW_DRAGABLE)
    }

    fun deleteCurrentSticker() {
        stickerList.removeAt(stickerList.size - 1)//TODO Способ удаления побезопаснее
        handler!!.sendEmptyMessage(MESSAGE_REDRAW_DRAGABLE)
    }

    private fun drawStickers(canvas: Canvas) : Canvas {
        canvas.drawColor(Color.BLACK)

        stickerList.forEach {
            matrix.setScale(it.scale, it.scale)
            matrix.preRotate(it.angle, it.widthCenter, it.heightCenter)
            matrix.postTranslate(it.x - it.widthCenter * it.scale, it.y - it.heightCenter * it.scale)

            canvas.drawBitmap(it.bitmap, matrix, paint)
        }

        return canvas
    }

    interface OnStickerMoveListner {
        fun onStickerMoveByOnePointer(pointer: FloatArray)
        fun onStickerStopMove()
    }
}
