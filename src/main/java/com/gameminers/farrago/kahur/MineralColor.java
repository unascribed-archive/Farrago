package com.gameminers.farrago.kahur;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public enum MineralColor {
	POTATO	(Items.potato, 0xEDC77C, "Potato [Spud]", 300),
	GLOW	(Blocks.glowstone, 0xF2CF21, "Glowstone [Torches]", 2400),
	STONE	(Blocks.cobblestone, 0x898989, "Stone", 130),
	COAL	(Items.coal, 0x454545, "Coal", 180),
	IRON	(Items.iron_ingot, 0xD8AF93, "Iron", 250),
	LAPIS	(new ItemStack(Items.dye, 1, 4), 0x1846B2, "Lapis Lazuli", 280),
	GOLD	(Items.gold_ingot, 0xFCEE4B, "Gold [Mobs]", 300),
	QUARTZ	(Items.quartz, 0xDACEC1, "Nether Quartz [Mobs]", 320),
	EMERALD	(Items.emerald, 0x17DD62, "Emerald [Mobs]", 450),
	DIAMOND	(Items.diamond, 0x5DECF5, "Diamond [Mobs]", 850),
	ENDER	(Items.ender_pearl, 0x258474, "Ender Pearl [Mobs, Predictable]", 140),
	OBSIDIAN(Blocks.obsidian, 0x1E001B, "Obsidian [Mobs, Rocket]", 1200),
	;
	private final ItemStack material;
	private final int color;
	private final String friendlyName;
	private final int durability;
	private MineralColor(Block block, int color, String friendlyName, int durability) {
		material = new ItemStack(block, 1, 32767);
		this.color = color;
		this.friendlyName = friendlyName;
		this.durability = durability;
	}
	private MineralColor(Item item, int color, String friendlyName, int durability) {
		material = new ItemStack(item, 1, 32767);
		this.color = color;
		this.friendlyName = friendlyName;
		this.durability = durability;
	}
	private MineralColor(ItemStack item, int color, String friendlyName, int durability) {
		material = item;
		this.color = color;
		this.friendlyName = friendlyName;
		this.durability = durability;
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
	public int getDurability() {
		return durability;
	}
}
