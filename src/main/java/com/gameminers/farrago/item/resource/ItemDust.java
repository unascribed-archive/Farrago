package com.gameminers.farrago.item.resource;

import gminers.kitchensink.Strings;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.oredict.OreDictionary;

import com.gameminers.farrago.FarragoMod;

public class ItemDust extends Item {
	private String[] dustTypes = {
		"iron",
		"gold",
		"emerald",
		"diamond",
		"dorito",
		"yttrium",
		"yttrium_copper",
		"copper",
		"ender"
	};
	private IIcon[] iconsByDamage = new IIcon[dustTypes.length];
	private IIcon iron;
	public ItemDust() {
		setUnlocalizedName("dust");
		setTextureName("farrago:dust");
		setCreativeTab(FarragoMod.creativeTab);
		setHasSubtypes(true);
		setMaxStackSize(64);
	}
	@Override
	public IIcon getIconFromDamage(int damage) {
		return damage >= dustTypes.length ? iron : iconsByDamage[damage];
	}
	@Override
	public String getUnlocalizedName(ItemStack p_77667_1_) {
		int damage = p_77667_1_.getItemDamage();
		return "item.dust_"+(damage >= dustTypes.length ? "iron" : dustTypes[damage]);
	}
	
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < dustTypes.length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}
	
	@Override
	public void addInformation(ItemStack p_77624_1_, EntityPlayer p_77624_2_,
			List p_77624_3_, boolean p_77624_4_) {
		super.addInformation(p_77624_1_, p_77624_2_, p_77624_3_, p_77624_4_);
		if (p_77624_1_.getItemDamage() == 4) {
			p_77624_3_.add(I18n.format("item.dust_dorito.description"));
		}
	}
	@Override
	public boolean hasEffect(ItemStack par1ItemStack, int pass) {
		return par1ItemStack.getItemDamage() == 4;
	}
	@Override
	public void registerIcons(IIconRegister registry) {
		for (int i = 0; i < dustTypes.length; i++) {
			iconsByDamage[i] = registry.registerIcon("farrago:dust_"+dustTypes[i]);
		}
		iron = iconsByDamage[0];
	}
	public void registerOres() {
		for (int i = 0; i < dustTypes.length; i++) {
			OreDictionary.registerOre("dust"+Strings.formatTitleCase(dustTypes[i]).replace(" ", ""), new ItemStack(this, 1, i));
		}
	}
	
}
