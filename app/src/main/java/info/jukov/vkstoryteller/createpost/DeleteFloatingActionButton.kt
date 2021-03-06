package info.jukov.vkstoryteller.createpost

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import info.jukov.vkstoryteller.R
import kotlinx.android.synthetic.main.view_create_post.view.*

/**
 * User: jukov
 * Date: 11.09.2017
 * Time: 21:35
 */
class DeleteFloatingActionButton @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : FloatingActionButton(context, attrs, defStyleAttr) {

    enum class State {
        HIDDEN,
        VISIBLE,
        SELECTED
    }

    private val hideAnimaion: Animation
    private val showAnimaion: Animation

    private val defaultWidth: Int
    private val selectedWidth: Int

    private val defaultIcon: Drawable
    private val selectedIcon: Drawable

    init {
        hideAnimaion = AnimationUtils.loadAnimation(context, R.anim.fab_disappear)
        showAnimaion = AnimationUtils.loadAnimation(context, R.anim.fab_appear)

        defaultWidth = context.resources.getDimensionPixelSize(R.dimen.fabSurfaceViewDefaultSize)
        selectedWidth = context.resources.getDimensionPixelSize(R.dimen.fabSurfaceViewSelectedSize)

        defaultIcon = ContextCompat.getDrawable(context, R.drawable.ic_fab_trash)
        selectedIcon = ContextCompat.getDrawable(context, R.drawable.ic_fab_trash_released)
    }

    var state: State = State.HIDDEN

    override fun hide() {
        startAnimation(hideAnimaion)
        visibility = View.GONE
        this.state = State.HIDDEN
    }

    override fun show() {
        startAnimation(showAnimaion)
        visibility = View.VISIBLE
        this.state = State.VISIBLE
    }

    fun select() {
        val layoutParams = fabDelete.layoutParams

        layoutParams.width = selectedWidth
        layoutParams.height = selectedWidth

        fabDelete.setImageDrawable(selectedIcon)
        fabDelete.requestLayout()

        state = State.SELECTED
    }

    fun deselect() {
        val layoutParams = fabDelete.layoutParams

        layoutParams.width = defaultWidth
        layoutParams.height = defaultWidth

        fabDelete.setImageDrawable(defaultIcon)
        fabDelete.requestLayout()

        state = State.VISIBLE
    }


}