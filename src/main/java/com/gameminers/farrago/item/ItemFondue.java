package com.gameminers.farrago.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.gameminers.farrago.FarragoMod;

public class ItemFondue extends ItemFood {
	private IIcon[] icons;
	private int[] healAmounts = {
			8,
			18,
			5
	};
	private String[] names = {
			"cheese",
			"chinese",
			"chocolate"
	};
	public ItemFondue() {
		super(0, false);
		setUnlocalizedName("fondue");
		setTextureName("farrago:fondue");
		setCreativeTab(CreativeTabs.tabFood);
		setHasSubtypes(true);
		setMaxStackSize(1);
	}
	public ItemStack onEaten(ItemStack p_77654_1_, World p_77654_2_, EntityPlayer p_77654_3_) {
        super.onEaten(p_77654_1_, p_77654_2_, p_77654_3_);
        return new ItemStack(FarragoMod.CAQUELON);
    }
	@Override
	public IIcon getIconFromDamage(int damage) {
		return icons[damage];
	}
	@Override
	public int func_150905_g(ItemStack p_150905_1_) {
		// getHealAmount
		return healAmounts[p_150905_1_.getItemDamage()];
	}
	@Override
	public float func_150906_h(ItemStack p_150906_1_) {
		// getSaturation
		return healAmounts[p_150906_1_.getItemDamage()]/10f;
	}
	@Override
	public String getUnlocalizedName(ItemStack p_77667_1_) {
		return "item."+names[p_77667_1_.getItemDamage()]+"_fondue";
	}
	@Override
	public void registerIcons(IIconRegister r) {
		icons = new IIcon[] {
				r.registerIcon("farrago:cheese_fondue"),
				r.registerIcon("farrago:chinese_fondue"),
				r.registerIcon("farrago:chocolate_fondue")
		};
	}
}
