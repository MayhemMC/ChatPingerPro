package org.mayhemmc.chatpingerpro;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	// Create variable to store Main instance in later.
	public static Main instance;
	public static FileConfiguration cfg;
	
	@Override
	public void onEnable() {
		
		// Register instance of Main.
		instance = this;

		// Save default config file.
		this.saveDefaultConfig();
		
		// Save current config into `cfg`.
		cfg = this.getConfig();
		
	}
	
	@Override
	public void onDisable() {
		
		// Unregister Main instance.
		instance = null;
		
	}
	
}
