package com.edgeburnmedia.customloottables.utils;

import com.edgeburnmedia.customloottables.CustomLootTables;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public abstract class ConfigFileManager<T> {
	private final CustomLootTables plugin;
	private FileConfiguration configuration;
	private File configFile;
	private ArrayList<T> entries;

	public ConfigFileManager(CustomLootTables plugin, String fileName) {
		this.plugin = plugin;

		createCustomConfig(fileName);
		reloadConfig();
	}

	public abstract void reloadConfig();

	public abstract void addEntry(T entry);

	public abstract T getEntry(String searchParameter);

	public ArrayList<T> getAllEntries() {
		return entries;
	}

	private void createCustomConfig(String fileName) {
		configFile = new File(plugin.getDataFolder(), fileName);
		if (!configFile.exists()) {
			configFile.getParentFile().mkdirs();
			plugin.saveResource(fileName, false);
		}

		configuration = new YamlConfiguration();
		try {
			configuration.load(configFile);
		} catch (IOException | InvalidConfigurationException e) {
			Bukkit.getLogger().severe("Failed to load " + fileName + " config file!");
		}
	}

	public CustomLootTables getPlugin() {
		return plugin;
	}

	public FileConfiguration getConfiguration() {
		return configuration;
	}

	public File getConfigFile() {
		return configFile;
	}
}
