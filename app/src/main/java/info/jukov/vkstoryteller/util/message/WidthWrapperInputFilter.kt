package info.jukov.vkstoryteller.util.message

import android.graphics.Paint
import android.text.InputFilter
import android.text.Spanned
import android.util.Log


/**
 * User: jukov
 * Date: 16.09.2017
 * Time: 22:39
 */

private const val MAX_LINES = 8

private const val RETURN_SYM = '\n'
private const val SPACE = ' '

class WidthWrapperInputFilter(private val paint: Paint, private val maxWidth: Int) : InputFilter {

    override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
        val destPart = dest?.subSequence(0, dstart)
        val sourcePart = source?.subSequence(start, end)

        if (destPart == null || sourcePart == null || sourcePart.length < 1) {
            return null;
        }

        if ((dest.toString() + sourcePart).count { it == RETURN_SYM } > MAX_LINES - 1) {
            return ""
        }

        var previousReturnIndex = -1

        for (i in destPart.length - 1 downTo 0) {
            if (destPart[i] == RETURN_SYM) {
                previousReturnIndex = i
                break
            }
        }

        var previousTextWidth: Int

        if (previousReturnIndex > 0) {
            previousTextWidth = paint.measureText(destPart, previousReturnIndex, destPart.length).toInt()
        } else {
            previousTextWidth = paint.measureText(destPart, 0, destPart.length).toInt()
        }

        val resultBuilder = StringBuffer()
        val sourceBuilder = StringBuffer()

        sourcePart.forEach {
            sourceBuilder.append(it)

            if (paint.measureText(sourceBuilder.toString()) + previousTextWidth > maxWidth) {
                previousTextWidth = 0

                //Если строка больше заданной ширины, проверяем еще раз что не привысим максимальное количество строк
                if ((dest.toString() + resultBuilder).count { it == RETURN_SYM } >= MAX_LINES - 1) {
                    return resultBuilder
                }

                val lastSpace = getLastSpaceIndex(sourceBuilder)

                if (lastSpace > 0) {
                    resultBuilder
                            .append(sourceBuilder.substring(0, lastSpace))
                            .append(RETURN_SYM)
                    sourceBuilder.delete(0, lastSpace)
                } else {
                    resultBuilder
                            .append(RETURN_SYM)
                            .append(sourceBuilder.substring(0, sourceBuilder.length))
                    sourceBuilder.delete(0, sourceBuilder.length)
                }
            }
        }

        resultBuilder.append(sourceBuilder)

        val destReturnCount = dest.count() { it == RETURN_SYM}
        val resultReturnCount = resultBuilder.count { it == RETURN_SYM }

        if (destReturnCount + resultReturnCount > MAX_LINES - 1) {

            val acceptableReturnCountInResult = MAX_LINES - 1 - destReturnCount

            var returnCount = 0
            for (i in 0..resultBuilder.length - 1) {
                if (resultBuilder[i] == RETURN_SYM) {
                    returnCount++

                    if (returnCount >= acceptableReturnCountInResult) {
                        return resultBuilder.substring(i)
                    }
                }
            }
        }

        return resultBuilder.toString()
    }

    private fun getLastSpaceIndex(input: CharSequence): Int {

        for (i in input.length - 1 downTo 0) {
            if (input[i] == SPACE) {
                return i
            }
        }

        return -1
    }
}