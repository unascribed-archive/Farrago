package com.gameminers.farrago.item.chromatic;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class Chromatics {
	public static int getColorFromItemStack(ItemStack stack, int pass) {
		if (hasColor(stack) && pass == 0) {
			return stack.getTagCompound().getInteger("Color");
		}
		return 0xFFFFFF;
	}
	public static boolean hasColor(ItemStack stack) {
		return stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("Color");
	}
	public static void setColor(ItemStack stack, int color) {
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
