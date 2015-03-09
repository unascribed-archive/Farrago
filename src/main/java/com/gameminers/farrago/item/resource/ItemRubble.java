package com.gameminers.farrago.item.resource;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import com.gameminers.farrago.FarragoMod;

public class ItemRubble extends Item {
	public static final int typeCount = 6;
	private IIcon[] icons = new IIcon[typeCount];
	public ItemRubble() {
		setUnlocalizedName("rubble");
		setTextureName("farrago:rubble");
		setCreativeTab(FarragoMod.creativeTab);
		setHasSubtypes(true);
		setMaxStackSize(64);
	}
	@Override
	public IIcon getIconFromDamage(int damage) {
		return damage >= typeCount ? icons[0] : icons[damage];
	}
	@Override
	public String getUnlocalizedName(ItemStack p_77667_1_) {
		return "item.rubble_"+(p_77667_1_.getItemDamage()+1);
	}
	
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < typeCount; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}
	@Override
	public void registerIcons(IIconRegister registry) {
		for (int i = 0; i < typeCount; i++) {
			icons[i] = registry.registerIcon("farrago:rubble_"+(i+1));
		}
	}
}
