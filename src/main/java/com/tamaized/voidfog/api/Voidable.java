package com.tamaized.voidfog.api;

import com.tamaized.voidfog.VoidFog;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * These are the defaults.
 *
 * You can choose to implement this interface and override any of these methods
 * to change how Void Fog interacts with your modded dimension.
 */
public interface Voidable {

    default int getDepthParticleRate(BlockPos pos) {
        return pos.getY();
    }

    default boolean hasInsanity(BlockPos pos, World world) {
        return pos.getY() <= 10 || world.isNight();
    }

    default boolean hasDepthFog(Entity entity, World world) {

        if (entity.isSpectator() || (
                   VoidFog.config.disableInCreative
                && entity instanceof PlayerEntity
                && ((PlayerEntity)entity).isCreative())) {
            return false;
        }

        return world.isClient
            && !entity.isSubmergedInWater()
            && ((ClientWorld)world).getLevelProperties().getSkyDarknessHeight(world) != 0
            && !world.getDimension().hasCeiling();
    }

    default boolean isVoidFogDisabled(Entity player, World world) {
        return !hasDepthFog(player, world);
    }
}
