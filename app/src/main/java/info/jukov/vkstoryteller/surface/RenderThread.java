package info.jukov.vkstoryteller.surface;

import static info.jukov.vkstoryteller.surface.MathUtilsKt.getAverageAngleBetweenPointsPairsAndCentroid;
import static info.jukov.vkstoryteller.surface.MathUtilsKt.getAverageDistanceFromPointsToCentroid;
import static info.jukov.vkstoryteller.surface.MathUtilsKt.getCentroid;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.SurfaceHolder;

/**
 * User: jukov
 * Date: 07.09.2017
 * Time: 22:09
 */
class RenderThread extends HandlerThread implements Handler.Callback {

	private static final String RENDER_THREAD_NAME = "RenderThread";
	private static final String TAG = "RenderThread";

	private static final int MESSAGE_ADD_DRAGABLE = 2110;
	private static final int MESSAGE_POINTER_MOVE = 2111;
	private static final int MESSAGE_POINTER_UP = 2112;

	private static final String KEY_POINTER_COUNT = "KEY_POINTER_COUNT";
	private static final String KEY_POINTER = "KEY_POINTER";
	private static final String KEY_X = "KEY_X";
	private static final String KEY_Y = "KEY_Y";
	private static final String KEY_POINTERS = "KEY_POINTERS";

	private static final int FIRST_POINTER_ID = 0;
	private final SurfaceHolder surfaceHolder;
	private final Paint paint;
	private final Paint greenPaint;
	private Handler handler;
	private DragableImage dragableImage;

	private float[] previousCentroid;
	private Float previousDistanceSumFromPointsToCentroid;
	private float[] previousPointers;

	RenderThread(final SurfaceHolder surfaceHolder, final DragableImage dragableImage) {
		super(RENDER_THREAD_NAME);
		this.surfaceHolder = surfaceHolder;
		this.dragableImage = dragableImage;
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		greenPaint = new Paint();
		greenPaint.setColor(Color.GREEN);

		//TODO draw on ui thread
		final Canvas canvas = surfaceHolder.lockCanvas();
		dragableImage.setX(256);
		dragableImage.setY(256);

		final Matrix matrix = new Matrix();
		matrix.setTranslate(256 - dragableImage.getWidth() / 2, 256 - dragableImage.getHeight() / 2);
		canvas.drawBitmap(dragableImage.getBitmap(), matrix, paint);
		surfaceHolder.unlockCanvasAndPost(canvas);
	}

	@Override
	protected void onLooperPrepared() {
		handler = new Handler(getLooper(), this);
	}

	@Override
	public boolean quit() {
		handler.removeCallbacksAndMessages(null);
		return super.quit();
	}

	@Override
	public boolean handleMessage(final Message message) {

		final int pointerId;

		switch (message.what) {
			case MESSAGE_ADD_DRAGABLE:
				break;
			case MESSAGE_POINTER_MOVE:
				final float[] currentPointers = message.getData().getFloatArray(KEY_POINTERS);
				if (currentPointers == null) {
					throw new IllegalStateException();
				}

				if (previousCentroid == null) {//TODO нормальное условие
					previousCentroid = getCentroid(currentPointers);
					previousDistanceSumFromPointsToCentroid = getAverageDistanceFromPointsToCentroid(currentPointers);
					previousPointers = currentPointers;
					return true;
				}

				final Matrix matrix = new Matrix();

				//Translate
				final float[] currentCentroid = getCentroid(currentPointers);

				final float newX = dragableImage.getX() - (previousCentroid[0] - currentCentroid[0]);
				final float newY = dragableImage.getY() - (previousCentroid[1] - currentCentroid[1]);

				//Scale
				final float currentDistanceSumFromPointsToCentroid = getAverageDistanceFromPointsToCentroid(currentPointers);
				final float distanceDiff = (previousDistanceSumFromPointsToCentroid - currentDistanceSumFromPointsToCentroid) / 600;
				final float newScale = dragableImage.getScale() - distanceDiff;

				//Rotate
				final float averageAngle = getAverageAngleBetweenPointsPairsAndCentroid(previousPointers, currentPointers);

				float newAngle = dragableImage.getAngle();

				if (!Float.isNaN(averageAngle)) {
					newAngle += (averageAngle * 1.5);
				}

				previousCentroid = currentCentroid;
				previousDistanceSumFromPointsToCentroid = currentDistanceSumFromPointsToCentroid;
				previousPointers = currentPointers;

				matrix.setScale(newScale, newScale);
				matrix.preRotate(newAngle, (dragableImage.getWidthCenter() * dragableImage.getScale()),
					(dragableImage.getHeightCenter() * dragableImage.getScale()));
				matrix.postTranslate(newX - (dragableImage.getWidthCenter() * dragableImage.getScale()),
					newY - (dragableImage.getHeightCenter() * dragableImage.getScale()));

				final Canvas canvas = surfaceHolder.lockCanvas();
				canvas.drawColor(Color.BLACK);
				canvas.drawBitmap(dragableImage.getBitmap(), matrix, paint);
				canvas.drawRect(newX - 3, newY - 3, newX + 3, newY + 3, greenPaint);
				dragableImage.setAngle(newAngle);
				dragableImage.setScale(newScale);
				dragableImage.setX(newX);
				dragableImage.setY(newY);
				surfaceHolder.unlockCanvasAndPost(canvas);

				break;
			case MESSAGE_POINTER_UP:
				previousCentroid = null;
				previousDistanceSumFromPointsToCentroid = null;
				previousPointers = null;
				break;
			default:
				break;
		}
		return true;
	}

	public void updateSize(final int width, final int height) {

	}

	public void onPointerMoveEvent(final float[] pointers) {
		final Bundle bundle = new Bundle();
		bundle.putFloatArray(KEY_POINTERS, pointers);

		final Message message = handler.obtainMessage(MESSAGE_POINTER_MOVE);
		message.setData(bundle);
		handler.sendMessage(message);
	}

	public void onPointerCountChangeEvent(final int pointerId) {
		final Bundle bundle = new Bundle();

		final Message message = handler.obtainMessage(MESSAGE_POINTER_UP);
		message.setData(bundle);
		handler.sendMessage(message);
	}
}
