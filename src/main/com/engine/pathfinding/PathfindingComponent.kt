package com.engine.pathfinding

import com.engine.common.time.Timer
import com.engine.components.IGameComponent
import com.engine.entities.IGameEntity
import java.util.function.Consumer
import java.util.function.Supplier

/**
 * A component that handles pathfinding.
 *
 * @param entity The entity.
 * @param params The pathfinder params.
 * @param consumer The consumer of the pathfinder result.
 * @param doUpdate The function that determines if the pathfinder should update. By default, the
 *   pathfinder will always update.
 * @property currentPath The current path from the last pathfinder update.
 * @property updateIntervalTimer The timer for the interval between pathfinding updates. Default
 *   interval is 0.1 seconds (10 updates per second).
 */
class PathfindingComponent(
    override val entity: IGameEntity,
    var params: PathfinderParams,
    var consumer: (PathfinderResult) -> Unit,
    var doUpdate: () -> Boolean = { true }
) : IGameComponent {

    /**
     * Creates a new pathfinding component.
     *
     * @param entity The entity.
     * @param params The pathfinder params.
     * @param consumer The consumer of the pathfinder result.
     * @param doUpdate The function that determines if the pathfinder should update. By default, the
     */
    constructor(
        entity: IGameEntity,
        params: PathfinderParams,
        consumer: Consumer<PathfinderResult>,
        doUpdate: Supplier<Boolean> = Supplier { true }
    ) : this(
        entity,
        params,
        { consumer.accept(it) },
        { doUpdate.get() }
    )

    internal var currentPath: PathfinderResult? = null
    var updateIntervalTimer: Timer = Timer(0.1f)
}
