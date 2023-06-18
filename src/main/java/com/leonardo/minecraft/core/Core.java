package com.leonardo.minecraft.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leonardo.minecraft.core.api.database.ConnectionProvider;
import com.leonardo.minecraft.core.api.uuid.UUIDProvider;
import com.leonardo.minecraft.core.config.DatabaseConfig;
import com.leonardo.minecraft.core.config.DatabaseName;
import com.leonardo.minecraft.core.internal.database.HikariMysqlConnectionProvider;
import com.leonardo.minecraft.core.internal.database.HikariPostgresConnectionProvider;
import com.leonardo.minecraft.core.internal.profile.CachedOkHttpUUIDProvider;
import fr.minuskube.inv.InventoryManager;
import okhttp3.OkHttpClient;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Core extends JavaPlugin {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final OkHttpClient client = new OkHttpClient.Builder().retryOnConnectionFailure(true).build();
    private File customConfigFile;
    private FileConfiguration customConfig;
    private UUIDProvider uuidProvider;
    private InventoryManager inventoryManager;
    private DatabaseConfig databaseConfig;
    private ConnectionProvider connectionProvider;


    @Override
    public void onEnable() {
        this.uuidProvider = new CachedOkHttpUUIDProvider(this);
        this.inventoryManager = new InventoryManager(this);
        this.inventoryManager.init();
        this.customConfigFile = new File(getDataFolder(), "config.yml");
        this.customConfig = this.provideConfig(this.customConfigFile);
        this.databaseConfig = this.provideDatabaseConfig(this.customConfig);
        this.connectionProvider = this.provideConnectionProvider(this.databaseConfig);
    }

    private FileConfiguration provideConfig(final File customConfigFile) {
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }
        return YamlConfiguration.loadConfiguration(customConfigFile);
    }

    private DatabaseConfig provideDatabaseConfig(final FileConfiguration cfg) {
        final DatabaseConfig databaseConfig = new DatabaseConfig();
        databaseConfig.setDbname(DatabaseName.valueOf(cfg.getInt("data_access.db_id")));
        databaseConfig.setHost(cfg.getString("data_access.host"));
        databaseConfig.setPassword(cfg.getString("data_access.password"));
        databaseConfig.setUser(cfg.getString("data_access.user"));
        databaseConfig.setDatabase(cfg.getString("data_access.database"));
        return databaseConfig;
    }

    private ConnectionProvider provideConnectionProvider(final DatabaseConfig databaseConfig) {
        return databaseConfig.getDbname() == DatabaseName.POSTGRES ?
                new HikariPostgresConnectionProvider(databaseConfig) : new HikariMysqlConnectionProvider(databaseConfig);
    }

    public File getCustomConfigFile() {
        return customConfigFile;
    }

    public FileConfiguration getCustomConfig() {
        return customConfig;
    }

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

    public OkHttpClient getClient() {
        return this.client;
    }

}
