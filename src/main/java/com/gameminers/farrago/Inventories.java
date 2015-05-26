package com.gameminers.farrago;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;

public class Inventories {

	public static boolean consumeInventoryItem(InventoryPlayer inv, Item item, int meta) {
	    int i = Inventories.find(inv, item, meta);
	
	    if (i < 0) {
	        return false;
	    } else {
	        if (--inv.mainInventory[i].stackSize <= 0) {
	            inv.mainInventory[i] = null;
	        }
	        return true;
	    }
	}

	public static int find(InventoryPlayer inv, Item item, int meta) {
	    for (int i = 0; i < inv.mainInventory.length; i++) {
	        if (inv.mainInventory[i] != null && inv.mainInventory[i].getItem() == item && inv.mainInventory[i].getItemDamage() == meta) {
	            return i;
	        }
	    }
	    
	    return -1;
	}
}
