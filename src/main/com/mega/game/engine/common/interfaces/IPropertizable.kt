package com.mega.game.engine.common.interfaces

import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.Predicate
import com.mega.game.engine.common.objects.Properties
import kotlin.reflect.KClass

/**
 * An interface for objects that can have properties.
 *
 * @see Properties
 */
interface IPropertizable {

    /** The [Properties] of this object. */
    val properties: Properties

    /**
     * Puts a property into this object's [Properties].
     *
     * @param key The key of the property.
     * @param p The property.
     * @return The old property with the given key, or null if there was no prior property.
     */
    fun putProperty(key: Any, p: Any?) = properties.put(key, p)

    /**
     * Puts all the properties from the given [HashMap] into this object's [Properties].
     *
     * @param p The [HashMap] of properties.
     */
    fun putAllProperties(p: ObjectMap<Any, Any?>) = properties.putAll(p)

    /**
     * Put all the properties from the given [Properties] into this object's [Properties].
     *
     * @param p The [Properties] of properties.
     */
    fun putAllProperties(p: Properties) = properties.putAll(p)

    /**
     * Gets a property from this object's [Properties]. The key of the property is the given object's
     * [toString] value.
     *
     * @param key The key of the property.
     * @return The property.
     */
    fun getProperty(key: Any) = properties.get(key)

    /**
     * Gets all the properties from this object's [Properties] where the key matches the given predicate.
     *
     * @param keyPredicate The predicate to match the keys.
     * @return The properties that match the predicate.
     */
    fun getAllMatchingProperties(keyPredicate: Predicate<Any>) = properties.getAllMatching(keyPredicate)

    /**
     * Gets all the properties from this object's [Properties] where the key matches the given predicate.
     *
     * @param keyPredicate The predicate to match the keys.
     * @return The properties that match the predicate.
     */
    fun getAllMatchingProperties(keyPredicate: (Any) -> Boolean) = properties.getAllMatching(keyPredicate)

    /**
     * Gets a property from this object's [Properties] and casts it to the given type.
     *
     * @param key The key of the property.
     * @param type The type to cast the property to.
     * @return The property cast to the given type.
     */
    fun <T : Any> getProperty(key: Any, type: KClass<T>) = properties.get(key, type)

    /**
     * Gets a property from this object's [Properties] and casts it to the given type.
     *
     * @param key The key of the property.
     * @param type The type to cast the property to.
     * @return The property cast to the given type.
     */
    fun <T : Any> getProperty(key: Any, type: Class<T>) = properties.get(key, type)

    /**
     * Gets a property from this object's [Properties] and returns the given default if the property is not found.
     * The key of the property is the given object's [toString] value.
     *
     * @param key The key of the property.
     * @param default The default value to return if the property is not found.
     * @return The property, or the given default if the property is not found.
     */
    fun getOrDefaultProperty(key: Any, default: Any) = properties.getOrDefault(key, default)

    /**
     * Gets a property from this object's [Properties] and returns the given default if the property is not found.
     * The key of the property is the given object's [toString] value.
     *
     * @param key The key of the property.
     * @param default The default value to return if the property is not found.
     * @param type The KClass type to cast the property to.
     * @return The property, or the given default if the property is not found.
     */
    fun <T : Any> getOrDefaultProperty(key: Any, default: T, type: KClass<T>) =
        properties.getOrDefault(key, default, type)

    /**
     * Gets a property from this object's [Properties] and returns the given default if the property is not found.
     *
     * @param key The key of the property.
     * @param default The default value to return if the property is not found.
     * @param type The type to cast the property to.
     */
    fun <T : Any> getOrDefaultProperty(key: Any, default: T, type: Class<T>): T =
        properties.getOrDefault(key, default, type)

    /**
     * Checks if this object's [Properties] contains a property with the given key.
     *
     * @param key The key of the property.
     * @return True if this object's [Properties] contains a property with the given key, otherwise
     */
    fun hasProperty(key: Any) = properties.containsKey(key)

    /**
     * Checks if this object's [Properties] contains a property with the given key and value.
     *
     * @param key The key of the property.
     * @param value The value of the property.
     * @return True if this object's [Properties] contains a property with the given key and value, otherwise
     */
    fun isProperty(key: Any, value: Any) = properties.isProperty(key, value)

    /**
     * Removes a property from this object's [Properties].
     *
     * @param key The key of the property.
     * @return The property removed.
     */
    fun removeProperty(key: Any) = properties.remove(key)

    /** Clears all the properties from this object's [Properties]. */
    fun clearProperties() = properties.clear()
}
