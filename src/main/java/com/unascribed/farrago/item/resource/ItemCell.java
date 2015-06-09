package com.unascribed.farrago.item.resource;

import java.util.List;

import com.unascribed.farrago.FarragoMod;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemCell extends Item {
	private String[] cellTypes = {
		"empty",
		"rifle",
		"glow",
		"mine",
		"explode",
		"teleport",
		"blaze"
	};
	private IIcon[] iconsByDamage = new IIcon[cellTypes.length];
	private IIcon empty;
	public ItemCell() {
		setUnlocalizedName("cell");
		setTextureName("farrago:cell");
		setCreativeTab(FarragoMod.creativeTab);
		setHasSubtypes(true);
	}
	@Override
	public int getItemStackLimit(ItemStack stack) {
		return stack.getItemDamage() == 0 ? 64 : 16;
	}
	@Override
	public IIcon getIconFromDamage(int damage) {
		return damage >= cellTypes.length ? empty : iconsByDamage[damage];
	}
	@Override
	public String getUnlocalizedName(ItemStack p_77667_1_) {
		int damage = p_77667_1_.getItemDamage();
		return "item.cell_"+(damage >= cellTypes.length ? "empty" : cellTypes[damage]);
	}
	
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < cellTypes.length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}
	@Override
	public void registerIcons(IIconRegister registry) {
		for (int i = 0; i < cellTypes.length; i++) {
			iconsByDamage[i] = registry.registerIcon("farrago:cell_"+cellTypes[i]);
		}
		empty = iconsByDamage[0];
	}
}
