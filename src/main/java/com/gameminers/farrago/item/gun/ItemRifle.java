package com.gameminers.farrago.item.gun;

import gminers.kitchensink.ReadableNumbers;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import com.gameminers.farrago.FarragoMod;
import com.gameminers.farrago.Inventories;
import com.gameminers.farrago.entity.EntityRifleProjectile;
import com.gameminers.farrago.enums.RifleMode;

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
		RifleMode mode = getMode(stack);
		return (int)((float)getChargeTicksWithOverload(mode)/mode.getChargeSpeed());
	}
	
	public int getChargeTicksWithOverload(RifleMode mode) {
		return mode.isShort() ? 70 : 142;
	}
	
	public int getChargeTicks(RifleMode mode) {
		return mode.isShort() ? 23 : 114;
	}

	@Override
	public ItemStack onEaten(ItemStack gun, World world, EntityPlayer player) {
		gun.damageItem(32, player);
		player.attackEntityFrom(new DamageSource("rifle_backfire"), 8);
		Inventories.consumeInventoryItem(player.inventory, FarragoMod.CELL, getMode(gun).getCellType());
		world.playSoundAtEntity(player, "farrago:laser_overcharge", 1.0f, 1.0f);
		if (world instanceof WorldServer) {
			((WorldServer)world).func_147487_a("reddust", player.posX, player.posY+0.81f, player.posZ, 120, 0.5f, 0.6f, 0.5f, 0f);
		}
		return gun;
	}
	
	@Override
	public boolean shouldRotateAroundWhenRendering() {
		return false;
	}
	
	@Override
	public void onPlayerStoppedUsing(ItemStack gun, World world, EntityPlayer player, int remaining) {
		int useTime = getMaxItemUseDuration(gun) - remaining;
		FarragoMod.proxy.stopSounds();
		if (useTime >= getTicksToFire(gun)) {
			if (player.capabilities.isCreativeMode || Inventories.consumeInventoryItem(player.inventory, FarragoMod.CELL, getMode(gun).getCellType())) {
				if (!world.isRemote) {
					world.playSoundAtEntity(player, "farrago:laser_fire", 1.0f, 1.0f);
					RifleMode mode = getMode(gun);
					float spread = 0.0f;
					float speed = 4f;
					int count = 1;
					if (mode.getCellType() == 0) {
						speed *= 2;
					}
					if (mode == RifleMode.SCATTER) {
						spread = 10f;
						count = itemRand.nextInt(10)+5;
					} else if (mode == RifleMode.TELEPORT) {
						speed = 3f;
					} else if (mode == RifleMode.PRECISION_MINING || mode == RifleMode.MINING) {
						speed = 0.9f;
					}
					for (int i = 0; i < count; i++) {
						EntityRifleProjectile proj = new EntityRifleProjectile(world, player, speed, spread);
						proj.setMode(mode);
						world.spawnEntityInWorld(proj);
					}
					if (!player.inventory.addItemStackToInventory(new ItemStack(FarragoMod.CELL, 1, 0))) {
						player.entityDropItem(new ItemStack(FarragoMod.CELL, 1, 0), 1.0f);
					}
				}
			}
		}
	}
	
	public int getTicksToFire(ItemStack item) {
		RifleMode mode = getMode(item);
		return (int)((float)getChargeTicks(mode) / mode.getChargeSpeed());
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
	public IIcon getIconFromDamage(int p_77617_1_) {
		return icons[0];
	}
	
	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
		if (entityLiving instanceof EntityPlayer) {
			EntityPlayer player = ((EntityPlayer)entityLiving);
			if (player.isSneaking()) {
				if (player.worldObj.isRemote) {
					FarragoMod.proxy.scope(player);
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack gun, World world, EntityPlayer player) {
		if (hasAmmoFor(player, getMode(gun)) && player.hurtTime < 5) {
			player.setItemInUse(gun, getMaxItemUseDuration(gun));
			RifleMode mode = getMode(gun);
			if (mode.isShort()) {
				world.playSoundAtEntity(player, "farrago:laser_charge_short", 1.0f, mode.getChargeSpeed());
			} else {
				world.playSoundAtEntity(player, "farrago:laser_charge", 1.0f, mode.getChargeSpeed());
			}
		} else {
			world.playSoundAtEntity(player, "random.click", 1.0f, 2.0f);
		}
		return gun;
	}
	
	public void modifyMode(EntityPlayer player, ItemStack gun, boolean absolute, int i) {
		RifleMode mode;
		RifleMode[] vals = RifleMode.values();
		if (absolute) {
			if (i == getMode(gun).ordinal()) return;
			mode = vals[i];
		} else {
			if (i == 0) return;
			int idx = getMode(gun).ordinal()+i;
			if (idx < 0) {
				idx += vals.length;
			}
			mode = vals[idx%vals.length];
			int tries = 0;
			while (!hasAmmoFor(player, mode)) {
				if (tries++ >= vals.length) {
					idx = getMode(gun).ordinal()+i;
					break;
				}
				idx += i;
				if (idx < 0) {
					idx += vals.length;
				}
				mode = vals[idx%vals.length];
			}
		}
		player.worldObj.playSoundAtEntity(player, "farrago:laser_mode", 1.0f, (mode.ordinal()*0.15f)+1.0f);
		setMode(gun, mode);
	}

	public boolean hasAmmoFor(EntityPlayer player, RifleMode mode) {
		return player.capabilities.isCreativeMode || Inventories.find(player.inventory, FarragoMod.CELL, mode.getCellType()) >= 0;
	}

	private static final String[] cellTypeNames = {
		"Empty",
		"Redstone-Copper",
		"Yttrium-Glowstone",
		"Gold-Diamond",
		"Iron-Gunpowder",
		"Ender-Emerald",
		"Redstone-Blaze-Copper"
	};
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean adv) {
		RifleMode mode = getMode(stack);
		list.add("Mode: "+mode.getDisplayName());
		list.add("Cell Type: "+cellTypeNames[mode.getCellType()]);
		list.add("Charge Time: "+ReadableNumbers.humanReadableMillis((long) (((getChargeTicks(mode)/mode.getChargeSpeed())/20f)*1000)));
		list.add("Overcharge Tolerance: "+ReadableNumbers.humanReadableMillis((long) ((((getChargeTicksWithOverload(mode)-getChargeTicks(mode))/mode.getChargeSpeed())/20f)*1000)));
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
		return RifleMode.RIFLE;
	}
	
	@Override
	public boolean getIsRepairable(ItemStack a, ItemStack b) {
        return b.getItem() == FarragoMod.INGOT && b.getItemDamage() == 1 ? true : super.getIsRepairable(a, b);
    }
}
