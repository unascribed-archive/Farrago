package com.gameminers.farrago.item.chromatic;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public abstract class ItemChromatic extends Item {
	@Override
	public int getColorFromItemStack(ItemStack stack, int pass) {
		if (hasColor(stack) && pass == 0) {
			return stack.getTagCompound().getInteger("Color");
		}
		return 0xFFFFFF;
	}
	public boolean hasColor(ItemStack stack) {
		return stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("Color");
	}
	public void setColor(ItemStack stack, int color) {
		if (stack != null) {
			NBTTagCompound compound = stack.getTagCompound();
			if (compound == null) {
				compound = new NBTTagCompound();
				stack.setTagCompound(compound);
			}
			compound.setInteger("Color", color);
		}
	}
}
