package com.engine.common.interfaces

import com.badlogic.gdx.math.Vector2

/** Interface for objects that have a position. */
interface PositionSupplier {

  /**
   * Gets the x-coordinate of this object.
   *
   * @return the x-coordinate of this object
   */
  fun getX(): Float

  /**
   * Gets the y-coordinate of this object.
   *
   * @return the y-coordinate of this object
   */
  fun getY(): Float

  /**
   * Gets the position of this object.
   *
   * @return the position of this object
   */
  fun getPosition() = Vector2(getX(), getY())
}