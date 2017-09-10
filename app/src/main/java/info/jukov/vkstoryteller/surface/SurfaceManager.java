package info.jukov.vkstoryteller.surface;

import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

import java.lang.ref.WeakReference;

/**
 * User: jukov
 * Date: 06.09.2017
 * Time: 20:58
 */

public class SurfaceManager {

	private static final String TAG = "SurfaceManager";

	private WeakReference<SurfaceView> surfaceView;

	private RenderThread renderThread;

	public SurfaceManager(final SurfaceView surfaceView, final DragableImage dragableImage) {
		this.surfaceView = new WeakReference<SurfaceView>(surfaceView);

		surfaceView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(final View v, final MotionEvent event) {

				switch (event.getActionMasked()) {
//					case MotionEvent.ACTION_DOWN:
//						break;
//					case MotionEvent.ACTION_UP:
//						break;
//					case MotionEvent.ACTION_POINTER_DOWN:
//						break;
//					case MotionEvent.ACTION_POINTER_UP:
//						break;
					case MotionEvent.ACTION_MOVE:

						final float[] pointers = new float[event.getPointerCount() * 2];

						for (int i = 0; i < event.getPointerCount(); i++) {
							pointers[i * 2] = event.getX(i);
							pointers[i * 2 + 1] = event.getY(i);
						}

						renderThread.onPointerMoveEvent(pointers);

						break;
					case MotionEvent.ACTION_DOWN:
					case MotionEvent.ACTION_POINTER_DOWN:
					case MotionEvent.ACTION_POINTER_UP:
					case MotionEvent.ACTION_UP:
						renderThread.onPointerCountChangeEvent(event.getActionIndex());
					default:
						break;
				}

				return true;
			}
		});

		surfaceView.getHolder().addCallback(new Callback() {
			@Override
			public void surfaceCreated(final SurfaceHolder holder) {
				renderThread = new RenderThread(holder, dragableImage);
				renderThread.start();
			}

			@Override
			public void surfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height) {
				renderThread.updateSize(width, height);
			}

			@Override
			public void surfaceDestroyed(final SurfaceHolder holder) {
				renderThread.quit();
				renderThread = null;
			}
		});
	}

	public void addDragableImage() {

	}

}
