package com.backpackinv.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

/** Настройки лежат в файле config/backpackinv.json. Перечитываются при каждом открытии инвентаря. */
public class BackpackInvConfig {
    public boolean enabled = true;

    // длительность появления / убирания рюкзака в тиках (20 тиков = 1 секунда)
    public int raiseTicks = 12;
    public int lowerTicks = 8;

    // наклон рюкзака: true = как у карты в ванили (зависит от взгляда), false = фиксированный
    public boolean useVanillaTilt = false;
    public float fixedTilt = 0.35f;

    // положение и размер рюкзака относительно рук
    public float scale = 1.5f;
    public float offsetX = 0.0f;
    public float offsetY = 0.05f;
    public float offsetZ = 0.0f;
    public float rotX = 0.0f;
    public float rotY = 0.0f;
    public float rotZ = 0.0f;

    public static BackpackInvConfig load() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Path path = FabricLoader.getInstance().getConfigDir().resolve("backpackinv.json");
        try {
            if (Files.exists(path)) {
                try (Reader reader = Files.newBufferedReader(path)) {
                    BackpackInvConfig loaded = gson.fromJson(reader, BackpackInvConfig.class);
                    if (loaded != null) {
                        return loaded;
                    }
                }
            }
            BackpackInvConfig def = new BackpackInvConfig();
            Files.writeString(path, gson.toJson(def));
            return def;
        } catch (Exception e) {
            e.printStackTrace();
            return new BackpackInvConfig();
        }
    }
}
