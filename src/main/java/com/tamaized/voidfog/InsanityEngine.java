package com.tamaized.voidfog;

import com.tamaized.voidfog.api.Voidable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InsanityEngine {
    private int timeToNextSound = 0;
    private int insanity;

    private final SoundEvent[] events = new SoundEvent[] {
            SoundEvents.ENTITY_POLAR_BEAR_WARNING,
            SoundEvents.AMBIENT_CAVE,
            SoundEvents.ENTITY_CREEPER_PRIMED,
            SoundEvents.ENTITY_ZOMBIE_DESTROY_EGG,
            SoundEvents.BLOCK_CHEST_CLOSE,
            SoundEvents.UI_TOAST_IN,
            SoundEvents.BLOCK_COMPOSTER_READY,
            SoundEvents.BLOCK_METAL_STEP,
            SoundEvents.UI_BUTTON_CLICK,
            SoundEvents.ENTITY_ZOGLIN_ANGRY,
            SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON,
            SoundEvents.ENTITY_ZOMBIE_STEP
    };

    public void update(World world, Entity entity, Voidable dimension) {

        if (!dimension.hasInsanity(entity.getBlockPos(), world)) {
            return;
        }

        int chance = 1 + Math.abs(100 * (int)entity.getY());
        float brightness = entity.getBrightnessAtEyes();

        if (brightness <= 0.3F) {
            if (timeToNextSound-- > 0) {
                return;
            }

            insanity++;
            timeToNextSound = world.random.nextInt(Math.max(1, 20 + chance / (3 + insanity)));

            if (world.random.nextInt((int)Math.max(40, 60 + chance * 3 * brightness - insanity)) == 0) {
                doAScary(world, entity.getBlockPos());
            }
        } else {
            insanity = 0;
        }
    }

    private void doAScary(World world, BlockPos pos) {
        SoundEvent event = events[world.random.nextInt(events.length)];
        float pitch = 1 + world.random.nextFloat();
        world.playSound(MinecraftClient.getInstance().player, pos, event, SoundCategory.AMBIENT, 2, pitch);
    }
}
