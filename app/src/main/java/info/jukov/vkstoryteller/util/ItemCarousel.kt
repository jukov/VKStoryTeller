package info.jukov.vkstoryteller.util

/**
 * User: jukov
 * Date: 16.09.2017
 * Time: 11:18
 */

public class ItemCarousel<T>(itemArray: Array<T>) {

    private val itemList: List<T>

    private var iterator: Iterator<T>

    init {
        require(itemArray.size > 0, { "itemArray cannot be empty" })

        this.itemList = itemArray.toList()

        iterator = itemList.iterator()
    }

    public fun next(): T {
        if (iterator.hasNext()) {
            return iterator.next()
        }

        iterator = itemList.iterator()

        return iterator.next()
    }

}