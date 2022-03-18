package com.edgeburnmedia.customloottables;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * @author Edgeburn Media
 */
public class LootItem {
	private final CustomLootTables plugin;
	private final UUID uuid;
	private double chance;
	private ItemStack itemStack;
	private int stackSize;


	public LootItem(CustomLootTables plugin, ItemStack itemStack, double chance, int stackSize) {
		this(plugin, itemStack, chance, stackSize, UUID.randomUUID());
	}

	public LootItem(CustomLootTables plugin, ItemStack itemStack, double chance, int stackSize, UUID uuid) {
		this.plugin = plugin;
		this.uuid = uuid;
		this.itemStack = itemStack;
		if (validateChance(chance)) {
			this.chance = chance;
		} else {
			this.plugin.getLogger().warning("An out of range value for chance (" + chance + ") was specified. Setting to 0%");
			this.chance = 0.0;
		}

		if (validateStackSize(stackSize)) {
			this.stackSize = stackSize;
		}

	}

	public LootItem(CustomLootTables plugin, ItemStack itemStack, double chance) {
		this(plugin, itemStack, chance, 1);
	}

	/**
	 * Validate whether a chance is in the valid range 0.0-1.0
	 *
	 * @param chance The chance to validate
	 * @return True if valid, false if invalid
	 */
	private static boolean validateChance(double chance) {
		return chance <= 1 && chance >= 0;
	}

	/**
	 * Validate whether a stack amount is in the correct range 0-64
	 *
	 * @param i The amount to validate
	 * @return True if valid, false if invalid
	 */
	private static boolean validateStackSize(int i) {
		return i <= 64 && i >= 0;
	}

	public double getChance() {
		return chance;
	}

	public void setChance(double chance) {
		this.chance = chance;
	}

	/**
	 * Get the {@link ItemStack}. <br><br>
	 * <b style="color:orange;">IMPORTANT: Do not use this method for getting the item based on a random chance.</b><br>
	 *
	 * @return The item for this {@link LootItem}
	 */
	public ItemStack getItemStack() {
		return itemStack;
	}

	public void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	/**
	 * Based on the percentage chance of {@link LootItem#chance}, either get the {@link ItemStack} represented within
	 * this {@link LootItem}, or null, meaning the item will not be part of the loot generated
	 *
	 * @return The item or null
	 */
	public ItemStack getRandomly() {
		double gennedNum = CustomLootTables.random.nextDouble();
		plugin.getDebuggingLogger().log("generated " + String.format("%.2f", gennedNum), "chance " + getChance());
		if (gennedNum <= getChance()) {
			plugin.getDebuggingLogger().log("item " + getItemStack().getType() + " was generated");
			ItemStack i = itemStack;
			i.setAmount(stackSize);
			return i.clone();
		} else {
			plugin.getDebuggingLogger().log("item " + getItemStack().getType() + " was not generated");
			return null;
		}
	}

	public UUID getUuid() {
		return uuid;
	}

}
