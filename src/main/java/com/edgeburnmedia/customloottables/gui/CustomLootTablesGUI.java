package com.edgeburnmedia.customloottables.gui;

import com.edgeburnmedia.customloottables.CustomLootTable;
import com.edgeburnmedia.customloottables.CustomLootTables;
import com.edgeburnmedia.customloottables.LootItem;
import com.edgeburnmedia.customloottables.utils.CLTUtilities;
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

import java.util.*;

public class CustomLootTablesGUI {
	private final CustomLootTables plugin;


	public CustomLootTablesGUI(CustomLootTables pl) {
		this.plugin = pl;
	}

	private static ItemStack getAcceptButton(String s) {
		ItemStack stack = new ItemStack(Material.EMERALD_BLOCK);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName("§2§l" + s);
		stack.setItemMeta(meta);
		return stack;
	}

	private static ItemStack getReturnToMainMenuButton(String s) {
		ItemStack stack = new ItemStack(Material.COMMAND_BLOCK);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName("§c§l" + s);
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

		GuiItem addLootItem = new GuiItem(getAcceptButton("Add New Loot"), inventoryClickEvent -> {
			clickSound(player);
			inventoryClickEvent.setCancelled(true);
			openAddLootItemToTable(table, player);

		});

		GuiItem returnToMain = new GuiItem(getReturnToMainMenuButton("Return to Main Menu"), inventoryClickEvent -> {
			clickSound(player);
			inventoryClickEvent.setCancelled(true);
			inventoryClickEvent.getWhoClicked().closeInventory();
			table.getPlugin().getGui().openLootTableSelector(player);
		});

		GuiItem setReplButton = new GuiItem(getReplacementButton(), inventoryClickEvent -> {
			clickSound(player);
			inventoryClickEvent.setCancelled(true);
			openReplacementSetter(table, player);
		});

		optionsPane.addItem(returnToMain, 0, 0);
		optionsPane.addItem(addLootItem, 1, 0);
		optionsPane.addItem(setReplButton, 2, 0);

		gui.addPane(optionsPane);
		gui.addPane(itemsPane);
		gui.show(player);
	}

	public static void openAddLootItemToTable(CustomLootTable table, Player player) {
		ArrayList<GuiItem> items = new ArrayList<>();
		table.getPlugin().getCustomItemManager().getAllEntries().forEach((uuid, lootItem) -> {
			if (table.getLoot().contains(lootItem)) { // we only want to add items that aren't already there

			} else {
				GuiItem guiItem;
				ArrayList<String> lore;
				ItemStack stack = lootItem.getItemStack().clone();
				ItemMeta m = stack.getItemMeta();
				List<String> originalLore = m.getLore();
				if (originalLore == null) {
					lore = null;
				} else {
					lore = new ArrayList<>(originalLore);
				}
				if (lore == null) {
					ArrayList<String> l = new ArrayList<>();
					l.add("§7Chance: " + lootItem.getChance());
					l.add("§7UUID: " + lootItem.getUuid());
					l.add("§2Click me to add to loot table");
					m.setLore(l);
				} else {
					lore.add("§7Chance: " + lootItem.getChance());
					lore.add("§7UUID: " + lootItem.getUuid());
					lore.add("§2Click me to add to loot table");
					m.setLore(lore);
				}
				stack.setItemMeta(m);

				guiItem = new GuiItem(stack, inventoryClickEvent -> {
					clickSound(player);
					inventoryClickEvent.setCancelled(true);
					table.addLoot(lootItem);
					inventoryClickEvent.getCurrentItem().setAmount(0);
				});
				items.add(guiItem);
			}
		});

		ChestGui g = new ChestGui(6, "Add items to pool for " + table.getUuid());

		PaginatedPane paginatedPane = new PaginatedPane(0, 0, 9, 5);
		paginatedPane.populateWithGuiItems(items);
		StaticPane optionsPane = new StaticPane(0, 5, 9, 1);

		GuiItem prevPage = new GuiItem(getPrevPageButton(), inventoryClickEvent -> {
			clickSound(player);
			inventoryClickEvent.setCancelled(true);
			paginatedPane.setPage(paginatedPane.getPage() - 1);
			g.update();
		});

		GuiItem nextPage = new GuiItem(getNextPageButton(), inventoryClickEvent -> {
			clickSound(player);
			inventoryClickEvent.setCancelled(true);
			paginatedPane.setPage(paginatedPane.getPage() + 1);
			g.update();
		});

		GuiItem backButtGuiItem = new GuiItem(getReturnToMainMenuButton("Back"), inventoryClickEvent -> {
			clickSound(player);
			inventoryClickEvent.setCancelled(true);
			inventoryClickEvent.getWhoClicked().closeInventory();
			openLootTableEditor(table, player);
		});

		optionsPane.addItem(prevPage, 0, 0);
		optionsPane.addItem(nextPage, 1, 0);
		optionsPane.addItem(backButtGuiItem, 2, 0);
		g.addPane(paginatedPane);
		g.addPane(optionsPane);
		g.show(player);
	}

	public static void openReplacementSetter(CustomLootTable table, Player player) {
		ChestGui g = new ChestGui(6, "Set replacement for " + table.getUuid());
		PaginatedPane paginatedPane = new PaginatedPane(0, 0, 9, 5);
		paginatedPane.populateWithGuiItems(new ArrayList<>(getReplaceablesItems(table)));
		StaticPane optionsPane = new StaticPane(0, 5, 9, 1);

		GuiItem prevPage = new GuiItem(getPrevPageButton(), inventoryClickEvent -> {
			clickSound(player);
			inventoryClickEvent.setCancelled(true);
			paginatedPane.setPage(paginatedPane.getPage() - 1);
			g.update();
		});

		GuiItem nextPage = new GuiItem(getNextPageButton(), inventoryClickEvent -> {
			clickSound(player);
			inventoryClickEvent.setCancelled(true);
			paginatedPane.setPage(paginatedPane.getPage() + 1);
			g.update();
		});

		GuiItem backButtGuiItem = new GuiItem(getReturnToMainMenuButton("Back"), inventoryClickEvent -> {
			clickSound(player);
			inventoryClickEvent.setCancelled(true);
			inventoryClickEvent.getWhoClicked().closeInventory();
			openLootTableEditor(table, player);
		});

		optionsPane.addItem(prevPage, 0, 0);
		optionsPane.addItem(nextPage, 1, 0);
		optionsPane.addItem(backButtGuiItem, 2, 0);
		g.addPane(paginatedPane);
		g.addPane(optionsPane);
		g.show(player);
	}

	public static Set<GuiItem> getReplaceablesItems(CustomLootTable table) {
		Set<GuiItem> items = new HashSet<>();

		ItemStack asteriskStack = new ItemStack(Material.NETHER_STAR);
		ItemMeta asteriskMeta = asteriskStack.getItemMeta();
		asteriskMeta.setDisplayName("§6*");
		asteriskMeta.setLore(Collections.singletonList("§7Unless there is another table which appears first, this table will apply to all loot"));
		asteriskStack.setItemMeta(asteriskMeta);
		GuiItem asterisk = new GuiItem(asteriskStack, inventoryClickEvent -> {
			inventoryClickEvent.setCancelled(true);
			clickSound((Player) inventoryClickEvent.getWhoClicked());

			table.setReplaces("*");
			table.getPlugin().getLootManager().replaceEntry(table.getUuid(), table);

		});
		items.add(asterisk);

		ItemStack noneStack = new ItemStack(Material.BARRIER);
		ItemMeta noneMeta = noneStack.getItemMeta();
		noneMeta.setDisplayName("§6NONE");
		noneStack.setItemMeta(noneMeta);
		GuiItem none = new GuiItem(noneStack, inventoryClickEvent -> {
			inventoryClickEvent.setCancelled(true);
			clickSound((Player) inventoryClickEvent.getWhoClicked());

			table.setReplaces("NONE");
			table.getPlugin().getLootManager().replaceEntry(table.getUuid(), table);
		});
		items.add(none);

		CLTUtilities.getReplaceableChestLoot().forEach(s -> {
			GuiItem guiItem;
			ItemStack stack = new ItemStack(Material.CHEST);
			ItemMeta m = stack.getItemMeta();
			m.setDisplayName("§6" + s);
			stack.setItemMeta(m);
			guiItem = new GuiItem(stack, inventoryClickEvent -> {
				inventoryClickEvent.setCancelled(true);
				clickSound((Player) inventoryClickEvent.getWhoClicked());

				table.setReplaces(s);
				table.getPlugin().getLootManager().replaceEntry(table.getUuid(), table);
			});
			items.add(guiItem);
		});

		return items;

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
