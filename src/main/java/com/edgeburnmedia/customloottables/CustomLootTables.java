package com.edgeburnmedia.customloottables;

import com.edgeburnmedia.customloottables.command.CLTCommandTabCompletion;
import com.edgeburnmedia.customloottables.command.CLTCommands;
import com.edgeburnmedia.customloottables.configmanager.CustomItemManager;
import com.edgeburnmedia.customloottables.configmanager.CustomLootTableManager;
import com.edgeburnmedia.customloottables.gui.CustomLootTablesGUI;
import com.edgeburnmedia.customloottables.utils.DebuggingLogger;
import org.bstats.bukkit.Metrics;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

/**
 * Plugin object
 *
 * @author Edgeburn Media
 */
@SuppressWarnings("ConstantConditions")
public final class CustomLootTables extends JavaPlugin {
	/**
	 * RNG used throughout the plugin to avoid creating a new Random every time.
	 */
	public static final Random random = new Random();
	private static final int BSTATS_PLUGIN_ID = 14627;
	private final NamespacedKey customLootTablesNamespace = new NamespacedKey(this, "custom_loot_tables");
	private CustomLootTableManager lootManager;
	private CustomItemManager customItemManager;
	private DebuggingLogger debuggingLogger;
	private CustomLootTablesGUI gui;
	/**
	 * If locked, do not allow use of the GUI to prevent conflicting actions.
	 */
	private boolean locked = false;


	@Override
	public void onDisable() {
		// Plugin shutdown logic
	}

	@Override
	public void onEnable() {
		final Metrics metrics = new Metrics(this, BSTATS_PLUGIN_ID);
		customItemManager = new CustomItemManager(this, "custom_items.yml");
		lootManager = new CustomLootTableManager(this, "custom_loot_tables.yml");
		debuggingLogger = new DebuggingLogger(this);
		gui = new CustomLootTablesGUI(this);
		getCommand("editloottable").setExecutor(new CLTCommands(this));
		getCommand("editloottable").setTabCompleter(new CLTCommandTabCompletion(this));
		getServer().getPluginManager().registerEvents(new CLTListeners(this, lootManager), this);
		saveDefaultConfig();
	}

	public NamespacedKey getCustomLootTablesNamespace() {
		return customLootTablesNamespace;
	}

	public CustomLootTableManager getLootManager() {
		return lootManager;
	}

	public DebuggingLogger getDebuggingLogger() {
		return debuggingLogger;
	}

	public CustomItemManager getCustomItemManager() {
		return customItemManager;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public CustomLootTablesGUI getGui() {
		return gui;
	}
}
