package com.engine.damage

import com.engine.entities.GameEntity
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.*

class DamageSystemTest :
    DescribeSpec({
      describe("DamageSystem") {
        lateinit var damageSystem: DamageSystem
        lateinit var entity: GameEntity
        lateinit var damageable: Damageable
        lateinit var damager: Damager
        lateinit var damagerComponent: DamagerComponent
        lateinit var damageableComponent: DamageableComponent

        var takeDamageFrom = true
        var canDamage = true

        beforeEach {
          clearAllMocks()
          damageSystem = DamageSystem()
          entity = GameEntity()
          damageable = mockk { every { takeDamageFrom(any()) } answers { takeDamageFrom } }
          damager = mockk {
            every { canDamage(any()) } answers { canDamage }
            every { onDamageInflictedTo(any()) } just Runs
          }
          damagerComponent = DamagerComponent(damager)
          damageableComponent = DamageableComponent(damageable)
          damageableComponent.damagers.add(damagerComponent)
          entity.addComponent(damageableComponent)
          damageSystem.add(entity)
        }

        it("should call when a Damager damages a Damageable") {
          // if
          takeDamageFrom = true
          canDamage = true

          // when
          damageSystem.update(1f)

          // then
          verify(exactly = 1) { damager.canDamage(damageable) }
          verify(exactly = 1) { damager.onDamageInflictedTo(damageable) }
          verify(exactly = 1) { damageable.takeDamageFrom(damager) }
        }

        it("should not call when Damager cannot damage Damageable") {
          // if
          takeDamageFrom = true
          canDamage = false

          // when
          damageSystem.update(1f)

          // then
          verify(exactly = 1) { damager.canDamage(damageable) }
          verify(exactly = 0) { damager.onDamageInflictedTo(damageable) }
          verify(exactly = 0) { damageable.takeDamageFrom(damager) }
        }

        it("should not call onDamageInflictedTo when Damageable doesn't take damage") {
          // if
          takeDamageFrom = false
          canDamage = true

          // when
          damageSystem.update(1f)

          // then
          verify(exactly = 1) { damager.canDamage(damageable) }
          verify(exactly = 0) { damager.onDamageInflictedTo(damageable) }
          verify(exactly = 1) { damageable.takeDamageFrom(damager) }
        }

        it("should not call onDamageInflictedTo when the system is off") {
          // if
          damageSystem.on = false
          takeDamageFrom = true
          canDamage = true

          // when
          damageSystem.update(1f)

          // then
          verify(exactly = 0) { damager.canDamage(damageable) }
          verify(exactly = 0) { damager.onDamageInflictedTo(damageable) }
          verify(exactly = 0) { damageable.takeDamageFrom(damager) }
        }
      }
    })
