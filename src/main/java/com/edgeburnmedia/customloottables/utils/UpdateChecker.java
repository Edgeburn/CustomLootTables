package com.edgeburnmedia.customloottables.utils;

import com.edgeburnmedia.customloottables.CustomLootTables;
import org.bukkit.Bukkit;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;

public class UpdateChecker {

	public UpdateChecker(CustomLootTables pl) {
		Bukkit.getScheduler().runTaskAsynchronously(pl, () -> {
			boolean updateAvailable = false;
			String updateUrl = "";
			try {
				JSONObject json = JsonReader.readJsonFromUrl("https://api.edgeburnmedia.com/plugin_update/customloottables/" + pl);
				updateAvailable = json.getBoolean("update_available");
				updateUrl = json.getString("update_url");
			} catch (IOException e) {
				pl.getLogger().warning("Couldn't check for updates! The update server may be down.");
			} finally {
				if (updateAvailable) {
					pl.setUpdateAvailable(true);
					pl.setUpdateURL(updateUrl);
				} else {
					pl.getLogger().info("No updates found");
				}
			}
		});
	}

	public static class JsonReader {

		private static String readAll(Reader rd) throws IOException {
			StringBuilder sb = new StringBuilder();
			int cp;
			while ((cp = rd.read()) != -1) {
				sb.append((char) cp);
			}
			return sb.toString();
		}

		public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
			InputStream is = new URL(url).openStream();
			try {
				BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
				String jsonText = readAll(rd);
				JSONObject json = new JSONObject(jsonText);
				return json;
			} finally {
				is.close();
			}
		}
	}
}
