package com.edgeburnmedia.customloottables.command;

import com.edgeburnmedia.customloottables.CustomLootTable;
import com.edgeburnmedia.customloottables.CustomLootTables;
import com.edgeburnmedia.customloottables.LootItem;
import com.edgeburnmedia.customloottables.configmanager.CustomItemManager;
import com.edgeburnmedia.customloottables.configmanager.CustomLootTableManager;
import com.edgeburnmedia.customloottables.gui.CustomLootTablesGUI;
import com.edgeburnmedia.customloottables.utils.CLTUtilities;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("ClassCanBeRecord")
public class CLTCommands implements CommandExecutor, TabCompleter {
	private final CustomLootTables plugin;

	public CLTCommands(CustomLootTables plugin) {
		this.plugin = plugin;
	}


	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			switch (args[0]) {
				case "registerhand":
					if (args.length == 1) {
						sender.sendMessage("§cNo chance specified! Usage: /editloottable registerhand <chance>");
						return false;
					}
					ItemStack handItem = ((Player) sender).getInventory().getItemInMainHand();
					if (handItem.getType().equals(Material.AIR)) {
						sender.sendMessage("§cYou must be holding an item!");
						return true;
					}
					LootItem registeredLootItem = new LootItem(plugin, handItem, Double.parseDouble(args[1]));
					plugin.getCustomItemManager().addEntry(registeredLootItem.getUuid(), registeredLootItem);
					sender.sendMessage("§aRegistered item with UUID: " + registeredLootItem.getUuid());
					return true;
				case "gui":
					if (!plugin.isLocked()) {
						CustomLootTablesGUI guiMgr = plugin.getGui();
						guiMgr.openLootTableSelector((Player) sender);
					} else {
						sender.sendMessage("§cGUI editor is being used by another player. Please wait for them to finish before using the GUI to prevent conflicts.");
					}
					return true;
				case "version":
					String version = plugin.toString();
					sender.sendMessage(version);
					return true;
				case "savetables":
					plugin.getLootManager().getAllEntries().forEach((uuid, entry) -> {
						plugin.getLootManager().saveEntry(entry);
					});
					sender.sendMessage("§aSaved all loot tables.");
					return true;
				case "table":
					switch (args[1]) {
						case "create":
							CustomLootTable lootTable;
							lootTable = plugin.getLootManager().createEmptyEntry();
							sender.sendMessage("§2Added new custom loot table +" + lootTable.getUuid() + ".");
							return true;
						case "replaces":
							if (args[2] != null && args[3] != null) {
								plugin.getLootManager().getEntry(args[2]).setReplaces(args[3]);
								plugin.getLootManager().replaceEntry(UUID.fromString(args[2]), plugin.getLootManager().getEntry(args[2]));
								sender.sendMessage("§2The " + args[3] + " loot table is now being modified by custom loot table " + args[2]);
								return true;
							} else {
								sender.sendMessage("§cInvalid command syntax.");
								return false;
							}
						case "additem":
							if (args[2] != null && args[3] != null) {
								UUID tableUUID = UUID.fromString(args[2]);
								UUID itemUUID = UUID.fromString(args[3]);

								CustomLootTableManager lootMgr = plugin.getLootManager();
								CustomItemManager itemMgr = plugin.getCustomItemManager();
								LootItem referencedItem = itemMgr.getEntry(itemUUID);
								CustomLootTable table = lootMgr.getEntry(tableUUID);

								table.addLoot(referencedItem);

								lootMgr.replaceEntry(tableUUID, table);
								sender.sendMessage("§2Added item " + itemUUID + " to loot table " + tableUUID);

								return true;
							} else {
								sender.sendMessage("§cInvalid command syntax.");
								return false;
							}
					}
					break;
				default:
					return false;

			}
		} else {
			sender.sendMessage("This command must be executed by a player.");
			return true;
		}
		return false;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

		if (sender instanceof Player) {
			List<String> commands = new ArrayList<>();
			List<String> completions = new ArrayList<>();

			if (args.length == 1) {
				commands.add("registerhand");
				commands.add("gui");
				commands.add("version");
				commands.add("savetables");
				StringUtil.copyPartialMatches(args[0], commands, completions);
			} else if (args.length == 2) {
				switch (args[0]) {
					case "registerhand" -> {
						for (int i = 0; i < 10; i++) {
							commands.add("0.0" + i);
						}
						for (int i = 0; i < 99; i++) {
							commands.add("0." + i);
						}
						StringUtil.copyPartialMatches(args[1], commands, completions);
					}
					case "table" -> {
						commands.add("create");
						commands.add("replaces");
						commands.add("additem");
						commands.add("removeitem");
						commands.add("delete");
						StringUtil.copyPartialMatches(args[1], commands, completions);
					}
					default -> {
					}
				}
			} else if (args.length == 3) {
				switch (args[1]) {
					case "replaces" -> {
						commands.addAll(plugin.getLootManager().getConfiguration().getKeys(false));
						StringUtil.copyPartialMatches(args[2], commands, completions);
					}
					case "removeitem", "additem" -> {
						Player player = (Player) sender;
						player.sendTitle("§2Loot Table UUID", "", 1, 100, 1);
						commands.addAll(plugin.getLootManager().getConfiguration().getKeys(false));
						StringUtil.copyPartialMatches(args[2], commands, completions);
					}
				}
			} else if (args.length == 4) {
				switch (args[1]) {
					case "additem", "removeitem" -> {
						Player player = (Player) sender;
						player.sendTitle("§2Loot Item UUID", "", 1, 100, 1);
						commands.addAll(plugin.getCustomItemManager().getConfiguration().getKeys(false));
						StringUtil.copyPartialMatches(args[3], commands, completions);
					}
					case "replaces" -> {
						commands.addAll(CLTUtilities.getReplaceables());
						StringUtil.copyPartialMatches(args[3], commands, completions);
					}
				}

			}

			Collections.sort(completions);
			return completions;
		} else {
			return null;
		}

	}
}
