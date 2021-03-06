package org.mayhemmc.chatpingerpro;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.stream.Collectors;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.File;

import org.bukkit.event.inventory.InventoryClickEvent;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R1.NBTTagCompound;

public class Main extends JavaPlugin implements Listener {

	// Create variable to store Main instance in later.
	public static Main instance;
	public static FileConfiguration cfg;
	public static File swears;

	// Copy plugin asset utility
	private void copy(InputStream in, File file) {
    	try {
        	OutputStream out = new FileOutputStream(file);
        	byte[] buf = new byte[1024];
        	int len;
        	while((len = in.read(buf)) > 0) out.write(buf,0,len);
	        out.close();
	        in.close();
    	} catch (Exception e) {
        	e.printStackTrace();
    	}
	}

	@Override
	public void onEnable() {

		// Register instance of
		instance = this;

		// Save default config file.
		this.saveDefaultConfig();

		// Save default swear words file
		swears = new File(this.getDataFolder(), "swears.txt");
		if(!swears.exists()) copy(getResource("swears.txt"), swears);

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

	// Internal command for when a user clicks on an inventory link
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("chatpingerproviewinventory")) {
			Player viewer = (Player) sender;
			String uuid = args[0].toLowerCase();
			InventoryManager.showInv(viewer, uuid);
			return true;
		}
		return false;
	}

	// Make sure players cant steal items out of the inventory preview
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getInventory().getType().getDefaultTitle().equals("Player")) {
			event.setCancelled(true);
		}
	}

	// Moniter and parse chat events
	@EventHandler(priority = EventPriority.MONITOR)
	public void onChat(AsyncPlayerChatEvent event) {

		// Make sure the plugin is only canceling events it has to
		boolean stringWasModified = false;

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
						TextComponent ping = new TextComponent(new PlaceholderParser(cfg.getString("ping.format")).parseAs(player));
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

			// If pings are enabled & Player has permission to use them
			if(cfg.getBoolean("inventory.enable") && (!cfg.getBoolean("inventory.permission.use") || sender.hasPermission(cfg.getString("inventory.permission.node")))) {

				// Iterate over the delimiter used for items in the config:
				for (String delimiter : cfg.getStringList("inventory.delimiter")) {

					// If word is a delimiter:
					if(word.equalsIgnoreCase(delimiter)) {

						wasWordProcessed = true;

						// Parse the placeholders and add it to the new component
						TextComponent link = new TextComponent(new PlaceholderParser(cfg.getString("inventory.format")).parseAs(sender));
						link.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, new InventoryManager(sender).getCommand()));
					  	component.addExtra(link);

					}

				}

			}

			// If swear filter is enabled & Player does not have bypass permission
			if(cfg.getBoolean("swear-filter.enable") && !sender.hasPermission(cfg.getString("swear-filter.bypass-permission"))) {

				try {

					// Iterate over each swear word:
					BufferedReader br = new BufferedReader(new FileReader(swears));

					for (String line : br.lines().collect(Collectors.toList())) {

						// If word is a delimiter:
						if(word.equalsIgnoreCase(line)) {

							wasWordProcessed = true;

							// Parse the placeholders and add it to the new component
						  	component.addExtra(
						  	  ChatColor.translateAlternateColorCodes("&".charAt(0),
						  	    cfg.getString("swear-filter.format")
						  	      .replaceAll("\\{CENSORED\\}", StringUtils.repeat("*", line.length()))
						  	));

						}

					}

					br.close();

				} catch(IOException err) {
					err.printStackTrace();
				}

			}

			// If the word wasnt yet converted to JSON, just add the word into the component
			if(wasWordProcessed) {
				stringWasModified = true;
			} else {
				component.addExtra(new TextComponent(word));
			}

			// Ensure that spaces are put back inline
			component.addExtra(" ");

		}

		// Send JSON message to all players if the event wasnt canceled by another plugin
		if(!event.isCancelled() || stringWasModified) {

			// Send JSON message
			for (Player player : event.getRecipients()) player.spigot().sendMessage(component);

			// Cancel origonal chat event
			event.setCancelled(true);

		}

	}

}
