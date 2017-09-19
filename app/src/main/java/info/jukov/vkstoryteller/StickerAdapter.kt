package info.jukov.vkstoryteller

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import info.jukov.vkstoryteller.util.CachedAssetsImageLoader
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * User: jukov
 * Date: 17.09.2017
 * Time: 18:45
 */
class StickerAdapter(val imageLoader: CachedAssetsImageLoader, stickerFileNames: Collection<String>) : RecyclerView.Adapter<StickerAdapter.StickerHolder>() {

    var stickerClickListener: OnStickerClickListener? = null

    private val stickerPaths = ArrayList<String>()

    init {
        this.stickerPaths.addAll(stickerFileNames)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): StickerHolder {
        if (parent == null) {
            throw IllegalStateException()
        }

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sticker, parent, false)

        return StickerHolder(view)
    }

    override fun getItemCount() = stickerPaths.size

    override fun onBindViewHolder(holder: StickerHolder?, position: Int) {
        holder?.bind(stickerPaths.get(position), imageLoader, stickerClickListener)
    }

    class StickerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(path: String, imageLoader: CachedAssetsImageLoader, onClickListener: OnStickerClickListener?) {

            imageLoader.getImage(path)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(Consumer {
                        (itemView as ImageView).setImageBitmap(it)
                        if (onClickListener != null) {
                            itemView.setOnClickListener(View.OnClickListener {
                                onClickListener.onClick(path)
                            })
                        }
                    })
        }

    }

    interface OnStickerClickListener {

        fun onClick(path: String)

    }
}