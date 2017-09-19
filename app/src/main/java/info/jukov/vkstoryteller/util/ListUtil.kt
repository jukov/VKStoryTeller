package info.jukov.vkstoryteller.util

/**
 * User: jukov
 * Date: 10.09.2017
 * Time: 14:58
 */

fun <T> MutableList<T>.moveToEnd(any: T) {
    remove(any)
    add(any)
}