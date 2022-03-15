package com.edgeburnmedia.customloottables;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;

import java.util.Arrays;
import java.util.Collection;

/**
 * Class containing the Listeners for events in which the loot tables may need to be modified
 *
 * @author Edgeburn Media
 */
public class CLTListeners implements Listener {
	private final CustomLootTables plugin;
	private final DebuggingLogger debuggingLogger;
	private CLTManager lootManager;

	public CLTListeners(CustomLootTables plugin, CLTManager lootManager) {
		this.plugin = plugin;
		this.lootManager = lootManager;
		this.debuggingLogger = plugin.getDebuggingLogger();
	}

	@EventHandler
	public void onMobDeath(EntityDeathEvent event) {
		if (event.getEntity().getType() != EntityType.PLAYER) { // no loot should be generated on player deaths
			debuggingLogger.log("entity of type " + event.getEntity().getType().name() + " died");
			ItemStack[] originalDrops = event.getDrops().toArray(new ItemStack[0]);
			debuggingLogger.log(originalDrops.length + " vanilla drops from this entity");
			CustomLootTable customLootTable = getDebugLootTable();
			Collection<ItemStack> mergedLoot = customLootTable.getMergedLoot(CustomLootTables.random, null, originalDrops);
			event.getDrops().clear();
			event.getDrops().addAll(mergedLoot);
			debuggingLogger.log("finished loot generation. " + event.getDrops().size() + " loot items generated");
		}
	}

	@EventHandler
	public void onLootGen(LootGenerateEvent event) {
		debuggingLogger.log("loot generate event fired!");
		LootTable eventLootTable = event.getLootTable();
		CustomLootTable customLootTable = getDebugLootTable();
		ItemStack[] newLoot = customLootTable.getMergedLoot(CustomLootTables.random, event.getLootContext(), eventLootTable).toArray(new ItemStack[0]);
		event.setLoot(Arrays.asList(newLoot));
		debuggingLogger.log("finished loot generation. " + event.getLoot().size() + " loot items generated");
	}

	/**
	 * Preset {@link CustomLootTable} for debug purposes. Has a 50% chance to give a Dead Bush
	 *
	 * @return The loot table
	 * @deprecated TODO remove in final version
	 */
	@Deprecated
	public CustomLootTable getDebugLootTable() {
		debuggingLogger.log("using temporary custom loot table!");
		CustomLootTable customLootTable;
		LootItem l = new LootItem(plugin, new ItemStack(Material.DEAD_BUSH), 0.5);
		LootItem l2 = new LootItem(plugin, new ItemStack(Material.BRICK), 0.8);
		customLootTable = new CustomLootTable(plugin, l, l2);
		return customLootTable;
	}

}
