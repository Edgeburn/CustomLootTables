package com.edgeburnmedia.customloottables;

import org.bstats.bukkit.Metrics;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

/**
 * Plugin object
 *
 * @author Edgeburn Media
 */
public final class CustomLootTables extends JavaPlugin {
	/**
	 * RNG used throughout the plugin to avoid creating a new Random every time.
	 */
	public static final Random random = new Random();
	private final NamespacedKey customLootTablesNamespace = new NamespacedKey(this, "custom_loot_tables");
	private CLTManager lootManager;
	private DebuggingLogger debuggingLogger;
	private static final int BSTATS_PLUGIN_ID = 14627;

	@Override
	public void onEnable() {
		Metrics metrics = new Metrics(this, BSTATS_PLUGIN_ID);
		lootManager = new CLTManager(this, "custom_loot_tables.yml");
		debuggingLogger = new DebuggingLogger(this);
		getServer().getPluginManager().registerEvents(new CLTListeners(this, lootManager), this);
		saveDefaultConfig();
	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic
	}

	public NamespacedKey getCustomLootTablesNamespace() {
		return customLootTablesNamespace;
	}

	public CLTManager getLootManager() {
		return lootManager;
	}

	public DebuggingLogger getDebuggingLogger() {
		return debuggingLogger;
	}
}
