package com.mega.game.engine.world

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.ObjectSet
import com.badlogic.gdx.utils.OrderedSet
import com.mega.game.engine.common.extensions.exp
import com.mega.game.engine.common.objects.ImmutableCollection
import com.mega.game.engine.common.objects.Pool
import com.mega.game.engine.common.objects.Properties
import com.mega.game.engine.common.shapes.GameRectangle
import com.mega.game.engine.entities.IGameEntity
import com.mega.game.engine.systems.GameSystem
import com.mega.game.engine.world.body.Body
import com.mega.game.engine.world.body.BodyComponent
import com.mega.game.engine.world.body.IFixture
import com.mega.game.engine.world.collisions.ICollisionHandler
import com.mega.game.engine.world.contacts.Contact
import com.mega.game.engine.world.contacts.IContactListener
import com.mega.game.engine.world.container.IWorldContainer
import java.util.function.Supplier
import kotlin.math.abs

/**
 * A system that handles the physics of the game. This system is responsible for updating the
 * positions of all bodies, resolving collisions, and notifying the [IContactListener] of any
 * contacts that occur. This system is stateful. This system processes entities that have a
 * [BodyComponent].
 *
 * This system uses a [IWorldContainer] to represent the world and determine which bodies are overlapping. This might be
 * a representation of the game level where the x and y of the graph map is the min coordinate of the level and the max
 * x and max y are determined by the min coordinate plus the width or height of the level. Although the lambda allows
 * for the container object to be nullable, if it is null when [WorldSystem] is run, then a null-pointer exception will
 * be thrown.
 *
 * This system uses a [IContactListener] to notify of contacts.
 *
 * This system uses a [ICollisionHandler] to resolve collisions.
 *
 * This system uses a [fixedStep] to update the physics. If the delta time during an update is
 * greater than the fixed step, the physics will possibly be updated multiple times in one update
 * cycle. On the other hand, if the delta time during an update is less than the fixed step, then
 * possibly there won't be a physic update during that update.
 *
 * Keep in mind that different values for the fixed step will result in different physics outcomes.
 * It is up to the user to determine what the best value for the fixed step is. I personally
 * recommend the fixed step be half the target FPS. More physics updates per frame will result in
 * more accurate and reliable physics, but will also result in more processing time.
 *
 * This system uses an optional [contactFilterMap] to optimize the determination of which contacts
 * to notify the [IContactListener] of. The [contactFilterMap] can be used to filter only cases
 * where the user has an implementation for two fixtures. For example, let's say there's a contact
 * between one fixture with type "Type1" and another fixture with type "Type2". If there's no
 * implementation in the provided [IContactListener] for the case of "Type1" touching "Type2", then
 * by not providing the entry in the [contactFilterMap], the [IContactListener] will NOT be
 * notified. The order of the entry is not important, so using "Type1" as the key with "Type2"
 * inside the value [Set<String>], or else vice versa, will NOT change the result. If the
 * [contactFilterMap] is null, then the [IContactListener] will be notified of ALL contacts.
 *
 * The [preProcess] function is run for each [Body] directly after the body's own [Body.preProcess]
 * function has been run. This is useful if you want to run common logic for all bodies in the
 * pre-processing stage.
 *
 * The [postProcess] function is run for each [Body] directly after the body's own
 * [Body.postProcess] function has been run. This is useful if you want to run common logic for all
 * bodies in the post-processing stage.
 *
 * The [fixedStepScalar] property is useful for speeding up or slowing down the delta time, mainly for debugging
 * purposes. It should be set to 1f (the default value) in production. The value must be greater than 0f or else an
 * exception will be thrown.
 *
 * @property ppm the pixels per meter of the world representation
 * @property contactListener the [IContactListener] to notify of contacts
 * @property worldContainerSupplier the supplier for the [IWorldContainer] to use
 * @property fixedStep the fixed step to update the physics
 * @property collisionHandler the [ICollisionHandler] to resolve collisions
 * @property contactFilterMap the optional [ObjectMap] to filter contacts
 * @property fixedStepScalar the scalar to apply to the fixed step, useful for debugging; should be 1f in prod.
 * Default value is 1f (no scaling applied). If the value is less than 1f, the rendering of physics will be slowed down.
 * If greater than 1f, then rendering will be sped up. Must be greater than 0f or else an exception will be thrown.
 */
class WorldSystem(
    private val ppm: Int,
    private val fixedStep: Float,
    private val worldContainerSupplier: () -> IWorldContainer?,
    private val contactListener: IContactListener,
    private val collisionHandler: ICollisionHandler,
    private val contactFilterMap: ObjectMap<Any, ObjectSet<Any>>,
    var fixedStepScalar: Float = 1f
) : GameSystem(BodyComponent::class) {

    companion object {
        const val TAG = "WorldSystem"
    }

    init {
        if (fixedStepScalar <= 0f) throw IllegalArgumentException("fixedStepScalar must be greater than 0")
    }

    private class DummyFixture : IFixture {
        override fun getShape() =
            throw IllegalStateException("The `getType` method should never be called on a DummyTypable instance")

        override fun isActive() =
            throw IllegalStateException("The `getType` method should never be called on a DummyTypable instance")

        override fun getType() =
            throw IllegalStateException("The `getType` method should never be called on a DummyTypable instance")

        override val properties: Properties
            get() = throw IllegalStateException("The `getType` method should never be called on a DummyTypable instance")
    }

    private val worldContainer: IWorldContainer
        get() = worldContainerSupplier()!!
    private val reusableBodyArray = Array<Body>()
    private val contactPool = Pool(supplier = { Contact(DummyFixture(), DummyFixture()) })
    private val reusableGameRect = GameRectangle()

    private var priorContactSet = OrderedSet<Contact>()
    private var currentContactSet = OrderedSet<Contact>()
    private var accumulator = 0f

    /**
     * Constructor that accepts the world graph supplier as a [Supplier] (for easy compatibility with Java). See
     * [WorldSystem] main constructor.
     *
     * @param ppm the pixels per meter of the world representation
     * @param fixedStep the fixed step
     * @param contactListener the contact listener
     * @param _worldContainerSupplier the world graph supplier as a [Supplier] object
     * @param collisionHandler the collision handler
     * @param contactFilterMap the contact filter map
     */
    constructor(
        ppm: Int,
        fixedStep: Float,
        _worldContainerSupplier: Supplier<IWorldContainer?>,
        contactListener: IContactListener,
        collisionHandler: ICollisionHandler,
        contactFilterMap: ObjectMap<Any, ObjectSet<Any>>,
    ) : this(
        ppm, fixedStep, { _worldContainerSupplier.get() }, contactListener, collisionHandler, contactFilterMap
    )

    override fun process(on: Boolean, entities: ImmutableCollection<IGameEntity>, delta: Float) {
        if (!on) return

        accumulator += delta

        if (accumulator >= fixedStep) {
            entities.forEach {
                val body = it.getComponent(BodyComponent::class)!!.body
                reusableBodyArray.add(body)
            }

            while (accumulator >= fixedStep) {
                accumulator -= fixedStep / fixedStepScalar
                cycle(reusableBodyArray, fixedStep)
            }

            worldContainer.clear()
            reusableBodyArray.forEach { body ->
                worldContainer.addBody(body)
                body.fixtures.forEach { (_, fixture) -> worldContainer.addFixture(fixture) }
            }

            reusableBodyArray.clear()
        }
    }

    override fun reset() {
        super.reset()
        accumulator = 0f
        priorContactSet.forEach { contactPool.pool(it) }
        currentContactSet.forEach { contactPool.pool(it) }
        priorContactSet.clear()
        currentContactSet.clear()
        worldContainerSupplier()?.clear()
    }

    internal fun filterContact(fixture1: IFixture, fixture2: IFixture) =
        (fixture1 != fixture2) && (contactFilterMap.get(fixture1.getType())?.contains(
            fixture2.getType()
        ) == true || contactFilterMap.get(fixture2.getType())?.contains(
            fixture1.getType()
        ) == true)

    private fun cycle(bodies: Array<Body>, delta: Float) {
        preProcess(bodies, delta)
        worldContainer.clear()
        bodies.forEach { body ->
            updatePhysics(body, delta)
            worldContainer.addBody(body)
            body.fixtures.forEach { (_, fixture) -> worldContainer.addFixture(fixture) }
        }
        bodies.forEach { body ->
            checkForContacts(body)
            resolveCollisions(body)
        }
        processContacts()
        postProcess(bodies, delta)
    }

    private fun preProcess(bodies: Array<Body>, delta: Float) =
        bodies.forEach { body -> body.preProcess.values().forEach { it.update(delta) } }

    private fun postProcess(bodies: Array<Body>, delta: Float) =
        bodies.forEach { body -> body.postProcess.values().forEach { it.update(delta) } }

    private fun processContacts() {
        currentContactSet.forEach {
            if (priorContactSet.contains(it)) contactListener.continueContact(it, fixedStep)
            else contactListener.beginContact(it, fixedStep)
        }

        priorContactSet.forEach {
            if (!currentContactSet.contains(it)) {
                contactListener.endContact(it, fixedStep)
                contactPool.pool(it)
            }
        }

        priorContactSet.clear()
        priorContactSet.addAll(currentContactSet)
        currentContactSet.clear()
    }

    private fun updatePhysics(body: Body, delta: Float) {
        body.physics.let { physics ->
            /*
            if (physics.takeFrictionFromOthers) {
                if (physics.frictionOnSelf.x > 0f) physics.velocity.x /= physics.frictionOnSelf.x
                if (physics.frictionOnSelf.y > 0f) physics.velocity.y /= physics.frictionOnSelf.y
            }
             */
            if (physics.takeFrictionFromOthers) {
                if (physics.frictionOnSelf.x > 0f)
                    physics.velocity.x *= exp(-physics.frictionOnSelf.x * delta)
                if (physics.frictionOnSelf.y > 0f)
                    physics.velocity.y *= exp(-physics.frictionOnSelf.y * delta)
            }
            physics.frictionOnSelf.set(physics.defaultFrictionOnSelf)

            if (physics.gravityOn) physics.velocity.add(physics.gravity)

            physics.velocity.x =
                physics.velocity.x.coerceIn(-abs(physics.velocityClamp.x), abs(physics.velocityClamp.x))
            physics.velocity.y =
                physics.velocity.y.coerceIn(-abs(physics.velocityClamp.y), abs(physics.velocityClamp.y))

            body.x += physics.velocity.x * delta
            body.y += physics.velocity.y * delta
        }
    }

    private fun checkForContacts(body: Body) = body.fixtures.forEach { (_, fixture) ->
        if (fixture.isActive() && contactFilterMap.containsKey(fixture.getType())) {
            fixture.getShape().getBoundingRectangle(reusableGameRect)
            val worldGraphResults = worldContainer.getFixtures(
                MathUtils.floor(reusableGameRect.x / ppm),
                MathUtils.floor(reusableGameRect.y / ppm),
                MathUtils.floor(reusableGameRect.getMaxX() / ppm),
                MathUtils.floor(reusableGameRect.getMaxY() / ppm)
            )

            worldGraphResults.forEach {
                if (it.isActive() && filterContact(fixture, it) &&
                    fixture.getShape().overlaps(it.getShape())
                ) {
                    val contact = contactPool.fetch()
                    contact.set(fixture, it)
                    currentContactSet.add(contact)
                }
            }
        }
    }

    private fun resolveCollisions(body: Body) {
        val bounds = body.getBodyBounds()
        worldContainer.getBodies(
            MathUtils.floor(bounds.x / ppm),
            MathUtils.floor(bounds.y / ppm),
            MathUtils.floor(bounds.getMaxX() / ppm),
            MathUtils.floor(bounds.getMaxY() / ppm)
        ).forEach {
            if (it != body && it.getBodyBounds().overlaps(bounds as Rectangle))
                collisionHandler.handleCollision(body, it)
        }
    }
}
