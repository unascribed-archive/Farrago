package com.unascribed.farrago.item.chromatic;

import com.unascribed.farrago.FarragoMod;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.IIcon;

public class ItemChromaticSword extends ItemSword {

	public ItemChromaticSword() {
		super(ToolMaterial.IRON);
		setCreativeTab(FarragoMod.creativeTab);
	}
	
	private IIcon bg;
	private IIcon fg;
	
	
	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}
	
	@Override
	public int getRenderPasses(int metadata) {
		return 2;
	}
	
	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		return pass == 0 ? bg : fg;
	}
	
	@Override
	public void registerIcons(IIconRegister register) {
		bg = register.registerIcon("farrago:chromatic_sword_head");
		fg = register.registerIcon("farrago:chromatic_sword_handle");
	}
	
	@Override
	public int getColorFromItemStack(ItemStack stack, int pass) {
		return Chromatics.getColorFromItemStack(stack, pass);
	}
	
	@Override
	public ItemChromaticSword setUnlocalizedName(String p_77655_1_) {
		super.setUnlocalizedName(p_77655_1_);
		return this;
	}
	
	@Override
	public boolean getIsRepairable(ItemStack a, ItemStack b) {
        return b.getItem() == FarragoMod.INGOT && b.getItemDamage() == 0 ? true : super.getIsRepairable(a, b);
    }

}
