package info.jukov.vkstoryteller

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.SurfaceView
import info.jukov.vkstoryteller.surface.DragableImage
import kotlinx.android.synthetic.main.screen_main.*
import java.io.IOException

class SurfaceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_main)

        val bitmap = BitmapFactory.decodeStream(assets.open(STICKER))

        buttonAddSticker.setOnClickListener {
            createPostView.addSticker(DragableImage(bitmap))
        }
    }

    companion object {

        private val STICKER = "1.png"
    }

}
