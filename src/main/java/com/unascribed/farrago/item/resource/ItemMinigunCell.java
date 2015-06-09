package com.unascribed.farrago.item.resource;

import java.util.List;

import com.unascribed.farrago.FarragoMod;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemMinigunCell extends Item {
	private IIcon empty;
	private IIcon full;
	public ItemMinigunCell() {
		setUnlocalizedName("cell");
		setCreativeTab(FarragoMod.creativeTab);
		setHasSubtypes(true);
	}
	@Override
	public void registerIcons(IIconRegister registry) {
		empty = registry.registerIcon("farrago:cell_minigun_empty");
		full = registry.registerIcon("farrago:cell_minigun");
	}
	@Override
	public int getItemStackLimit(ItemStack stack) {
		return stack.getItemDamage() == getCapacity()+1 ? 16 : 1;
	}
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		list.add(new ItemStack(item, 1, 0));
		list.add(new ItemStack(item, 1, getCapacity()+1));
	}
	@Override
	public IIcon getIconFromDamage(int meta) {
		return meta == getCapacity()+1 ? empty : full;
	}
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return stack.getItemDamage() == getCapacity()+1 ? "item.cell_minigun_empty" : "item.cell_minigun";
	}
	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return stack.getItemDamage() < getCapacity()+1;
	}
	public int getCapacity() {
		return FarragoMod.config.getInt("minigun.shotsPerDrum");
	}
	@Override
	public int getMaxDamage() {
		return getCapacity()+1;
	}
}
