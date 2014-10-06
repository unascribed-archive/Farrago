package com.gameminers.kahur;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

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
	public int getMaxDamage(ItemStack stack) {
		MineralColor pumpColor = MineralColor.IRON;
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("KahurPumpMaterial")) {
			pumpColor = MineralColor.valueOf(stack.getTagCompound().getString("KahurPumpMaterial"));
		}
		return pumpColor.getDurability();
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
	
	@Override
	public ItemStack onItemRightClick(final ItemStack gun, final World world, final EntityPlayer player) {
		ItemStack item = null;
		int iter = 0;
		int slot = 0;
		MineralColor pumpColor = MineralColor.IRON;
		if (gun.hasTagCompound() && gun.getTagCompound().hasKey("KahurPumpMaterial")) {
			pumpColor = MineralColor.valueOf(gun.getTagCompound().getString("KahurPumpMaterial"));
		}
		if (pumpColor == MineralColor.ENDER) {
			for (slot = 27; slot < 36; slot++) {
				item = player.inventory.mainInventory[slot];
				if (item != null && item.getItem() != null && item.stackSize > 0 && item != gun) {
					break;
				}
			}
			if (item == null) return gun;
		} else {
			while (item == null || item.getItem() == null || item.stackSize == 0 || item == gun) {
				if (iter++ > 2000) {
					return gun;
				}
				slot = itemRand.nextInt(player.inventory.mainInventory.length+player.inventory.armorInventory.length);
				if (slot >= player.inventory.mainInventory.length) {
					item = player.inventory.armorInventory[slot-player.inventory.mainInventory.length];
				} else {
					item = player.inventory.mainInventory[slot];
				}
			}
		}
		fire(gun, slot, world, player);
		return gun;
	}

	private void fire(ItemStack gun, int slot, World world, EntityPlayer player) {
		EntityKahurProjectile proj = new EntityKahurProjectile(world, player);
		ItemStack item = player.inventory.getStackInSlot(slot);
		ItemStack copy = player.inventory.decrStackSize(slot, 1);
		proj.setItem(copy);
		proj.setDamage((int)(KahurMod.getMagic(copy)*2f));
		world.playSoundAtEntity(player, "mob.enderdragon.hit", 1.0F, (itemRand.nextFloat() * 0.4F + 1.2F));
		gun.damageItem(1, player);
		if (!world.isRemote) {
			if (item.getItem() == Items.gunpowder) {
				world.createExplosion(null, player.posX, player.posY, player.posZ, 0.4f, false);
				gun.damageItem(12, player);
			} else if (item.getItem() == Items.arrow) {
				world.spawnEntityInWorld(new EntityArrow(world, player, 0.8f));
			} else {
				world.spawnEntityInWorld(proj);
			}
		}
	}
}
