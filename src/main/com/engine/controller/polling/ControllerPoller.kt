package com.engine.controller.polling

import com.badlogic.gdx.utils.ObjectMap
import com.engine.controller.ControllerUtils
import com.engine.controller.buttons.ButtonStatus
import com.engine.controller.buttons.Buttons

/**
 * Polls the controller buttons and updates the status of each button.
 *
 * @param buttons The map of buttons to poll.
 */
open class ControllerPoller(val buttons: Buttons) : IControllerPoller {

    val statusMap = ObjectMap<Any, ButtonStatus>()
    override var on = true

    init {
        buttons.keys().forEach { statusMap.put(it, ButtonStatus.RELEASED) }
    }

    override fun getStatus(key: Any): ButtonStatus? = statusMap[key]

    override fun run() {
        if (!on) return

        buttons.forEach { e ->
            val key = e.key
            val button = e.value

            if (!statusMap.containsKey(key)) statusMap.put(key, ButtonStatus.RELEASED)
            val status = statusMap.get(key)

            var pressed: Boolean
            button.keyboardCode.let { pressed = ControllerUtils.isKeyboardKeyPressed(it) }
            if (!pressed)
                button.controllerCode?.let { pressed = ControllerUtils.isControllerKeyPressed(it) }

            val newStatus =
                if (button.enabled) {
                    when (status) {
                        ButtonStatus.RELEASED,
                        ButtonStatus.JUST_RELEASED ->
                            if (pressed) ButtonStatus.JUST_PRESSED else ButtonStatus.RELEASED

                        else -> if (pressed) ButtonStatus.PRESSED else ButtonStatus.JUST_RELEASED
                    }
                } else if (status == ButtonStatus.JUST_RELEASED) ButtonStatus.RELEASED
                else ButtonStatus.JUST_RELEASED

            statusMap.put(key, newStatus)
        }
    }
}
