package info.jukov.vkstoryteller.util.span

/**
 * User: jukov
 * Date: 16.09.2017
 * Time: 11:18
 */

public class StyleCarousel() {

    private val styleList: List<MessageStyle>

    private var iterator: Iterator<MessageStyle>

    init {
        styleList = MessageStyle.values().toList()

        iterator = styleList.iterator()

        require(styleList.size > 0, { "styleList cannot be empty" })
    }

    public fun next(): MessageStyle {
        if (iterator.hasNext()) {
            return iterator.next()
        }

        iterator = styleList.iterator()

        return iterator.next()
    }

}