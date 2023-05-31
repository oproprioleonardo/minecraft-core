package com.leonardo.minecraft.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leonardo.minecraft.core.api.uuid.UUIDProvider;
import com.leonardo.minecraft.core.internal.uuid.CachedOkHttpUUIDProvider;
import fr.minuskube.inv.InventoryManager;
import okhttp3.OkHttpClient;
import org.bukkit.plugin.java.JavaPlugin;

public class Core extends JavaPlugin {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final OkHttpClient client = new OkHttpClient.Builder().retryOnConnectionFailure(true).build();
    private UUIDProvider uuidProvider;
    private InventoryManager inventoryManager;

    public Gson getGson() {
        return this.gson;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public UUIDProvider getUuidProvider() {
        return this.uuidProvider;
    }

    public OkHttpClient getClient() {
        return this.client;
    }

    @Override
    public void onEnable() {
        this.uuidProvider = new CachedOkHttpUUIDProvider(this);
        this.inventoryManager = new InventoryManager(this);
        this.inventoryManager.init();
    }
}
