package info.jukov.vkstoryteller.util.span

import android.graphics.*
import android.text.style.LineBackgroundSpan

/**
 * User: jukov
 * Date: 11.09.2017
 * Time: 22:26
 */
private const val EDGE_RADIUS = 10
private const val EDGE_DIAMETER = EDGE_RADIUS * 2

private const val EDGE_OFFSET = 5

class BackgroundAroundLineSpan : LineBackgroundSpan {

    private val currentRect = Rect()
    private val previousRect = Rect()

    public var color: Int = Color.WHITE
    public var alpha: Int = 255

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
        paint.color = color
        paint.alpha = alpha
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC))

        val textCenter = (right - left) / 2
        val width = paint.measureText(text.toString(), start, end)

        currentRect.bottom = baseline
        currentRect.top = (currentRect.bottom + paint.ascent()).toInt();
        currentRect.left = (textCenter - width / 2).toInt()
        currentRect.right = (textCenter + width / 2).toInt()

        currentRect.top -= 20
        currentRect.bottom += 20
        currentRect.left -= 20
        currentRect.right += 23

        canvas.drawRoundRect(RectF(currentRect), EDGE_RADIUS.toFloat(), EDGE_RADIUS.toFloat(), paint)

        if (!isFirstLine) {

            paint.setXfermode(null)

            if (currentRect.width() > previousRect.width() + EDGE_DIAMETER + EDGE_OFFSET) {

                makeBottomRightEdge(canvas, previousRect.left - EDGE_DIAMETER, currentRect.top - EDGE_DIAMETER)

                makeBottomLeftEdge(canvas, previousRect.right, currentRect.top - EDGE_DIAMETER)

            } else if (currentRect.width() + EDGE_DIAMETER + EDGE_OFFSET < previousRect.width()) {

                makeTopRightEdge(canvas, currentRect.left - EDGE_DIAMETER, previousRect.bottom)

                makeTopLeftEdge(canvas, currentRect.right, previousRect.bottom)

            }
        }

        previousRect.set(currentRect)
    }

    private fun makeBottomLeftEdge(canvas: Canvas, left: Int, top: Int) {
        createEdge(canvas, EDGE_DIAMETER, left, top + EDGE_RADIUS, left, top)
    }

    private fun makeBottomRightEdge(canvas: Canvas, left: Int, top: Int) {
        createEdge(canvas, EDGE_DIAMETER, left + EDGE_RADIUS, top + EDGE_RADIUS, left, top)
    }

    private fun makeTopLeftEdge(canvas: Canvas, left: Int, top: Int) {
        createEdge(canvas, EDGE_DIAMETER, left, top, left, top)
    }

    private fun makeTopRightEdge(canvas: Canvas, left: Int, top: Int) {
        createEdge(canvas, EDGE_DIAMETER, left + EDGE_RADIUS, top, left, top)
    }

    private fun createEdge(canvas: Canvas,
                           width: Int,
                           squareLeft: Int,
                           squareTop: Int,
                           circleLeft: Int,
                           circleTop: Int) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        paint.color = color
        paint.alpha = alpha
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC))

        val squareRect = Rect(0, 0, width / 2, width / 2)
        squareRect.offsetTo(squareLeft, squareTop)
        canvas.drawRect(squareRect, paint)

        paint.color = Color.TRANSPARENT
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_OUT))

        val circleRect = RectF(0f, 0f, width.toFloat(), width.toFloat())
        circleRect.offsetTo(circleLeft.toFloat(), circleTop.toFloat())
        canvas.drawOval(circleRect, paint)

        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.CLEAR))
        canvas.drawOval(circleRect, paint)

    }
}