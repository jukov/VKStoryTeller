package info.jukov.vkstoryteller.util

/**
 * User: jukov
 * Date: 16.09.2017
 * Time: 11:18
 */

public class ItemCarousel<T>(itemList: List<T>) {

    private val itemList: List<T>

    private var iterator: Iterator<T>

    init {
        require(itemList.size > 0, { "itemList cannot be empty" })

        this.itemList = itemList.toList()

        iterator = this.itemList.iterator()
    }

    public fun next(): T {
        if (iterator.hasNext()) {
            return iterator.next()
        }

        iterator = itemList.iterator()

        return iterator.next()
    }

}