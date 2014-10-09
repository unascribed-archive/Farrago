package com.gameminers.farrago.kahur.item;

import gminers.kitchensink.RandomPool;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.gameminers.farrago.kahur.KahurIota;
import com.gameminers.farrago.kahur.MineralColor;
import com.gameminers.farrago.kahur.WoodColor;
import com.gameminers.farrago.kahur.entity.EntityKahurProjectile;

public class ItemKahur extends Item {
	private IIcon body;
	private IIcon drum;
	private IIcon pump;
	private IIcon drumBulge;
	private IIcon entitySpeckles;
	private IIcon blank;
	public ItemKahur() {
		setUnlocalizedName("kahur");
		setTextureName("kahur:kahur");
		setCreativeTab(KahurIota.creativeTab);
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
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("KahurEntityId")) {
			list.add("\u00A77Contains: "+EntityList.getStringFromID(stack.getTagCompound().getInteger("KahurEntityId")));
		}
	}
	
	@Override
	public int getRenderPasses(int metadata) {
		return 4;
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
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey("KahurEntityId")) {
				return drumBulge;
			}
			return drum;
		} else if (pass == 2) {
			return pump;
		} else if (pass == 3) {
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey("KahurEntityId")) {
				return entitySpeckles;
			}
			return blank;
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
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey("KahurEntityId")) {
				int entityId = stack.getTagCompound().getInteger("KahurEntityId");
				EntityEggInfo eggInfo = (EntityEggInfo) EntityList.entityEggs.get(entityId);
				if (eggInfo == null) return -1;
				return eggInfo.primaryColor;
			} else {
				WoodColor drumColor = WoodColor.SPRUCE;
				if (stack.hasTagCompound() && stack.getTagCompound().hasKey("KahurDrumMaterial")) {
					drumColor = WoodColor.valueOf(stack.getTagCompound().getString("KahurDrumMaterial"));
				}
				return drumColor.getColor();
			}
		} else if (pass == 2) {
			MineralColor pumpColor = MineralColor.IRON;
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey("KahurPumpMaterial")) {
				pumpColor = MineralColor.valueOf(stack.getTagCompound().getString("KahurPumpMaterial"));
			}
			return pumpColor.getColor();
		} else if (pass == 3) {
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey("KahurEntityId")) {
				int entityId = stack.getTagCompound().getInteger("KahurEntityId");
				EntityEggInfo eggInfo = (EntityEggInfo) EntityList.entityEggs.get(entityId);
				if (eggInfo == null) return -1;
				return eggInfo.secondaryColor;
			} else {
				return -1;
			}
		}
		return -1;
	}
	
	@Override
	public void registerIcons(IIconRegister r) {
		body = r.registerIcon("kahur:kahur_body");
		drum = r.registerIcon("kahur:kahur_drum");
		pump = r.registerIcon("kahur:kahur_pump");
		drumBulge = r.registerIcon("kahur:kahur_drum_entity");
		entitySpeckles = r.registerIcon("kahur:kahur_drum_entity_overlay");
		blank = r.registerIcon("kahur:blank");
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
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target) {
		if (player.isSneaking()) {
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey("KahurEntityId")) {
				return true;
			}
			MineralColor pumpColor = MineralColor.IRON;
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey("KahurPumpMaterial")) {
				pumpColor = MineralColor.valueOf(stack.getTagCompound().getString("KahurPumpMaterial"));
			}
			if (pumpColor.ordinal() <= MineralColor.LAPIS.ordinal()) return true;
			if (stack.getMaxDamage()-stack.getItemDamage() < 12) {
				stack.damageItem(2000, player);
			} else {
				NBTTagCompound tag = stack.getTagCompound();
				if (tag == null) {
					tag = new NBTTagCompound();
					stack.setTagCompound(tag);
				}
				tag.setInteger("KahurEntityId", EntityList.getEntityID(target));
				NBTTagCompound entityTag = new NBTTagCompound();
				target.writeToNBT(entityTag);
				tag.setTag("KahurEntity", entityTag);
				target.setDead();
				player.worldObj.playSoundAtEntity(player, "tile.piston.in", 1.0F, (itemRand.nextFloat() * 0.4F + 1.2F));
				stack.damageItem(10, player);
			}
			return true;
		}
		return false;
	}
	
	@Override
	public ItemStack onItemRightClick(final ItemStack gun, final World world, final EntityPlayer player) {
		if (gun.hasTagCompound() && gun.getTagCompound().hasKey("KahurEntityId")) {
			if (!world.isRemote) {
				world.playSoundAtEntity(player, "tile.piston.out", 1.0F, (itemRand.nextFloat() * 0.4F + 1.2F));
				Entity ent = EntityList.createEntityByID(gun.getTagCompound().getInteger("KahurEntityId"), world);
				if (gun.getTagCompound().hasKey("KahurEntity")) {
					ent.readFromNBT(gun.getTagCompound().getCompoundTag("KahurEntity"));
				}
				gun.getTagCompound().removeTag("KahurEntityId");
				gun.getTagCompound().removeTag("KahurEntity");
				gun.damageItem(5, player);
				ent.setLocationAndAngles(player.posX, player.posY + (double)player.getEyeHeight(), player.posZ, player.rotationYaw, player.rotationPitch);
		        ent.posX -= (double)(MathHelper.cos(ent.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
		        ent.posY -= 0.10000000149011612D;
		        ent.posZ -= (double)(MathHelper.sin(ent.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
		        ent.setPosition(ent.posX, ent.posY, ent.posZ);
		        ent.yOffset = 0.0F;
		        float f = 0.4F;
		        ent.motionX = (double)(-MathHelper.sin(ent.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(ent.rotationPitch / 180.0F * (float)Math.PI) * f);
		        ent.motionZ = (double)(MathHelper.cos(ent.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(ent.rotationPitch / 180.0F * (float)Math.PI) * f);
		        ent.motionY = (double)(-MathHelper.sin((ent.rotationPitch + 0.0f) / 180.0F * (float)Math.PI) * f);
		        float f2 = MathHelper.sqrt_double(ent.motionX * ent.motionX + ent.motionY * ent.motionY + ent.motionZ * ent.motionZ);
		        ent.motionX /= (double)f2;
		        ent.motionY /= (double)f2;
		        ent.motionZ /= (double)f2;
		        ent.motionX += RandomPool.nextGaussian() * 0.007499999832361937D * (double)1.0f;
		        ent.motionY += RandomPool.nextGaussian() * 0.007499999832361937D * (double)1.0f;
		        ent.motionZ += RandomPool.nextGaussian() * 0.007499999832361937D * (double)1.0f;
		        ent.motionX *= (double)1.5f;
		        ent.motionY *= (double)1.5f;
		        ent.motionZ *= (double)1.5f;
		        ent.motionX = ent.motionX;
		        ent.motionY = ent.motionY;
		        ent.motionZ = ent.motionZ;
		        float f3 = MathHelper.sqrt_double(ent.motionX * ent.motionX + ent.motionZ * ent.motionZ);
		        ent.prevRotationYaw = ent.rotationYaw = (float)(Math.atan2(ent.motionX, ent.motionZ) * 180.0D / Math.PI);
		        ent.prevRotationPitch = ent.rotationPitch = (float)(Math.atan2(ent.motionY, (double)f3) * 180.0D / Math.PI);
				world.spawnEntityInWorld(ent);
			}
		} else if (!player.isSneaking()) {
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
		}
		return gun;
	}

	private void fire(ItemStack gun, int slot, World world, EntityPlayer player) {
		EntityKahurProjectile proj = new EntityKahurProjectile(world, player);
		ItemStack item = player.inventory.getStackInSlot(slot);
		ItemStack copy = player.inventory.decrStackSize(slot, 1);
		proj.setItem(copy);
		proj.setDamage(KahurIota.getMass(copy)+(KahurIota.getMagic(copy)*2f));
		world.playSoundAtEntity(player, "mob.enderdragon.hit", 1.0F, (itemRand.nextFloat() * 0.4F + 1.2F));
		gun.damageItem(1, player);
		if (!world.isRemote) {
			gun.damageItem((int)KahurIota.getMagic(copy), player);
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
