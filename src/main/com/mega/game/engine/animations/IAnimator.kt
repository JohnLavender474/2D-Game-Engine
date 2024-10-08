package com.mega.game.engine.animations

import com.mega.game.engine.common.interfaces.Resettable
import com.mega.game.engine.drawables.sprites.GameSprite

/** An animator that can be used to animate a sprite. */
interface IAnimator : Resettable {

    /**
     * Animates the specified sprite.
     *
     * @param sprite the sprite to animate
     * @param delta the time in seconds since the last update
     */
    fun animate(sprite: GameSprite, delta: Float)
}
