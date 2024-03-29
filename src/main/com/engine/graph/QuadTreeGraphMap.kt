package com.engine.graph

import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.OrderedSet
import com.engine.common.objects.IntPair
import com.engine.common.shapes.GameRectangle
import com.engine.common.shapes.IGameShape2D

// TODO: This class DOES NOT WORK as is! It needs to be fixed.
/**
 * A [IGraphMap] that uses a quad tree to store and retrieve objects.
 *
 * @param width The width of the graph.
 * @param height The height of the graph.
 * @param ppm The number of pixels per meter.
 * @param depth The depth of the quad tree.
 */
open class QuadTreeGraphMap(
    override val x: Int,
    override val y: Int,
    override val width: Int,
    override val height: Int,
    override val ppm: Int,
    val depth: Int
) : IGraphMap {

    protected val objects = ObjectMap<IntPair, OrderedSet<Any>>()

    override fun get(x: Int, y: Int): OrderedSet<Any> = objects.get(IntPair(x, y)) ?: OrderedSet()

    override fun get(minX: Int, minY: Int, maxX: Int, maxY: Int): OrderedSet<Any> {
        val set = OrderedSet<Any>()
        for (x in minX..maxX) for (y in minY..maxY) set.addAll(get(x, y))
        return set
    }

    /**
     * Adds the given object to this graph. This method is recursive. It will add the object to the
     * correct cell. If the recursive call is not at the maximum depth, it will split the cell into
     * four sub-cells and add the object to the correct sub-cell.
     *
     * @param obj The object to add.
     * @param currentDepth The currentDepth depth of the recursive call.
     * @param minX The minimum first coordinate of the cell.
     * @param minY The minimum second coordinate of the cell.
     * @param maxX The maximum first coordinate of the cell.
     * @param maxY The maximum second coordinate of the cell.
     */
    protected fun add(
        obj: Any,
        shape: IGameShape2D,
        currentDepth: Int,
        minX: Int,
        minY: Int,
        maxX: Int,
        maxY: Int
    ): Boolean {
        val overlap =
            GameRectangle(minX * ppm, minY * ppm, (maxX - minX) * ppm, (maxY - minY) * ppm)
                .overlaps(shape)

        if (overlap) {
            if (currentDepth < depth) {
                val midX = (minX + maxX) / 2
                val midY = (minY + maxY) / 2

                val quad1 = add(obj, shape, currentDepth + 1, minX, minY, midX, midY)
                val quad2 = add(obj, shape, currentDepth + 1, midX, minY, maxX, midY)
                val quad3 = add(obj, shape, currentDepth + 1, minX, midY, midX, maxY)
                val quad4 = add(obj, shape, currentDepth + 1, midX, midY, maxX, maxY)

                return quad1 || quad2 || quad3 || quad4
            } else {
                for (x in minX..maxX) {
                    for (y in minY..maxY) {
                        if (!objects.containsKey(IntPair(x, y))) objects.put(IntPair(x, y), OrderedSet())
                        val set = objects.get(IntPair(x, y))
                        set.add(obj)

                        return true
                    }
                }
            }
        }

        return false
    }

    override fun add(obj: Any, shape: IGameShape2D) = add(obj, shape, 0, 0, 0, width, height)

    override fun reset() = objects.clear()
}
