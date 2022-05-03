package com.edgeburnmedia.customloottables.command;

import com.edgeburnmedia.customloottables.CustomLootTable;
import com.edgeburnmedia.customloottables.CustomLootTables;
import com.edgeburnmedia.customloottables.LootItem;
import com.edgeburnmedia.customloottables.configmanager.CustomItemManager;
import com.edgeburnmedia.customloottables.configmanager.CustomLootTableManager;
import com.edgeburnmedia.customloottables.gui.CustomLootTablesGUI;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@SuppressWarnings("ClassCanBeRecord")
public class CLTCommands implements CommandExecutor {
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
}
