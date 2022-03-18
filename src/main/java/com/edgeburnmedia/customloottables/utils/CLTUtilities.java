package com.edgeburnmedia.customloottables.utils;

import org.bukkit.block.Chest;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.LootTables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

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
}
