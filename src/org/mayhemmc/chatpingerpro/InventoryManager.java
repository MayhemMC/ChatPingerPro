package org.mayhemmc.chatpingerpro;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

public class InventoryManager {

	private static Map<String,Player> players = new HashMap<>();
	private static Map<String,PlayerInventory> inventorys = new HashMap<>();
	private String uuid = null;

	public InventoryManager(Player sender) {

		// Generate a UUID for this players inventory
		uuid = UUID.randomUUID().toString();

		// Add inventory to map
		players.put(uuid, sender);
		inventorys.put(uuid, sender.getInventory());

	}

	public String getCommand() {

		// Return command used to see this inventory
		return "/chatpingerproviewinventory " + uuid;

	}

	public static void showInv(Player viewer, String uuid) {
		
		Inventory inv = (Inventory) inventorys.get(uuid);

		// Open the inventory
		viewer.openInventory(inv);

	}

}
