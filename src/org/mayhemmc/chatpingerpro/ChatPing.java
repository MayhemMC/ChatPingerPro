package org.mayhemmc.chatpingerpro;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatPing {

	public ChatPing(AsyncPlayerChatEvent event) {

		// Get player that sent the chat.
		Player sender = event.getPlayer();

		// Make sure the sender has permissions to use this function...
		if(Main.cfg.getBoolean("pings.permission.use")) {
			if(sender.hasPermission(Main.cfg.getString("pings.permission.node"))) parseChatEvent(event);
		} else {
			parseChatEvent(event);
		}

	}

	// Process the chat event.
	private void parseChatEvent(AsyncPlayerChatEvent event) {

		// Get chat message as array of words
		String[] words = event.getMessage().split(" ");

		// Build message string
		TextComponent component = new TextComponent(String.format(event.getFormat(), event.getPlayer().getDisplayName(), ""));

		// Iterate over words:
		for(String word : words) {

			boolean isPlayerName = false;

			// Iterate over online players:
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {

				// If word is a players name:
				if(word.equalsIgnoreCase(player.getName())) {

					isPlayerName = true;

					// Formulate hover text
					ComponentBuilder hover = new ComponentBuilder();
					for(String line : Main.cfg.getStringList("pings.tooltip")) hover.appendLegacy(new PlaceholderParser(line + "&r\n").parseAs(player));
					hover.removeComponent(hover.getParts().size() -1);

					// Parse the placeholders and add it to the new component
					TextComponent ping = new TextComponent(new PlaceholderParser(Main.cfg.getString("pings.format")).parseAs(event.getPlayer()));
					ping.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover.create()));
				  	component.addExtra(ping);

				}

			}

			// If the word wasnt an online players name, just add the word into the component
			if(!isPlayerName) component.addExtra(new TextComponent(word));

			// Ensure that spaces are put back inline
			component.addExtra(" ");

		}

		// Set new message string to the component
		event.setCancelled(true);
		for (Player player : Bukkit.getServer().getOnlinePlayers()) player.spigot().sendMessage(component);

	}

}
