package info.jukov.vkstoryteller.surface;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * User: jukov
 * Date: 07.09.2017
 * Time: 22:17
 */

class Pointer implements Parcelable {

	private final int id;
	private float x;
	private float y;

	Pointer(final int id, final float x, final float y) {
		this.id = id;
		this.x = x;
		this.y = y;
	}

	int getId() {
		return id;
	}

	float getX() {
		return x;
	}

	float getY() {
		return y;
	}

	void setX(final float x) {
		this.x = x;
	}

	void setY(final float y) {
		this.y = y;
	}

	protected Pointer(final Parcel in) {
		id = in.readInt();
		x = in.readFloat();
		y = in.readFloat();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(@NonNull final Parcel dest, final int flags) {
		dest.writeInt(id);
		dest.writeFloat(x);
		dest.writeFloat(y);
	}

	public static final Creator<Pointer> CREATOR = new Creator<Pointer>() {
		@Override
		public Pointer createFromParcel(final Parcel in) {
			return new Pointer(in);
		}

		@Override
		public Pointer[] newArray(final int size) {
			return new Pointer[size];
		}
	};
}
