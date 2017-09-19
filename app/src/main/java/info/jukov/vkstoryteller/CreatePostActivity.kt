package info.jukov.vkstoryteller

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.screen_main.*
import info.jukov.vkstoryteller.util.CachedAssetsImageLoader
import info.jukov.vkstoryteller.util.hideKeyboard
import java.io.File


private const val STICKERS_PATH = "stickers"

private const val STICKER_PREVIEW_SAMPLE_SIZE = 3

class SurfaceActivity : AppCompatActivity() {

    private lateinit var stickerPickerHideAnimaion: Animation
    private lateinit var stickerPickerShowAnimaion: Animation
    private lateinit var shadowHideAnimaion: Animation
    private lateinit var shadowShowAnimaion: Animation

    private var isStickerPickerVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_main)

        buttonMessageStyle.setOnClickListener {
            createPostView.changeMessageStyle()
        }

        buttonStickerSelect.setOnClickListener {
            toggleStickerPickerVisibility()
        }

        stickerPickerShowAnimaion = AnimationUtils.loadAnimation(this, R.anim.sticker_picker_appear)
        stickerPickerHideAnimaion = AnimationUtils.loadAnimation(this, R.anim.sticker_picker_disappear)
        shadowShowAnimaion = AnimationUtils.loadAnimation(this, R.anim.shadow_appear)
        shadowHideAnimaion = AnimationUtils.loadAnimation(this, R.anim.shadow_disappear)

//        initTabLayout()
        initStickerPicker()
        initShadow()
    }

//    private fun initTabLayout() {
//        tabLayout.addTab(tabLayout.newTab().setText(R.string.wallPost))
//        tabLayout.addTab(tabLayout.newTab().setText(R.string.story))
//        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabReselected(tab: TabLayout.Tab?) {
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab?) {
//            }
//
//            override fun onTabSelected(tab: TabLayout.Tab?) {
//            }
//        })
//    }

    private fun initStickerPicker() {

        val stickerPaths = assets.list(STICKERS_PATH).map { STICKERS_PATH + File.separator + it }

        val imageLoader = CachedAssetsImageLoader(assets, 128000, stickerPaths.size, STICKER_PREVIEW_SAMPLE_SIZE)

        val adapter = StickerAdapter(this, imageLoader, stickerPaths)

        adapter.stickerClickListener = object : StickerAdapter.OnStickerClickListener {
            override fun onClick(path: String) {
                toggleStickerPickerVisibility()
                createPostView.addSticker(path)
            }
        }

        gridViewStickers.adapter = adapter
    }

    private fun initShadow() {
        shadowView.setOnClickListener {
            toggleStickerPickerVisibility()
        }
    }

    private fun toggleStickerPickerVisibility() {
        if (isStickerPickerVisible) {
            stickerPickerContainer.startAnimation(stickerPickerHideAnimaion)
            shadowView.startAnimation(shadowHideAnimaion)
            stickerPickerContainer.visibility = View.GONE
            shadowView.visibility = View.GONE

            isStickerPickerVisible = false
        } else {
            hideKeyboard(this)

            stickerPickerContainer.startAnimation(stickerPickerShowAnimaion)
            shadowView.startAnimation(shadowShowAnimaion)
            stickerPickerContainer.visibility = View.VISIBLE
            shadowView.visibility = View.VISIBLE

            isStickerPickerVisible = true
        }
    }

    override fun onBackPressed() {
        if (isStickerPickerVisible) {
            toggleStickerPickerVisibility()
        } else {
            super.onBackPressed()
        }
    }
}
