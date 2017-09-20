package info.jukov.vkstoryteller.util.message

import android.graphics.*
import android.text.style.LineBackgroundSpan
import android.util.DisplayMetrics

/**
 * User: jukov
 * Date: 11.09.2017
 * Time: 22:26
 */

/*  TODO Заменить рисование background с помощью spans на рисование напрямую в EditText,
    посредством обводки всех строк с помощью Path в onDraw().
    Так же, этот подход решит проблему с неправильной отрисовкой курсора в CustomColotEditText на api 16.*/
private const val EDGE_OFFSET = 5

class BackgroundAroundLineSpan(val displayMetrics: DisplayMetrics) : LineBackgroundSpan {

    private val currentRect = Rect()
    private val previousRect = Rect()

    private val edgeRadius = (displayMetrics.density * 4 + 0.5).toInt()
    private val egdeDiameter = edgeRadius * 2

    var color: Int = Color.WHITE
     var alpha: Int = 255

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

        val padding = (displayMetrics.density * 6 + 0.5f).toInt()
        currentRect.inset(-padding, -padding)

        canvas.drawRoundRect(RectF(currentRect), edgeRadius.toFloat(), edgeRadius.toFloat(), paint)

        if (!isFirstLine) {

            paint.setXfermode(null)

            if (currentRect.width() > previousRect.width() + egdeDiameter + EDGE_OFFSET) {

                makeBottomRightEdge(canvas, previousRect.left - egdeDiameter, currentRect.top - egdeDiameter)

                makeBottomLeftEdge(canvas, previousRect.right, currentRect.top - egdeDiameter)

            } else if (currentRect.width() + egdeDiameter + EDGE_OFFSET < previousRect.width()) {

                makeTopRightEdge(canvas, currentRect.left - egdeDiameter, previousRect.bottom)

                makeTopLeftEdge(canvas, currentRect.right, previousRect.bottom)

            }
        }

        previousRect.set(currentRect)
    }

    private fun makeBottomLeftEdge(canvas: Canvas, left: Int, top: Int) {
        createEdge(canvas, egdeDiameter, left, top + edgeRadius, left, top)
    }

    private fun makeBottomRightEdge(canvas: Canvas, left: Int, top: Int) {
        createEdge(canvas, egdeDiameter, left + edgeRadius, top + edgeRadius, left, top)
    }

    private fun makeTopLeftEdge(canvas: Canvas, left: Int, top: Int) {
        createEdge(canvas, egdeDiameter, left, top, left, top)
    }

    private fun makeTopRightEdge(canvas: Canvas, left: Int, top: Int) {
        createEdge(canvas, egdeDiameter, left + edgeRadius, top, left, top)
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