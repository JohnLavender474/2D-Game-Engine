package com.engine.cullables

import com.engine.common.interfaces.IContainable

/**
 * A [ICullable] that will be culled if [containable] is not contained in [containerSupplier].
 *
 * @param containerSupplier a function that returns the container.
 * @param containable the containable.
 * @param T the type of the container.
 */
class CullableOnUncontained<T>(
    val containerSupplier: () -> T,
    val containable: IContainable<T>,
    var timeToCull: Float = 0f
) : ICullable {

    private var timeUncontained = 0f
    private var shouldBeCulled = false

    override fun shouldBeCulled(delta: Float): Boolean {
        if (shouldBeCulled) return true

        val uncontained = !containable.isContainedIn(containerSupplier())
        if (uncontained) {
            timeUncontained += delta
            if (timeUncontained >= timeToCull) {
                timeUncontained = 0f
                shouldBeCulled = true
            }
        } else timeUncontained = 0f
        return shouldBeCulled
    }

    override fun reset() {
        super.reset()
        shouldBeCulled = false
    }

    override fun toString() = "CullableOnUncontained"
}
