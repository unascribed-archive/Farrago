package com.gameminers.farrago.item.resource;

import gminers.kitchensink.Strings;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.oredict.OreDictionary;

import com.gameminers.farrago.FarragoMod;

public class ItemIngot extends Item {
	private String[] ingotTypes = {
		"yttrium",
		"yttrium_copper",
		"copper"
	};
	private IIcon[] iconsByDamage = new IIcon[ingotTypes.length];
	private IIcon iron;
	public ItemIngot() {
		setUnlocalizedName("ingot");
		setTextureName("farrago:ingot");
		setCreativeTab(FarragoMod.creativeTab);
		setHasSubtypes(true);
		setMaxStackSize(64);
	}
	@Override
	public IIcon getIconFromDamage(int damage) {
		return damage >= ingotTypes.length ? iron : iconsByDamage[damage];
	}
	@Override
	public String getUnlocalizedName(ItemStack p_77667_1_) {
		int damage = p_77667_1_.getItemDamage();
		return "item.ingot_"+(damage >= ingotTypes.length ? "yttrium" : ingotTypes[damage]);
	}
	
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < ingotTypes.length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}
	@Override
	public void registerIcons(IIconRegister registry) {
		for (int i = 0; i < ingotTypes.length; i++) {
			iconsByDamage[i] = registry.registerIcon("farrago:ingot_"+ingotTypes[i]);
		}
		iron = iconsByDamage[0];
	}
	public void registerOres() {
		for (int i = 0; i < ingotTypes.length; i++) {
			OreDictionary.registerOre("ingot"+Strings.formatTitleCase(ingotTypes[i]).replace(" ", ""), new ItemStack(this, 1, i));
		}
	}
}
