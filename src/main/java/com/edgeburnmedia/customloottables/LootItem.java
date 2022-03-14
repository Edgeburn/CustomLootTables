package com.edgeburnmedia.customloottables;

import com.edgeburnmedia.customloottables.utils.CLTUtilities;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.*;

/**
 *
 * @author Edgeburn Media
 */
public class LootItem {
	private final CustomLootTables plugin;
	private final UUID uuid;
	private double chance;
	private ItemStack itemStack;
	private int minStackSize;
	private int maxStackSize;


	public double getChance() {
		return chance;
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

	public LootItem(CustomLootTables plugin, ItemStack itemStack, double chance, int minStackSize, int maxStackSize) {
		this.plugin = plugin;
		this.uuid = UUID.randomUUID();
		this.itemStack = itemStack;
		if (validateChance(chance)) {
			this.chance = chance;
		} else {
			this.plugin.getLogger().warning("An out of range value for chance (" + chance + ") was specified. Setting to 0%");
			this.chance = 0.0;
		}

		if (validateStackSize(minStackSize)) {
			this.minStackSize = minStackSize;
		} else {
			this.plugin.getLogger().warning("An out of range value for minStackSize (" + minStackSize + ") was specified. Setting to 0");
			this.minStackSize = 0;
		}
		if (validateStackSize(maxStackSize)) {
			this.maxStackSize = maxStackSize;
		} else {
			this.plugin.getLogger().warning("An out of range value for maxStackSize (" + maxStackSize + ") was specified. Setting to 0");
			this.maxStackSize = 0;

		}

	}

	public LootItem(CustomLootTables plugin, ItemStack itemStack, double chance) {
		this(plugin, itemStack, chance, 0, 0);
	}

	public void setChance(double chance) {
		this.chance = chance;
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
			plugin.getDebuggingLogger().log("item "  + getItemStack().getType() + " was generated");
			ItemStack i = itemStack;
			i.setAmount(randomStackSize());
			return i;
		} else {
			plugin.getDebuggingLogger().log("item " + getItemStack().getType() + " was not generated");
			return null;
		}
	}

	private int randomStackSize() {
		if (getMinStackSize() < 1 && getMaxStackSize() < 1) {
			return 1;
		} else {
			if (getMinStackSize() < 1) {
				setMinStackSize(1);
			}
			if (getMinStackSize() < 1) {
				setMaxStackSize(1);
			}
			return CLTUtilities.getRandomNumber(getMinStackSize(), getMaxStackSize());
		}
	}

	public int getMinStackSize() {
		return minStackSize;
	}

	public void setMinStackSize(int minStackSize) {
		this.minStackSize = minStackSize;
	}

	public int getMaxStackSize() {
		return maxStackSize;
	}

	public void setMaxStackSize(int maxStackSize) {
		this.maxStackSize = maxStackSize;
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

	public UUID getUuid() {
		return uuid;
	}

	/**
	 * Build a {@link LootItem} from each attribute that can be saved to {@code custom_items.yml}
	 */
	public static class LootItemBuilder {
		private CustomLootTables plugin;
		private ItemStack itemStack;
		private double chance;
		private String itemName;
		private String itemLore;
		private HashMap<Enchantment, Integer> enchants = new HashMap<>();
		private ItemFlag[] flags;

		/**
		 * Begin constructing a new {@link LootItem}, starting with the item's {@link Material}
		 * 
		 * @param plugin Plugin reference
		 * @param material Material the item should be made from
		 */
		public LootItemBuilder(CustomLootTables plugin, Material material) {
			this.plugin = plugin;
			this.itemStack = new ItemStack(material);
			this.itemStack.setAmount(1);
		}

		/**
		 * Set the amount of items that should appear in the ItemStack
		 *
		 * @param amount Amount of items
		 */
		public LootItemBuilder amount(int amount) {
			if (LootItem.validateStackSize(amount)) {
				this.itemStack.setAmount(amount);
			} else {
				plugin.getLogger().warning("Stack amount specified is not within valid range 0-64; defaulting to 1");
				this.itemStack.setAmount(1);
			}
			return this;
		}

		/**
		 * Set the chance that the item will appear in a given loot generation
		 *
		 * @param chance Percentage chance the item will appear represented by a number from 0.0-1.0, with 0.0 meaning 
		 *               the item will never appear, and 1.0 meaning the item will always appear.      
		 */
		public LootItemBuilder chance(double chance) {
			if (LootItem.validateChance(chance)) {
				this.chance = chance;
			} else {
				plugin.getLogger().warning("Chance specified is not within valid range 0.0-1.0; defaulting to 1.0 (100%)");
				this.chance = 1;
			}
			return this;
		}

		/**
		 * Set a name for the item as it will appear in the inventory
		 *
		 * @param name The item's name
		 */
		public LootItemBuilder itemName(String name) {
			this.itemName = name;
			return this;
		}

		/**
		 * Set the {@link ItemFlag}s that the item will have. Item flags
		 *
		 * @param flags The item's {@link ItemFlag}
		 */
		public LootItemBuilder setItemFlags(ItemFlag... flags) {
			this.flags = new ItemFlag[flags.length];

			for (int i = 0; i < this.flags.length; i++) {
				this.flags[i] = flags[i];
			}

			return this;
		}

		/**
		 * Set the item's lore. The lore is the text that can appear below an item's name that gives information about it
		 *
		 * @param lore The item's lore
		 */
		public LootItemBuilder itemLore(String lore) {
			this.itemLore = lore;
			return this;
		}

		/**
		 * Add an {@link Enchantment} to the item. If there are multiple enchantments to add, use this method for each
		 * one
		 *
		 * @param enchantment The enchantment type
		 * @param level The enchantment's level. In Minecraft versions >=1.17 this can be no higher than 255, however
		 *              this will not be enforced by the plugin as the game reverts all enchantment levels above this
		 *              to 255 on its own
		 */
		public LootItemBuilder addEnchant(Enchantment enchantment, int level) {
			enchants.put(enchantment, level);
			return this;
		}

		/**
		 * Build the finalized {@link LootItem}
		 *
		 * @return
		 */
		public LootItem build() {
			ItemMeta meta = this.itemStack.getItemMeta();
			meta.setDisplayName(itemName);
			meta.setLore(Collections.singletonList(itemLore));

			// add the enchants defined in the enchants hashmap
			for (Enchantment enchantment : enchants.keySet()) {
				meta.addEnchant(enchantment, enchants.get(enchantment), true);
			}

			meta.addItemFlags(flags);


		}

	}
}
