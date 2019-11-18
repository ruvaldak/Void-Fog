package com.tamaized.voidfog;

import java.util.Random;

import com.tamaized.voidfog.api.Voidable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FogParticleSpawner {

    private static final int RADIUS = 16;
    private static final int MAX_PASSES = 1000;

    private BlockPos randomPos(Random rand) {
        return new BlockPos(rand.nextInt(RADIUS), rand.nextInt(RADIUS), rand.nextInt(RADIUS));
    }

    public void update(World world, Entity entity) {

        if (!VoidFog.config.enabled) {
            return;
        }

        Voidable voidable = (Voidable)world.getDimension();

        if (!voidable.hasDepthFog(entity, world)) {
            return;
        }

        BlockPos playerPos = entity.getBlockPos();

        Random rand = world.getRandom();

        for (int pass = 0; pass < MAX_PASSES; pass++) {
            BlockPos pos = randomPos(rand).subtract(randomPos(rand)).add(playerPos);
            BlockState state = world.getBlockState(pos);

            if (state.isAir()) {
                if (rand.nextInt(8) > voidable.getDepthParticleRate(pos)) {
                    world.addParticle(ParticleTypes.MYCELIUM,
                            pos.getX() + rand.nextFloat(),
                            pos.getY() + rand.nextFloat(),
                            pos.getZ() + rand.nextFloat(),
                            0, 0, 0);
                }
            } else {
                state.getBlock().randomDisplayTick(state, world, pos, rand);
            }
        }
    }

}
