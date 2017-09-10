package info.jukov.vkstoryteller

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.SurfaceView
import info.jukov.vkstoryteller.surface.DragableImage
import info.jukov.vkstoryteller.surface.SurfaceManager
import kotlinx.android.synthetic.main.screen_main.*
import java.io.IOException

class SurfaceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_main)

        val dragableImage: DragableImage

        try {
            dragableImage = DragableImage(BitmapFactory.decodeStream(assets.open(STICKER)))
        } catch (e: IOException) {
            throw IllegalStateException()
        }

        val surfaceManager = SurfaceManager(surfaceView, dragableImage)
    }

    companion object {

        private val STICKER = "1.png"
    }

}
