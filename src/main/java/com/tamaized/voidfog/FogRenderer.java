package com.tamaized.voidfog;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tamaized.voidfog.api.Voidable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.BackgroundRenderer.FogType;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class FogRenderer {

    private float lastFogDistance = 1000;

    public void render(Camera camera,  FogType type, float viewDistance, boolean isThick) {

        if (!VoidFog.config.enabled) {
            return;
        }

        Entity entity = camera.getFocusedEntity();
        World world = entity.getEntityWorld();
        Voidable voidable = (Voidable)world.getDimension();

        if (!voidable.hasDepthFog(entity, world)) {
            return;
        }

        float distance = getFogDistance(world, entity);

        float delta = MinecraftClient.getInstance().getTickDelta();

        distance = MathHelper.lerp(delta / (distance > lastFogDistance ? 20 : 2), lastFogDistance, distance);

        lastFogDistance = distance;

        RenderSystem.fogMode(GlStateManager.FogMode.LINEAR);
        RenderSystem.fogStart(getFogStart(distance, type, world, isThick));
        RenderSystem.fogEnd(getFogEnd(distance, type, world, isThick));
        RenderSystem.setupNvFogDistance();

        RenderSystem.enableColorMaterial();
        RenderSystem.enableFog();
        RenderSystem.colorMaterial(1028, 4608);
    }

    private int getLightLevelU(Entity entity) {
        if (VoidFog.config.respectTorches) {
            return entity.world.getLightLevel(entity.getSenseCenterPos());
        }
        return entity.world.getLightLevel(LightType.SKY, entity.getSenseCenterPos());
    }

    private double getLightLevelV(Voidable voidable, World world, Entity entity) {
        return voidable.isVoidFogDisabled(entity, world) ? 15 : (entity.getY() + 4);
    }

    private float getFogDistance(World world, Entity entity) {
        Voidable voidable = (Voidable)world.getDimension();

        float viewDistance = MinecraftClient.getInstance().gameRenderer.getViewDistance();
        double maxHeight = 32 * (world.getDifficulty().getId() + 1);
        double fogDistance = getLightLevelU(entity) / 16D
                           + getLightLevelV(voidable, world, entity) / maxHeight;

        if (fogDistance >= 1) {
            return viewDistance;
        }
        fogDistance = Math.pow(Math.max(fogDistance, 0), 2);

        return (float)Math.min(viewDistance, Math.max(100 * fogDistance, 5));
    }

    private float getFogStart(float intensity, FogType type, World world, boolean loc) {
        if (loc) {
            return intensity * 0.05F;
        }

        if (type == FogType.FOG_SKY) {
            return 0;
        }

        return intensity * 0.75F;
    }

    private float getFogEnd(float intensity, FogType type, World world, boolean loc) {
        if (loc) {
            return Math.min(intensity, 192) / 2F;
        }

        return intensity;
    }
}
