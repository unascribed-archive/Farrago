package com.gameminers.farrago.item.chromatic;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

import com.gameminers.farrago.FarragoMod;

public class ItemChromaticPickaxe extends ItemPickaxe {
	public ItemChromaticPickaxe() {
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
	public int getHarvestLevel(ItemStack stack, String toolClass) {
		return "pickaxe".equals(toolClass) ? stack.hasTagCompound() && stack.getTagCompound().getBoolean("Diamond") ? 3 : 2 : 0;
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
		bg = register.registerIcon("farrago:chromatic_pickaxe_head");
		fg = register.registerIcon("farrago:chromatic_pickaxe_handle");
	}
	
	@Override
	public int getColorFromItemStack(ItemStack stack, int pass) {
		return Chromatics.getColorFromItemStack(stack, pass);
	}
	
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		list.add(new ItemStack(item, 1, 0));
		ItemStack diamond = new ItemStack(item, 1, 0);
		diamond.setTagCompound(new NBTTagCompound());
		diamond.getTagCompound().setBoolean("Diamond", true);
		list.add(diamond);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return stack.hasTagCompound() && stack.getTagCompound().getBoolean("Diamond") ? "item.chromatic_pickaxe_diamond" : "item.chromatic_pickaxe";
	}
	
	@Override
	public boolean getIsRepairable(ItemStack a, ItemStack b) {
        return b.getItem() == FarragoMod.INGOT && b.getItemDamage() == 0 ? true : super.getIsRepairable(a, b);
    }

}
