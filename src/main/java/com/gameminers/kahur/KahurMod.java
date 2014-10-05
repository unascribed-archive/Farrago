package com.gameminers.kahur;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid="kahur",name="Kahur",version="0.1")
public class KahurMod {
	/*
	 * Damage is calculated based off of two values: Mass and Magic.
	 * 
	 * Mass is determined by the depth and contents of the crafting chain.
	 * An item with no way to craft it has a mass of 1.
	 * For example, a diamond has a mass of 1. A diamond block has a mass of 9.
	 * A gold ingot has a mass of 9 due to the nugget recipe, and a gold block has a mass of 81.
	 * Crafting recipes involving only one item are skipped. As such, a gold nugget has a mass of 1 instead of Infinity.
	 * Crafting trees deeper than 12 entries will be truncated to 12.
	 * 
	 * Magic is determined by enchantments and enchantment glow.
	 * If the item hasEffect, but no enchantments, magic += 12. (Assume it's a special item.)
	 * For every enchantment on the item, magic += (1 + enchantmentLevel)
	 * If the item has the generic.attackDamage attribute, round it down to an integer and add half of that to the magic.
	 * For every other attribute, add attributeValue/8, rounded down, to the magic.
	 * 
	 * Damage is equal to mass + (magic*2).
	 * 
	 * Drop chance is determined by mass and magic.
	 * For a given item, it's chance of dropping is 1 in max(1, 50-(mass+magic)).
	 * Probably not the best method. Needs research.
	 */
	public static ItemKahur KAHUR;
	public static CreativeTabs creativeTab = new CreativeTabs("kahur") {
		
		@Override
		public Item getTabIconItem() {
			return KAHUR;
		}
	};
	@EventHandler
	public void onInit(FMLInitializationEvent e) {
		KAHUR = new ItemKahur();
		GameRegistry.registerItem(KAHUR, "kahur");
		for (WoodColor body : WoodColor.values()) {
			for (WoodColor drum : WoodColor.values()) {
				for (MineralColor pump : MineralColor.values()) {
					ItemStack kahur = new ItemStack(KAHUR);
					NBTTagCompound tag = new NBTTagCompound();
					tag.setString("KahurBodyMaterial", body.name());
					tag.setString("KahurDrumMaterial", drum.name());
					tag.setString("KahurPumpMaterial", pump.name());
					kahur.setTagCompound(tag);
					GameRegistry.addRecipe(kahur,
							"B  ",
							"PD ",
							" /B",
							'B', new ItemStack(Blocks.planks, 1, body.ordinal()),
							'D', new ItemStack(Blocks.planks, 1, drum.ordinal()),
							'P', pump.getMaterial(),
							'/', Items.stick);
					GameRegistry.addRecipe(kahur,
							"  B",
							" DP",
							"B/ ",
							'B', new ItemStack(Blocks.planks, 1, body.ordinal()),
							'D', new ItemStack(Blocks.planks, 1, drum.ordinal()),
							'P', pump.getMaterial(),
							'/', Items.stick);
				}
			}
		}
	}
}
