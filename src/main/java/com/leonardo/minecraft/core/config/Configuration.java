package com.leonardo.minecraft.core.config;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Configuration {

    private final Plugin plugin;
    private final String fileName;
    private final YamlConfiguration config;

    public Configuration(Plugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        if (!new File(plugin.getDataFolder(), this.fileName + ".yml").exists()) {
            plugin.saveResource(this.fileName + ".yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration(new File(this.plugin.getDataFolder() + File.separator + this.fileName + ".yml"));
    }

    public void save() {
        if (getFile().exists()) {
            try {
                this.config.save(getFile());
            } catch (IOException e) {
                e.printStackTrace();
                Bukkit.getConsoleSender().sendMessage("§cCould not save file " + getFile().getName());
            }
        }
    }

    public String get(String path, Boolean color) {
        if (!this.config.contains(path))
            return "";
        if (color)
            return ChatColor.translateAlternateColorCodes('&', this.config.getString(path));
        return this.config.getString(path);
    }

    public String getString(String path) {
        return get(path, false);
    }

    public String getMessage(String message) {
        return get("Messages." + message, true);
    }

    public List<String> getList(String path, Boolean color) {
        if (color) {
            return this.config.getStringList(path).stream().map(s -> s.replaceAll("&", "§")).collect(Collectors.toList());
        }
        return this.config.getStringList(path);
    }

    public Set<String> section(String path) {
        return this.config.getConfigurationSection(path).getKeys(false);
    }

    public Boolean is(String path) {
        return this.config.getBoolean(path);
    }

    public Boolean isDouble(String path) {
        try {
            this.config.getDouble(path);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public Integer getInt(String path) {
        if (has(path)) return this.config.getInt(path);
        else return 0;
    }

    public Double getDouble(String path) {
        if (!has(path)) return 0D;
        return this.config.getDouble(path);
    }

    public Long getLong(String path) {
        if (!has(path)) return 0L;
        return this.config.getLong(path);
    }

    public Float getFloat(String path) {
        return Float.valueOf(this.getObject(path).toString());
    }

    public Object getObject(String path) {
        return this.config.get(path);
    }

    public void set(String path, Object value) {
        this.config.set(path, value);
    }

    public Boolean has(String path) {
        return this.getObject(path) != null;
    }

    public void saveLocationInConfig(String pathName, Location location) {
        final String path = "Locations." + pathName + ".";
        this.set(path + "world", location.getWorld().getName());
        this.set(path + "x", location.getX());
        this.set(path + "y", location.getY());
        this.set(path + "z", location.getZ());
        this.set(path + "yaw", String.valueOf(location.getYaw()));
        this.set(path + "pitch", String.valueOf(location.getPitch()));
        this.save();
    }

    public String getFileName() {
        return this.fileName;
    }

    public YamlConfiguration getConfig() {
        return this.config;
    }

    public File getFile() {
        return new File(this.plugin.getDataFolder(), this.fileName + ".yml");
    }

}
