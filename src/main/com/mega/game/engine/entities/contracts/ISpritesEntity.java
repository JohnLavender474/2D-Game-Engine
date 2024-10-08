package com.mega.game.engine.entities.contracts;

import com.badlogic.gdx.utils.ObjectMap;
import com.mega.game.engine.common.ClassInstanceUtils;
import com.mega.game.engine.common.interfaces.UpdateFunction;
import com.mega.game.engine.entities.IGameEntity;
import com.mega.game.engine.drawables.sprites.GameSprite;
import com.mega.game.engine.drawables.sprites.SpritesComponent;
import kotlin.reflect.KClass;

/**
 * Interface for entities that have sprites.
 */
public interface ISpritesEntity extends IGameEntity {

    /**
     * Get the sprites component.
     *
     * @return The sprites component.
     */
    default SpritesComponent getSpritesComponent() {
        KClass<SpritesComponent> key = ClassInstanceUtils.convertToKClass(SpritesComponent.class);
        return getComponent(key);
    }

    /**
     * Get the sprites.
     *
     * @return The sprites.
     */
    default ObjectMap<String, GameSprite> getSprites() {
        return getSpritesComponent().getSprites();
    }

    /**
     * Get the first sprite.
     *
     * @return The first sprite.
     */
    default GameSprite getFirstSprite() {
        return getSprites().iterator().next().value;
    }

    /**
     * Get the sprite by key.
     *
     * @param key            The key.
     * @param updateFunction The update function.
     */
    default void putUpdateFunction(String key, UpdateFunction<GameSprite> updateFunction) {
        getSpritesComponent().putUpdateFunction(key, updateFunction);
    }
}
