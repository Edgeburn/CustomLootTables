package com.edgeburnmedia.customloottables.configmanager;

import com.edgeburnmedia.customloottables.CustomLootTable;
import com.edgeburnmedia.customloottables.CustomLootTables;
import com.edgeburnmedia.customloottables.LootItem;
import com.edgeburnmedia.customloottables.utils.ConfigFileManager;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.loot.LootTables;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Manage the custom loot tables file and it's entries
 *
 * @author Edgeburn Media
 */
public class CustomLootTableManager extends ConfigFileManager<CustomLootTable> {

	public CustomLootTableManager(CustomLootTables plugin, String fileName) {
		super(plugin, fileName);
	}

	@Override
	public void reloadConfig() {
		getAllEntries().clear();
		String[] keys = getConfiguration().getKeys(false).toArray(new String[0]);

		for (int i = 0; i < keys.length; i++) {
			String uuid = keys[i];
			CustomLootTable newTable;

			List<String> lootItemUUIDs = getConfiguration().getStringList(uuid + ".item_pool");
			ArrayList<LootItem> lootItemArrayList = new ArrayList<>();

			for (String u : lootItemUUIDs) {
				lootItemArrayList.add(getPlugin().getCustomItemManager().getEntry(u));
			}
			LootItem[] lootItems = new LootItem[lootItemArrayList.size()];
			lootItems = lootItemArrayList.toArray(new LootItem[0]);

			newTable = new CustomLootTable(getPlugin(), UUID.fromString(uuid), lootItems);
			newTable.setReplaces(getConfiguration().getString(uuid + ".replaces"));
			addEntry(uuid, newTable);
		}


	}

	@Override
	public void saveEntry(CustomLootTable entry) {
		final String entryUUID = entry.getUuid().toString();
		ArrayList<LootItem> lootItems = entry.getLoot();
		String[] uuids = new String[lootItems.size()];

		for (int i = 0; i < lootItems.size(); i++) {
			uuids[i] = lootItems.get(i).getUuid().toString();
		}

		getConfiguration().set(entryUUID + ".item_pool", uuids);
		getConfiguration().set(entryUUID + ".replaces", entry.getReplaces());

	}

	/**
	 * Based on the original {@link LootTables} or {@link EntityType}, get the modified {@link CustomLootTable} to generate additional loot
	 *
	 * @param original The original, as specified in {@code custom_loot_tables.yml}
	 * @return The {@link CustomLootTable} containing for generating new loot
	 */
	public @Nullable
	CustomLootTable getReplacementLootTable(String original) {
		for (UUID uuid : getAllEntries().keySet()) {

			CustomLootTable check = getEntry(uuid);
			if (check.getReplaces() == null) {
				continue;
			}

			if (check.getReplaces().equalsIgnoreCase(original) || check.getReplaces().equalsIgnoreCase("*")) {
				return check;
			}
		}
		return null; // if the for loop did not return anything, then it can be assumed no loot table exists.
	}


	/**
	 * Based on the original {@link LootTables} of a given {@link org.bukkit.block.Chest}, get the
	 * {@link CustomLootTable} that should be used for generating additional loot. <br><br>
	 * This method's intended use is in
	 * {@link com.edgeburnmedia.customloottables.CLTListeners#onLootGen(LootGenerateEvent event)}, which is used to
	 * modify loot generation upon opening a generated chest.
	 *
	 * @param l The {@link LootTables} of the {@link org.bukkit.block.Chest} being opened
	 * @return The {@link CustomLootTable} containing for generating new loot
	 */
	public @Nullable
	CustomLootTable getReplacementLootTable(LootTables l) {
		return getReplacementLootTable(l.name().toUpperCase(Locale.ROOT));
	}

	/**
	 * Based on the original {@link EntityType} killed, get the {@link CustomLootTable} that was specified in
	 * {@code custom_loot_tables.yml} to generate additional loot. <br><br>
	 * This method's intended use is in
	 * {@link com.edgeburnmedia.customloottables.CLTListeners#onMobDeath(EntityDeathEvent event)}, where the
	 * {@link EntityType} killed should be passed to this method.
	 *
	 * @param entityType The {@link EntityType} killed
	 * @return The {@link CustomLootTable} containing for generating new loot
	 */
	public @Nullable
	CustomLootTable getReplacementLootTable(EntityType entityType) {
		return getReplacementLootTable(entityType.name().toUpperCase(Locale.ROOT));
	}

	/**
	 * Create a new blank {@link CustomLootTable} and add it to the records.
	 *
	 * @return The newly created {@link CustomLootTable} for any further use.
	 */
	public CustomLootTable createEmptyEntry() {
		CustomLootTable lootTable = new CustomLootTable(getPlugin());
		addEntry(lootTable.getUuid(), lootTable);
		return lootTable;
	}


}

