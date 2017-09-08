package info.jukov.vkstoryteller;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import butterknife.BindView;
import butterknife.ButterKnife;
import info.jukov.vkstoryteller.surface.DragableImage;
import info.jukov.vkstoryteller.surface.SurfaceManager;
import java.io.IOException;

public class SurfaceActivity extends AppCompatActivity {

	private static final String STICKER = "1.png";

	@BindView(R.id.surfaceView) SurfaceView surfaceView;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen_main);
		ButterKnife.bind(this);

		final DragableImage dragableImage;

		try {
			dragableImage = new DragableImage(BitmapFactory.decodeStream(getAssets().open(STICKER)));
		} catch (final IOException e) {
			throw new IllegalStateException();
		}

		final SurfaceManager surfaceManager = new SurfaceManager(surfaceView, dragableImage);
	}

}
