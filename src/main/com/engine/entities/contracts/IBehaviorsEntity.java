package com.engine.entities.contracts;

import com.engine.behaviors.AbstractBehavior;
import com.engine.behaviors.BehaviorsComponent;
import com.engine.common.ClassInstanceUtils;
import com.engine.entities.IGameEntity;
import kotlin.jvm.functions.Function2;
import kotlin.reflect.KClass;

import java.util.stream.StreamSupport;

/**
 * An interface for IGameEntity that have AbstractBehaviors.
 *
 * @see com.engine.behaviors.BehaviorsComponent
 */
public interface IBehaviorsEntity extends IGameEntity {

    /**
     * Returns the BehaviorsComponent of this IBehaviorsEntity.
     *
     * @return The BehaviorsComponent of this IBehaviorsEntity.
     */
    default BehaviorsComponent getBehaviorsComponent() {
        KClass<BehaviorsComponent> key = ClassInstanceUtils.convertToKClass(BehaviorsComponent.class);
        return getComponent(key);
    }

    /**
     * Returns if the AbstractBehavior with the given key is active.
     *
     * @param key The key of the AbstractBehavior to check.
     * @return If the AbstractBehavior with the given key is active.
     */
    default boolean isBehaviorActive(Object key) {
        return getBehaviorsComponent().isBehaviorActive(key);
    }

    /**
     * Returns if any of the AbstractBehaviors with the given keys are active.
     *
     * @param keys The keys of the AbstractBehaviors to check.
     * @return If any of the AbstractBehaviors with the given keys are active.
     */
    default boolean isAnyBehaviorActive(Object... keys) {
        return isAnyBehaviorActive(java.util.Arrays.asList(keys));
    }

    /**
     * Returns if any of the AbstractBehaviors with the given keys are active.
     *
     * @param keys The keys of the AbstractBehaviors to check.
     * @return If any of the AbstractBehaviors with the given keys are active.
     */
    default boolean isAnyBehaviorActive(Iterable<Object> keys) {
        return StreamSupport.stream(keys.spliterator(), false)
                .anyMatch(this::isBehaviorActive);
    }

    /**
     * Returns if all the AbstractBehaviors with the given keys are active.
     *
     * @param keys The keys of the AbstractBehaviors to check.
     * @return If all the AbstractBehaviors with the given keys are active.
     */
    default boolean areAllBehaviorsActive(Object... keys) {
        return areAllBehaviorsActive(java.util.Arrays.asList(keys));
    }

    /**
     * Returns if all the AbstractBehaviors with the given keys are active.
     *
     * @param keys The keys of the AbstractBehaviors to check.
     * @return If all the AbstractBehaviors with the given keys are active.
     */
    default boolean areAllBehaviorsActive(Iterable<Object> keys) {
        return StreamSupport.stream(keys.spliterator(), false)
                .allMatch(this::isBehaviorActive);
    }

    /**
     * Forces the AbstractBehavior with the given key to quit. This will force the AbstractBehavior to disregard
     * the result of its evaluate method for one update cycle and end the AbstractBehavior.
     *
     * @param key The key of the AbstractBehavior to force quit.
     */
    default void forceQuitBehavior(Object key) {
        getBehaviorsComponent().forceQuitBehavior(key);
    }

    /**
     * Returns if the behavior is allowed.
     *
     * @param key the key of the behavior
     * @return if the behavior is allowed
     */
    default boolean isBehaviorAllowed(Object key) {
        return getBehaviorsComponent().isBehaviorAllowed(key);
    }

    /**
     * Sets if the behavior should be allowed.
     *
     * @param key     the key of the behavior
     * @param allowed if the behavior should be allowed
     */
    default void setBehaviorAllowed(Object key, boolean allowed) {
        getBehaviorsComponent().setBehaviorAllowed(key, allowed);
    }

    /**
     * Sets if behaviors should be allowed based on the function.
     *
     * @param function the function
     */
    default void setBehaviorsAllowed(Function2<Object, AbstractBehavior, Boolean> function) {
        getBehaviorsComponent().setBehaviorsAllowed(function);
    }

    /**
     * Sets if the behaviors with the given keys should be allowed.
     *
     * @param keys    the keys of the behaviors
     * @param allowed if the behaviors should be allowed
     */
    default void setBehaviorsAllowed(Iterable<Object> keys, boolean allowed) {
        getBehaviorsComponent().setBehaviorsAllowed(keys, allowed);
    }

    /**
     * Sets if all behaviors should be allowed.
     *
     * @param allowed if all behaviors should be allowed
     */
    default void setAllBehaviorsAllowed(boolean allowed) {
        getBehaviorsComponent().setAllBehaviorsAllowed(allowed);
    }
}

