package com.gameminers.farrago.kahur;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

import com.gameminers.farrago.selector.ItemSelector;
import com.gameminers.farrago.selector.OreSelector;
import com.gameminers.farrago.selector.Selector;

public enum MineralColor {
	// Special Types
	GLASS	(new OreSelector("blockGlass"), 0xDDDDFF, 800, "Glass [Potions]"),
	POTATO	(new ItemSelector(Items.potato), 0xEDC77C, 300, "Potato [Spud]"),
	GLOW	(new OreSelector("glowstone"), 0xF2CF21, 2400, "Glowstone [Torches]"),
	OBSIDIAN(new OreSelector("ingotObsidian",
			 new ItemSelector(Blocks.obsidian)), 0x1E001B, 1200, "Obsidian [Rocket]"),
	
	// Regular Tiers
	STONE	(new OreSelector("cobblestone"), 		0x898989, 130, "Stone"),
	COAL	(new OreSelector("coal",
			 new ItemSelector(Items.coal)), 	0x454545, 180, "Coal"),
	QUARTZ	(new OreSelector("gemQuartz"), 			0xDACEC1, 200, "Nether Quartz"),
	IRON	(new OreSelector("ingotIron"), 			0xD8AF93, 250, "Iron"),
	LAPIS	(new OreSelector("gemLapis"), 			0x1846B2, 280, "Lapis Lazuli"),
	GOLD	(new OreSelector("ingotGold"), 			0xFCEE4B, 300, "Gold [Mobs]"),
	YTTRIUM (new OreSelector("ingotYttrium"), 		0xB1B1B1, 320, "Yttrium [Mobs]"),
	EMERALD	(new OreSelector("gemEmerald"), 		0x17DD62, 450, "Emerald [Mobs]"),
	SYTTRIUM(new OreSelector("ingotYttriumSteel"), 	0x9D9D9D, 600, "Yttric Steel [Mobs]"),
	DIAMOND	(new OreSelector("gemDiamond"), 		0x5DECF5, 850, "Diamond [Mobs]"),
	ENDER	(new OreSelector("enderPearl",
			 new ItemSelector(Items.ender_pearl)), 	0x258474, 200, "Ender Pearl [Mobs, Predictable]"),
	;
	private final Selector selector;
	private final int color;
	private final String friendlyName;
	private final int durability;
	private MineralColor(Selector selector, int color, int durability, String friendlyName) {
		this.selector = selector;
		this.color = color;
		this.friendlyName = friendlyName;
		this.durability = durability;
	}
	
	public Selector getSelector() {
		return selector;
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
