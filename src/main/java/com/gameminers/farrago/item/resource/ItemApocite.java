package com.gameminers.farrago.item.resource;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.gameminers.farrago.FarragoMod;

public class ItemApocite extends Item {
	private IIcon[] icons = new IIcon[20];
	public ItemApocite() {
		setUnlocalizedName("apocite");
		setTextureName("farrago:apocite");
		setCreativeTab(FarragoMod.creativeTab);
		setHasSubtypes(true);
		setMaxStackSize(64);
	}
	@Override
	public IIcon getIconFromDamage(int damage) {
		return damage >= icons.length ? icons[icons.length-1] : icons[damage];
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean held) {
		if (world.isRemote) return;
		if (rot(stack, stack.getItemDamage() == 0 ? 100 : held ? 30 : 50)) {
			stack.stackSize = 0;
			if (entity instanceof EntityPlayer) {
				((EntityPlayer)entity).inventory.setInventorySlotContents(slot, null);
			}
		}
	}
	
	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		if (entityItem.ticksExisted < 40) return false;
		if (rot(entityItem.getEntityItem(), 20)) {
			entityItem.setDead();
		}
		return false;
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return false;
	}
	
	@Override
	public int getMaxDamage() {
		return icons.length-1;
	}
	
	@Override
	public boolean isRepairable() {
		return true;
	}
	
	private boolean rot(ItemStack stack, int i) {
		if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("Stable")) return false;
		if (itemRand.nextInt(i) == 0) {
			stack.setItemDamage(stack.getItemDamage()+1);
		}
		return stack.getItemDamage() >= icons.length;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("Stable")) return "item.stable_apocite";
		return "item.apocite";
	}
	
	@Override
	public void registerIcons(IIconRegister registry) {
		for (int i = 0; i < icons.length; i++) {
			icons[i] = registry.registerIcon("farrago:apocite_"+i);
		}
	}
}
