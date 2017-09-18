package info.jukov.vkstoryteller.createpost.canvas;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * User: jukov
 * Date: 18.09.2017
 * Time: 21:27
 */

public class Sticker implements Parcelable {

	private final String imagePath;
	private Bitmap bitmap;
	private float x;
	private float y;
	private float scale;
	private float angle;
	private int alpha;

	private float width;
	private float height;
	private float widthCenter;
	private float heightCenter;

	public Sticker(final Bitmap bitmap, final String imagePath) {
		setBitmap(bitmap);
		this.imagePath = imagePath;
	}

	public Sticker(final Parcel in) {
		imagePath = in.readString();
		x = in.readFloat();
		y = in.readFloat();
		scale = in.readFloat();
		angle = in.readFloat();
		alpha = in.readInt();
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	@SuppressWarnings("MagicNumber")
	public void setBitmap(final Bitmap bitmap) {
		this.bitmap = bitmap;

		width = bitmap.getWidth();
		height = bitmap.getHeight();
		widthCenter = bitmap.getWidth() / 2.0f;
		heightCenter = bitmap.getHeight() / 2.0f;
	}

	public String getImagePath() {
		return imagePath;
	}

	public float getX() {
		return x;
	}

	public void setX(final float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(final float y) {
		this.y = y;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(final float scale) {
		this.scale = scale;
	}

	public float getAngle() {
		return angle;
	}

	public void setAngle(final float angle) {
		this.angle = angle;
	}

	public int getAlpha() {
		return alpha;
	}

	public void setAlpha(final int alpha) {
		this.alpha = alpha;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(final float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(final float height) {
		this.height = height;
	}

	public float getWidthCenter() {
		return widthCenter;
	}

	public void setWidthCenter(final float widthCenter) {
		this.widthCenter = widthCenter;
	}

	public float getHeightCenter() {
		return heightCenter;
	}

	public void setHeightCenter(final float heightCenter) {
		this.heightCenter = heightCenter;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(@NonNull final Parcel dest, final int flags) {
		dest.writeString(imagePath);
		dest.writeFloat(x);
		dest.writeFloat(y);
		dest.writeFloat(scale);
		dest.writeFloat(angle);
		dest.writeInt(alpha);
	}

	public static final Creator<Sticker> CREATOR = new Creator<Sticker>() {
		@Override
		public Sticker createFromParcel(final Parcel in) {
			return new Sticker(in);
		}

		@Override
		public Sticker[] newArray(final int size) {
			return new Sticker[size];
		}
	};

}
