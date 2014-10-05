package com.gameminers.kahur;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public enum MineralColor {
	STONE	(Blocks.cobblestone, 0x898989, "Stone"),
	COAL	(Items.coal, 0x454545, "Coal"),
	IRON	(Items.iron_ingot, 0xD8AF93, "Iron"),
	LAPIS	(new ItemStack(Items.dye, 1, 4), 0x1846B2, "Lapis Lazuli"),
	GOLD	(Items.gold_ingot, 0xFCEE4B, "Gold"),
	QUARTZ	(Items.quartz, 0xDACEC1, "Nether Quartz"),
	EMERALD	(Items.emerald, 0x17DD62, "Emerald"),
	DIAMOND	(Items.diamond, 0x5DECF5, "Diamond"),
	;
	private final ItemStack material;
	private final int color;
	private final String friendlyName;
	private MineralColor(Block block, int color, String friendlyName) {
		material = new ItemStack(block, 1, 32767);
		this.color = color;
		this.friendlyName = friendlyName;
	}
	private MineralColor(Item item, int color, String friendlyName) {
		material = new ItemStack(item, 1, 32767);
		this.color = color;
		this.friendlyName = friendlyName;
	}
	private MineralColor(ItemStack item, int color, String friendlyName) {
		material = item;
		this.color = color;
		this.friendlyName = friendlyName;
	}
	public ItemStack getMaterial() {
		return material;
	}
	public int getColor() {
		return color;
	}
	public String getFriendlyName() {
		return friendlyName;
	}
}
