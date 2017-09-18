package info.jukov.vkstoryteller.createpost.canvas

import android.graphics.Bitmap

/**
 * User: jukov
 * Date: 10.09.2017
 * Time: 13:26
 */
data class DragableImage(
        val bitmap: Bitmap,
        var x: Float = 0.0f,
        var y: Float = 0.0f,
        var scale: Float = 1.0f,
        var angle: Float = 0.0f,
        var alpha: Int = 255) {

    val width = bitmap.width;
    val height = bitmap.height;
    val widthCenter = bitmap.width / 2.0f;
    val heightCenter = bitmap.height / 2.0f;

}
