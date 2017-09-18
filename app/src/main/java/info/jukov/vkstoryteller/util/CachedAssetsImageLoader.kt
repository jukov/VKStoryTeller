package info.jukov.vkstoryteller.util

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import android.util.LruCache
import io.reactivex.Single
import java.util.NoSuchElementException

/**
 * User: jukov
 * Date: 17.09.2017
 * Time: 19:31
 */

public class CachedAssetsImageLoader(
        val assets: AssetManager,
        val itemAverageSizeBytes: Int = 128000,
        val itemCount: Int = 10,
        val sampleSize: Int = 1) {

    private val cache: BitmapLruCache<String>

    init {
        cache = BitmapLruCache(itemAverageSizeBytes * itemCount)
    }

    public fun getImageScaled(path: String): Single<Bitmap> {

        val cachedBitmap = cache.get(path)

        if (cachedBitmap != null) {
            return Single.just(cachedBitmap)
        }

        return Single.create<Bitmap> {

            val options = BitmapFactory.Options()
            options.inSampleSize = sampleSize

            val bitmap = BitmapFactory.decodeStream(assets.open(path), null, options)
            if (bitmap != null) {
                cache.put(path, bitmap)

                Log.i("TAG", cache.size().toString() + "/" + cache.maxSize())

                it.onSuccess(bitmap)
            } else {
                it.onError(NoSuchElementException())
            }
        }

    }

    class BitmapLruCache<K> : LruCache<K, Bitmap> {

        constructor(maxSize: Int) : super(maxSize)

        override fun sizeOf(key: K, value: Bitmap?): Int {
            if (value == null) {
                return 1
            }
            return value.byteCount;
        }
    }
}