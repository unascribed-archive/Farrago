package com.gameminers.farrago.block;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.gameminers.farrago.FarragoMod;
import com.google.common.collect.Lists;

public class BlockGlow extends Block {

	public BlockGlow() {
		super(Material.circuits);
		setCreativeTab(null);
		setHardness(0.0f);
		setResistance(0.0f);
		setStepSound(soundTypeCloth);
		setBlockName("glow");
		setBlockTextureName("farrago:blank");
		setLightLevel(0.9f);
	}
	
	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
		return false;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean isNormalCube() {
		return false;
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_) {
        return null;
    }
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		float x1, y1, z1, x2, y2, z2;
		switch (world.getBlockMetadata(x, y, z)) {
		case 0:
			// top
			x1 = 0.0f;
			y1 = 0.9f;
			z1 = 0.0f;
			x2 = 1.0f;
			y2 = 1.0f;
			z2 = 1.0f;
			break;
		case 1:
			// bottom
			x1 = 0.0f;
			y1 = 0.0f;
			z1 = 0.0f;
			x2 = 1.0f;
			y2 = 0.1f;
			z2 = 1.0f;
			break;
		case 3:
			// west (+Z)
			x1 = 0.0f;
			y1 = 0.0f;
			z1 = 0.0f;
			x2 = 1.0f;
			y2 = 1.0f;
			z2 = 0.1f;
			break;
		case 2:
			// east (-Z)
			x1 = 0.0f;
			y1 = 0.0f;
			z1 = 0.9f;
			x2 = 1.0f;
			y2 = 1.0f;
			z2 = 1.0f;
			break;
		case 5:
			// south (+X)
			x1 = 0.0f;
			y1 = 0.0f;
			z1 = 0.0f;
			x2 = 0.1f;
			y2 = 1.0f;
			z2 = 1.0f;
			break;
		case 4:
			// north (-X)
			x1 = 0.9f;
			y1 = 0.0f;
			z1 = 0.0f;
			x2 = 1.0f;
			y2 = 1.0f;
			z2 = 1.0f;
			break;
		default:
			x1 = y1 = z1 = 0.0f;
			x2 = y2 = z2 = 1.0f;
		}
		setBlockBounds(x1, y1, z1, x2, y2, z2);
	}
	
	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> drops = Lists.newArrayList();
		drops.add(new ItemStack(FarragoMod.DUST, 1, 5));
		drops.add(new ItemStack(Items.glowstone_dust));
		return drops;
	}
	
	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		FarragoMod.proxy.glowRandomDisplayTick(world, x, y, z, rand);
	}

}
