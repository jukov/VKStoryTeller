package info.jukov.vkstoryteller.surface;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import android.view.SurfaceHolder;
import info.jukov.vkstoryteller.MathUtils;

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

	private static final String KEY_POINTER_ID = "KEY_POINTER_ID";
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

	private Pair<Float, Float> previousCentroid;
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
		matrix.postTranslate(256 - dragableImage.getWidth() / 2, 256 - dragableImage.getHeight() / 2);
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
					previousCentroid = MathUtils.getCentroid(currentPointers);
					previousDistanceSumFromPointsToCentroid = MathUtils.getAverageDistanceFromPointsToCentroid(currentPointers);
					previousPointers = currentPointers;
					return true;
				}

				final Matrix matrix = new Matrix();

				//Translate
				final Pair<Float, Float> currentCentroid = MathUtils.getCentroid(currentPointers);

				final float newX = dragableImage.getX() - (previousCentroid.first - currentCentroid.first);
				final float newY = dragableImage.getY() - (previousCentroid.second - currentCentroid.second);

				//Scale
				final float currentDistanceSumFromPointsToCentroid = MathUtils.getAverageDistanceFromPointsToCentroid(currentPointers);
				final float distanceDiff = (previousDistanceSumFromPointsToCentroid - currentDistanceSumFromPointsToCentroid) / 300;
				final float newScale = dragableImage.getScale() - distanceDiff;

				//Rotate
				final float averageAngle = MathUtils.getAverageAngleBetweenPointsPairsAndCentroid(currentPointers, previousPointers);
				final float newAngle = dragableImage.getAngle() + averageAngle;

				Log.i(TAG, "handleMessage: " + averageAngle);

				previousCentroid = currentCentroid;
				previousDistanceSumFromPointsToCentroid = currentDistanceSumFromPointsToCentroid;
				previousPointers = currentPointers;

				matrix.preRotate(newAngle);
				matrix.setScale(newScale, newScale);
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
//				pointerId = message.getData().getInt(KEY_POINTER_ID);
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

	public void onPointerMoveEvent(final float x, final float y, final int pointerId) {
		final Bundle bundle = new Bundle();
		bundle.putInt(KEY_POINTER_ID, pointerId);
		bundle.putFloat(KEY_X, x);
		bundle.putFloat(KEY_Y, y);

		final Message message = handler.obtainMessage(MESSAGE_POINTER_MOVE);
		message.setData(bundle);
		handler.sendMessage(message);
	}

	public void onPointerMoveEvent(final float[] pointers) {
		final Bundle bundle = new Bundle();
		bundle.putFloatArray(KEY_POINTERS, pointers);

		final Message message = handler.obtainMessage(MESSAGE_POINTER_MOVE);
		message.setData(bundle);
		handler.sendMessage(message);
	}

	public void onPointerUpEvent(final int pointerId) {
		final Bundle bundle = new Bundle();
		bundle.putInt(KEY_POINTER_ID, pointerId);

		final Message message = handler.obtainMessage(MESSAGE_POINTER_UP);
		message.setData(bundle);
		handler.sendMessage(message);
	}
}
