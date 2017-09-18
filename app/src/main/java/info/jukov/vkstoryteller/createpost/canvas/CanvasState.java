package info.jukov.vkstoryteller.createpost.canvas;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.View.BaseSavedState;
import info.jukov.vkstoryteller.util.CachedAssetsImageLoader;
import io.reactivex.functions.Consumer;

/**
 * User: jukov
 * Date: 18.09.2017
 * Time: 21:21
 */

public class CanvasState implements Parcelable {

	private Parcelable viewState;
	private Sticker[] stickers;

	public CanvasState(final Parcelable viewState, final Sticker[] stickers) {
		this.viewState = viewState;
		this.stickers = stickers;
	}

	public CanvasState(final Parcel in) {
		viewState = in.readParcelable(BaseSavedState.class.getClassLoader());
		stickers = in.createTypedArray(Sticker.CREATOR);
	}

	public void restoreBitmaps(final CachedAssetsImageLoader imageLoader) {
		for (final Sticker sticker : stickers) {
			imageLoader.getImage(sticker.getImagePath())
				.subscribe(new Consumer<Bitmap>() {
					@Override
					public void accept(final Bitmap bitmap) throws Exception {
						sticker.setBitmap(bitmap);
					}
				});
		}
	}

	public Sticker[] getStickers() {
		return stickers;
	}

	public void setStickers(final Sticker[] stickers) {
		this.stickers = stickers;
	}

	public Parcelable getViewState() {
		return viewState;
	}

	public void setViewState(final Parcelable viewState) {
		this.viewState = viewState;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(@NonNull final Parcel dest, final int flags) {
		dest.writeParcelable(viewState, 0);
		dest.writeTypedArray(stickers, 0);
	}

	public static final Creator<CanvasState> CREATOR = new Creator<CanvasState>() {
		@Override
		public CanvasState createFromParcel(final Parcel in) {
			return new CanvasState(in);
		}

		@Override
		public CanvasState[] newArray(final int size) {
			return new CanvasState[size];
		}
	};
}
