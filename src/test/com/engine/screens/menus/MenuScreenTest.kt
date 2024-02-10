package com.engine.screens.menus

import com.engine.common.enums.Direction
import com.engine.common.extensions.objectMapOf
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.spyk
import io.mockk.verify

class MenuScreenTest :
    DescribeSpec({
        describe("MenuScreen") {
            lateinit var pauseSupplier: () -> Boolean
            lateinit var menuScreen: MenuScreen

            var direction: Direction?
            var selectionRequested: Boolean
            var onAnyMovement = false

            val firstButtonKey = "firstButtonKey"
            val secondButtonKey = "secondButtonKey"
            val buttons =
                objectMapOf(
                    firstButtonKey to
                            spyk(
                                object : IMenuButton {
                                    override fun onSelect(delta: Float) = true

                                    override fun onNavigate(direction: Direction, delta: Float) =
                                        secondButtonKey
                                }),
                    secondButtonKey to
                            spyk(
                                object : IMenuButton {
                                    override fun onSelect(delta: Float) = false

                                    override fun onNavigate(direction: Direction, delta: Float) =
                                        firstButtonKey
                                })
                )

            beforeEach {
                clearAllMocks()

                direction = null
                selectionRequested = false
                onAnyMovement = false

                pauseSupplier = { false }

                menuScreen =
                    spyk(
                        object : MenuScreen(pauseSupplier, firstButtonKey, buttons) {

                            override fun onAnyMovement() {
                                onAnyMovement = true
                            }

                            override fun getNavigationDirection() = direction

                            override fun selectionRequested() = selectionRequested

                            override fun resize(width: Int, height: Int) {}

                            override fun pause() {}

                            override fun resume() {}

                            override fun hide() {}

                            override fun dispose() {}
                        })
            }

            it("should show the menu screen correctly") {
                // when
                menuScreen.show()

                // then
                menuScreen.selectionMade shouldBe false
                menuScreen.currentButtonKey shouldBe firstButtonKey
            }

            describe("renderLevelMap") {
                it("should renderLevelMap the menu screen correctly") {
                    // when
                    menuScreen.render(0.0f)

                    // then
                    menuScreen.selectionMade shouldBe false
                    menuScreen.currentButtonKey shouldBe firstButtonKey
                }

                it("should call button 1 navigation") {
                    // if
                    direction = Direction.UP

                    // when
                    menuScreen.render(0.0f)

                    // then
                    menuScreen.selectionMade shouldBe false
                    menuScreen.currentButtonKey shouldBe secondButtonKey
                    verify(exactly = 1) { buttons[firstButtonKey]?.onNavigate(Direction.UP, 0.0f) }
                    verify(exactly = 0) { buttons[secondButtonKey]?.onNavigate(Direction.DOWN, 0.0f) }
                    verify(exactly = 0) { buttons[secondButtonKey]?.onSelect(0.0f) }
                    onAnyMovement shouldBe true
                }

                it("should call button 2 navigation") {
                    // given
                    direction = Direction.DOWN
                    menuScreen.currentButtonKey = secondButtonKey

                    // when
                    menuScreen.render(0.0f)

                    // then
                    menuScreen.selectionMade shouldBe false
                    menuScreen.currentButtonKey shouldBe firstButtonKey
                    verify(exactly = 1) { buttons[secondButtonKey]?.onNavigate(Direction.DOWN, 0.0f) }
                    verify(exactly = 0) { buttons[firstButtonKey]?.onNavigate(Direction.UP, 0.0f) }
                    verify(exactly = 0) { buttons[secondButtonKey]?.onSelect(0.0f) }
                    onAnyMovement shouldBe true
                }

                it("should make selection") {
                    // if
                    selectionRequested = true

                    // when
                    menuScreen.render(0.0f)

                    // then
                    menuScreen.selectionMade shouldBe true
                    menuScreen.currentButtonKey shouldBe firstButtonKey
                    verify(exactly = 0) { buttons[firstButtonKey]?.onNavigate(any(), 0.0f) }
                    verify(exactly = 1) { buttons[firstButtonKey]?.onSelect(0.0f) }
                    verify(exactly = 0) { buttons[secondButtonKey]?.onSelect(0.0f) }
                    onAnyMovement shouldBe false
                }
            }
        }
    })
