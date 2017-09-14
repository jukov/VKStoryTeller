package info.jukov.vkstoryteller.util

import android.graphics.*
import android.text.style.LineBackgroundSpan
import android.util.Log

/**
 * User: jukov
 * Date: 11.09.2017
 * Time: 22:26
 */
private const val EDGE_RADIUS = 10
private const val EDGE_DIAMETER = EDGE_RADIUS * 2

private const val EDGE_OFFSET = 5

class BackgroundAroundLetterSpan : LineBackgroundSpan {

    private val currentRect = Rect()
    private val previousRect = Rect()

    private val bottomLeftEdge = makeBottomLeftEdge()
    private val bottomRightEdge = makeBottomRightEdge()
    private val topLeftEdge = makeTopLeftEdge()
    private val topRightEdge = makeTopRightEdge()

    override fun drawBackground(c: Canvas?,
                                p: Paint?,
                                left: Int,
                                right: Int,
                                top: Int,
                                baseline: Int,
                                bottom: Int,
                                text: CharSequence?,
                                start: Int,
                                end: Int,
                                lnum: Int) {
        val paint = Paint(p)
        val canvas = requireNotNull(c)
        val isFirstLine = lnum == 0

        paint.flags = 0
        paint.color = Color.WHITE
        paint.alpha = 120
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC))

        paint.getTextBounds(text.toString(), start, end, currentRect)
        currentRect.top = (currentRect.bottom + paint.ascent()).toInt();
        val textCenter = (right - left) / 2
        currentRect.offsetTo(textCenter - currentRect.centerX(), baseline - currentRect.height())
        currentRect.inset(-30, -20)

        canvas.drawRoundRect(RectF(currentRect), EDGE_RADIUS.toFloat(), EDGE_RADIUS.toFloat(), paint)

        if (!isFirstLine) {

            paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.DST))

            if (currentRect.width() > previousRect.width() + EDGE_DIAMETER + EDGE_OFFSET) {

                canvas.drawBitmap(
                        bottomRightEdge,
                        (previousRect.left - EDGE_DIAMETER).toFloat(),
                        (currentRect.top - EDGE_DIAMETER).toFloat(),
                        paint)

                canvas.drawBitmap(
                        bottomLeftEdge,
                        previousRect.right.toFloat(),
                        (currentRect.top - EDGE_DIAMETER).toFloat(),
                        paint)

            } else if (currentRect.width() + EDGE_DIAMETER + EDGE_OFFSET < previousRect.width() ) {

                canvas.drawBitmap(
                        topRightEdge,
                        (currentRect.left - EDGE_DIAMETER).toFloat(),
                        (previousRect.bottom).toFloat(),
                        paint)

                canvas.drawBitmap(
                        topLeftEdge,
                        currentRect.right.toFloat(),
                        (previousRect.bottom).toFloat(),
                        paint)

            }
        }

        previousRect.set(currentRect)
    }

    private fun makeBottomLeftEdge(): Bitmap {
        return createEdge(EDGE_DIAMETER, 0, EDGE_RADIUS)
    }

    private fun makeBottomRightEdge(): Bitmap {
        return createEdge(EDGE_DIAMETER, EDGE_RADIUS, EDGE_RADIUS)
    }

    private fun makeTopLeftEdge(): Bitmap {
        return createEdge(EDGE_DIAMETER, 0, 0)
    }

    private fun makeTopRightEdge(): Bitmap {
        return createEdge(EDGE_DIAMETER, EDGE_RADIUS, 0)
    }

    private fun createEdge(width: Int, squareLeft: Int, squareTop: Int): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        val circleBitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888)//TODO optimize configs
        circleBitmap.setHasAlpha(true)
        val circleCanvas = Canvas(circleBitmap);

        paint.color = Color.RED

        val circleRect = RectF(0f, 0f, width.toFloat(), width.toFloat())

        circleCanvas.drawOval(circleRect, paint)

        val squareBitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888)
        squareBitmap.setHasAlpha(true)
        val squareCanvas = Canvas(squareBitmap);

        paint.color = Color.WHITE

        val squareRect = Rect(0, 0, width / 2, width / 2)
        squareRect.offsetTo(squareLeft, squareTop)

        squareCanvas.drawRect(squareRect, paint)

        val resultBitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888)
        resultBitmap.setHasAlpha(true)
        val resultCanvas = Canvas(resultBitmap);

        resultCanvas.drawBitmap(circleBitmap, 0f, 0f, paint)
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_OUT))
        resultCanvas.drawBitmap(squareBitmap, 0f, 0f, paint)

        return resultBitmap
    }
}