package com.gameminers.farrago.item;

import java.util.List;
import java.util.Locale;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemVividOrb extends Item {
	public ItemVividOrb() {
		setUnlocalizedName("vivid_orb");
		setTextureName("farrago:vivid_orb");
		setCreativeTab(CreativeTabs.tabMisc);
		setMaxStackSize(16);
	}
	@Override
	public int getColorFromItemStack(ItemStack stack, int pass) {
		if (hasColor(stack)) {
			return stack.getTagCompound().getInteger("OrbColor");
		}
		return 0xDBCCBF;
	}
	public boolean hasColor(ItemStack stack) {
		return stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("OrbColor");
	}
	public void setColor(ItemStack stack, int color) {
		if (stack != null) {
			NBTTagCompound compound = stack.getTagCompound();
			if (compound == null) {
				compound = new NBTTagCompound();
				stack.setTagCompound(compound);
			}
			compound.setInteger("OrbColor", color);
		}
	}
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
		super.addInformation(stack, player, list, advanced);
		if (hasColor(stack) && advanced) {
			list.add("Color: #"+Integer.toHexString(getColorFromItemStack(stack, 0)).toUpperCase(Locale.ENGLISH));
		}
	}
}
