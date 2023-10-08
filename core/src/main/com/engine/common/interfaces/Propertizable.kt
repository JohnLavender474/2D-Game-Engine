package com.engine.common.interfaces

import com.badlogic.gdx.utils.ObjectMap
import com.engine.common.objects.Properties
import kotlin.reflect.KClass

/**
 * An interface for objects that can have properties.
 *
 * @see Properties
 */
interface Propertizable {

  /** The [Properties] of this object. */
  val properties: Properties

  /**
   * Puts a property into this object's [Properties].
   *
   * @param key The key of the property.
   * @param p The property.
   * @return The old property with the given key, or null if there was no prior property.
   */
  fun putProperty(key: String, p: Any?) = properties.put(key, p)

  /**
   * Puts a property into this object's [Properties]. The key of the property is the given object's
   * [toString] value.
   *
   * @param key The key of the property.
   * @param p The property.
   * @return The old property with the given key, or null if there was no prior property.
   */
  fun putProperty(key: Any, p: Any?) = properties.put(key.toString(), p)

  /**
   * Puts all the properties from the given [HashMap] into this object's [Properties].
   *
   * @param p The [HashMap] of properties.
   */
  fun putAllProperties(p: ObjectMap<String, Any?>) = properties.putAll(p)

  /**
   * Gets a property from this object's [Properties].
   *
   * @param key The key of the property.
   * @return The property.
   */
  fun getProperty(key: String) = properties[key]

  /**
   * Gets a property from this object's [Properties]. The key of the property is the given object's
   * [toString] value.
   *
   * @param key The key of the property.
   * @return The property.
   */
  fun getProperty(key: Any) = properties[key.toString()]

  /**
   * Gets a property from this object's [Properties] and casts it to the given type.
   *
   * @param key The key of the property.
   * @param type The type to cast the property to.
   * @return The property cast to the given type.
   */
  fun <T : Any> getProperty(key: String, type: KClass<T>) = properties.get(key, type)

  /**
   * Gets a property from this object's [Properties] and casts it to the given type. The key of the
   * property is the given object's [toString] value.
   *
   * @param key The key of the property.
   * @param type The type to cast the property to.
   * @return The property cast to the given type.
   */
  fun <T : Any> getProperty(key: Any, type: KClass<T>) = properties.get(key.toString(), type)

  /**
   * Checks if this object's [Properties] contains a property with the given key.
   *
   * @param key The key of the property.
   * @return True if this object's [Properties] contains a property with the given key, otherwise
   */
  fun hasProperty(key: String) = properties.containsKey(key)

  /**
   * Removes a property from this object's [Properties].
   *
   * @param key The key of the property.
   * @return The property removed.
   */
  fun removeProperty(key: String) = properties.remove(key)

  /** Clears all the properties from this object's [Properties]. */
  fun clearProperties() = properties.clear()
}
