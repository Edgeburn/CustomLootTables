package com.edgeburnmedia.customloottables.gui;

import com.edgeburnmedia.customloottables.CustomLootTable;
import com.edgeburnmedia.customloottables.CustomLootTables;
import com.edgeburnmedia.customloottables.LootItem;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CustomLootTablesGUI {
	private final CustomLootTables plugin;
	private final GuiItem tablesButton;
	private final GuiItem customItemsButton;
	private StaticPane mainPane;
	private ChestGui mainMenu;


	public CustomLootTablesGUI(CustomLootTables pl) {
		this.plugin = pl;
		tablesButton = new GuiItem(getCustomLootTablesButton());
		tablesButton.setAction(inventoryClickEvent -> {
			Player player = (Player) inventoryClickEvent.getWhoClicked();
			clickSound(player);
			inventoryClickEvent.setCancelled(true);
		});
		customItemsButton = new GuiItem(getCustomItemsButton());
		customItemsButton.setAction(inventoryClickEvent -> {
			Player player = (Player) inventoryClickEvent.getWhoClicked();
			player.sendMessage("edit items");
			clickSound(player);
			inventoryClickEvent.setCancelled(true);
		});
		mainMenu = new ChestGui(4, "Loot Editor");

		mainPane = new StaticPane(2, 1);
		mainPane.addItem(tablesButton, 0, 0);
		mainPane.addItem(customItemsButton, 1, 0);
		mainMenu.addPane(mainPane);

		mainMenu.setOnClose(inventoryCloseEvent -> {
			plugin.setLocked(false);
			plugin.getDebuggingLogger().log("unlocked gui");
		});

	}

	private static ItemStack getCustomLootTablesButton() {
		ItemStack stack = new ItemStack(Material.CHEST);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName("§6§lLoot Tables");
		stack.setItemMeta(meta);
		return stack;
	}

	private static ItemStack getAcceptButton(String s) {
		ItemStack stack = new ItemStack(Material.EMERALD_BLOCK);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName("§2§l" + s);
		stack.setItemMeta(meta);
		return stack;
	}

	private static ItemStack getCancelButton(String s) {
		ItemStack stack = new ItemStack(Material.BARRIER);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName("§c§l" + s);
		stack.setItemMeta(meta);
		return stack;
	}

	private static ItemStack getCustomItemsButton() {
		ItemStack stack = new ItemStack(Material.DIAMOND);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName("§6§lItems");
		stack.setItemMeta(meta);
		return stack;
	}

	public static void clickSound(Player player) {
		player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.MASTER, 1.0f, 1.0f);
	}

	public static ItemStack getNextPageButton() {
		ItemStack stack = new ItemStack(Material.ARROW);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName("§6Next Page");
		stack.setItemMeta(meta);
		return stack;
	}

	public static ItemStack getPrevPageButton() {
		ItemStack stack = new ItemStack(Material.ARROW);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName("§6Previous Page");
		stack.setItemMeta(meta);
		return stack;
	}

	public static GuiItem getGuiItemFromLootTable(CustomLootTable table) {
		ItemStack stack = new ItemStack(Material.CHEST);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName("§6Loot Table");
		List<String> lore = List.of("Items: " + table.getLoot().size(), "Replaces: " + table.getReplaces(), "UUID: " + table.getUuid());
		meta.setLore(lore);
		meta.addEnchant(Enchantment.DAMAGE_ALL, 0, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		stack.setItemMeta(meta);
		GuiItem res = new GuiItem(stack, inventoryClickEvent -> {
			inventoryClickEvent.setCancelled(true);
			clickSound((Player) inventoryClickEvent.getWhoClicked());
			openLootTableEditor(table, (Player) inventoryClickEvent.getWhoClicked());
		});
		return res;
	}

	public static void openLootTableEditor(CustomLootTable table, Player player) {
		ChestGui gui = new ChestGui(6, "Edit " + table.getUuid());
		StaticPane optionsPane = new StaticPane(0, 5, 9, 1);
		PaginatedPane itemsPane = new PaginatedPane(0, 0, 9, 5);
		itemsPane.populateWithGuiItems(getDeletableLootItems(table));

		GuiItem deleteBtn = new GuiItem(getCancelButton("Delete"), inventoryClickEvent -> {
			clickSound(player);
			inventoryClickEvent.setCancelled(true);
			table.getPlugin().getLootManager().removeEntry(table.getUuid());
			inventoryClickEvent.getWhoClicked().closeInventory();
		});

		GuiItem addLootItem = new GuiItem(getAcceptButton("Add New Loot"), inventoryClickEvent -> {
			clickSound(player);
			inventoryClickEvent.setCancelled(true);
			openAddLootItemToTable(table, player);

		});

		optionsPane.addItem(deleteBtn, 0, 0);
		optionsPane.addItem(addLootItem, 1, 0);

		gui.addPane(optionsPane);
		gui.addPane(itemsPane);
		gui.show(player);
	}

	public static void openAddLootItemToTable(CustomLootTable table, Player player) {
//TODO
	}

	public static ItemStack getReplacementButton() {
		ItemStack stack = new ItemStack(Material.WRITABLE_BOOK);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName("§6Set Replacement");
		meta.setLore(Collections.singletonList("§7Set the vanilla loot table this loot table will alter"));
		stack.setItemMeta(meta);
		return stack;
	}

	public static List<GuiItem> getDeletableLootItems(CustomLootTable table) {
		ArrayList<GuiItem> items = new ArrayList<>();

		for (LootItem item : table.getLoot()) {
			ItemStack stack = item.getItemStack().clone();
			ItemMeta meta = stack.getItemMeta();
			ArrayList<String> lore = (ArrayList<String>) meta.getLore();
			if (lore == null) {
				ArrayList<String> l = new ArrayList<>();
				l.add("§7Chance: " + item.getChance());
				l.add("§cRight click me to remove from loot table");
				meta.setLore(l);
			} else {
				lore.add("§7Chance: " + item.getChance());
				lore.add("§cRight click me to remove from loot table");
				meta.setLore(lore);
			}
			stack.setItemMeta(meta);
			GuiItem guiItem = new GuiItem(stack, inventoryClickEvent -> {
				inventoryClickEvent.setCancelled(true);
				if (inventoryClickEvent.getClick().isRightClick()) {
					table.getLoot().remove(item);
					table.getPlugin().getLootManager().replaceEntry(table.getUuid(), table);
					inventoryClickEvent.getCurrentItem().setAmount(0);
					((Player) inventoryClickEvent.getWhoClicked()).playSound(inventoryClickEvent.getWhoClicked().getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1.0f, 1.0f);
				}
			});
			items.add(guiItem);
		}
		return items;
	}

	public void openLootTableSelector(Player player) {
		ChestGui gui = new ChestGui(6, "Edit loot tables");
		PaginatedPane paginatedPane = new PaginatedPane(0, 0, 9, 5);
		StaticPane optionsPane = new StaticPane(0, 5, 9, 1);
		ArrayList<GuiItem> tableItems = new ArrayList<>();

		for (UUID lootTableUUID : plugin.getLootManager().getAllEntries().keySet()) {
			tableItems.add(getGuiItemFromLootTable(plugin.getLootManager().getEntry(lootTableUUID)));
		}

		paginatedPane.populateWithGuiItems(tableItems);

		GuiItem prevPage = new GuiItem(getPrevPageButton(), inventoryClickEvent -> {
			clickSound(player);
			inventoryClickEvent.setCancelled(true);
			paginatedPane.setPage(paginatedPane.getPage() - 1);
		});

		GuiItem nextPage = new GuiItem(getNextPageButton(), inventoryClickEvent -> {
			clickSound(player);
			inventoryClickEvent.setCancelled(true);
			paginatedPane.setPage(paginatedPane.getPage() + 1);
		});

		GuiItem createNewLootTable = new GuiItem(getAcceptButton("New Loot Table"), inventoryClickEvent -> {
			clickSound(player);
			inventoryClickEvent.setCancelled(true);
			plugin.getLootManager().createEmptyEntry();
			openLootTableSelector(player);
		});

		optionsPane.addItem(prevPage, 0, 0);
		optionsPane.addItem(nextPage, 1, 0);
		optionsPane.addItem(createNewLootTable, 2, 0);
		gui.addPane(paginatedPane);
		gui.addPane(optionsPane);
		gui.show(player);
	}
}
