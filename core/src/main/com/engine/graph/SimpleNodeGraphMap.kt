package com.engine.graph

import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.OrderedSet
import com.engine.common.objects.IntPair
import com.engine.common.objects.pairTo
import com.engine.common.shapes.IGameShape2D
import kotlin.math.floor

/**
 * A simple graph map where a matrix of nodes represents the graph. Each node is equivalent to a
 * cell in the tiled map where the size of the cell is ppm * ppm. Each node contains a set of
 * objects that are in that cell.
 *
 * @param x The starting x coordinate of the graph (almost always zero).
 * @param y The starting y coordinate of the graph (almost always zero).
 * @param width The width of the graph.
 * @param height The height of the graph.
 * @param ppm The number of pixels per meter.
 */
class SimpleNodeGraphMap(
    override val x: Int,
    override val y: Int,
    override val width: Int,
    override val height: Int,
    override val ppm: Int
) : IGraphMap {

  private val map = ObjectMap<IntPair, OrderedSet<Any>>()

  override fun add(obj: Any, shape: IGameShape2D): Boolean {
    val bounds = shape.getBoundingRectangle()

    val minX = floor(bounds.x / ppm).toInt()
    val minY = floor(bounds.y / ppm).toInt()
    val maxX = floor(bounds.getMaxX() / ppm).toInt()
    val maxY = floor(bounds.getMaxY() / ppm).toInt()

    for (column in minX..maxX) for (row in minY..maxY) {
      val set = map.get(column pairTo row) ?: OrderedSet()
      set.add(obj)
      map.put(column pairTo row, set)
    }

    return true
  }

  override fun get(x: Int, y: Int) = map.get(x pairTo y) ?: OrderedSet()

  override fun get(minX: Int, minY: Int, maxX: Int, maxY: Int): OrderedSet<Any> {
    val set = OrderedSet<Any>()
    for (column in minX..maxX) for (row in minY..maxY) set.addAll(get(column, row))
    return set
  }

  override fun reset() = map.clear()
}