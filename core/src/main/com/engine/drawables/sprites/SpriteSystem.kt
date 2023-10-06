package com.engine.drawables.sprites

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.engine.common.objects.ImmutableCollection
import com.engine.entities.IGameEntity
import com.engine.systems.GameSystem
import java.util.*

/**
 * A system that can be used to draw sprites. This system requires a [SpriteBatch].
 *
 * @param batch the sprite batch to use
 */
class SpriteSystem(private val batch: Batch) : GameSystem(SpriteComponent::class) {

  override fun process(on: Boolean, entities: ImmutableCollection<IGameEntity>, delta: Float) {
    if (!on) {
      return
    }

    val sortedSprites = PriorityQueue<IGameSprite>()

    entities.forEach { entity ->
      val spriteComponent = entity.getComponent(SpriteComponent::class)
      spriteComponent?.sprites?.values()?.forEach { sprite -> sortedSprites.add(sprite) }
    }

    while (!sortedSprites.isEmpty()) {
      val sprite = sortedSprites.poll()
      sprite.draw(batch)
    }
  }
}
