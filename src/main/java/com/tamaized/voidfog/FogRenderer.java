package com.tamaized.voidfog;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tamaized.voidfog.api.Voidable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.BackgroundRenderer.FogType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class FogRenderer {

    private float lastFogDistance = 1000;

    public void render(Camera camera,  FogType type, float viewDistance, boolean thickFog, float delta) {

        if (!canRenderDepthFog(camera)) {
            return;
        }

        Entity entity = camera.getFocusedEntity();

        if (entity.hasVehicle()) {
            entity = entity.getRootVehicle();
        }

        World world = entity.getEntityWorld();
        Voidable voidable = Voidable.of(world);

        if (!voidable.hasDepthFog(entity, world)) {
            return;
        }

        float distance = getFogDistance(world, entity);

        if (entity instanceof LivingEntity && ((LivingEntity)entity).hasStatusEffect(StatusEffects.NIGHT_VISION)) {
            distance *= 4 * GameRenderer.getNightVisionStrength((LivingEntity)entity, delta);
        }

        distance = MathHelper.lerp(.05F, lastFogDistance, distance);

        lastFogDistance = distance;

        RenderSystem.setShaderFogStart(Math.min(RenderSystem.getShaderFogStart(), getFogStart(distance, type, world, thickFog)));
        RenderSystem.setShaderFogEnd(Math.min(RenderSystem.getShaderFogEnd(), getFogEnd(distance, type, world, thickFog)));
    }

    private boolean canRenderDepthFog(Camera camera) {
        return VoidFog.config.enabled
                && camera.getSubmersionType() == CameraSubmersionType.NONE
                && !(camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).hasStatusEffect(StatusEffects.BLINDNESS));
    }

    private int getLight(Entity entity) {
        if (VoidFog.config.respectTorches) {
            return entity.world.getLightLevel(entity.getBlockPos());
        }
        return entity.world.getLightLevel(LightType.SKY, entity.getBlockPos());
    }

    private double getAltitude(Voidable voidable, World world, Entity entity) {
        return voidable.isVoidFogDisabled(entity, world) ? 15 : (entity.getY() - world.getBottomY());
    }

    private float getFogDistance(World world, Entity entity) {
        Voidable voidable = Voidable.of(world);

        float viewDistance = MinecraftClient.getInstance().gameRenderer.getViewDistance();
        double maxHeight = (VoidFog.config.scaleWithDifficulty) ? VoidFog.config.maxFogHeight * (world.getDifficulty().getId() + 1)
                                                                : VoidFog.config.maxFogHeight;
        double fogDistance = getLight(entity) / 14D
                           + getAltitude(voidable, world, entity) / maxHeight;

        if (fogDistance >= 1) {
            return viewDistance;
        }
        //fogDistance = Math.pow(Math.max(fogDistance, 0), 2);

        return (float)MathHelper.clamp(100 * fogDistance, 5, viewDistance);
    }

    private float getFogStart(float distance, FogType type, World world, boolean thickFog) {
        if (type == FogType.FOG_SKY) {
            return 0;
        }

        if (thickFog) {
            return distance * 0.05F;
        }

        float factor = (distance - 16F) / 10F;

        return distance * Math.max(0, factor);
    }

    private float getFogEnd(float distance, FogType type, World world, boolean thickFog) {
        return thickFog ? Math.min(distance, 192) / 2F : distance;
    }
}
