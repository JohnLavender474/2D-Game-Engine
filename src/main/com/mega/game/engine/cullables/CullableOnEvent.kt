package com.mega.game.engine.cullables

import com.badlogic.gdx.utils.ObjectSet
import com.mega.game.engine.events.Event
import com.mega.game.engine.events.IEventListener
import java.util.function.Predicate

/**
 * A [ICullable] that will be culled if [cullOnEvent] returns true for any event.
 *
 * @param cullOnEvent a function that returns true if the [ICullable] should be culled for the
 *   event.
 */
class CullableOnEvent(
    private val cullOnEvent: (Event) -> Boolean,
    override val eventKeyMask: ObjectSet<Any> = ObjectSet()
) : ICullable, IEventListener {

    companion object {
        const val TAG = "CullableOnEvent"
    }

    private var cull: Boolean = false

    /**
     * Constructor that takes a lambda for the cullOnEvent function.
     *
     * @param cullOnEvent Lambda to be called when the object should be culled.
     * @param eventKeyMask The set of event keys that this cullable is interested in.
     */
    constructor(
        cullOnEvent: Predicate<Event>,
        eventKeyMask: ObjectSet<Any> = ObjectSet()
    ) : this(cullOnEvent::test, eventKeyMask)

    override fun shouldBeCulled(delta: Float) = cull

    override fun onEvent(event: Event) {
        if (!cull && cullOnEvent(event)) cull = true
    }

    override fun reset() {
        cull = false
    }

    override fun toString() = "CullableOnEvent"
}
