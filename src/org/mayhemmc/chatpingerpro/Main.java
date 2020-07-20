package org.mayhemmc.chatpingerpro;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

	// Create variable to store Main instance in later.
	public static Main instance;
	public static FileConfiguration cfg;

	@Override
	public void onEnable() {

		// Register instance of Main.
		instance = this;

		// Save default config file.
		this.saveDefaultConfig();

		// Register event listeners
		getServer().getPluginManager().registerEvents(this, this);

		// Save current config into `cfg`.
		cfg = this.getConfig();

	}

	@Override
	public void onDisable() {

		// Unregister Main instance.
		instance = null;

	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {

		// If pings are enabled in the config...
		if(cfg.getBoolean("pings.enable")) new ChatPing(event);

		// If items are enabled in the config...
		if(cfg.getBoolean("items.enable")) new ChatItem(event);

		// If inventory are enabled in the config...
		if(cfg.getBoolean("inventory.enable")) new ChatInventory(event);

	}

}
