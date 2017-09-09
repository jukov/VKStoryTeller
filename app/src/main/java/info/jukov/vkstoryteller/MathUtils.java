package info.jukov.vkstoryteller;

import android.graphics.Rect;
import android.util.Log;
import android.util.Pair;
import java.util.Collection;
import java.util.StringTokenizer;

/**
 * User: jukov
 * Date: 06.09.2017
 * Time: 22:31
 */

public final class MathUtils {


	private static final String TAG = "MathUtils";

	public static float getDistanceBetweenTwoPoints(final float x1, final float y1, final float x2, final float y2) {
		final float catheter1 = Math.abs(x1 - x2);
		final float catheter2 = Math.abs(y1 - y2);

		return (float) Math.hypot(catheter1, catheter2);
	}

	public static float getScalarProduct(
		final float x1Source,
		final float y1Source,
		final float x1Dest,
		final float y1Dest,
		final float x2Source,
		final float y2Source,
		final float x2Dest,
		final float y2Dest) {

		final float x1 = x1Dest - x1Source;
		final float x2 = x2Dest - x2Source;
		final float y1 = y1Dest - y1Source;
		final float y2 = y2Dest - y2Source;
		return x1*x2 + y1*y2;
	}

	public static float getAverageAngleBetweenPointsPairsAndCentroid(final float[] beginPoints, final float[] endPoints) {

		final Pair<Float, Float> centroid = getCentroid(beginPoints);

		float angleSum = 0;

		for (int i = 0; i < beginPoints.length; i += 2) {
			final float beginPointToCentroidLength = getDistanceBetweenTwoPoints(
				beginPoints[i],
				beginPoints[i + 1],
				centroid.first,
				centroid.second
			);

			final float scalarProduct = getScalarProduct(
				centroid.first,
				centroid.second,
				beginPoints[i],
				beginPoints[i + 1],
				centroid.first,
				centroid.second,
				endPoints[i],
				endPoints[i + 1]
			);

			final float vector1Length = getDistanceBetweenTwoPoints(
				centroid.first,
				centroid.second,
				beginPoints[i],
				beginPoints[i + 1]
			);

			final float vector2Length = getDistanceBetweenTwoPoints(
				centroid.first,
				centroid.second,
				endPoints[i],
				endPoints[i + 1]
			);

			final float cos = scalarProduct / (vector1Length * vector2Length);
			final float angle = (float) Math.toDegrees(Math.acos(cos));

			final float signedAngle = angle * getVectorDirectionOnCircle(
				endPoints[i],
				endPoints[i + 1],
				beginPoints[i],
				beginPoints[i + 1],
				centroid
			);

			angleSum += angle;
		}

		return angleSum / (beginPoints.length / 2);
	}

	public static float getAverageDistanceFromPointsToCentroid(final float[] points) {
		if (points.length < 2) {
			throw new IllegalStateException("Require at least 1 point for caclulate distance");
		}

		final Pair<Float, Float> centroid = getCentroid(points);

		float distance = 0;

		for (int i = 0; i < points.length; i += 2) {
			distance += getDistanceBetweenTwoPoints(
				points[i],
				points[i + 1],
				centroid.first,
				centroid.second
			);
		}

		return distance / (points.length / 2);
	}

	public static boolean isPointerInBounds(final Pair<Float, Float> pointerCoordinates, final Rect bounds) {
		return bounds.contains(pointerCoordinates.first.intValue(), pointerCoordinates.second.intValue());
	}

	public static Pair<Float, Float> getCentroid(final float[] points) {

		float centroidX = 0;
		float centroidY = 0;

		for (int i = 0; i < points.length; i += 2) {
			centroidX += points[i];
			centroidY += points[i + 1];
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

	private static float getVectorDirectionOnCircle(
		final float xSource,
		final float ySource,
		final float xDest,
		final float yDest,
		final Pair<Float, Float> circleCenter) {

		if (circleCenter.second > yDest) {

		} else {

		}

	}

	private MathUtils() {
	}
}
