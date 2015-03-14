package com.gameminers.farrago.item.gun;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import com.gameminers.farrago.FarragoMod;
import com.gameminers.farrago.entity.EntityBlunderbussProjectile;

public class ItemBlunderbuss extends Item {
	public ItemBlunderbuss() {
		setUnlocalizedName("blunderbuss");
		setTextureName("farrago:blunderbuss");
		setCreativeTab(FarragoMod.creativeTab);
	}
	
	@Override
	public boolean isFull3D() {
		return true;
	}
	
	@Override
	public int getItemStackLimit() {
		return 1;
	}
	
	@Override
	public boolean isDamageable() {
		return true;
	}
	
	@Override
	public int getMaxDamage() {
		return 840;
	}
	
	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.bow;
	}
	
	@Override
	public int getColorFromItemStack(ItemStack stack, int pass) {
		if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("Zealon")) return 0xFFAAFF;
		return -1;
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("Zealon")) return 1;
		return 10;
	}
	
	@Override
	public ItemStack onEaten(ItemStack gun, World world, EntityPlayer player) {
		if (player.inventory.hasItem(Items.gunpowder)) {
			if (player.inventory.hasItem(Item.getItemFromBlock(Blocks.gravel))) {
				if (!world.isRemote) {
					if (itemRand.nextInt(4) == 1) {
						player.inventory.consumeInventoryItem(Items.gunpowder);
						player.inventory.consumeInventoryItem(Item.getItemFromBlock(Blocks.gravel));
					}
					if (itemRand.nextInt(12) == 1) {
						player.attackEntityFrom(new DamageSource("blunderbuss_backfire"), 2f);
					}
					player.inventory.consumeInventoryItem(Item.getItemFromBlock(Blocks.gravel));
					gun.damageItem(itemRand.nextInt(3)+1, player);
					world.playSoundAtEntity(player, "random.break", 1.0F, (itemRand.nextFloat() * 0.8F));
					world.playSoundAtEntity(player, "random.explode", 0.6F, (itemRand.nextFloat() * 0.8F + 0.3F));
					for (int i = 0; i < itemRand.nextInt(30)+15; i++) {
						EntityBlunderbussProjectile proj = new EntityBlunderbussProjectile(world, player);
						world.spawnEntityInWorld(proj);
					}
				}
			}
		}
		return gun;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack gun, World world, EntityPlayer player) {
		if (player.inventory.hasItem(Items.gunpowder) && player.inventory.hasItem(Item.getItemFromBlock(Blocks.gravel))) {
			player.setItemInUse(gun, getMaxItemUseDuration(gun));
		}
		return gun;
	}
	
	@Override
	public boolean getIsRepairable(ItemStack a, ItemStack b) {
        return b.getItem() == FarragoMod.INGOT && b.getItemDamage() == 0 ? true : super.getIsRepairable(a, b);
    }
}
