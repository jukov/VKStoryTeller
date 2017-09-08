package info.jukov.vkstoryteller;

import android.graphics.Rect;
import android.util.Pair;

import java.util.Collection;
import java.util.List;

/**
 * User: jukov
 * Date: 06.09.2017
 * Time: 22:31
 */

public final class MathUtils {

	private MathUtils() {
	}

	public static float getDistanceBetweenTwoPoints(final float x1, final float y1, final float x2, final float y2) {
		final float catheter1 = Math.abs(x1 - x2);
		final float catheter2 = Math.abs(y1 - y2);

		return (float) Math.hypot(catheter1, catheter2);
	}

	public static boolean isPointerInBounds(final Pair<Float, Float> pointerCoordinates, final Rect bounds) {
		return bounds.contains(pointerCoordinates.first.intValue(), pointerCoordinates.second.intValue());
	}

	public static Pair<Float, Float> getCentroid(final float[] points) {

		float centroidX = 0;
		float centroidY = 0;

		for (int i = 0; i < points.length; i += 2) {
			centroidX += points[i];
			centroidY += points[i+1];
		}

		final int pointsCount = points.length / 2;
		return new Pair<Float, Float>(centroidX / pointsCount, centroidY / pointsCount);
	}

	public static Pair<Float, Float> getCentroid(final Collection<Pair<Float, Float>> points) {

		float centroidX = 0;
		float centroidY = 0;

		for (final Pair<Float, Float> point : points) {
			centroidX += point.first;
			centroidY += point.second;
		}

		return new Pair<Float, Float>(centroidX / points.size(), centroidY / points.size());
	}
}
