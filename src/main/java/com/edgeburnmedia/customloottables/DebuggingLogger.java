package com.edgeburnmedia.customloottables;

public class DebuggingLogger {
	private final CustomLootTables plugin;
	private final boolean enabled;

	public DebuggingLogger(CustomLootTables plugin) {
		this.plugin = plugin;
		this.enabled = plugin.getConfig().getBoolean("debug");
	}

	public void log(String... messages) {
		if (enabled) {
			for (String msg : messages) {
				plugin.getServer().broadcast("§6CLT DEBUG > " + msg, "customloot.debug");
			}
		}
	}
}
