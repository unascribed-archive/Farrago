package com.gameminers.farrago.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import com.gameminers.farrago.FarragoMod;
import com.gameminers.farrago.RifleMode;
import com.gameminers.farrago.entity.EntityRifleProjectile;

public class ItemRifle extends Item {
	private IIcon[] icons;
	public ItemRifle() {
		setUnlocalizedName("rifle");
		setTextureName("farrago:yttrium_rifle");
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
		return 1250;
	}
	
	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.bow;
	}
	
	@Override
	public int getColorFromItemStack(ItemStack stack, int pass) {
		return -1;
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return (int)(136f/getMode(stack).getChargeSpeed());
	}
	
	@Override
	public ItemStack onEaten(ItemStack gun, World world, EntityPlayer player) {
		gun.damageItem(32, player);
		player.attackEntityFrom(new DamageSource("rifle_backfire"), 8);
		consumeInventoryItem(player.inventory, FarragoMod.CELL, getMode(gun).getCellType());
		world.playSoundAtEntity(player, "farrago:laser_overcharge", 1.0f, 1.0f);
		if (world instanceof WorldServer) {
			((WorldServer)world).func_147487_a("reddust", player.posX, player.posY+0.81f, player.posZ, 120, 0.5f, 0.6f, 0.5f, 0f);
		}
		return gun;
	}
	
	@Override
	public void onPlayerStoppedUsing(ItemStack gun, World world, EntityPlayer player, int remaining) {
		int useTime = getMaxItemUseDuration(gun) - remaining;
		FarragoMod.proxy.stopSounds();
		if (useTime >= getTicksToFire(gun)) {
			if (consumeInventoryItem(player.inventory, FarragoMod.CELL, getMode(gun).getCellType())) {
				if (!world.isRemote) {
					world.playSoundAtEntity(player, "farrago:laser_fire", 1.0f, 1.0f);
					EntityRifleProjectile proj = new EntityRifleProjectile(world, player);
					proj.setMode(getMode(gun));
					world.spawnEntityInWorld(proj);
					if (!player.inventory.addItemStackToInventory(new ItemStack(FarragoMod.CELL, 1, 0))) {
						player.entityDropItem(new ItemStack(FarragoMod.CELL, 1, 0), 1.0f);
					}
				}
			}
		}
	}
	
	private boolean consumeInventoryItem(InventoryPlayer inv, Item item, int meta) {
        int i = find(inv, item, meta);

        if (i < 0) {
            return false;
        } else {
            if (--inv.mainInventory[i].stackSize <= 0) {
                inv.mainInventory[i] = null;
            }
            return true;
        }
    }
	
	private int find(InventoryPlayer inv, Item item, int meta) {
        for (int i = 0; i < inv.mainInventory.length; i++) {
            if (inv.mainInventory[i] != null && inv.mainInventory[i].getItem() == item && inv.mainInventory[i].getItemDamage() == meta) {
                return i;
            }
        }

        return -1;
    }
	
	private int getTicksToFire(ItemStack item) {
		return (int)(114f / getMode(item).getChargeSpeed());
	}

	@Override
	public IIcon getIcon(ItemStack item, int renderPass, EntityPlayer player, ItemStack itemInUse, int remaining) {
		if (remaining == 0) return icons[0];
		int useTime = getMaxItemUseDuration(itemInUse) - remaining;
		return icons[Math.min(useTime/(getTicksToFire(item)/5), 5)];
	}
	
	public IIcon getIcon(ItemStack stack, int pass) {
		return icons[0];
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack gun, World world, EntityPlayer player) {
		if (player.isSneaking()) {
			RifleMode[] vals = RifleMode.values();
			int idx = getMode(gun).ordinal()+1;
			RifleMode mode = vals[idx%vals.length];
			int tries = 0;
			while (find(player.inventory, FarragoMod.CELL, mode.getCellType()) < 0) {
				if (tries++ > vals.length) {
					idx = getMode(gun).ordinal()+1;
					break;
				}
				idx++;
				mode = vals[idx%vals.length];
			}
			world.playSoundAtEntity(player, "farrago:laser_mode", 1.0f, (mode.ordinal()*0.15f)+1.0f);
			setMode(gun, mode);
		} else {
			if (find(player.inventory, FarragoMod.CELL, getMode(gun).getCellType()) >= 0 && player.hurtTime < 5) {
				player.setItemInUse(gun, getMaxItemUseDuration(gun));
				world.playSoundAtEntity(player, "farrago:laser_charge", 1.0f, getMode(gun).getChargeSpeed());
			}
		}
		return gun;
	}
	
	private static final String[] cellTypeNames = {
		"Empty",
		"Redstone-Copper",
		"Yttrium-Glowstone",
		"Diamond-Gold",
		"Iron-Gunpowder"
	};
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean adv) {
		RifleMode mode = getMode(stack);
		list.add("Mode: "+mode.getDisplayName());
		list.add("Cell Type: "+cellTypeNames[mode.getCellType()]);
	}
	
	@Override
	public void registerIcons(IIconRegister registry) {
		icons = new IIcon[] {
			registry.registerIcon("farrago:yttrium_rifle"),
			registry.registerIcon("farrago:yttrium_rifle_0"),
			registry.registerIcon("farrago:yttrium_rifle_1"),
			registry.registerIcon("farrago:yttrium_rifle_2"),
			registry.registerIcon("farrago:yttrium_rifle_3"),
			registry.registerIcon("farrago:yttrium_rifle_4")
		};
	}

	public void setMode(ItemStack item, RifleMode mode) {
		if (!item.hasTagCompound()) {
			item.setTagCompound(new NBTTagCompound());
		}
		item.getTagCompound().setString("RifleMode", mode.name());
	}
	
	public RifleMode getMode(ItemStack item) {
		if (item.hasTagCompound() && item.getTagCompound().hasKey("RifleMode")) {
			try {
				return RifleMode.valueOf(item.getTagCompound().getString("RifleMode"));
			} catch (Exception e) {
				item.getTagCompound().setString("RifleMode", "DAMAGE");
			}
		}
		return RifleMode.DAMAGE;
	}
}
