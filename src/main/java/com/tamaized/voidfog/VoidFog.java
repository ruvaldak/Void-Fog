package com.tamaized.voidfog;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tamaized.voidfog.config.ConfigHandler;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

public class VoidFog implements ClientModInitializer {

	public final static String VERSION = "${version}";
	public static final String MOD_ID = "voidfog";

	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public static final FogParticleSpawner particleSpawner = new FogParticleSpawner();
	public static final FogColor fogColor = new FogColor();
	public static final FogRenderer renderer = new FogRenderer();

	public static ConfigHandler config = new ConfigHandler();

    @Override
    public void onInitializeClient() {
        config = ConfigHandler.load(FabricLoader.getInstance().getConfigDirectory().toPath().resolve("voidfog.json"));
        ClientTickCallback.EVENT.register(this::onTick);
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
