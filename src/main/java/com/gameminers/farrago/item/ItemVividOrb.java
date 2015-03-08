package com.gameminers.farrago.item;

import java.awt.Color;
import java.util.List;
import java.util.Locale;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

import com.gameminers.farrago.FarragoMod;

public class ItemVividOrb extends Item {
	private IIcon orb;
	private IIcon shine;
	public ItemVividOrb() {
		setUnlocalizedName("vivid_orb");
		setTextureName("farrago:vivid_orb");
		setCreativeTab(FarragoMod.creativeTab);
		setMaxStackSize(16);
	}
	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}
	@Override
	public int getRenderPasses(int metadata) {
		return 2;
	}
	@Override
	public int getColorFromItemStack(ItemStack stack, int pass) {
		if (hasColor(stack)) {
			if (pass == 0) {
				return stack.getTagCompound().getInteger("OrbColor");
			} else if (pass == 1) {
				if (!stack.getTagCompound().hasKey("OrbColorBright")) {
					setColor(stack, stack.getTagCompound().getInteger("OrbColor"));
				}
				return stack.getTagCompound().getInteger("OrbColorBright");
			}
		}
		return 0xDBCCBF;
	}
	public boolean hasColor(ItemStack stack) {
		return stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("OrbColor");
	}
	public void setColor(ItemStack stack, int color) {
		if (stack != null) {
			NBTTagCompound compound = stack.getTagCompound();
			if (compound == null) {
				compound = new NBTTagCompound();
				stack.setTagCompound(compound);
			}
			compound.setInteger("OrbColor", color);
			int r = (color >> 16) & 0xFF;
			int g = (color >> 8) & 0xFF;
			int b = color & 0xFF;
			float[] hsb = Color.RGBtoHSB(r, g, b, null);
			hsb[2] = (float)Math.min(1.0f, hsb[2] * 1.3);
			hsb[1] = (float)Math.min(1.0f, hsb[1] * 1.2);
			int rgb = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
			compound.setInteger("OrbColorBright", rgb);
		}
	}
	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		return pass == 0 ? orb : shine;
	}
	@Override
	public void registerIcons(IIconRegister registry) {
		orb = registry.registerIcon("farrago:vivid_orb");
		shine = registry.registerIcon("farrago:vivid_orb_shine");
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
		super.addInformation(stack, player, list, advanced);
		if (hasColor(stack) && advanced) {
			list.add("Color: #"+Integer.toHexString(getColorFromItemStack(stack, 0)).toUpperCase(Locale.ENGLISH));
		}
	}
}
