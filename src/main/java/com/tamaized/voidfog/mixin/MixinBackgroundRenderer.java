package com.tamaized.voidfog.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tamaized.voidfog.VoidFog;

import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.BackgroundRenderer.FogType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;

@Mixin(BackgroundRenderer.class)
abstract class MixinBackgroundRenderer {

    @Shadow
    private static float red;
    @Shadow
    private static float green;
    @Shadow
    private static float blue;

    @Inject(method = "applyFog("
            + "Lnet/minecraft/client/render/Camera;"
            + "Lnet/minecraft/client/render/BackgroundRenderer$FogType;"
            + "F"
            + "Z"
            + "F"
        + ")V",
            at = @At("RETURN"))
    private static void onApplyFog(Camera camera, FogType type, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo info) {
        VoidFog.RENDERER.render(camera, type, viewDistance, thickFog, tickDelta);
    }

    @Inject(method = "render("
            + "Lnet/minecraft/client/render/Camera;"
            + "F"
            + "Lnet/minecraft/client/world/ClientWorld;"
            + "I"
            + "F"
        + ")V",
            at = @At("RETURN"))
    private static void onUpdateColorNotInWater(Camera camera, float tickDelta, ClientWorld world, int renderDistance, float skyDarkness, CallbackInfo info) {
        changeFogColour(VoidFog.FOG_COLOR.getFogBrightness(world, camera.getFocusedEntity(), tickDelta));
    }

    private static void changeFogColour(double factor) {
        red *= factor;
        green *= factor;
        blue *= factor;
        RenderSystem.clearColor(red, green, blue, 0);
    }
}
