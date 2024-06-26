package com.engine

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.viewport.Viewport
import com.engine.common.GameLogger
import com.engine.common.objects.Properties
import com.engine.controller.buttons.Buttons
import com.engine.controller.polling.IControllerPoller
import com.engine.events.IEventsManager
import com.engine.screens.IScreen

/**
 * Implementation of [IGame2D] that also derives [Game]. The following fields must be initialized in the overriding
 * class's [create] method: [shapeRenderer], [batch], [buttons], [controllerPoller], [assMan], [eventsMan], and
 * [engine]. These fields use the lateinit keyword to avoid nullability and to allow them to be initialized in
 * the [create] method rather than in the constructor.
 *
 * @see IGame2D the interface for the game object; if this implementation does not suit your needs, then you can
 * opt to implement the interface instead in your own class, or forego this entirely and use the LibGDX [Game] class
 * @see Game the LibGDX game class
 */
abstract class Game2D : IGame2D, Game() {

    companion object {
        const val TAG = "Game2D"
    }

    override lateinit var shapeRenderer: ShapeRenderer
    override lateinit var batch: SpriteBatch
    override lateinit var buttons: Buttons
    override lateinit var controllerPoller: IControllerPoller
    override lateinit var assMan: AssetManager
    override lateinit var eventsMan: IEventsManager
    override lateinit var engine: IGameEngine
    override val screens = ObjectMap<String, IScreen>()
    override val viewports = ObjectMap<String, Viewport>()
    override val currentScreen: IScreen?
        get() = currentScreenKey?.let { screens[it] }
    override val properties = Properties()
    override var paused = false

    private var currentScreenKey: String? = null

    /**
     * Hides the old screen and removes it from the events manager. After that, if there is a screen
     * mapped to the specified key, then that screen is shown, resized, and added as a listener to the
     * events manager.
     *
     * @param key the key
     */
    override fun setCurrentScreen(key: String) {
        GameLogger.debug(TAG, "setCurrentScreen: set to screen with key = $key")

        // hide old screen and remove it from events manager
        currentScreenKey
            ?.let { screens[it] }
            ?.let {
                it.hide()
                eventsMan.removeListener(it)
            }

        // set next screen key
        currentScreenKey = key

        // get next screen, and if present show it, resize it, add it as an events listener, and pause
        // it if necessary
        screens[key]?.let { nextScreen ->
            nextScreen.show()
            nextScreen.resize(Gdx.graphics.width, Gdx.graphics.height)

            eventsMan.addListener(nextScreen)

            if (paused) nextScreen.pause()
        }
    }

    /**
     * Resizes the viewports and current screen.
     *
     * @param width the width
     * @param height the height
     */
    override fun resize(width: Int, height: Int) {
        viewports.values().forEach { it.update(width, height) }
        currentScreen?.resize(width, height)
    }

    /**
     * Clears the screen; updates the controller poller, and events manager; renders the current
     * screen; and updates all the viewports contained in [viewports]
     */
    override fun render() {
        val delta = Gdx.graphics.deltaTime
        controllerPoller.run()
        eventsMan.run()
        currentScreen?.render(delta)
        viewports.values().forEach { it.apply() }
    }

    /** Pauses the current screen and sets [paused] to true. */
    override fun pause() {
        GameLogger.debug(TAG, "pause()")
        if (paused) return
        paused = true
        currentScreen?.pause()
    }

    /** Resumes the current screen and sets [paused] to false. */
    override fun resume() {
        GameLogger.debug(TAG, "resume()")
        if (!paused) return
        paused = false
        currentScreen?.resume()
    }

    /** Disposes of the [batch], [shapeRenderer], and [IScreen]s. */
    override fun dispose() {
        GameLogger.debug(TAG, "dispose()")
        batch.dispose()
        shapeRenderer.dispose()
        screens.values().forEach { it.dispose() }
    }
}
