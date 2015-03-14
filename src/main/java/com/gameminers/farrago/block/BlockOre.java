package com.gameminers.farrago.block;

import gminers.kitchensink.Strings;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import com.gameminers.farrago.FarragoMod;
import com.google.common.collect.Lists;


public class BlockOre extends Block implements NameDelegate {
	private String[] oreTypes = {
		"yttrium",
		"apocite",
		"xenotime"
	};
	private int[] harvestLevels = {
		2,
		3,
		0
	};
	private IIcon[] icons = new IIcon[oreTypes.length];
	private IIcon xenotimePole;
	public BlockOre() {
		super(Material.rock);
		setBlockTextureName("farrago:watashi");
		setCreativeTab(FarragoMod.creativeTab);
		setHardness(3.0f);
		setResistance(5.0f);
		setStepSound(soundTypePiston);
		for (int i = 0; i < oreTypes.length; i++) {
			setHarvestLevel("pickaxe", harvestLevels[i], i);
		}
	}
	
	@Override
	public String getUnlocalizedName(int meta) {
		return "tile.ore_"+(meta >= oreTypes.length ? oreTypes[0] : oreTypes[meta]);
	}
	
	@Override
	public int damageDropped(int meta) {
		return meta;
	}
	
	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> drops = Lists.newArrayList();
		if (metadata == 0) {
			for (int i = 0; i < world.rand.nextInt(fortune+3)+1; i++) {
				drops.add(new ItemStack(FarragoMod.DUST, 1, 5));
			}
			if (world.rand.nextInt(8) == 0) {
				drops.add(new ItemStack(FarragoMod.DUST, 1, 6));
			}
			if (FarragoMod.copperlessEnvironment) {
				for (int i = 0; i < world.rand.nextInt(fortune+2); i++) {
					drops.add(new ItemStack(FarragoMod.DUST, 1, 7));
				}
			}
		} else if (metadata == 1) {
			drops.add(new ItemStack(FarragoMod.APOCITE, world.rand.nextInt(fortune+1)+1, 0));
		} else if (metadata == 2) {
			if (world.rand.nextInt(80) == 0) {
				drops.add(new ItemStack(FarragoMod.DUST, 1, 5));
			}
			drops.add(new ItemStack(Blocks.cobblestone));
		}
		return drops;
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < oreTypes.length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}
	
	@Override
	public IIcon getIcon(int side, int meta) {
		if ((meta == 2 || meta == 1) && (side == 0 || side == 1)) {
			return xenotimePole;
		}
		return meta >= oreTypes.length ? icons[0] : icons[meta];
	}
	
	@Override
	public void registerBlockIcons(IIconRegister registry) {
		xenotimePole = registry.registerIcon("farrago:ore_xenotime_top");
		for (int i = 0; i < oreTypes.length; i++) {
			icons[i] = registry.registerIcon("farrago:ore_"+oreTypes[i]);
		}
	}
	
	public void registerOres() {
		for (int i = 0; i < oreTypes.length; i++) {
			OreDictionary.registerOre("ore"+Strings.formatTitleCase(oreTypes[i]).replace(" ", ""), new ItemStack(this, 1, i));
		}
	}
	
}
