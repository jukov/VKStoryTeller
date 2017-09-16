package info.jukov.vkstoryteller.surface

import android.content.Context
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
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

    public enum class State {
        HIDDEN,
        VISIBLE,
        SELECTED
    }

    public var state: State = State.HIDDEN

    public override fun hide() {
        val animation = AnimationUtils.loadAnimation(context, R.anim.disappear_to_bottom)//TODO попробовать положить анимацию в класс
        startAnimation(animation)
        visibility = View.GONE
        this.state = State.HIDDEN
    }

    public override fun show() {
        val animation = AnimationUtils.loadAnimation(context, R.anim.appear_from_bottom)
        startAnimation(animation)
        visibility = View.VISIBLE
        this.state = State.VISIBLE
    }

    public fun select() {
        val layoutParams = fabDelete.layoutParams

        layoutParams.width = context.resources.getDimensionPixelSize(R.dimen.fabSurfaceViewSelectedSize)
        layoutParams.height = context.resources.getDimensionPixelSize(R.dimen.fabSurfaceViewSelectedSize)

        fabDelete.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_fab_trash_released))
        fabDelete.requestLayout()

        state = State.SELECTED
    }

    public fun deselect() {
        val layoutParams = fabDelete.layoutParams

        layoutParams.width = context.resources.getDimensionPixelSize(R.dimen.fabSurfaceViewDefaultSize)
        layoutParams.height = context.resources.getDimensionPixelSize(R.dimen.fabSurfaceViewDefaultSize)

        fabDelete.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_fab_trash))
        fabDelete.requestLayout()

        state = State.VISIBLE
    }


}