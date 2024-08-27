package com.engine.world

import com.badlogic.gdx.math.Vector2
import com.engine.common.interfaces.ICopyable
import com.engine.common.objects.Properties
import com.engine.common.shapes.GameRectangle
import com.engine.common.shapes.IGameShape2D

/**
 * An implementation for [IFixture]. This implementation stores a shape variable labeled [rawShape] and
 * calculates the body-relative shape in [getShape].
 *
 * @param body The body this fixture belongs to. This is used when calculating the body-relative shape in [getShape].
 * @param rawShape The raw bounds of this fixture. Defaults to a [GameRectangle] with size of zero. To get the bounds
 * of this fixture relative to the body it is attached to, use [getShape].
 * @param type The type for this fixture. Used to determine how this fixture interacts with other
 *   fixtures. It can be anything (String, Int, Enum, etc.) so long as it properly implements
 *   [Any.equals] and [Any.hashCode] such that any two fixtures with the same type are considered
 *   equal (only in terms of contact interaction, not in terms of object equality) and any two
 *   fixtures with different types are considered not equal (again, only in terms of contact
 *   interaction).
 * @param active Whether this fixture is active. If not active, this fixture will not be used to
 *   detect collisions.
 * @param attachedToBody Whether this fixture is attached to a body. If not attached to a body, this
 *   fixture will not move with the body it is attached to.
 * @param offsetFromBodyCenter The offset of this fixture from the center of the body it is attached
 *   to. Used to position this fixture relative to the body it is attached to. This is the offset
 *   before the body is rotated.
 * @param properties The properties of this fixture.
 */
class Fixture(
    var body: Body,
    var type: Any,
    var rawShape: IGameShape2D = GameRectangle(),
    var active: Boolean = true,
    var attachedToBody: Boolean = true,
    var offsetFromBodyCenter: Vector2 = Vector2(),
    override var properties: Properties = Properties(),
) : IFixture, ICopyable<Fixture> {

    companion object {
        const val TAG = "Fixture"
    }

    /**
     * Fetches a copy of the body-relative shape of this fixture. The body-relative shape is recalculated each
     * time this function is called. If [attachedToBody] is false, then [rawShape] is returned unmodified.
     *
     * @return the body-relative shape of this fixture
     */
    override fun getShape(): IGameShape2D {
        if (!attachedToBody) return rawShape

        val bodyCenter = body.getCenter()
        rawShape.setCenter(bodyCenter).translation(offsetFromBodyCenter)

        val relativeShape = rawShape.copy()
        relativeShape.originX = if (body.originXCenter) bodyCenter.x else body.originX
        relativeShape.originY = if (body.originYCenter) bodyCenter.y else body.originY

        relativeShape.color = rawShape.color
        relativeShape.shapeType = rawShape.shapeType

        val cardinalRotation = body.cardinalRotation

        return if (cardinalRotation == null) relativeShape
        else relativeShape.getCardinallyRotatedShape(cardinalRotation, false)
    }

    override fun getFixtureType() = type

    override fun isActive() = active

    override fun toString() =
        "Fixture(raw_shape=$rawShape, type=$type, active=$active, attachedToBody=$attachedToBody, " +
                "offsetFromBodyCenter=$offsetFromBodyCenter, properties=$properties)"

    /**
     * Returns if this fixture overlaps the given shape.
     *
     * @param other the other shape
     * @return if this fixture overlaps the given shape
     */
    fun overlaps(other: IGameShape2D) = getShape().overlaps(other)

    /**
     * Returns if this fixture overlaps the given fixture.
     *
     * @param other the other fixture
     * @return if this fixture overlaps the given fixture
     */
    fun overlaps(other: IFixture) = overlaps(other.getShape())

    override fun copy() = Fixture(
        body,
        getFixtureType(),
        rawShape.copy(),
        active,
        attachedToBody,
        offsetFromBodyCenter.cpy(),
        Properties(properties)
    )
}
