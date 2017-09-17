package info.jukov.vkstoryteller

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.design.widget.TabLayout
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

        buttonMessageStyle.setOnClickListener {
            createPostView.changeMessageStyle()
        }

        buttonStickerSelect.setOnClickListener {
            createPostView.addSticker(DragableImage(bitmap))
        }

        initTabLayout()
    }

    fun initTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText(R.string.wallPost))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.story))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
            }
        })
    }

    companion object {

        private val STICKER = "1.png"
    }

}
