package com.engine.drawables.fonts

import com.badlogic.gdx.utils.OrderedMap
import com.engine.common.extensions.objectMapOf
import com.engine.common.interfaces.Updatable
import com.engine.common.interfaces.UpdateFunction
import com.engine.components.IGameComponent
import com.engine.entities.IGameEntity

/**
 * A component that holds a collection of [BitmapFontHandle] objects. The fonts are stored using a key
 * that can be used to retrieve the font. The fonts can be added, removed, and retrieved. The fonts are
 * stored in a map where the key is the key used to store the font and the value is the font itself.
 * The fonts can be used to draw text on the screen.
 *
 * @param entity the entity that this component belongs to
 */
class FontsComponent(override val entity: IGameEntity, val fonts: OrderedMap<Any, BitmapFontHandle> = OrderedMap()) :
    IGameComponent, Updatable {

    internal val updatables = objectMapOf<Any, UpdateFunction<BitmapFontHandle>>()

    /**
     * Creates a [FontsComponent] with the given [fonts].
     *
     * @param entity the entity that this [FontsComponent] belongs to
     * @param fonts the fonts to add to this [FontsComponent]
     */
    constructor(
        entity: IGameEntity,
        vararg fonts: Pair<Any, BitmapFontHandle>
    ) : this(entity, OrderedMap<Any, BitmapFontHandle>().apply { fonts.forEach { put(it.first, it.second) } })

    /**
     * Puts the given [function] into this [FontsComponent] with the given [key].
     *
     * @param key the key
     * @param function the function
     */
    fun putUpdateFunction(key: Any, function: UpdateFunction<BitmapFontHandle>) {
        updatables.put(key, function)
    }

    /**
     * Removes the function with the given [key] from this [FontsComponent].
     *
     * @param key the key
     */
    fun removeUpdateFunction(key: Any) {
        updatables.remove(key)
    }

    /**
     * Updates the fonts in this [FontsComponent] using the corresponding update functions. If no update
     * function is found for a font, it will not be updated.
     *
     * @param delta the time in seconds since the last update
     */
    override fun update(delta: Float) {
        updatables.forEach { e ->
            val key = e.key
            val function = e.value
            fonts[key]?.let { function.update(delta, it) }
        }
    }

    /**
     * Adds a font to the component. The font is stored using the given key. If a font with the same key
     * already exists, it will be replaced.
     *
     * @param key the key to store the font with
     * @param font the font to store
     */
    fun add(key: Any, font: BitmapFontHandle) {
        fonts.put(key, font)
    }

    /**
     * Removes a font from the component. If the font does not exist, nothing happens.
     *
     * @param key the key of the font to remove
     */
    fun remove(key: Any) {
        fonts.remove(key)
    }

    /**
     * Retrieves a font from the component. If the font does not exist, null is returned.
     *
     * @param key the key of the font to retrieve
     * @return the font with the given key, or null if the font does not exist
     */
    fun get(key: Any): BitmapFontHandle? {
        return fonts[key]
    }

}