package com.mega.game.engine.points

/**
 * Points are a value between the minimum stat and the maximum stat.
 *
 * @param min The minimum value.
 * @param max The maximum value.
 * @param current The current value.
 */
class Points(var min: Int, var max: Int, current: Int) {

    var current = current
        private set

    /**
     * Sets the current stat. If the current stat is less than the minimum stat, the current stat will
     * be set to the minimum stat. If the current stat is greater than the maximum stat, the current
     * stat will be set to the maximum stat.
     *
     * @param points The current pointsHandles.
     */
    fun set(points: Int) {
        current = points
        if (current < min) current = min
        if (current > max) current = max
    }

    /**
     * Translates the current stat by the specified delta. If the current stat is less than the
     * minimum stat, the current stat will be set to the minimum stat. If the current stat is greater
     * than the maximum stat, the current stat will be set to the maximum stat.
     *
     * @param delta The delta.
     */
    fun translate(delta: Int) = set(current + delta)

    /** Sets the current stat to the max stat. */
    fun setToMax() = set(max)

    /** Sets the current stat to the min stat. */
    fun setToMin() = set(min)

    /**
     * Returns true if the current stat is equal to the min stat. This method returns true if the current stat
     * is equal to the min stat, false otherwise.
     *
     * @return True if the current stat is equal to the min stat, false otherwise.
     */
    fun isMin() = current == min

    /**
     * Returns true if the current stat is equal to the max stat. This method returns true if the current stat
     * is equal to the max stat, false otherwise.
     *
     * @return True if the current stat is equal to the max stat, false otherwise.
     */
    fun isMax() = current == max
}
