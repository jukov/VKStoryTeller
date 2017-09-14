package info.jukov.vkstoryteller.surface

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.EditText
import info.jukov.vkstoryteller.R

/**
 * User: jukov
 * Date: 13.09.2017
 * Time: 23:24
 *
 * Мотивация создания этого класса:
 * Span рисуются на EditText после отрисовки курсора.
 * Из-за этого, при отрисовке background для текста с помощью Spans, курсор оказывается под этим background.
 *
 * Этот класс рисует курсор после отрисовки всех остальных элементов.
 */
private const val STROKE = 5f

class CustomCursorEditText : EditText {

    private val cursorPaint = Paint()

    private var isNeedBlinkCursor = true

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        val typedArray = context.obtainStyledAttributes(intArrayOf(R.attr.colorAccent))

        cursorPaint.color = typedArray.getColor(0, 0)
        cursorPaint.strokeWidth = STROKE
    }

    override fun onDraw(c: Canvas?) {
        super.onDraw(c)
        val canvas = requireNotNull(c)

        if (isNeedBlinkCursor) {
            drawCursor(canvas)
        }
        isNeedBlinkCursor = !isNeedBlinkCursor
    }

    private fun drawCursor(canvas: Canvas) {
        var cursorX = layout.getPrimaryHorizontal(selectionStart)

        if (cursorX < STROKE / 2) {
            cursorX = STROKE / 2f
        }

        val selectedLine = layout.getLineForOffset(selectionStart)
        val textHeight = paint.fontSpacing

        val cursorStart = canvas.height / 2 + (selectedLine * 2 - lineCount) * (textHeight / 2)

        canvas.drawLine(cursorX, cursorStart, cursorX, cursorStart + textHeight, cursorPaint)
    }
}
