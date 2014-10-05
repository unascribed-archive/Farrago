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
							"S  ",
							"MP ",
							" /S",
							'S', new ItemStack(Blocks.planks, 1, body.ordinal()),
							'P', new ItemStack(Blocks.planks, 1, drum.ordinal()),
							'M', pump.getMaterial(),
							'/', Items.stick);
					GameRegistry.addRecipe(kahur,
							"  S",
							" PM",
							"S/ ",
							'S', new ItemStack(Blocks.planks, 1, body.ordinal()),
							'P', new ItemStack(Blocks.planks, 1, drum.ordinal()),
							'M', pump.getMaterial(),
							'/', Items.stick);
				}
			}
		}
	}
}
