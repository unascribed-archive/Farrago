package com.gameminers.farrago.item;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemUtilityBelt extends ItemArmor {
	public ItemUtilityBelt() {
		super(ArmorMaterial.CHAIN, 0, 2);
		setTextureName("farrago:utility_belt");
		setUnlocalizedName("utility_belt");
	}
	
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot,
			String type) {
		return "farrago:textures/items/blank.png";
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
		int count = getExtraRows(stack);
		if (count == 1) {
			list.add("\u00A77Adds 1 extra hotbar");
		} else {
			list.add("\u00A77Adds "+count+" extra hotbars");
		}
	}
	
	public int getExtraRows(ItemStack stack) {
		if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("Rows", 1)) {
			return 1;
		}
		return stack.getTagCompound().getByte("Rows");
	}
	
	public ItemStack setExtraRows(ItemStack stack, int rows) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setByte("Rows", (byte)rows);
		return stack;
	}

	public String getRowName(ItemStack belt, int idx) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
