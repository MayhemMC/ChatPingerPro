package org.mayhemmc.chatpingerpro;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlaceholderParser {

	String str = null;
	public PlaceholderParser(String str) {
		this.str = str;
	}

	public String parseAs(Player sender) {
		String str = this.str;

		// Parse USERNAME placeholder
		str = str.replaceAll("\\{USERNAME\\}", sender.getName());

		// Parse UUID placeholder
		str = str.replaceAll("\\{UUID\\}", sender.getUniqueId().toString());

		// Parse DISPLAYNAME placeholder
		str = str.replaceAll("\\{DISPLAYNAME\\}", sender.getDisplayName());

		// Parse ITEM placeholder
		try {
			ItemStack item = sender.getInventory().getItemInHand();
			ItemMeta meta = item.getItemMeta();
			str = str.replaceAll("\\{ITEM\\}", meta.hasDisplayName() ? meta.getDisplayName() : (meta.hasLocalizedName() ? meta.getLocalizedName() : item.getType().name()));
		} catch (NullPointerException error) {
			// TODO
		}

		// Translate color codes
		str = ChatColor.translateAlternateColorCodes("&".charAt(0), str);
		return str;
	}

}
