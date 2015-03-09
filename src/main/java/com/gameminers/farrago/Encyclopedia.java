package com.gameminers.farrago;

import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Maps;


public class Encyclopedia {
	private static final Map<Item, String> descriptions = Maps.newHashMap();
	public static void init() {
		descriptions.put(FarragoMod.KAHUR, "A strange weapon that uses everyday items as ammunition. The more complex they are, the more damage they do.");
		descriptions.put(FarragoMod.RUBBLE, "Leftover trash from scrapping an object. Can be used as fuel in a furnace, but it will only last until all flammable material is burnt.");
		descriptions.put(FarragoMod.VIVID_ORB, "This orb is capable of storing the very essence of a color. Nobody knows how to make brightly colored ones anymore, and as such they can only be found in the world.");
		descriptions.put(Item.getItemFromBlock(FarragoMod.COMBUSTOR), "This machine uses gunpowder or TNT as an alternate, and very fast, way to smelt items.");
		descriptions.put(Item.getItemFromBlock(FarragoMod.SCRAPPER), "This machine can break down items and blocks into what was used to craft them. It can only recover metals and gems, however.");
		descriptions.put(Item.getItemFromBlock(FarragoMod.NETHER_STAR_BLOCK), "A solid block of Nether Stars. You're either rich or insane.");
		descriptions.put(FarragoMod.BLUNDERBUSS, "This somewhat shoddy gun uses gunpowder to detonate gravel and fire the shards in the general direction it's pointed in. Good for crowd control.");
		descriptions.put(FarragoMod.RIFLE, "This advanced weapon can use cells filled with a variety of dusts to cause reactions and shoot lasers with different effects. Sneak+Shoot to change modes. Be careful not to overcharge it!");
		descriptions.put(FarragoMod.DUST, "Metal powder. Don't breathe this. Obtained by scrapping objects; scrapping an ingot or gem is a 1:1 conversion.");
		descriptions.put(FarragoMod.INGOT, "A solid bar of metal. Useful for a variety of crafting recipes.");
	}
	
	public static void process(ItemStack itemStack, EntityPlayer entityPlayer, List<String> toolTip, boolean showAdvancedItemTooltips) {
		if (itemStack.getItem() == FarragoMod.RUBBLE && itemStack.getItemDamage() == 5) return;
		if (descriptions.containsKey(itemStack.getItem())) {
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
				toolTip.add("\u00A7bInformation");
				String descr = descriptions.get(itemStack.getItem());
				toolTip.addAll(Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(descr, 200));
			} else {
				toolTip.add("Hold \u00A7b<Shift> \u00A77for more information");
			}
		}
	}

}
