package com.tamaized.voidfog;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.minelittlepony.common.util.GamePaths;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class VoidFog implements ClientModInitializer {

	public static final Logger LOGGER = LogManager.getLogger("voidfog");

	public static final FogParticleSpawner particleSpawner = new FogParticleSpawner();
	public static final FogColor fogColor = new FogColor();
	public static final FogRenderer renderer = new FogRenderer();

	public static Settings config = new Settings();

    @Override
    public void onInitializeClient() {
        config = Settings.load(GamePaths.getConfigDirectory().resolve("voidfog.json"));
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    private void onTick(MinecraftClient client) {
        if (!client.isPaused() && client.world != null && client.getCameraEntity() != null) {
            if (config.enabled) {
                particleSpawner.update(client.world, client.getCameraEntity());

                if (config.imABigBoi) {
                    particleSpawner.updateBigBoi(client.world, client.getCameraEntity());
                }
            }
        }
    }
}
