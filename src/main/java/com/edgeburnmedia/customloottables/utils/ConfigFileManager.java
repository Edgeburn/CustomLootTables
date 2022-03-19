package com.edgeburnmedia.customloottables.utils;

import com.edgeburnmedia.customloottables.CustomLootTables;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

/**
 * Abstract class which can be extended for management of a custom configuration file, including saving, reading, and
 * removing entries
 *
 * @param <T> Type being managed in the subclass
 */
public abstract class ConfigFileManager<T> {
	private final CustomLootTables plugin;
	private final String fileName;
	private FileConfiguration configuration;
	private File configFile;
	@SuppressWarnings({"FieldMayBeFinal", "CanBeFinal"})
	private HashMap<UUID, T> entries = new HashMap<>();

	public ConfigFileManager(CustomLootTables plugin, String fileName) {
		this.plugin = plugin;
		this.fileName = fileName;

		createCustomConfig(fileName);
		reloadConfig();
	}

	/**
	 * Get the name of the file on the filesystem as a String
	 *
	 * @return The name of the configuration file on the filesystem as a String
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Read the configuration file, and interpret its contents as a HashMap of {@link T} objects corresponding to UUIDs
	 */
	public abstract void reloadConfig();

	/**
	 * Save an entry to the config. Note that this method may not necessarily save to the "entries" table in memory.
	 *
	 * @param entry Entry to save
	 */
	public abstract void saveEntry(T entry);

	/**
	 * Add an entry to the entries HashMap in memory
	 *
	 * @param uuid  The UUID of the entry
	 * @param entry Entry to add
	 */
	public void addEntry(UUID uuid, T entry) {
		getAllEntries().put(uuid, entry);
		saveEntry(entry);
		try {
			getConfiguration().save(getConfigFile());
		} catch (IOException e) {
			getPlugin().getLogger().severe("Failed to save config file " + getFileName() + "!");
		}
	}

	/**
	 * Delete an existing entry, and then replace it with a new one.
	 *
	 * @param uuid  The UUID of the entry to replace
	 * @param entry The replacement entry
	 */
	public void replaceEntry(UUID uuid, T entry) {
//		removeEntry(uuid);
		addEntry(uuid, entry);
	}

	/**
	 * Add an entry to the entries HashMap in memory
	 *
	 * @param uuid  The UUID of the entry
	 * @param entry Entry to add
	 */
	public void addEntry(String uuid, T entry) {
		addEntry(UUID.fromString(uuid), entry);
	}

	/**
	 * Get an entry in the entries HashMap in memory based on it's UUID.
	 *
	 * @param uuid The UUID of the entry to retrieve
	 * @return The entry with the passed UUID
	 */
	public T getEntry(UUID uuid) {
		return getAllEntries().get(uuid);
	}

	/**
	 * Get an entry in the entries HashMap in memory based on it's UUID.
	 *
	 * @param uuid The UUID of the entry to retrieve
	 * @return The entry with the passed UUID
	 */
	public T getEntry(String uuid) {
		return getEntry(UUID.fromString(uuid));
	}

	/**
	 * Get the HashMap containing the entries each corresponding to their IDs
	 *
	 * @return The HashMap of UUIDs to their entries
	 */
	public HashMap<UUID, T> getAllEntries() {
		return entries;
	}

	/**
	 * Initialize the custom config
	 *
	 * @param fileName The name for the file in the filesystem for the config file
	 */
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

	/**
	 * Get a reference to the {@link org.bukkit.plugin.java.JavaPlugin} object
	 *
	 * @return The {@link org.bukkit.plugin.java.JavaPlugin} object
	 */
	public CustomLootTables getPlugin() {
		return plugin;
	}

	/**
	 * Get a reference to the {@link FileConfiguration} object
	 *
	 * @return The {@link FileConfiguration} object
	 */
	public FileConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * Get a reference to the {@link File object}
	 *
	 * @return The {@link File} object
	 */
	public File getConfigFile() {
		return configFile;
	}


	/**
	 * Remove an entry from the configuration.
	 *
	 * @param uuid The UUID of the entry to remove
	 */
	public void removeEntry(UUID uuid) {
		HashMap<UUID, T> entriesCopy = new HashMap<>(entries);
		entriesCopy.remove(uuid);
		entries.clear();
		getConfigFile().delete();
		createCustomConfig(getFileName());

		for (UUID u : entriesCopy.keySet()) {
			addEntry(u, entriesCopy.get(u));
		}

		try {
			getConfiguration().save(getConfigFile());
			reloadConfig();
		} catch (IOException e) {
			getPlugin().getLogger().severe("Failed to save config file " + getFileName() + "!");
		}
	}
}
