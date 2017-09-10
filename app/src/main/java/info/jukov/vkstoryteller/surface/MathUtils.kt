package info.jukov.vkstoryteller.surface

/**
 * User: jukov
 * Date: 06.09.2017
 * Time: 22:31
 *
 * Методы работют с массивами точек, координаты которых рассположены последовательно. Например:
 * Points [x1, y1, x2, y2, ... xN, yN]
 * Vector [xA, yA, xB, yB]
 */

fun getDistanceBetweenTwoPoints(point1: FloatArray, point2: FloatArray): Float {
    require(point1.size == 2 && point2.size == 2, { "Arrays must be contain only one point" })

    val catheter1 = Math.abs(point1[0] - point2[0])
    val catheter2 = Math.abs(point1[1] - point2[1])

    return Math.hypot(catheter1.toDouble(), catheter2.toDouble()).toFloat()
}

fun getScalarProduct(vector1: FloatArray, vector2: FloatArray): Float {
    require(vector1.size == 4 && vector2.size == 4, { "Arrays must be contain only two points of begining and ending of vector" })

    val x1 = vector1[2] - vector1[0]
    val x2 = vector2[2] - vector2[0]
    val y1 = vector1[3] - vector1[1]
    val y2 = vector2[3] - vector2[1]

    return x1 * x2 + y1 * y2
}

/**
 * @return центроид (среднюю точку для произвольного многоугольника)
 * для переданных координат вершин многоугольника.
 * */
fun getCentroid(points: FloatArray): FloatArray {
    require(points.size.rem(2) == 0, { "Array must be contain pairs of coordinates of points" })

    var centroidX = 0f
    var centroidY = 0f

    for (i in 0..points.size - 1 step 2) {
        centroidX += points[i]
        centroidY += points[i + 1]
    }

    val pointsCount = points.size / 2
    return floatArrayOf(centroidX / pointsCount, centroidY / pointsCount)
}

/**
 * @return среднее расстояние до центроида для переданных координат вершин многоугольника.
 * */
fun getAverageDistanceFromPointsToCentroid(points: FloatArray): Float {
    require(points.size >= 2, { "Require at least 1 point for caclulate distance" })
    require(points.size.rem(2) == 0, { "Array must be contain pairs of coordinates of points" })

    val centroid = getCentroid(points)

    var distance = 0f

    for (i in 0..points.size - 1 step 2) {
        distance += getDistanceBetweenTwoPoints(
                floatArrayOf(
                        points[i],
                        points[i + 1]),
                floatArrayOf(
                        centroid[0],
                        centroid[1])
        )
    }

    return distance / (points.size / 2)
}

/**
 * @return средний угол, прилегающий к центроиду для треугольников,
 * образованных между центроидом и точками по смежным индексам в @param beginPoints и @param endPoints.
 *
 * Кроме того, если вектор, образованный точккми
 * по смежным индексам в @param beginPoints и @param endPoints,
 * конец которого лежит на окружности с радиусом равным расстоянию от точки в @param beginPoints до центроида,
 * будет обращен по часовой стрелке, значение будет положительны. В противном случае, отрицательным.
 * */
fun getAverageAngleBetweenPointsPairsAndCentroid(beginPoints: FloatArray, endPoints: FloatArray): Float {
    require(beginPoints.size == endPoints.size, { "Arrays must be have equal length" })
    require(beginPoints.size.rem(2) == 0, { "Arrays must be contain pairs of coordinates of points" })
    require(endPoints.size.rem(2) == 0, { "Arrays must be contain pairs of coordinates of points" })

    val centroid = getCentroid(beginPoints)

    var angleSum = 0f

    for (i in 0..beginPoints.size - 1 step 2) {

        val scalarProduct = getScalarProduct(
                floatArrayOf(
                        centroid[0],
                        centroid[1],
                        beginPoints[i],
                        beginPoints[i + 1]),
                floatArrayOf(
                        centroid[0],
                        centroid[1],
                        endPoints[i],
                        endPoints[i + 1]))

        val vector1Length = getDistanceBetweenTwoPoints(
                floatArrayOf(centroid[0],
                        centroid[1]),
                floatArrayOf(beginPoints[i],
                        beginPoints[i + 1])
        )

        val vector2Length = getDistanceBetweenTwoPoints(
                floatArrayOf(
                        centroid[0],
                        centroid[1]),
                floatArrayOf(
                        endPoints[i],
                        endPoints[i + 1])
        )

        val cos = scalarProduct / (vector1Length * vector2Length)
        val angle = Math.toDegrees(Math.acos(cos.toDouble())).toFloat()

        val signedAngle = angle * getTriangleDirection(floatArrayOf(
                beginPoints[i],
                beginPoints[i + 1],
                endPoints[i],
                endPoints[i + 1],
                centroid[0],
                centroid[1]))


        angleSum += signedAngle
    }

    return angleSum / (beginPoints.size / 2)
}

/**
 * @return направление переданного треугольника.
 * В случае, если вершины переданы по часовой стрелке, возвращает 1, если против - -1.
 *
 * Использует для подсчета нахождение ориентированной площади треугольника.
 * */
private fun getTriangleDirection(array: FloatArray): Float {
    require(array.size == 6, { "Array must be contain 3 points" })

    val a = array[0] - array[4]
    val b = array[2] - array[4]
    val c = array[1] - array[5]
    val d = array[3] - array[5]

    return Math.copySign(1f, a * d - b * c)
}