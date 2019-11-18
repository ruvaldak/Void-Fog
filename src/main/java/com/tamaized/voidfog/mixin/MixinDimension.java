package com.tamaized.voidfog.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.tamaized.voidfog.api.Voidable;

import net.minecraft.world.dimension.Dimension;

@Mixin(Dimension.class)
abstract class MixinDimension implements Voidable {

}
