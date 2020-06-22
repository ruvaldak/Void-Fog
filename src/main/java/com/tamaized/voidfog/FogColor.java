package com.tamaized.voidfog;

import com.tamaized.voidfog.api.Voidable;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;

public class FogColor {

    private double prevBrightness;

    public double getFogBrightness(ClientWorld world, Entity entity, float delta) {
        if (entity.hasVehicle()) {
            entity = entity.getRootVehicle();
        }

        double brightness = computeBrightness(world, entity, delta);
        double lerped = MathHelper.lerp(delta / (brightness > prevBrightness ? 10 : 2), prevBrightness, brightness);
        prevBrightness = brightness;
        return lerped;
    }

    private double computeBrightness(ClientWorld world, Entity entity, float delta) {

        if (!VoidFog.config.enabled) {
            return 1;
        }

        Voidable voidable = (Voidable)world.getDimension();

        if (voidable.isVoidFogDisabled(entity, world)) {
            return 1;
        }

        double yPosition = MathHelper.lerp(delta, entity.prevY, entity.getY());
        double brightness = yPosition * world.getLevelProperties().getHorizonShadingRatio();

        if (brightness >= 1) {
            return 1;
        }

        float light = entity.world.getLightLevel(LightType.SKY, entity.getBlockPos()) / 15F;

        brightness *= light;

        return Math.pow(Math.max(0, brightness), 3);
    }
}
