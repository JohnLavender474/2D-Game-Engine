package com.engine.drawables.sprites

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.engine.common.interfaces.Positional
import com.engine.common.interfaces.Sizable
import com.engine.drawables.IComparableDrawable

/** An interface for objects that can be sorted, positioned, sized, and drawn. */
interface ISprite : IComparableDrawable<Batch>, Positional, Sizable {

  /**
   * Sets the region of this sprite.
   *
   * @param _region the region to set
   */
  fun setRegion(_region: TextureRegion)

  /**
   * Sets the texture of this sprite.
   *
   * @param _texture the texture to set
   */
  fun setTexture(_texture: Texture)
}