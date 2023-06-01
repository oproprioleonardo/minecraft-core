package com.leonardo.minecraft.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leonardo.minecraft.core.api.database.ConnectionProvider;
import com.leonardo.minecraft.core.api.uuid.UUIDProvider;
import com.leonardo.minecraft.core.config.Configuration;
import com.leonardo.minecraft.core.config.DatabaseConfig;
import com.leonardo.minecraft.core.config.DatabaseName;
import com.leonardo.minecraft.core.internal.database.HikariMysqlConnectionProvider;
import com.leonardo.minecraft.core.internal.database.HikariPostgresConnectionProvider;
import com.leonardo.minecraft.core.internal.uuid.CachedOkHttpUUIDProvider;
import fr.minuskube.inv.InventoryManager;
import okhttp3.OkHttpClient;
import org.bukkit.plugin.java.JavaPlugin;

public class Core extends JavaPlugin {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final OkHttpClient client = new OkHttpClient.Builder().retryOnConnectionFailure(true).build();
    private UUIDProvider uuidProvider;
    private InventoryManager inventoryManager;
    private Configuration defaultConfiguration;
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

    public Configuration getDefaultConfiguration() {
        return defaultConfiguration;
    }

    public OkHttpClient getClient() {
        return this.client;
    }


    @Override
    public void onEnable() {
        this.uuidProvider = new CachedOkHttpUUIDProvider(this);
        this.inventoryManager = new InventoryManager(this);
        this.inventoryManager.init();
        this.defaultConfiguration = new Configuration(this, "config");
        this.databaseConfig = this.provideDatabaseConfig();
        this.connectionProvider = this.provideConnectionProvider();
    }

    private DatabaseConfig provideDatabaseConfig() {
        final Configuration cfg = this.defaultConfiguration;
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
