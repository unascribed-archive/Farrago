package com.gameminers.farrago.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
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
	public ItemStack onItemRightClick(ItemStack gun, World world, EntityPlayer player) {
		if (player.inventory.hasItem(Items.gunpowder)) {
			if (player.inventory.hasItem(Item.getItemFromBlock(Blocks.cobblestone))) {
				if (!world.isRemote) {
					if (itemRand.nextInt(4) == 1) {
						player.inventory.consumeInventoryItem(Items.gunpowder);
						player.inventory.consumeInventoryItem(Item.getItemFromBlock(Blocks.cobblestone));
					}
					if (itemRand.nextInt(12) == 1) {
						player.attackEntityFrom(DamageSource.causePlayerDamage(player), 2f);
					}
					player.inventory.consumeInventoryItem(Item.getItemFromBlock(Blocks.cobblestone));
					gun.damageItem(itemRand.nextInt(3)+1, player);
					world.playSoundAtEntity(player, "random.break", 1.0F, (itemRand.nextFloat() * 0.8F));
					world.playSoundAtEntity(player, "random.explode", 0.6F, (itemRand.nextFloat() * 0.8F + 0.3F));
					for (int i = 0; i < itemRand.nextInt(30)+15; i++) {
						EntityBlunderbussProjectile proj = new EntityBlunderbussProjectile(world, player);
						world.spawnEntityInWorld(proj);
					}
				}
			} else {
				if (!world.isRemote) {
					world.playSoundAtEntity(player, "random.click", 1.0F, 2.0f);
					player.addChatMessage(new ChatComponentText("\u00A7cCan't find any cobblestone."));
				}
			}
		} else {
			if (!world.isRemote) {
				world.playSoundAtEntity(player, "random.click", 1.0F, 2.0f);
				player.addChatMessage(new ChatComponentText("\u00A7cCan't find any gunpowder."));
			}
		}
		return gun;
	}
}
