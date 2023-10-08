package com.engine.damage

/**
 * A [IDamageable] is an object that can take damage from a [IDamager]. The [IDamageable] is
 * responsible for determining whether it can take damage from a [IDamager] and how much damage it
 * takes.
 */
fun interface IDamageable {

  /**
   * Take damage from the [IDamager]. Return true if damage was taken, otherwise false.
   *
   * @param damager the [IDamager] that is dealing damage to this [IDamageable]
   * @return true if damage was taken, otherwise false
   */
  fun takeDamageFrom(damager: IDamager): Boolean
}