package com.edgeburnmedia.customloottables.configmanager;

import com.edgeburnmedia.customloottables.CustomLootTables;
import com.edgeburnmedia.customloottables.LootItem;
import com.edgeburnmedia.customloottables.utils.ConfigFileManager;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Manage and store the {@link LootItem}s which can be used in
 * {@link com.edgeburnmedia.customloottables.CustomLootTable}s, stored in {@code custom_loot_tables.yml}
 *
 * @author Edgeburn Media
 */
public class CustomItemManager extends ConfigFileManager<LootItem> {
	public CustomItemManager(CustomLootTables plugin, String fileName) {
		super(plugin, fileName);
	}

	@Override
	public void reloadConfig() {
		getAllEntries().clear();
		String[] keys = getConfiguration().getKeys(false).toArray(new String[0]);

		for (int i = 0; i < keys.length; i++) {
			LootItem lootItem;
			String uuid = keys[i];
			ItemStack stack = getConfiguration().getItemStack(uuid + ".itemstack");
			double chance = getConfiguration().getDouble(uuid + ".chance");
			lootItem = new LootItem(getPlugin(), stack, chance, 1, UUID.fromString(uuid));
			addEntry(uuid, lootItem);
		}
	}

	@Override
	public void saveEntry(LootItem entry) {
		String entryUUID = entry.getUuid().toString();
		ItemStack stack = entry.getItemStack();
		double chance = entry.getChance();

		getConfiguration().set(entryUUID + ".itemstack", stack);
		getConfiguration().set(entryUUID + ".chance", chance);
	}
}
