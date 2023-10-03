package com.engine.screens.levels.tiledmap

import com.badlogic.gdx.maps.MapObjects
import com.badlogic.gdx.utils.ObjectMap

/**
 * A [TiledMapLayerBuilder] is used to build a [MapObjects] layer. It is used in conjunction with
 * [TiledMapLevelBuilder]. The [TiledMapLevelBuilder] will iterate over each layer and call the
 * [TiledMapLayerBuilder.build] method. The [TiledMapLayerBuilder.build] method will be passed the
 * [MapObjects] for that layer.
 *
 * @see TiledMapLevelBuilder
 */
fun interface TiledMapLayerBuilder {

  /**
   * Builds the [MapObjects] for a layer.
   *
   * @param objects the [MapObjects] for a layer.
   */
  fun build(objects: MapObjects)
}

/**
 * A [TiledMapLevelBuilder] is used to build a level from a [Map] of [MapObjects] layers. It is used
 * in conjunction with [TiledMapLayerBuilder]. The [TiledMapLevelBuilder] will iterate over each
 * layer and call the [TiledMapLayerBuilder.build] method. The [TiledMapLayerBuilder.build] method
 * will be passed the [MapObjects] for that layer.
 */
object TiledMapLevelBuilder {

  /**
   * Builds from a map containing [MapObjects] layers.
   *
   * @param map the map of layer names to [MapObjects]
   * @param layerBuilders the map of layer names to [TiledMapLayerBuilder]
   * @see TiledMapLayerBuilder
   */
  fun build(map: ObjectMap<String, MapObjects>, layerBuilders: ObjectMap<String, TiledMapLayerBuilder>) {
    map.forEach { e ->
      val layerName = e.key
      val objects = e.value
      layerBuilders[layerName]?.build(objects)
    }
  }
}