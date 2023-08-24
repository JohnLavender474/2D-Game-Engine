package com.engine.common.shapes

import com.badlogic.gdx.math.Vector2
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class GameLineTest :
    DescribeSpec({
      describe("GameLine class") {
        var point1 = Vector2(1f, 2f)
        var point2 = Vector2(4f, 5f)
        var gameLine = GameLine(point1, point2)

        beforeEach {
          point1 = Vector2(1f, 2f)
          point2 = Vector2(4f, 5f)
          gameLine = GameLine(point1, point2)
        }

        it("should contain a point") {
          val pointInside = Vector2(2f, 3f)
          val pointOutside = Vector2(0f, 0f)

          gameLine.contains(pointInside) shouldBe true
          gameLine.contains(pointOutside) shouldBe false
        }

        it("should contain coordinates") {
          gameLine.contains(2f, 3f) shouldBe true
          gameLine.contains(0f, 0f) shouldBe false
        }

        it("should get the center") { gameLine.getCenter() shouldBe Vector2(2.5f, 3.5f) }

        it("should translate the line") {
          gameLine.translation(1f, 2f)
          gameLine.point1 shouldBe Vector2(2f, 4f)
          gameLine.point2 shouldBe Vector2(5f, 7f)
        }

        it("should get the maximum X value") { gameLine.getMaxX() shouldBe 4f }

        it("should get the maximum Y value") { gameLine.getMaxY() shouldBe 5f }

        it("should check overlaps with other shapes") {
          val rectangle = GameRectangle(2f, 3f, 5f, 6f)
          val overlappingLine = GameLine(Vector2(1f, 5f), Vector2(4f, 2f))
          val nonOverlappingLine = GameLine(Vector2(7f, 8f), Vector2(10f, 11f))

          gameLine.overlaps(rectangle) shouldBe true
          gameLine.overlaps(overlappingLine) shouldBe true
          gameLine.overlaps(nonOverlappingLine) shouldBe false
        }

        it("should get the bounding rectangle") {
          val boundingRectangle = gameLine.getBoundingRectangle()
          boundingRectangle.x shouldBe 1f
          boundingRectangle.y shouldBe 2f
          boundingRectangle.width shouldBe 3f
          boundingRectangle.height shouldBe 3f
        }
      }
    })
