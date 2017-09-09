package info.jukov.vkstoryteller.surface;

import android.graphics.Bitmap;
import android.graphics.Rect;

/**
 * User: jukov
 * Date: 06.09.2017
 * Time: 21:11
 */

public final class DragableImage {

	private final Bitmap bitmap;
	private final int width;
	private final int height;
	private float x;
	private float y;
	private float scale = 1;
	private float angle = 0;

	public DragableImage(final Bitmap bitmap) {
		this.bitmap = bitmap;
		this.width = bitmap.getWidth();
		this.height = bitmap.getHeight();
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public void setX(final float x) {
		this.x = x;
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

	public int getWidthCenter() {
		return width / 2;
	}

	public int getHeightCenter() {
		return height / 2;
	}

	public Rect getRect() {
		return new Rect((int) (x + scale), (int) (y + scale), (int) (x + width - scale), (int) (y + height - scale));
	}
}
