package com.gameminers.farrago.item.resource;

import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

import com.gameminers.farrago.FarragoMod;
import com.google.common.collect.Maps;

public class ItemCrafting extends Item {
	private static final String[] materials = {
		"circuit_board:16",
		"small_plate_blank:1",
		"plate_blank:1"
	};
	private static Map<String, IIcon> icons = Maps.newHashMap();
	private static Map<String, Integer> stackSizeLimits = Maps.newHashMap();
	public ItemCrafting() {
		setCreativeTab(FarragoMod.creativeTab);
	}
	@Override
	public void registerIcons(IIconRegister registry) {
		icons.clear();
		for (String s : materials) {
			String[] split = s.split(":");
			System.out.println(split[0]);
			icons.put(split[0], registry.registerIcon("farrago:"+split[0]));
			stackSizeLimits.put(split[0], Integer.parseInt(split[1]));
		}
	}
	public String getType(ItemStack stack) {
		if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("Type", 8)) {
			FarragoMod.log.warn("Crafting item has no tag!?");
			stack.stackSize = 0;
			return "";
		}
		String type = stack.getTagCompound().getString("Type");
		if (!stackSizeLimits.containsKey(type)) {
			FarragoMod.log.warn("Crafting item has an invalid type!?");
			stack.stackSize = 0;
			return "";
		}
		return type;
	}
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item."+getType(stack);
	}
	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		return icons.get(getType(stack));
	}
	@Override
	public IIcon getIconIndex(ItemStack stack) {
		return icons.get(getType(stack));
	}
	@Override
	public int getItemStackLimit(ItemStack stack) {
		return stackSizeLimits.get(getType(stack));
	}
	@Override
	public int getColorFromItemStack(ItemStack stack, int pass) {
		return stack.hasTagCompound() && stack.getTagCompound().hasKey("Color", 3) ? stack.getTagCompound().getInteger("Color") : -1;
	}
	@Override
	public boolean getHasSubtypes() {
		return true;
	}
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (String s : materials) {
			ItemStack stack = new ItemStack(item, 0, 1);
			stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setString("Type", s.split(":")[0]);
			list.add(stack);
		}
	}
}
