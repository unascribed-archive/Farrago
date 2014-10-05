package com.gameminers.kahur;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

public class ItemKahur extends Item {
	private IIcon body;
	private IIcon drum;
	private IIcon pump;
	public ItemKahur() {
		setUnlocalizedName("kahur");
		setTextureName("kahur:kahur");
		setCreativeTab(KahurMod.creativeTab);
	}
	
	@Override
	public boolean isFull3D() {
		return true;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
		WoodColor bodyColor = WoodColor.BIG_OAK;
		WoodColor drumColor = WoodColor.SPRUCE;
		MineralColor pumpColor = MineralColor.IRON;
		if (stack.hasTagCompound()){ 
			if (stack.getTagCompound().hasKey("KahurBodyMaterial")) {
				bodyColor = WoodColor.valueOf(stack.getTagCompound().getString("KahurBodyMaterial"));
			}
			if (stack.getTagCompound().hasKey("KahurDrumMaterial")) {
				drumColor = WoodColor.valueOf(stack.getTagCompound().getString("KahurDrumMaterial"));
			}
			if (stack.getTagCompound().hasKey("KahurPumpMaterial")) {
				pumpColor = MineralColor.valueOf(stack.getTagCompound().getString("KahurPumpMaterial"));
			}
		}
		list.add("\u00A77Body: "+bodyColor.getFriendlyName());
		list.add("\u00A77Drum: "+drumColor.getFriendlyName());
		list.add("\u00A77Pump: "+pumpColor.getFriendlyName());
	}
	
	@Override
	public int getRenderPasses(int metadata) {
		return 3;
	}
	
	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	} 
	
	@Override
	public int getItemStackLimit() {
		return 1;
	}
	
	@Override
	public boolean getHasSubtypes() {
		return true;
	}
	
	@Override
	public int getMaxDamage() {
		return 250;
	}
	
	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		if (pass == 0) {
			return body;
		} else if (pass == 1) {
			return drum;
		} else if (pass == 2) {
			return pump;
		}
		System.out.println("Don't know what to do with pass #"+pass+"; returning null. Crash imminent. Someone doesn't know how to code.");
		return null;
	}
	
	@Override
	public int getColorFromItemStack(ItemStack stack, int pass) {
		if (pass == 0) {
			WoodColor bodyColor = WoodColor.BIG_OAK;
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey("KahurBodyMaterial")) {
				bodyColor = WoodColor.valueOf(stack.getTagCompound().getString("KahurBodyMaterial"));
			}
			return bodyColor.getColor();
		} else if (pass == 1) {
			WoodColor drumColor = WoodColor.SPRUCE;
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey("KahurDrumMaterial")) {
				drumColor = WoodColor.valueOf(stack.getTagCompound().getString("KahurDrumMaterial"));
			}
			return drumColor.getColor();
		} else if (pass == 2) {
			MineralColor pumpColor = MineralColor.IRON;
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey("KahurPumpMaterial")) {
				pumpColor = MineralColor.valueOf(stack.getTagCompound().getString("KahurPumpMaterial"));
			}
			return pumpColor.getColor();
		}
		return -1;
	}
	
	@Override
	public void registerIcons(IIconRegister r) {
		body = r.registerIcon("kahur:kahur_body");
		drum = r.registerIcon("kahur:kahur_drum");
		pump = r.registerIcon("kahur:kahur_pump");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (MineralColor pump : MineralColor.values()) {
			for (WoodColor body : WoodColor.values()) {
				for (WoodColor drum : WoodColor.values()) {
					ItemStack kahur = new ItemStack(item);
					NBTTagCompound tag = new NBTTagCompound();
					tag.setString("KahurBodyMaterial", body.name());
					tag.setString("KahurDrumMaterial", drum.name());
					tag.setString("KahurPumpMaterial", pump.name());
					kahur.setTagCompound(tag);
					list.add(kahur);
				}
			}
		}
	}
}
