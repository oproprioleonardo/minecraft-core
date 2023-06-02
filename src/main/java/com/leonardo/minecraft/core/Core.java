package com.leonardo.minecraft.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leonardo.minecraft.core.api.database.ConnectionProvider;
import com.leonardo.minecraft.core.api.uuid.UUIDProvider;
import com.leonardo.minecraft.core.config.DatabaseConfig;
import com.leonardo.minecraft.core.config.DatabaseName;
import com.leonardo.minecraft.core.internal.database.HikariMysqlConnectionProvider;
import com.leonardo.minecraft.core.internal.database.HikariPostgresConnectionProvider;
import com.leonardo.minecraft.core.internal.uuid.CachedOkHttpUUIDProvider;
import fr.minuskube.inv.InventoryManager;
import okhttp3.OkHttpClient;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Core extends JavaPlugin {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final OkHttpClient client = new OkHttpClient.Builder().retryOnConnectionFailure(true).build();
    private UUIDProvider uuidProvider;
    private InventoryManager inventoryManager;
    private YamlConfiguration configuration;
    private DatabaseConfig databaseConfig;
    private ConnectionProvider connectionProvider;

    public DatabaseConfig getDatabaseConfig() {
        return databaseConfig;
    }

    public ConnectionProvider getConnectionProvider() {
        return connectionProvider;
    }

    public Gson getGson() {
        return this.gson;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public UUIDProvider getUuidProvider() {
        return this.uuidProvider;
    }

    public YamlConfiguration getConfiguration() {
        return configuration;
    }

    public OkHttpClient getClient() {
        return this.client;
    }


    @Override
    public void onEnable() {
        this.uuidProvider = new CachedOkHttpUUIDProvider(this);
        this.inventoryManager = new InventoryManager(this);
        this.inventoryManager.init();
        this.configuration = this.provideConfiguration();
        this.databaseConfig = this.provideDatabaseConfig(this.configuration);
        this.connectionProvider = this.provideConnectionProvider();
    }
    private YamlConfiguration provideConfiguration() {
        if (!new File(this.getDataFolder(), "config.yml").exists()) {
            this.saveResource("config.yml", false);
        }
        return YamlConfiguration.loadConfiguration(new File(this.getDataFolder() + File.separator + "config.yml"));
    }

    private DatabaseConfig provideDatabaseConfig(YamlConfiguration cfg) {
        final DatabaseConfig databaseConfig = new DatabaseConfig();
        databaseConfig.setDbname(DatabaseName.valueOf(cfg.getInt("data_access.db_id")));
        databaseConfig.setHost(cfg.getString("data_access.host"));
        databaseConfig.setPassword(cfg.getString("data_access.password"));
        databaseConfig.setUser(cfg.getString("data_access.user"));
        databaseConfig.setDatabase(cfg.getString("data_access.database"));
        return databaseConfig;
    }

    private ConnectionProvider provideConnectionProvider() {
        return this.databaseConfig.getDbname() == DatabaseName.POSTGRES ?
                new HikariPostgresConnectionProvider(this.databaseConfig) : new HikariMysqlConnectionProvider(this.databaseConfig);
    }

}
