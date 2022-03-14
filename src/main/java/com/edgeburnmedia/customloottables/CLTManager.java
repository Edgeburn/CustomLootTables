package com.edgeburnmedia.customloottables;

import com.edgeburnmedia.customloottables.utils.ConfigFileManager;

/**
 * Manage the custom loot tables file
 *
 * @author Edgeburn Media
 */
public class CLTManager extends ConfigFileManager<CustomLootTable> {


	public CLTManager(CustomLootTables plugin, String fileName) {
		super(plugin, fileName);
	}

	@Override
	public void reloadConfig() {

	}

	@Override
	public void addEntry(CustomLootTable entry) {

	}

	@Override
	public CustomLootTable getEntry(String searchParameter) {
		return null;
	}


}

