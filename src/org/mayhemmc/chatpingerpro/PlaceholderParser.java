package org.mayhemmc.chatpingerpro;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class PlaceholderParser {

	String str = null;
	public PlaceholderParser(String str) {
		this.str = str;
	}

	public String parseAs(Player sender) {
		String str = this.str;
		str = str.replaceAll("\\{USERNAME\\}", sender.getName());
		str = str.replaceAll("\\{UUID\\}", sender.getUniqueId().toString());
		str = str.replaceAll("\\{DISPLAYNAME\\}", sender.getDisplayName());
		str = ChatColor.translateAlternateColorCodes("&".charAt(0), str);
		return str;
	}

}
