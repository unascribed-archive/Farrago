package com.unascribed.farrago.item;

import gminers.kitchensink.RandomPool;

import java.util.List;

import com.unascribed.farrago.FarragoMod;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMapBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class ItemUndefined extends ItemMapBase {
	private final String[] names = {
		"null",
		"nil",
		"undefined",
		"0",
		"** ERROR java.lang.NullPointerException **",
		"missingno.",
		"INTERNAL ERROR",
		"ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â£",
		"æ–‡å­—åŒ–ã‘",
		"������"
	};
	public ItemUndefined() {
		setUnlocalizedName("undefined");
		setCreativeTab(FarragoMod.creativeTab);
		setHasSubtypes(true);
		setMaxStackSize(1);
	}
	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		switch (itemRand.nextInt(3)) {
		case 0:
			return RandomPool.getRandomFrom(names);
		case 1:
			return Integer.toHexString(stack.hashCode());
		case 2:
			return Integer.toBinaryString(stack.hashCode());
		}
		return "undefined";
	}
	@Override
	public float getDigSpeed(ItemStack itemstack, Block block, int metadata) {
		return (float) itemRand.nextGaussian() * (itemRand.nextFloat()*5f);
	}
	@Override
	public boolean canHarvestBlock(Block par1Block, ItemStack itemStack) {
		return itemRand.nextBoolean();
	}
	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return itemRand.nextDouble();
	}
	@Override
	public boolean shouldRotateAroundWhenRendering() {
		return itemRand.nextBoolean();
	}
	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return itemRand.nextBoolean();
	}
	@Override
	public boolean isFull3D() {
		return itemRand.nextBoolean();
	}
	@Override
	public boolean hasEffect(ItemStack par1ItemStack, int pass) {
		return itemRand.nextBoolean();
	}
	@Override
	public int getHarvestLevel(ItemStack stack, String toolClass) {
		return itemRand.nextInt(12);
	}
	@Override
	public boolean isMap() {
		return itemRand.nextBoolean();
	}
	@Override
	public void getSubItems(Item p_150895_1_, CreativeTabs p_150895_2_, List p_150895_3_) {}
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float xo, float yo, float zo) {
		if (!world.isRemote) {
			Block block = world.getBlock(x, y, z);
			int id = Block.getIdFromBlock(block);
			String name = Block.blockRegistry.getNameForObject(block);
			int meta = world.getBlockMetadata(x, y, z);
			TileEntity te = world.getTileEntity(x, y, z);
			String tes = (te == null ? "undefined" : te.getClass().getName());
			player.addChatMessage(new ChatComponentText("["+id+"] "+name+"@"+meta+" "+tes));
		}
		return true;
	}
	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer holder, EntityLivingBase entity) {
		if (!holder.worldObj.isRemote) {
			holder.addChatMessage(new ChatComponentText("["+entity.getEntityId()+"] "+entity.getClass().getName()));
		}
		return true;
	}
}
