package com.engine.points

import com.engine.common.objects.ImmutableCollection
import com.engine.entities.IGameEntity
import com.engine.systems.GameSystem

/**
 * The points system. Processes all the points components by calling each listener in the component.
 */
class PointsSystem : GameSystem(PointsComponent::class) {

    override fun process(on: Boolean, entities: ImmutableCollection<IGameEntity>, delta: Float) {
        if (!on) return

        entities.forEach { entity ->
            val pointsComponent = entity.getComponent(PointsComponent::class)
            pointsComponent?.pointsListeners?.forEach { e ->
                val key = e.key
                val listener = e.value

                val points = pointsComponent.pointsMap[key]
                if (points != null) listener.invoke(points)
            }
        }
    }
}
