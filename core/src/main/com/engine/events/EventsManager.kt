package com.engine.events

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.OrderedSet
import com.engine.common.GameLogger
import com.engine.common.extensions.gdxArrayOf
import com.engine.common.extensions.putIfAbsentAndGet
import com.engine.common.objects.MultiCollectionIterable

/**
 * A manager for [Event]s and [IEventListener]s. [Event]s are submitted to the [EventsManager] and
 * [IEventListener]s are added to the [EventsManager]. When an [Event] is submitted, all
 * [IEventListener]s are notified of the [Event] when the [run] method is called.
 */
class EventsManager : IEventsManager {

  companion object {
    const val TAG = "EventsManager"
  }

  internal val listeners = OrderedSet<IEventListener>()
  internal val events = ObjectMap<Any, Array<Event>>()

  /**
   * Submits an [Event] to this [EventsManager].
   *
   * @param event The [Event] to submit.
   */
  override fun submitEvent(event: Event) {
    GameLogger.debug(TAG, "submitEvent(): Submitting event: $event")
    val eventKey = event.key
    events.putIfAbsentAndGet(eventKey, gdxArrayOf()).add(event)
  }

  /**
   * Adds an [IEventListener] to this [EventsManager]. The [IEventListener] will be notified of
   * [Event]s when the [run] method is called.
   *
   * @param listener The [IEventListener] to add.
   * @return If the [IEventListener] was added.
   */
  override fun addListener(listener: IEventListener): Boolean {
    GameLogger.debug(TAG, "addListener(): Adding listener: $listener")
    return listeners.add(listener)
  }

  /**
   * Removes an [IEventListener] from this [EventsManager]. The [IEventListener] will no longer be
   * notified of [Event]s when the [run] method is called.
   *
   * @param listener The [IEventListener] to remove.
   * @return If the [IEventListener] was removed.
   */
  override fun removeListener(listener: IEventListener): Boolean {
    GameLogger.debug(TAG, "removeListener(): Removing listener: $listener")
    return listeners.remove(listener)
  }

  /**
   * Removes all [IEventListener]s from this [EventsManager]. The [IEventListener]s will no longer
   * be notified of [Event]s when the [run] method is called.
   */
  override fun clearListeners() {
    GameLogger.debug(TAG, "clearListeners(): Clearing all listeners")
    listeners.clear()
  }

  /**
   * Notifies all [IEventListener]s of [Event]s that have been submitted to this [EventsManager].
   * After this method is finished, all events will be cleared. This method should be called once
   * per game loop.
   */
  override fun run() {
    val _events = ObjectMap(events)
    events.clear()

    listeners.forEach { listener ->
      val eventKeyMask = listener.eventKeyMask

      if (eventKeyMask.isEmpty) {
        _events.values().forEach { _events -> _events.forEach {
          GameLogger.debug(TAG, "run(): Notifying listener $listener of event: $it")
          listener.onEvent(it) }
        }
        return@forEach
      }

      val relevantEvents = Array<Iterable<Event>>()
      eventKeyMask.forEach { key -> _events.get(key)?.let { relevantEvents.add(it) } }
      val iterable = MultiCollectionIterable(relevantEvents)
      iterable.forEach {
        GameLogger.debug(TAG, "run(): Notifying listener $listener of event: $it")
        listener.onEvent(it)
      }
    }
  }
}
