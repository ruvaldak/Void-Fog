package com.tamaized.voidfog.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.tamaized.voidfog.VoidFog;

import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.world.World;

@Mixin(BackgroundRenderer.class)
abstract class MixinBackgroundRenderer {

    @Shadow
    private float red;
    @Shadow
    private float green;
    @Shadow
    private float blue;

    @Inject(method = "applyFog(Lnet/minecraft/client/render/Camera;I)V",
            at = @At("RETURN"))
    public void onApplyFog(Camera camera, int mode, CallbackInfo info) {
        VoidFog.renderer.render(camera, mode);
    }

    @Inject(method = "updateColorNotInWater(Lnet/minecraft/client/render/Camera;Lnet/minecraft/world/World;F)V",
            at = @At("RETURN"))
    private void onUpdateColorNotInWater(Camera camera, World world, float ticks, CallbackInfo info) {
        changeFogColour(VoidFog.fogColor.getFogBrightness(world, camera.getFocusedEntity(), ticks));
    }

    private void changeFogColour(double factor) {
        red *= factor;
        green *= factor;
        blue *= factor;
    }
}
