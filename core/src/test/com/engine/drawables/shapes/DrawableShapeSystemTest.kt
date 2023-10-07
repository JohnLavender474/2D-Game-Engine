package com.engine.drawables.shapes

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.utils.Array
import com.engine.common.objects.ImmutableCollection
import com.engine.entities.GameEntity
import com.engine.entities.IGameEntity
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.*

class DrawableShapeSystemTest :
    DescribeSpec({
      describe("DrawableShapeSystem") {
        lateinit var shapeRenderer: ShapeRenderer
        lateinit var mockDrawableShape: IDrawableShape
        lateinit var mockDrawableShapeHandle: DrawableShapeHandle
        lateinit var mockDrawableShapeComponent: DrawableShapeComponent
        lateinit var mockEntity: IGameEntity
        lateinit var drawableShapeSystem: DrawableShapeSystem

        beforeEach {
          clearAllMocks()
          shapeRenderer = mockk {
            every { begin(any()) } just Runs
            every { end() } just Runs
            every { rect(any(), any(), any(), any()) } just Runs
          }
          mockDrawableShape = IDrawableShape { it.rect(0.0f, 0.0f, 0.0f, 0.0f) }
          mockDrawableShapeHandle = spyk(DrawableShapeHandle(mockDrawableShape, ShapeType.Line))
          val array = Array<DrawableShapeHandle>()
          array.add(mockDrawableShapeHandle)
          mockEntity = GameEntity(mockk())
          mockDrawableShapeComponent = DrawableShapeComponent(mockEntity, array)
          mockEntity.addComponent(mockDrawableShapeComponent)
          drawableShapeSystem = DrawableShapeSystem(shapeRenderer)
        }

        it("should process entities correctly when 'on' is true") {
          // if
          val entities = ImmutableCollection(listOf(mockEntity))

          // when
          drawableShapeSystem.process(true, entities, 0.0f)

          // then
          verify(exactly = 1) {
            shapeRenderer.begin(any())
            shapeRenderer.end()
            shapeRenderer.rect(any(), any(), any(), any())
          }
        }

        it("should not process entities when 'on' is false") {
          // if
          val entities = ImmutableCollection(listOf(mockEntity))

          // when
          drawableShapeSystem.process(false, entities, 0.0f)

          // then
          verify(exactly = 0) {
            shapeRenderer.begin(any())
            shapeRenderer.end()
            shapeRenderer.rect(any(), any(), any(), any())
          }
        }

        it("should correctly order entities") {
          // if
          val otherMockDrawableShapeHandle =
              spyk(DrawableShapeHandle(mockDrawableShape, ShapeType.Point))
          val array = Array<DrawableShapeHandle>()
          array.add(otherMockDrawableShapeHandle)
          val otherMockEntity = GameEntity(mockk())
          val otherMockDrawableShapeComponent = DrawableShapeComponent(otherMockEntity, array)
          otherMockEntity.addComponent(otherMockDrawableShapeComponent)
          val entities = ImmutableCollection(listOf(mockEntity, otherMockEntity))

          // when
          drawableShapeSystem.process(true, entities, 0.0f)

          // then
          verify(exactly = 1) {
            mockDrawableShapeHandle.draw(any())
            otherMockDrawableShapeHandle.draw(any())
          }
          verify(exactly = 2) {
            shapeRenderer.begin(any())
            shapeRenderer.end()
            shapeRenderer.rect(any(), any(), any(), any())
          }
        }
      }
    })
