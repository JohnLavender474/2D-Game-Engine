package com.engine.entities

import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.OrderedSet
import com.engine.common.CAUSE_OF_DEATH_MESSAGE
import com.engine.common.CAUSE_OF_DEATH_TAG
import com.engine.common.GameLogger
import com.engine.common.objects.Properties
import com.engine.components.IGameComponent
import kotlin.reflect.KClass
import kotlin.reflect.cast

/**
 * Standard implementation for [IGameEntity].
 */
open class GameEntity : IGameEntity {

    companion object {
        const val TAG = "GameEntity"
    }

    val componentMap = ObjectMap<KClass<out IGameComponent>, IGameComponent>()

    override val runnablesOnSpawn = OrderedSet<Runnable>()
    override val runnablesOnDestroy = OrderedSet<Runnable>()
    override val properties = Properties()
    override var dead = false

    /**
     * Whether this [GameEntity] has been initialized. If this is false, then [init] will be called
     * when [spawn] is called.
     */
    var initialized = false

    override fun kill(props: Properties?) {
        super.kill(props)
        props?.let {
            if (it.containsKey(CAUSE_OF_DEATH_MESSAGE)) {
                val tag = it.getOrDefault(CAUSE_OF_DEATH_TAG, TAG) as String
                GameLogger.debug(
                    tag,
                    "${this::class.simpleName} killed. Cause of death: ${it.get(CAUSE_OF_DEATH_MESSAGE)}"
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        getComponents().forEach { it.reset() }
    }

    override fun spawn(spawnProps: Properties) {
        properties.clear()
        properties.putAll(spawnProps)
        if (!initialized) {
            init()
            initialized = true
        }
        runnablesOnSpawn.forEach { it.run() }
        dead = false
    }

    override fun addComponent(c: IGameComponent) {
        componentMap.put(c::class, c)
    }

    override fun <C : IGameComponent> getComponent(c: KClass<C>) =
        if (hasComponent(c)) c.cast(componentMap[c]) else null

    override fun getComponents(): Iterable<IGameComponent> = componentMap.values()

    override fun hasComponent(c: KClass<out IGameComponent>) = componentMap.containsKey(c)

    override fun removeComponent(c: KClass<out IGameComponent>): IGameComponent? =
        componentMap.remove(c)

    override fun clearComponents() = componentMap.clear()

    override fun print() = "${this::class.simpleName}"

    override fun getTag() = TAG
}
