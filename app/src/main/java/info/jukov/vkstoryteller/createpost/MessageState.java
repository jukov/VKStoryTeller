package info.jukov.vkstoryteller.createpost;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.View.BaseSavedState;

/**
 * User: jukov
 * Date: 18.09.2017
 * Time: 23:23
 */

public class MessageState implements Parcelable {

	private final Parcelable viewState;

	private final int backgroundColor;
	private final int backgroundAlpha;
	private final int textColor;
	private final int cursorColor;

	public MessageState(final Parcelable viewState, final int backgroundColor, final int backgroundAlpha, final int textColor,
		final int cursorColor) {
		this.viewState = viewState;
		this.backgroundColor = backgroundColor;
		this.backgroundAlpha = backgroundAlpha;
		this.textColor = textColor;
		this.cursorColor = cursorColor;
	}

	public MessageState(final Parcel in) {
		viewState = in.readParcelable(BaseSavedState.class.getClassLoader());
		backgroundColor = in.readInt();
		backgroundAlpha = in.readInt();
		textColor = in.readInt();
		cursorColor = in.readInt();
	}

	public Parcelable getViewState() {
		return viewState;
	}

	public int getBackgroundColor() {
		return backgroundColor;
	}

	public int getBackgroundAlpha() {
		return backgroundAlpha;
	}

	public int getTextColor() {
		return textColor;
	}

	public int getCursorColor() {
		return cursorColor;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(@NonNull final Parcel dest, final int flags) {
		dest.writeParcelable(viewState, 0);
		dest.writeInt(backgroundColor);
		dest.writeInt(backgroundAlpha);
		dest.writeInt(textColor);
		dest.writeInt(cursorColor);
	}

	public static final Creator<MessageState> CREATOR = new Creator<MessageState>() {
		@Override
		public MessageState createFromParcel(final Parcel in) {
			return new MessageState(in);
		}

		@Override
		public MessageState[] newArray(final int size) {
			return new MessageState[size];
		}
	};

}
