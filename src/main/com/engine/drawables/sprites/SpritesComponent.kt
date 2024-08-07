package com.engine.drawables.sprites

import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.OrderedMap
import com.engine.common.interfaces.Updatable
import com.engine.common.interfaces.UpdateFunction
import com.engine.components.IGameComponent
import com.engine.entities.IGameEntity

/**
 * A component that can be used to store sprites.
 *
 * @param sprites The sprites.
 */
class SpritesComponent(
    override val entity: IGameEntity,
    val sprites: OrderedMap<String, GameSprite> = OrderedMap(),
    val updatables: ObjectMap<String, UpdateFunction<GameSprite>> = ObjectMap()
) :
    IGameComponent, Updatable {

    companion object {
        const val SPRITE = "sprite"
    }

    /**
     * Creates a [SpritesComponent] with the given [sprites].
     *
     * @param entity The entity that this [SpritesComponent] belongs to.
     * @param _sprites The sprites to add to this [SpritesComponent].
     */
    constructor(
        entity: IGameEntity,
        vararg _sprites: Pair<String, GameSprite>
    ) : this(
        entity, OrderedMap<String, GameSprite>().apply { _sprites.forEach { put(it.first, it.second) } })

    /**
     * Constructs a [SpritesComponent] with the given [entity] and [sprite]. The sprite is stored with the key
     * [SPRITE]. This constructor is useful when only one sprite is needed.
     *
     * @param entity The entity that this [SpritesComponent] belongs to.
     * @param sprite The sprite to add to this [SpritesComponent].
     */
    constructor(
        entity: IGameEntity,
        sprite: GameSprite
    ) : this(entity, SPRITE to sprite)

    /**
     * Updates the sprites in this [SpritesComponent] using the corresponding update functions. If no
     * update function is found for a sprite, it will not be updated.
     *
     * @param delta The time in seconds since the last update.
     */
    override fun update(delta: Float) {
        updatables.forEach { e ->
            val name = e.key
            val function = e.value
            sprites[name]?.let { function.update(delta, it) }
        }
    }

    /**
     * Puts the given [function] into this [SpritesComponent] with the key [SPRITE]. This function will be used to
     * update the sprite stored with the key [SPRITE].
     *
     * @param function The function.
     */
    fun putUpdateFunction(function: UpdateFunction<GameSprite>) {
        putUpdateFunction(SPRITE, function)
    }

    /**
     * Puts the given [function] into this [SpritesComponent] with the given [key].
     *
     * @param key The key.
     * @param function The function.
     */
    fun putUpdateFunction(key: String, function: UpdateFunction<GameSprite>) {
        updatables.put(key, function)
    }

    /**
     * Removes the [UpdateFunction] with the given [key] from this [SpritesComponent].
     *
     * @param key The key.
     */
    fun removeUpdateFunction(key: String) {
        updatables.remove(key)
    }
}
