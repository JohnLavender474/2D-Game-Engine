package com.engine.common.shapes

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Line
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.engine.common.enums.Direction
import com.engine.common.extensions.gdxArrayOf
import com.engine.common.interfaces.IRotatable
import com.engine.common.interfaces.IScalable
import java.util.function.BiPredicate
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * A line that can be used in a game. A line is defined by two pointsHandles.
 */
class GameLine : IGameShape2D, IScalable, IRotatable {

    companion object {
        private var OVERLAP_EXTENSION: ((GameLine, IGameShape2D) -> Boolean)? = null

        /**
         * Sets the overlap extension function to the given function. This function will be called when
         * [GameLine.overlaps] is called with a [IGameShape2D] that is not a [GameRectangle] or
         * [GameLine]. This function should return true if the given [IGameShape2D] overlaps this
         * [GameLine] and false otherwise.
         *
         * @param overlapExtension The function to call when [GameLine.overlaps] is called with a
         *   [IGameShape2D] that is not a [GameRectangle] or [GameLine].
         */
        fun setOverlapExtension(overlapExtension: (GameLine, IGameShape2D) -> Boolean) {
            OVERLAP_EXTENSION = overlapExtension
        }

        /**
         * Sets the overlap extension function to the given function. This function will be called when
         * [GameLine.overlaps] is called with a [IGameShape2D] that is not a [GameRectangle] or [GameLine].
         *
         * @param overlapExtension The function to call when [GameLine.overlaps] is called with a
         */
        fun setOverlapExtension(overlapExtension: BiPredicate<GameLine, IGameShape2D>) {
            OVERLAP_EXTENSION = overlapExtension::test
        }
    }

    override var color: Color = Color.RED
    override var shapeType: ShapeType = Line

    /** The thickness of this line when drawn. */
    var thickness: Float = 1f

    private val position = Vector2()
    private val localPoint1 = Vector2()
    private val localPoint2 = Vector2()
    private val worldPoint1 = Vector2()
    private val worldPoint2 = Vector2()

    override var rotation = 0f
        set(value) {
            field = value
            dirty = true
        }

    override var originX = 0f
        set(value) {
            field = value
            dirty = true
        }

    override var originY = 0f
        set(value) {
            field = value
            dirty = true
        }

    override var scaleX = 1f
        set(value) {
            field = value
            dirty = true
            calculateScaledLength = true
        }

    override var scaleY = 1f
        set(value) {
            field = value
            dirty = true
            calculateScaledLength = true
        }

    private var dirty = true

    private var length = 0f
    private var calculateLength = true

    private var scaledLength = 0f
    private var calculateScaledLength = true

    /**
     * Sets the game line to the following local points.
     * @param x1 the first x coordinate
     * @param y1 the first y coordinate
     * @param x2 the second x coordinate
     * @param y2 the second y coordinate
     */
    constructor(x1: Float, y1: Float, x2: Float, y2: Float) {
        localPoint1.set(x1, y1)
        localPoint2.set(x2, y2)
    }

    /**
     * Creates a new line with the given line. Copies all the fields from the given [GameLine]
     * to this line.
     *
     * @param line The line to copy.
     * @return A line with the given fields copied.
     */
    constructor(line: GameLine) {
        localPoint1.set(line.localPoint1)
        localPoint2.set(line.localPoint2)
        scaleX = line.scaleX
        scaleY = line.scaleY
        rotation = line.rotation
        originX = line.originX
        originY = line.originY
        color = line.color
        shapeType = line.shapeType
        thickness = line.thickness
    }

    /**
     * Creates a line with the given points.
     *
     * @param point1 The first point of this line.
     * @param point2 The second point of this line.
     */
    constructor(point1: Vector2, point2: Vector2) : this(point1.x, point1.y, point2.x, point2.y)

    /** Creates a line with points at [0,0] and [0,0] */
    constructor() : this(0f, 0f, 0f, 0f)

    /**
     * Returns the vertices of this line as a float array. The vertices are the local points of this line.
     *
     * @return The vertices of this line.
     */
    fun getVertices(): FloatArray {
        return floatArrayOf(localPoint1.x, localPoint1.y, localPoint2.x, localPoint2.y)
    }

    /**
     * Returns the transformed vertices of this line as a float array. The vertices are the world points
     * of this line.
     *
     * @return The transformed vertices of this line.
     */
    fun getTransformedVertcies(): FloatArray {
        val (_worldPoint1, _worldPoint2) = getWorldPoints()
        return floatArrayOf(_worldPoint1.x, _worldPoint1.y, _worldPoint2.x, _worldPoint2.y)
    }

    /**
     * Sets this line to the provided [GameLine]. This sets the [position], [originX], [originY],
     * [localPoint1], and [localPoint2].
     *
     * @param line The line to copy.
     * @return This line.
     */
    fun set(line: GameLine): GameLine {
        setPosition(line.position)
        setOrigin(line.originX, line.originY)
        scaleX = line.scaleX
        scaleY = line.scaleY
        rotation = line.rotation
        return set(line.localPoint1, line.localPoint2)
    }

    /**
     * Sets the points of this line to the given points.
     *
     * @param point1 The first point of this line.
     * @param point2 The second point of this line.
     * @return This line.
     */
    fun set(point1: Vector2, point2: Vector2) = set(point1.x, point1.y, point2.x, point2.y)

    /**
     * Sets the points of this line to the given points.
     *
     * @param x1 The x-coordinate of the first point of this line.
     * @param y1 The y-coordinate of the first point of this line.
     * @param x2 The x-coordinate of the second point of this line.
     * @param y2 The y-coordinate of the second point of this line.
     * @return This line.
     */
    fun set(x1: Float, y1: Float, x2: Float, y2: Float): GameLine {
        localPoint1.x = x1
        localPoint1.y = y1
        localPoint2.x = x2
        localPoint2.y = y2
        dirty = true
        calculateLength = true
        return this
    }

    /** Sets this line to dirty which means that the world points need to be recalculated. */
    fun setToDirty() {
        dirty = true
    }

    /** Sets to recalculate the length of this line. */
    fun setToRecalculateLength() {
        calculateLength = true
    }

    /**
     * Returns a new line created from this line rotated by the given amount based on the [direction].
     * The [direction] is used to determine the direction to rotate this line. [Direction.UP] is 90
     * degrees, [Direction.RIGHT] is 180 degrees, [Direction.DOWN] is 270 degrees, and
     * [Direction.LEFT] is 360 degrees.
     *
     * @param direction The direction to rotate this line.
     * @param useNewShape If true, a new shape will be created and returned. If false, this shape will
     *   be rotated and returned.
     * @return The rotated line.
     */
    override fun getCardinallyRotatedShape(direction: Direction, useNewShape: Boolean): GameLine {
        val line = if (useNewShape) GameLine(this) else this
        line.rotation =
            when (direction) {
                Direction.UP -> 0f
                Direction.RIGHT -> 90f
                Direction.DOWN -> 180f
                Direction.LEFT -> 270f
            }
        return line
    }

    /**
     * Calculates and returns the length of this line.
     *
     * @return The length of this line.
     */
    fun getLength(): Float {
        if (calculateLength) {
            calculateLength = false
            val x = localPoint1.x - localPoint2.x
            val y = localPoint1.y - localPoint2.y
            length = sqrt((x * x + y * y).toDouble()).toFloat()
        }
        return length
    }

    /** Sets to recalculate the scaled length of this line. */
    fun setToRecalculateScaledLength() {
        calculateScaledLength = true
    }

    /**
     * Calculates and returns the scaled length of this line.
     *
     * @return The scaled length of this line.
     */
    fun getScaledLength(): Float {
        if (calculateScaledLength) {
            calculateScaledLength = false
            val x = (localPoint1.x - localPoint2.x) * scaleX
            val y = (localPoint1.y - localPoint2.y) * scaleY
            scaledLength = sqrt((x * x + y * y).toDouble()).toFloat()
        }
        return scaledLength
    }

    /**
     * Sets the first local point (unscaled, unrotated, etc.) of this line.
     *
     * @param point The first point of this line.
     * @return This shape for chaining
     */
    fun setFirstLocalPoint(point: Vector2) = setFirstLocalPoint(point.x, point.y)

    /**
     * Sets the first local point (unscaled, unrotated, etc.) of this line.
     *
     * @param x1 The x-coordinate of the first point of this line.
     * @param y1 The y-coordinate of the first point of this line.
     * @return This shape for chaining
     */
    fun setFirstLocalPoint(x1: Float, y1: Float): GameLine {
        val secondLocalPoint = getSecondLocalPoint()
        setLocalPoints(x1, y1, secondLocalPoint.x, secondLocalPoint.y)
        return this
    }

    /**
     * Sets the second local point (unscaled, unrotated, etc.) of this line.
     *
     * @param point The second point of this line.
     * @return This shape for chaining
     */
    fun setSecondLocalPoint(point: Vector2) = setSecondLocalPoint(point.x, point.y)

    /**
     * Sets the second local point (unscaled, unrotated, etc.) of this line.
     *
     * @param x2 The x-coordinate of the second point of this line.
     * @param y2 The y-coordinate of the second point of this line.
     * @return This shape for chaining
     */
    fun setSecondLocalPoint(x2: Float, y2: Float): GameLine {
        val firstLocalPoint = getFirstLocalPoint()
        setLocalPoints(firstLocalPoint.x, firstLocalPoint.y, x2, y2)
        return this
    }

    /**
     * Sets the local points (unscaled, unrotated, etc.) of this line.
     *
     * @param x1 The x-coordinate of the first point of this line.
     * @param y1 The y-coordinate of the first point of this line.
     * @param x2 The x-coordinate of the second point of this line.
     * @param y2 The y-coordinate of the second point of this line.
     * @return This shape for chaining
     */
    fun setLocalPoints(x1: Float, y1: Float, x2: Float, y2: Float): GameLine {
        localPoint1.set(x1, y1)
        localPoint2.set(x2, y2)
        dirty = true
        calculateLength = true
        return this
    }

    /**
     * Sets the local points (unscaled, unrotated, etc.) of this line.
     *
     * @param point1 The first point of this line.
     * @param point2 The second point of this line.
     * @return This shape for chaining
     */
    fun setLocalPoints(point1: Vector2, point2: Vector2) =
        setLocalPoints(point1.x, point1.y, point2.x, point2.y)

    /**
     * Gets the local points (unscaled, unrotated, etc.) of this line.
     *
     * @return The local points of this line.
     */
    fun getLocalPoints() = Pair(getFirstLocalPoint(), getSecondLocalPoint())

    /**
     * Gets the first local point (unscaled, unrotated, etc.) of this line.
     *
     * @return The first local point of this line.
     */
    fun getFirstLocalPoint() = Vector2(localPoint1)

    /**
     * Gets the second local point (unscaled, unrotated, etc.) of this line.
     *
     * @return The second local point of this line.
     */
    fun getSecondLocalPoint() = Vector2(localPoint2)

    /**
     * Gets the world points (scaled, rotated, etc.) of this line.
     *
     * @return The world points of this line.
     */
    fun getWorldPoints(): Pair<Vector2, Vector2> {
        if (!dirty) return Pair(Vector2(worldPoint1), Vector2(worldPoint2))
        dirty = false

        val cos = MathUtils.cosDeg(rotation)
        val sin = MathUtils.sinDeg(rotation)

        var first = true
        gdxArrayOf(localPoint1, localPoint2).forEach {
            var x = it.x - originX
            var y = it.y - originY

            x *= scaleX
            y *= scaleY

            if (rotation != 0f) {
                val oldX = x
                x = cos * x - sin * y
                y = sin * oldX + cos * y
            }

            val worldPoint = if (first) worldPoint1 else worldPoint2
            first = false

            worldPoint.x = position.x + x + originX
            worldPoint.y = position.y + y + originY
        }

        return Pair(Vector2(worldPoint1), Vector2(worldPoint2))
    }

    /**
     * Checks if the point is contained in this line. The world points are used for calculating the
     * containment via [getWorldPoints].
     *
     * @param point The point to check.
     * @return True if the point is contained in this line.
     */
    override fun contains(point: Vector2): Boolean {
        val (_worldPoint1, _worldPoint2) = getWorldPoints()
        return Intersector.pointLineSide(_worldPoint1, _worldPoint2, point) == 0 &&
                point.x <= getMaxX() &&
                point.x >= getX()
    }

    /**
     * Checks if the point is contained in this line. The world points are used for calculating the
     * containment via [getWorldPoints].
     *
     * @param x The first coordinate of the point to check.
     * @param y The second coordinate of the point to check.
     * @return True if the point is contained in this line.
     * @see contains
     */
    override fun contains(x: Float, y: Float) = contains(Vector2(x, y))

    /**
     * Draws this line using the world points.
     *
     * @return The drawer.
     */
    override fun draw(drawer: ShapeRenderer) {
        val (_worldPoint1, _worldPoint2) = getWorldPoints()
        drawer.color = color
        drawer.set(shapeType)
        drawer.rectLine(_worldPoint1, _worldPoint2, thickness)
    }

    override fun setCenter(centerX: Float, centerY: Float): GameLine {
        val currentCenter = getCenter()
        val centerDeltaX = centerX - currentCenter.x
        val centerDeltaY = centerY - currentCenter.y

        if (centerDeltaX == 0f && centerDeltaY == 0f) return this

        position.x += centerDeltaX
        position.y += centerDeltaY
        localPoint1.x += centerDeltaX
        localPoint1.y += centerDeltaY
        localPoint2.x += centerDeltaX
        localPoint2.y += centerDeltaY

        dirty = true
        calculateLength = true
        return this
    }

    /**
     * Centers the world coordinates of the line on the given point.
     *
     * @param center The point to center the world coordinates of the line on.
     * @return This line.
     */
    override fun setCenter(center: Vector2) = setCenter(center.x, center.y)

    /**
     * Returns the center of the world points.
     *
     * @return The center of the world points.
     */
    override fun getCenter(): Vector2 {
        val (_worldPoint1, _worldPoint2) = getWorldPoints()
        return Vector2((_worldPoint1.x + _worldPoint2.x) / 2f, (_worldPoint1.y + _worldPoint2.y) / 2f)
    }

    /**
     * Returns the center of the local points.
     *
     * @return The center of the local points.
     */
    fun getLocalCenter() =
        Vector2((localPoint1.x + localPoint2.x) / 2f, (localPoint1.y + localPoint2.y) / 2f)

    /**
     * Sets the x-coordinate of the first point of this line.
     *
     * @param x The new x-coordinate of the first point of this line.
     * @return This line.
     */
    override fun setX(x: Float): GameLine {
        if (position.x != x) {
            position.x = x
            dirty = true
        }
        return this
    }

    /**
     * Sets the y-coordinate of the first point of this line.
     *
     * @param y The new y-coordinate of the first point of this line.
     * @return This line.
     */
    override fun setY(y: Float): GameLine {
        if (position.y != y) {
            position.y = y
            dirty = true
        }
        return this
    }

    /**
     * Returns the x position of the line.
     *
     * @return The x position of the line.
     */
    override fun getX() = position.x

    /**
     * Returns the y position of the line.
     *
     * @return The y position of the line.
     */
    override fun getY() = position.y

    /**
     * Returns the max x of the line using the world points via [getWorldPoints].
     *
     * @return The max x of the line.
     */
    override fun getMaxX(): Float {
        val (_worldPoint1, _worldPoint2) = getWorldPoints()
        return max(_worldPoint1.x, _worldPoint2.x)
    }

    /**
     * Returns the max y of the line using the world points via [getWorldPoints].
     *
     * @return The max y of the line.
     */
    override fun getMaxY(): Float {
        val (_worldPoint1, _worldPoint2) = getWorldPoints()
        return max(_worldPoint1.y, _worldPoint2.y)
    }

    /**
     * Translates the position of this line.
     *
     * @param translateX The amount to translate the x-coordinate of this line.
     * @param translateY The amount to translate the y-coordinate of this line.
     * @return This line.
     */
    override fun translation(translateX: Float, translateY: Float): GameLine {
        position.x += translateX
        position.y += translateY
        dirty = true
        return this
    }

    /**
     * Sets the origin of this line.
     *
     * @param origin The origin.
     * @return This line.
     */
    fun setOrigin(origin: Vector2) = setOrigin(origin.x, origin.y)

    /**
     * Sets the origin of this line.
     *
     * @param originX The x-coordinate of the origin.
     * @param originY The y-coordinate of the origin.
     * @return This line.
     */
    fun setOrigin(originX: Float, originY: Float): GameLine {
        this.originX = originX
        this.originY = originY
        return this
    }

    /**
     * Returns a copy of this line.
     *
     * @return A copy of this line.
     */
    override fun copy(): GameLine = GameLine(this)

    /**
     * Returns true if the provided [IGameShape2D] overlaps this game line. The world points are used
     * for calculating the overlap via [getWorldPoints].
     *
     * @return True if the provided [IGameShape2D] overlaps this game line.
     */
    override fun overlaps(other: IGameShape2D): Boolean {
        val (_worldPoint1, _worldPoint2) = getWorldPoints()

        return when (other) {
            is GameRectangle -> Intersector.intersectSegmentRectangle(_worldPoint1, _worldPoint2, other)
            is GameCircle ->
                Intersector.intersectSegmentCircle(
                    _worldPoint1, _worldPoint2, other.getCenter(), other.getRadius() * other.getRadius()
                )

            is GameLine -> {
                val (otherWorldPoint1, otherWorldPoint2) = other.getWorldPoints()
                Intersector.intersectSegments(
                    _worldPoint1, _worldPoint2, otherWorldPoint1, otherWorldPoint2, null
                )
            }

            is GamePolygon -> Intersector.intersectLinePolygon(worldPoint1, worldPoint2, other.libgdxPolygon)
            else -> OVERLAP_EXTENSION?.invoke(this, other) ?: false
        }
    }

    /**
     * Returns the bounding rectangle of this line. The world points are used for calculating the
     * bounding rectangle via [getWorldPoints].
     *
     * @return The bounding rectangle of this line.
     */
    override fun getBoundingRectangle(): GameRectangle {
        val (_worldPoint1, _worldPoint2) = getWorldPoints()

        val minX = min(_worldPoint1.x, _worldPoint2.x)
        val maxX = max(_worldPoint1.x, _worldPoint2.x)
        val minY = min(_worldPoint1.y, _worldPoint2.y)
        val maxY = max(_worldPoint1.y, _worldPoint2.y)

        return GameRectangle(minX, minY, maxX - minX, maxY - minY)
    }

    override fun toString() = getWorldPoints().toString()
}
