package com.engine.drawables.shapes

import com.badlogic.gdx.utils.Array
import com.engine.common.extensions.toGdxArray
import com.engine.components.IGameComponent
import com.engine.entities.IGameEntity
import java.util.function.Supplier

/**
 * A [DrawableShapesComponent] is a [IGameComponent] that contains an of suppliers for
 * [IDrawableShape]s that can be drawn.
 *
 * @param entity the [IGameEntity] that this [DrawableShapesComponent] belongs to
 * @param prodShapeSuppliers the [IDrawableShape]s that should be drawn in the production release of
 *   the game
 * @param debugShapeSuppliers the [IDrawableShape]s that should be drawn in the debug release of the
 *   game
 * @param debug whether the debug shapes should be drawn when the game is in debug mode
 */
class DrawableShapesComponent(
    override val entity: IGameEntity,
    var prodShapeSuppliers: Array<() -> IDrawableShape?> = Array(),
    var debugShapeSuppliers: Array<() -> IDrawableShape?> = Array(),
    var debug: Boolean = false
) : IGameComponent {

    /**
     * Creates a [DrawableShapesComponent] with the given [IDrawableShape] suppliers.
     *
     * @param entity the [IGameEntity] that this [DrawableShapesComponent] belongs to
     * @param shapeSuppliers the suppliers for [IDrawableShape]s that can be drawn
     */
    constructor(
        entity: IGameEntity,
        vararg shapeSuppliers: () -> IDrawableShape?
    ) : this(entity, Array(shapeSuppliers))


    /**
     * Creates a [DrawableShapesComponent] with the given [IDrawableShape]s.
     *
     * @param entity the [IGameEntity] that this [DrawableShapesComponent] belongs to
     * @param shapes the [IDrawableShape]s that can be drawn
     */
    constructor(
        entity: IGameEntity,
        vararg shapes: IDrawableShape?
    ) : this(entity, shapes.map { { it } }.toGdxArray())

    /**
     * Sets the debug shapes to be drawn when the game is in debug mode.
     *
     * @param debugShapeSuppliers whether the debug shapes should be drawn when the game is in debug mode
     */
    fun setDebugShapeSuppliersToJavaArray(debugShapeSuppliers: Array<Supplier<IDrawableShape?>>) {
        this.debugShapeSuppliers = debugShapeSuppliers.map { it::get }.toGdxArray()
    }

    /**
     * Sets the production shapes to be drawn when the game is in production mode.
     *
     * @param prodShapeSuppliers whether the production shapes should be drawn when the game is in production mode
     */
    fun setProdShapeSuppliersToJavaArray(prodShapeSuppliers: Array<Supplier<IDrawableShape?>>) {
        this.prodShapeSuppliers = prodShapeSuppliers.map { it::get }.toGdxArray()
    }
}
