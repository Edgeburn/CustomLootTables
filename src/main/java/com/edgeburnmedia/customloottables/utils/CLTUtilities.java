package com.edgeburnmedia.customloottables.utils;

import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.LootTables;

import java.util.*;

public class CLTUtilities {
	public static <T> Collection<T> convertArrayListToCollection(List<T> arr) {
		return new ArrayList<>(arr);
	}

	/**
	 * Convert the {@link LootTable} retrieved from a chest or mob and convert it to a {@link LootTables}<br><br>
	 * First gets the index of the {@code /} character, after which the {@link LootTables} name is available using {@link LootTables#valueOf(String name)}
	 *
	 * @param lootTable Loot table object retrieved from a chest via {@link Chest#getLootTable()}
	 * @return {@link LootTables} object for use in referencing the loot table config
	 */
	public static LootTables getLootTablesFromLootTable(LootTable lootTable) {
		final String lootTableAsString = lootTable.toString();
		final int slashIndex = lootTableAsString.lastIndexOf('/');
		final String lootTableIdentifier = lootTableAsString.substring(slashIndex + 1).toUpperCase(Locale.ROOT);
		final LootTables resultingLootTables = LootTables.valueOf(lootTableIdentifier);
		return resultingLootTables;
	}

	/**
	 * Get a random {@code int} within a given range
	 *
	 * @param min Minimum number
	 * @param max Maximum number
	 * @return The number randomly generated
	 */
	public static int getRandomNumber(int min, int max) {
		return (int) ((Math.random() * (max - min)) + min);
	}

	public static Set<String> getReplaceables() {
		Set<String> replaceables = new HashSet<>();

		replaceables.add("*");
		replaceables.add("NONE");

		replaceables.addAll(getReplaceableMobLoot());
		replaceables.addAll(getReplaceableChestLoot());

		return replaceables;
	}

	public static Set<String> getReplaceableChestLoot() {
		ArrayList<String> chestLootTables = new ArrayList<>();
		for (LootTables table : LootTables.values()) {
			chestLootTables.add(table.name().toUpperCase(Locale.ROOT));
		}
		return new HashSet<>(chestLootTables);
	}

	/**
	 * @deprecated Use {@link CLTUtilities#getReplaceableChestLoot()} instead, as it includes these already
	 */
	public static Set<String> getReplaceableMobLoot() {

		ArrayList<String> entities = new ArrayList<>();
		for (EntityType type : EntityType.values()) {
			entities.add(type.name().toUpperCase(Locale.ROOT));
		}
		return new HashSet<>(entities);
	}
}
