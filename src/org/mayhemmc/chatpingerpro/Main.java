package org.mayhemmc.chatpingerpro;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R1.NBTTagCompound;

public class Main extends JavaPlugin implements Listener {

	// Create variable to store Main instance in later.
	public static Main instance;
	public static FileConfiguration cfg;

	@Override
	public void onEnable() {

		// Register instance of
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

		// Get the instance of the player that sent the message
		Player sender = event.getPlayer();

		// Get chat message as array of words
		String[] words = event.getMessage().split(" ");

		// Build message string
		TextComponent component = new TextComponent(String.format(event.getFormat(), sender.getDisplayName(), ""));

		// Iterate over words:
		for(String word : words) {

			boolean wasWordProcessed = false;

			// If pings are enabled & Player has permission to use them
			if(cfg.getBoolean("ping.enable") && (!cfg.getBoolean("ping.permission.use") || sender.hasPermission(cfg.getString("ping.permission.node")))) {

				// Iterate over online players:
				for (Player player : Bukkit.getServer().getOnlinePlayers()) {

					// If word is a players name:
					if(word.equalsIgnoreCase(player.getName())) {

						wasWordProcessed = true;

						// Formulate hover text
						ComponentBuilder hover = new ComponentBuilder();
						for(String line : cfg.getStringList("ping.tooltip")) hover.appendLegacy(new PlaceholderParser(line + "&r\n").parseAs(player));
						hover.removeComponent(hover.getParts().size() -1);

						// Parse the placeholders and add it to the new component
						TextComponent ping = new TextComponent(new PlaceholderParser(cfg.getString("ping.format")).parseAs(event.getPlayer()));
						ping.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover.create()));
					  	component.addExtra(ping);

					}

				}

			}

			// If items are enabled & Player has permission to use them
			if(cfg.getBoolean("item.enable") && (!cfg.getBoolean("item.permission.use") || sender.hasPermission(cfg.getString("item.permission.node")))) {

				// Iterate over the delimiter used for items in the config:
				for (String delimiter : cfg.getStringList("item.delimiter")) {

					// If word is a delimiter:
					if(word.equalsIgnoreCase(delimiter)) {

						// Get the item in the players hand
						ItemStack hand = sender.getInventory().getItemInHand();

						// Iterate over blacklisted items
						boolean isBlacklisted = false;
						for(String item : cfg.getStringList("item.blacklist")) {
							// Make sure the item isnt blacklisted
							if(hand.getType().equals(Material.getMaterial(item))) {
								isBlacklisted = true;
							}
						}

						// If the word is not blacklisted...
						if(!isBlacklisted) {

							wasWordProcessed = true;

							// Parse the placeholders and add it to the new component
							TextComponent item = new TextComponent(new PlaceholderParser(cfg.getString("item.format")).parseAs(event.getPlayer()));
							item.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, (new BaseComponent[]{
								new TextComponent(CraftItemStack.asNMSCopy(hand).save(new NBTTagCompound()).toString())
							}) ));
						  	component.addExtra(item);

						}

					}

				}

			}

			// If the word wasnt yet converted to JSON, just add the word into the component
			if(!wasWordProcessed) component.addExtra(new TextComponent(word));

			// Ensure that spaces are put back inline
			component.addExtra(" ");

		}

		// Cancel origonal chat event
		event.setCancelled(true);

		// Send JSON message to all players
		for (Player player : event.getRecipients()) player.spigot().sendMessage(component);

	}

}
