package com.tamaized.voidfog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;

public class Settings {

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @Expose
	public boolean enabled = true;

	@Expose
	public boolean disableInCreative = true;

	@Expose
	public int voidParticleDensity = 1000;

	@Expose
	public boolean imABigBoi = false;

	public static Settings load(Path path) {
	    if (Files.isReadable(path)) {
            try (BufferedReader s = Files.newBufferedReader(path)) {
                Settings result = gson.fromJson(s, Settings.class);

                if (result != null) {
                    return result.save(path);
                }
            } catch (IOException | JsonParseException e) {
                VoidFog.LOGGER.warn("Erorr whilst loading json config", e);
            }
        }
	    return new Settings().save(path);
	}

	protected void validate() {
	    voidParticleDensity = Math.max(0, voidParticleDensity);
	}

	public Settings save(Path path) {
	    validate();
	    try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            gson.toJson(this, writer);
        } catch (IOException e) {
            VoidFog.LOGGER.warn("Error whilst saving Json config", e);
        }
	    return this;
	}
}
