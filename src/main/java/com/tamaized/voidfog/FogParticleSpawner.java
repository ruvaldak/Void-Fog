package com.tamaized.voidfog;

import java.util.Random;

import com.tamaized.voidfog.api.Voidable;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FogParticleSpawner {

    private static final int RADIUS = 16;

    private int timeToNextSound = 0;

    private final SoundEvent[] events = new SoundEvent[] {
            SoundEvents.ENTITY_POLAR_BEAR_WARNING,
            SoundEvents.AMBIENT_CAVE,
            SoundEvents.ENTITY_CREEPER_PRIMED,
            SoundEvents.ENTITY_ZOMBIE_DESTROY_EGG,
            SoundEvents.BLOCK_CHEST_CLOSE,
            SoundEvents.UI_TOAST_IN,
            SoundEvents.BLOCK_COMPOSTER_READY,
            SoundEvents.BLOCK_METAL_STEP,
            SoundEvents.UI_BUTTON_CLICK
    };

    private BlockPos randomPos(Random rand) {
        return new BlockPos(rand.nextInt(RADIUS), rand.nextInt(RADIUS), rand.nextInt(RADIUS));
    }

    public void update(World world, Entity entity) {
        Voidable voidable = (Voidable)world.getDimension();

        if (!voidable.hasDepthFog(entity, world)) {
            return;
        }

        BlockPos playerPos = entity.getBlockPos();

        Random rand = world.getRandom();

        for (int pass = 0; pass < VoidFog.config.voidParticleDensity; pass++) {
            BlockPos pos = randomPos(rand).subtract(randomPos(rand)).add(playerPos);
            BlockState state = world.getBlockState(pos);

            if (state.isAir()) {
                if (rand.nextInt(8 * (world.getDifficulty().getId() + 1)) > voidable.getDepthParticleRate(pos)) {
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

    public void updateBigBoi(World world, Entity entity) {

        Voidable voidable = (Voidable)world.getDimension();

        if (!voidable.hasDepthFog(entity, world)) {
            return;
        }

        if (entity.getY() > 10) {
            return;
        }

        int chance = 1 + Math.abs(100 * (int)entity.getY());
        float brightness = entity.getBrightnessAtEyes();

        if (brightness <= 0.3F) {
            if (timeToNextSound-- > 0) {
                return;
            }

            timeToNextSound = world.random.nextInt(20 + chance / 3);

            if (world.random.nextInt((int)(100 + chance * 3 * brightness)) == 0) {
                doAScary(world, entity.getBlockPos());
            }
        }
    }

    private void doAScary(World world, BlockPos pos) {
        SoundEvent event = events[world.random.nextInt(events.length)];
        world.playSound(MinecraftClient.getInstance().player, pos, event, SoundCategory.AMBIENT, 2, 1);
    }
}







