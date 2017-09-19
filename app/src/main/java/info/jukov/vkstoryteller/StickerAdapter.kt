package info.jukov.vkstoryteller

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageView
import info.jukov.vkstoryteller.util.CachedAssetsImageLoader
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

/**
 * User: jukov
 * Date: 19.09.2017
 * Time: 23:10
 */

class StickerAdapter(val context: Context,
                     val imageLoader: CachedAssetsImageLoader,
                     stickerFileNames: Collection<String>) : BaseAdapter() {

    var stickerClickListener: OnStickerClickListener? = null

    private val stickerPaths = ArrayList<String>(stickerFileNames)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: ImageView

        if (convertView != null) {
            view = (convertView as ImageView)
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_sticker, parent, false) as ImageView
        }

        val path = stickerPaths.get(position)

        imageLoader.getImage(path)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer {
                    view.setImageBitmap(it)
                    if (stickerClickListener != null) {
                        view.setOnClickListener(View.OnClickListener {
                            stickerClickListener?.onClick(path)
                        })
                    }
                })

        return view
    }

    override fun getItem(position: Int) = stickerPaths.get(position)

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = stickerPaths.size

    interface OnStickerClickListener {

        fun onClick(path: String)

    }
}
