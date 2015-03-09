package com.gameminers.farrago.item.chromatic;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import com.gameminers.farrago.FarragoMod;

public class ItemChromaticArmor extends ItemArmor {
	public ItemChromaticArmor(int p_i45325_3_) {
		super(ArmorMaterial.IRON, 2, p_i45325_3_);
		setCreativeTab(FarragoMod.creativeTab);
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		if ("overlay".equals(type)) return "farrago:textures/items/blank.png";
		return slot == 2 ? "farrago:textures/models/armor/chromatic_layer_2.png" : "farrago:textures/models/armor/chromatic_layer_1.png";
	}
	
	@Override
	public boolean hasColor(ItemStack p_82816_1_) {
		return Chromatics.hasColor(p_82816_1_);
	}
	
	@Override
	public int getColor(ItemStack p_82814_1_) {
		return Chromatics.getColorFromItemStack(p_82814_1_, 0);
	}
	
	@Override
	public int getColorFromItemStack(ItemStack stack, int pass) {
		return Chromatics.getColorFromItemStack(stack, pass);
	}
	
	@Override
	public ItemChromaticArmor setTextureName(String p_111206_1_) {
		super.setTextureName(p_111206_1_);
		return this;
	}
	
	@Override
	public ItemChromaticArmor setUnlocalizedName(String p_77655_1_) {
		super.setUnlocalizedName(p_77655_1_);
		return this;
	}
	
	@Override
	public boolean getIsRepairable(ItemStack a, ItemStack b) {
        return b.getItem() == FarragoMod.INGOT && b.getItemDamage() == 0 ? true : super.getIsRepairable(a, b);
    }
}
