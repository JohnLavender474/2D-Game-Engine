package com.mega.game.engine.controller

import com.mega.game.engine.controller.buttons.ControllerButton
import com.mega.game.engine.controller.buttons.ButtonStatus
import com.mega.game.engine.controller.buttons.ControllerButtons
import com.mega.game.engine.controller.polling.ControllerPoller
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.spyk

class ControllerPollerTest :
    DescribeSpec({
        describe("IControllerPoller") {
            var pressed = true

            it("should initialize with the provided buttons") {
                mockkObject(ControllerUtils) {
                    every { ControllerUtils.isControllerKeyPressed(any()) } answers { pressed }
                    every { ControllerUtils.isKeyboardKeyPressed(any()) } answers { pressed }

                    val buttons = ControllerButtons()
                    buttons.put("ButtonA", ControllerButton(1, 1, true))
                    buttons.put("ButtonB", ControllerButton(1, 1, true))

                    val controllerPoller = ControllerPoller(buttons)
                    controllerPoller.init()

                    controllerPoller.getStatus("ButtonA") shouldBe ButtonStatus.RELEASED
                    controllerPoller.getStatus("ButtonB") shouldBe ButtonStatus.RELEASED
                }
            }

            it("should update button status when run") {
                mockkObject(ControllerUtils) {
                    every { ControllerUtils.isControllerKeyPressed(any()) } answers { pressed }
                    every { ControllerUtils.isKeyboardKeyPressed(any()) } answers { pressed }
                    val buttonPoller = spyk(ControllerButton(1, 1, true))
                    val buttons = ControllerButtons()
                    buttons.put("ButtonA", buttonPoller)

                    val controllerPoller = ControllerPoller(buttons)
                    controllerPoller.init()

                    controllerPoller.getStatus("ButtonA") shouldBe ButtonStatus.RELEASED

                    controllerPoller.run()
                    controllerPoller.getStatus("ButtonA") shouldBe ButtonStatus.JUST_PRESSED

                    controllerPoller.run()
                    controllerPoller.getStatus("ButtonA") shouldBe ButtonStatus.PRESSED

                    pressed = false

                    controllerPoller.run()
                    controllerPoller.getStatus("ButtonA") shouldBe ButtonStatus.JUST_RELEASED

                    controllerPoller.run()
                    controllerPoller.getStatus("ButtonA") shouldBe ButtonStatus.RELEASED

                    pressed = true

                    controllerPoller.run()
                    controllerPoller.getStatus("ButtonA") shouldBe ButtonStatus.JUST_PRESSED
                }
            }

            it("should put status for new button") {
                mockkObject(ControllerUtils) {
                    every { ControllerUtils.isControllerKeyPressed(any()) } answers { pressed }
                    every { ControllerUtils.isKeyboardKeyPressed(any()) } answers { pressed }
                    val buttonPollerA = spyk(ControllerButton(1, 1, true))
                    val buttons = ControllerButtons()
                    buttons.put("ButtonA", buttonPollerA)

                    val controllerPoller = ControllerPoller(buttons)
                    controllerPoller.init()

                    controllerPoller.getStatus("ButtonA") shouldBe ButtonStatus.RELEASED

                    controllerPoller.run()
                    controllerPoller.getStatus("ButtonA") shouldBe ButtonStatus.JUST_PRESSED
                    controllerPoller.getStatus("ButtonB") shouldBe null

                    val buttonPollerB = spyk(ControllerButton(1, 1, true))
                    buttons.put("ButtonB", buttonPollerB)

                    controllerPoller.run()
                    controllerPoller.getStatus("ButtonA") shouldBe ButtonStatus.PRESSED
                    controllerPoller.getStatus("ButtonB") shouldBe ButtonStatus.JUST_PRESSED
                }
            }

            it("should release button when disabled") {
                mockkObject(ControllerUtils) {
                    every { ControllerUtils.isControllerKeyPressed(any()) } answers { pressed }
                    every { ControllerUtils.isKeyboardKeyPressed(any()) } answers { pressed }
                    val buttonPollerA = spyk(ControllerButton(1, 1, true))
                    val buttons = ControllerButtons()
                    buttons.put("ButtonA", buttonPollerA)

                    val controllerPoller = ControllerPoller(buttons)
                    controllerPoller.init()

                    controllerPoller.getStatus("ButtonA") shouldBe ButtonStatus.RELEASED

                    controllerPoller.run()
                    controllerPoller.getStatus("ButtonA") shouldBe ButtonStatus.JUST_PRESSED
                    controllerPoller.run()
                    controllerPoller.getStatus("ButtonA") shouldBe ButtonStatus.PRESSED

                    buttons.get("ButtonA")?.enabled = false

                    controllerPoller.run()
                    controllerPoller.getStatus("ButtonA") shouldBe ButtonStatus.JUST_RELEASED
                    controllerPoller.run()
                    controllerPoller.getStatus("ButtonA") shouldBe ButtonStatus.RELEASED

                    buttons.get("ButtonA")?.enabled = true

                    controllerPoller.run()
                    controllerPoller.getStatus("ButtonA") shouldBe ButtonStatus.JUST_PRESSED
                    controllerPoller.run()
                    controllerPoller.getStatus("ButtonA") shouldBe ButtonStatus.PRESSED
                }
            }
        }
    })
