package com.gameminers.farrago.item.modular;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class ItemModularChestplate extends ItemArmor {

	public ItemModularChestplate() {
		super(ArmorMaterial.IRON, 2, 1);
		setTextureName("farrago:modular_chestplate");
		setUnlocalizedName("modular_chestplate");
	}
	
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot,
			String type) {
		return "farrago:textures/models/armor/modular_armor.png";
	}

}
