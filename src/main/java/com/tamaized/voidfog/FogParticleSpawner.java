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

    private BlockPos randomPos(Random rand) {
        return new BlockPos(rand.nextInt(RADIUS), rand.nextInt(RADIUS), rand.nextInt(RADIUS));
    }

    public void update(World world, Entity entity, Voidable dimension) {
        BlockPos playerPos = entity.getBlockPos();

        Random rand = world.getRandom();

        for (int pass = 0; pass < VoidFog.config.voidParticleDensity; pass++) {
            BlockPos pos = randomPos(rand).subtract(randomPos(rand)).add(playerPos);
            BlockState state = world.getBlockState(pos);

            if (state.isAir() && world.getFluidState(pos).isEmpty()) {
                if (rand.nextInt(8 * (world.getDifficulty().getId() + 1)) > dimension.getDepthParticleRate(pos)) {
                    boolean nearBedrock = dimension.isNearBedrock(pos, world);

                    world.addParticle(nearBedrock ? ParticleTypes.ASH : ParticleTypes.MYCELIUM,
                            pos.getX() + rand.nextFloat(),
                            pos.getY() + rand.nextFloat(),
                            pos.getZ() + rand.nextFloat(),
                            0,
                            nearBedrock ? rand.nextFloat() : 0,
                            0);
                }
            }
        }
    }
}







