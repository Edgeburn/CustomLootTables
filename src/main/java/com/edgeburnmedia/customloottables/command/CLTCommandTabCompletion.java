package com.edgeburnmedia.customloottables.command;

import com.edgeburnmedia.customloottables.CustomLootTables;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CLTCommandTabCompletion implements TabCompleter {
	private final CustomLootTables plugin;

	public CLTCommandTabCompletion(CustomLootTables plugin) {
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

		if (sender instanceof Player) {
			List<String> commands = new ArrayList<>();
			List<String> completions = new ArrayList<>();

			if (args.length == 1) {
				commands.add("registerhand");
				commands.add("gui");
				commands.add("table");
				StringUtil.copyPartialMatches(args[0], commands, completions);
			} else if (args.length == 2) {
				switch (args[0]) {
					case "registerhand":
						for (int i = 0; i < 10; i++) {
							commands.add("0.0" + i);
						}
						for (int i = 0; i < 99; i++) {
							commands.add("0." + i);
						}
						StringUtil.copyPartialMatches(args[1], commands, completions);

						break;
					case "table":
						commands.add("create");
						commands.add("replaces");
						commands.add("additem");
						commands.add("removeitem");
						commands.add("delete");
						StringUtil.copyPartialMatches(args[1], commands, completions);
						break;
					default:
						break;
				}
			} else if (args.length == 3) {
				switch (args[1]) {
					case "replaces":
						commands.addAll(plugin.getLootManager().getConfiguration().getKeys(false));
						StringUtil.copyPartialMatches(args[2], commands, completions);
						break;
					case "removeitem":
					case "additem":
						Player player = (Player) sender;
						player.sendTitle("§2Loot Table UUID", "", 1, 100, 1);
						commands.addAll(plugin.getLootManager().getConfiguration().getKeys(false));
						StringUtil.copyPartialMatches(args[2], commands, completions);
						break;
				}
			} else if (args.length == 4) {
				switch (args[1]) {
					case "additem":
					case "removeitem":
						Player player = (Player) sender;
						player.sendTitle("§2Loot Item UUID", "", 1, 100, 1);
						commands.addAll(plugin.getCustomItemManager().getConfiguration().getKeys(false));
						StringUtil.copyPartialMatches(args[3], commands, completions);
						break;
					case "replaces":
						// TODO add replaceable loot table tab completion suggestions
						commands.add("TODO!!!");
						StringUtil.copyPartialMatches(args[3], commands, completions);
				}

			}

			Collections.sort(completions);
			return completions;
		} else {
			return null;
		}

	}
}
