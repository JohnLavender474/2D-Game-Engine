package com.mega.game.engine.motion

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.OrderedMap
import com.mega.game.engine.common.interfaces.Resettable
import com.mega.game.engine.common.shapes.IGameShape2D
import com.mega.game.engine.components.IGameComponent
import java.util.function.BiConsumer

/**
 * A component that holds a list of [IMotion]s and a list of functions that are called when the
 * [IMotion]s are updated. The object to be moved by the motion value should be a [IGameShape2D].
 */
class MotionComponent : IGameComponent {

    /**
     * A definition of a [IMotion] and function pair. The function is called when the [IMotion] is
     * updated and a value has been obtained from [IMotion.getMotionValue].
     *
     * @param motion the [IMotion]
     * @param function the function
     */
    data class MotionDefinition(
        val motion: IMotion,
        val function: (Vector2, Float) -> Unit,
        var onReset: (() -> Unit)? = null
    ) : Resettable {

        /**
         * A definition of a [IMotion] and function pair. The function is called when the [IMotion] is
         * updated and a value has been obtained from [IMotion.getMotionValue].
         *
         * @param motion the [IMotion]
         * @param function the function
         * @param onReset the function to call when the motion is reset
         */
        constructor(
            motion: IMotion,
            function: BiConsumer<Vector2, Float>,
            onReset: Runnable? = null
        ) : this(
            motion,
            { vector, value -> function.accept(vector, value) },
            { onReset?.run() }
        )

        override fun reset() {
            motion.reset()
            onReset?.invoke()
        }
    }

    val definitions = OrderedMap<Any, MotionDefinition>()

    /**
     * Adds a [IMotion] to this component. The function is called when the [IMotion] is updated and a
     * value has been obtained from [IMotion.getMotionValue].
     *
     * @param key the key to associate with the [IMotion]
     * @param definition the [IMotion] and function pair
     * @return if the [IMotion] and function pair was added
     */
    fun put(key: Any, definition: MotionDefinition): MotionDefinition? = definitions.put(key, definition)

    /** Resets the motions in this component */
    override fun reset() = definitions.values().forEach { it.reset() }
}
