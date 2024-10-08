package com.mega.game.engine.motion

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.mega.game.engine.common.extensions.toGdxArray

/**
 * A data class representing a single trajectory definition with x and y velocities and time.
 *
 * @property xVelocity The horizontal velocity.
 * @property yVelocity The vertical velocity.
 * @property time The time duration for this trajectory definition.
 */
data class TrajectoryDefinition(val xVelocity: Float, val yVelocity: Float, val time: Float)

/**
 * A class that defines a trajectory by interpolating between multiple trajectory definitions.
 *
 * @param trajectoryDefinitions An array of trajectory definitions.
 * @param ppm The pixels per meter (PPM) conversion factor. If none is provided, then a default
 *   value of 1 is used.
 */
class Trajectory(
    private val trajectoryDefinitions: Array<TrajectoryDefinition>,
    private val ppm: Int = 1
) : IMotion {

    companion object {

        /**
         * Parses a string containing trajectory definitions into am array of [TrajectoryDefinition]
         * objects.
         *
         * @param trajectoryDefinitionString The string containing trajectory definitions in the format
         *   "xVelocity,yVelocity,time".
         * @return An array of parsed trajectory definitions.
         */
        fun parseTrajectoryDefinitions(trajectoryDefinitionString: String) =
            trajectoryDefinitionString
                .split(";".toRegex())
                .map {
                    val values = it.split(",".toRegex())
                    TrajectoryDefinition(values[0].toFloat(), values[1].toFloat(), values[2].toFloat())
                }
                .toGdxArray()
    }

    private var currentDefinition =
        if (!trajectoryDefinitions.isEmpty) trajectoryDefinitions[0] else null
    private var duration = 0f
    private var index = 0

    /**
     * Creates a trajectory with the specified pixels per meter (PPM) conversion factor and trajectory
     * definitions.
     *
     * @param trajectoryDefinitions The string that should be parsed into an array of trajectory
     *   definitions.
     * @param ppm The pixels per meter (PPM) conversion factor. If none is provided, then a default
     *   value of 1 is used.
     */
    constructor(
        trajectoryDefinitions: String,
        ppm: Int = 1
    ) : this(parseTrajectoryDefinitions(trajectoryDefinitions), ppm)

    /**
     * Gets the current trajectory values in pixels per second.
     *
     * @return The current trajectory values as a [Vector2] in pixels per second.
     */
    override fun getMotionValue() =
        currentDefinition?.let { Vector2(it.xVelocity, it.yVelocity).scl(ppm.toFloat()) }

    /**
     * Updates the trajectory motion based on elapsed time.
     *
     * @param delta The time elapsed since the last update.
     */
    override fun update(delta: Float) {
        duration += delta
        currentDefinition = trajectoryDefinitions[index]

        currentDefinition?.let {
            if (duration >= it.time) {
                duration = 0f
                index++

                if (index >= trajectoryDefinitions.size) index = 0
                currentDefinition = trajectoryDefinitions[index]
            }
        }
    }

    /** Resets the trajectory to its initial state. */
    override fun reset() {
        duration = 0f
        index = 0
    }
}
