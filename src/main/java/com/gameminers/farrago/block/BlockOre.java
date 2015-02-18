package com.gameminers.farrago.block;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import com.gameminers.farrago.FarragoMod;
import com.google.common.collect.Lists;


public class BlockOre extends Block {
	public BlockOre() {
		super(Material.rock);
		setBlockTextureName("farrago:watashi");
		setCreativeTab(FarragoMod.creativeTab);
		setHardness(3.0f);
		setResistance(5.0f);
		setStepSound(soundTypePiston);
		setBlockName("oreYttrium");
		setBlockTextureName("farrago:yttrium_ore");
		setHarvestLevel("pickaxe", 2, 0);
	}
	
	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> drops = Lists.newArrayList();
		drops.add(new ItemStack(this, 1, metadata));
		if (metadata == 0) {
			if (FarragoMod.copperlessEnvironment) {
				for (int i = 0; i < world.rand.nextInt(fortune+2); i++) {
					drops.add(new ItemStack(FarragoMod.DUST, 1, 7));
				}
			}
		}
		return drops;
	}

	public void registerOres() {
		OreDictionary.registerOre("oreYttrium", new ItemStack(this, 1, 0));
	}
	
}
