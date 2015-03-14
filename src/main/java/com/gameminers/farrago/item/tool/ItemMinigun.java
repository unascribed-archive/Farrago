package com.gameminers.farrago.item.tool;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.gameminers.farrago.FarragoMod;
import com.gameminers.farrago.entity.EntityRifleProjectile;

public class ItemMinigun extends Item {
	private IIcon icon;
	private IIcon alt_icon;
	public ItemMinigun() {
		setUnlocalizedName("minigun");
		setTextureName("farrago:yttrium_minigun");
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
		return 60;
	}
	
	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.bow;
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 72000;
	}
	
	@Override
	public void onPlayerStoppedUsing(ItemStack gun, World world, EntityPlayer player, int remaining) {
		FarragoMod.proxy.stopSounds();
	}
	
	@Override
	public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
		if (72000-count < 20) return;
		if (72000-count > 137) {
			if ((72000-count) == 138 || (72000-count) % 43 == 0) {
				player.playSound("farrago:minigun_spin", 1.0f, 1.0f);
			}
		}
		if (player.isSneaking()) return;
		if (count % getShotTime(72000-count) == 0) {
			player.playSound("farrago:laser_fire", 0.75f, 2.0f);
			InventoryPlayer inv = player.inventory;
			for (int i = 0; i < inv.getSizeInventory(); i++) {
				ItemStack is = inv.getStackInSlot(i);
				if (is == null) continue;
				if (is.getItem() == FarragoMod.MINIGUN_CELL) {
					if (is.getItemDamage() < FarragoMod.MINIGUN_CELL.getCapacity()+1) {
						if (!player.worldObj.isRemote) {
							is.damageItem(1, player);
							if (is.getItemDamage() == FarragoMod.MINIGUN_CELL.getCapacity()+1) {
								stack.setItemDamage(getMaxDamage());
								player.clearItemInUse();
							}
							EntityRifleProjectile proj = new EntityRifleProjectile(player.worldObj, player, 5.0f, 10.0f);
							proj.setMode(null);
							player.worldObj.spawnEntityInWorld(proj);
						}
						return;
					}
				}
			}
			stack.setItemDamage(getMaxDamage());
			player.clearItemInUse();
		}
	}
	
	@Override
	public IIcon getIcon(ItemStack item, int renderPass, EntityPlayer player, ItemStack itemInUse, int remaining) {
		if (remaining > 71980) return icon;
		if (remaining % getShotTime(72000-remaining) == 0) return alt_icon;
		return icon;
	}
	
	private int getShotTime(int useTicks) {
		return (int) Math.max(2, (120-useTicks)/2f);
	}

	public IIcon getIcon(ItemStack stack, int pass) {
		return icon;
	}
	
	@Override
	public IIcon getIconFromDamage(int p_77617_1_) {
		return icon;
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity holder, int slot, boolean held) {
		if (stack.getItemDamage() > 0) {
			stack.setItemDamage(stack.getItemDamage()-1);
		}
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack gun, World world, EntityPlayer player) {
		if (gun.getItemDamage() > 0) {
			world.playSoundAtEntity(player, "random.fizz", 1.0f, 0.75f);
			return gun;
		}
		boolean hasCell = false;
		InventoryPlayer inv = player.inventory;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack is = inv.getStackInSlot(i);
			if (is == null) continue;
			if (is.getItem() == FarragoMod.MINIGUN_CELL) {
				if (is.getItemDamage() < 129) {
					hasCell = true;
					break;
				}
			}
		}
		if (hasCell && player.hurtTime < 5) {
			player.playSound("farrago:minigun_spin_up", 1.0f, 1.0f);
			player.setItemInUse(gun, getMaxItemUseDuration(gun));
		} else {
			world.playSoundAtEntity(player, "random.click", 1.0f, 2.0f);
		}
		return gun;
	}

	
	@Override
	public void registerIcons(IIconRegister registry) {
		icon = registry.registerIcon("farrago:yttrium_minigun");
		alt_icon = registry.registerIcon("farrago:yttrium_minigun_alt");
	}

	@Override
	public boolean getIsRepairable(ItemStack a, ItemStack b) {
        return b.getItem() == FarragoMod.INGOT && b.getItemDamage() == 1 ? true : super.getIsRepairable(a, b);
    }
}
