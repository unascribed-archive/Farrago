package com.unascribed.farrago.item.gun;

import java.util.List;

import com.unascribed.farrago.FarragoMod;
import com.unascribed.farrago.Masses;
import com.unascribed.farrago.Material;
import com.unascribed.farrago.entity.EntityKahurProjectile;
import com.unascribed.farrago.enums.WoodColor;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemKahur extends Item {
	public enum Ability {
		SPUD,
		POTIONS,
		TORCHES,
		ROCKET
	}

	private IIcon body;
	private IIcon drum;
	private IIcon pump;
	private IIcon drumBulge;
	private IIcon entitySpeckles;
	private IIcon blank;
	public ItemKahur() {
		setUnlocalizedName("kahur");
		setTextureName("farrago:kahur");
		setCreativeTab(FarragoMod.creativeTab);
	}
	
	@Override
	public boolean isFull3D() {
		return true;
	}
	
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
		WoodColor bodyColor = WoodColor.BIG_OAK;
		WoodColor drumColor = WoodColor.SPRUCE;
		String pumpColor = "Unknown";
		if (stack.hasTagCompound()){ 
			if (stack.getTagCompound().hasKey("KahurBodyMaterial")) {
				bodyColor = WoodColor.valueOf(stack.getTagCompound().getString("KahurBodyMaterial"));
			}
			if (stack.getTagCompound().hasKey("KahurDrumMaterial")) {
				drumColor = WoodColor.valueOf(stack.getTagCompound().getString("KahurDrumMaterial"));
			}
			if (stack.getTagCompound().hasKey("KahurPumpName")) {
				pumpColor = stack.getTagCompound().getString("KahurPumpName");
			}
		}
		list.add("\u00A77Body: "+bodyColor.getFriendlyName());
		list.add("\u00A77Drum: "+drumColor.getFriendlyName());
		list.add("\u00A77Pump: "+pumpColor);
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("KahurEntityName")) {
			list.add("\u00A77Contains: "+stack.getTagCompound().getString("KahurEntityName"));
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
	public boolean isDamageable() {
		return true;
	}
	
	@Override
	public boolean getHasSubtypes() {
		return true;
	}
	
	@Override
	public int getMaxDamage(ItemStack stack) {
		// is this a strange and arbitrary place for this code?
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("KahurPumpMaterial")) {
			FarragoMod.log.info("Converting old-style (hardcoded) Kahur NBT to new-style (material)");
			String moniker = stack.getTagCompound().getString("KahurPumpMaterial");
			Material mat = FarragoMod.monikerLookup.get(moniker);
			if (mat == null) {
				FarragoMod.log.warn("Unknown moniker "+moniker);
			} else {
				NBTTagCompound tag = stack.getTagCompound();
				tag.setString("KahurPumpName", mat.name);
				tag.setInteger("KahurDurability", mat.kahurDurability);
				tag.setInteger("KahurPumpColor", mat.color);
				tag.setBoolean("KahurDeterministic", mat.kahurDeterministic);
				if (mat.kahurSpecial != null) {
					tag.setString("KahurAbility", mat.kahurSpecial.name());
				}
				tag.setBoolean("KahurCanPickUpMobs", mat.kahurMobs);
				stack.getTagCompound().removeTag("KahurPumpMaterial");
			}
		}
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("KahurDurability")) {
			return stack.getTagCompound().getInteger("KahurDurability");
		}
		return 250;
	}
	
	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		if (pass == 0) {
			return body;
		} else if (pass == 1) {
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey("KahurEntityName")) {
				return drumBulge;
			}
			return drum;
		} else if (pass == 2) {
			return pump;
		} else if (pass == 3) {
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey("KahurEntityName")) {
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
				try {
					bodyColor = WoodColor.valueOf(stack.getTagCompound().getString("KahurBodyMaterial"));
				} catch (IllegalArgumentException e) {
					stack.getTagCompound().setString("KahurBodyMaterial", "BIG_OAK");
				}
			}
			return bodyColor.getColor();
		} else if (pass == 1) {
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey("KahurEntityName")) {
				return -1;
			} else {
				WoodColor drumColor = WoodColor.SPRUCE;
				if (stack.hasTagCompound() && stack.getTagCompound().hasKey("KahurDrumMaterial")) {
					try {
						drumColor = WoodColor.valueOf(stack.getTagCompound().getString("KahurDrumMaterial"));
					} catch (IllegalArgumentException e) {
						stack.getTagCompound().setString("KahurDrumMaterial", "SPRUCE");
					}
				}
				return drumColor.getColor();
			}
		} else if (pass == 2) {
			return stack.hasTagCompound() && stack.getTagCompound().hasKey("KahurPumpColor") ? stack.getTagCompound().getInteger("KahurPumpColor") : -1;
		} else if (pass == 3) {
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey("KahurEntityName")) {
				return -1;
			} else {
				return -1;
			}
		}
		return -1;
	}
	
	@Override
	public void registerIcons(IIconRegister r) {
		body = r.registerIcon("farrago:kahur_body");
		drum = r.registerIcon("farrago:kahur_drum");
		pump = r.registerIcon("farrago:kahur_pump");
		drumBulge = r.registerIcon("farrago:kahur_drum_entity");
		entitySpeckles = r.registerIcon("farrago:kahur_drum_entity_overlay");
		blank = r.registerIcon("farrago:blank");
	}
	
	
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (Material mat : FarragoMod.materials) {
			if (!mat.validForKahur) continue;
			ItemStack kahur = new ItemStack(item);
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("KahurBodyMaterial", "BIG_OAK");
			tag.setString("KahurDrumMaterial", "SPRUCE");
			tag.setString("KahurPumpName", mat.name);
			tag.setInteger("KahurDurability", mat.kahurDurability);
			tag.setInteger("KahurPumpColor", mat.color);
			tag.setBoolean("KahurDeterministic", mat.kahurDeterministic);
			if (mat.kahurSpecial != null) {
				tag.setString("KahurAbility", mat.kahurSpecial.name());
			}
			tag.setBoolean("KahurCanPickUpMobs", mat.kahurMobs);
			kahur.setTagCompound(tag);
			list.add(kahur);
		}
	}
	
	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target) {
		if (FarragoMod.config.getBoolean("kahur.allowPickingUpMobs") && player.isSneaking()) {
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey("KahurEntityName")) {
				return true;
			}
			if (!stack.hasTagCompound() || !stack.getTagCompound().getBoolean("KahurCanPickUpMobs")) return true;
			if (target instanceof EntityPlayer) return true;
			if (target instanceof IBossDisplayData) return true;
			if (stack.getMaxDamage()-stack.getItemDamage() < 12) {
				stack.damageItem(2000, player);
			} else {
				NBTTagCompound tag = stack.getTagCompound();
				if (tag == null) {
					tag = new NBTTagCompound();
					stack.setTagCompound(tag);
				}
				
				tag.setString("KahurEntityName", EntityList.getEntityString(target));
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
		if (gun.hasTagCompound() && gun.getTagCompound().hasKey("KahurEntityName")) {
			if (!world.isRemote) {
				world.playSoundAtEntity(player, "tile.piston.out", 1.0F, (itemRand.nextFloat() * 0.4F + 1.2F));
				Entity ent = EntityList.createEntityByName(gun.getTagCompound().getString("KahurEntityName"), world);
				if (gun.getTagCompound().hasKey("KahurEntity")) {
					ent.readFromNBT(gun.getTagCompound().getCompoundTag("KahurEntity"));
				}
				gun.getTagCompound().removeTag("KahurEntityName");
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
		        ent.motionX += ent.worldObj.rand.nextGaussian() * 0.007499999832361937D * (double)1.0f;
		        ent.motionY += ent.worldObj.rand.nextGaussian() * 0.007499999832361937D * (double)1.0f;
		        ent.motionZ += ent.worldObj.rand.nextGaussian() * 0.007499999832361937D * (double)1.0f;
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
		} else if (!FarragoMod.config.getBoolean("kahur.allowPickingUpMobs") || !player.isSneaking()) {
			ItemStack item = null;
			int iter = 0;
			int slot = 0;
			if (!gun.hasTagCompound()) return gun;
			String ability = gun.getTagCompound().getString("KahurAbility");
			if (gun.getTagCompound().getBoolean("KahurDeterministic")) {
				for (slot = 27; slot < 36; slot++) {
					item = player.inventory.mainInventory[slot];
					if (item != null && item.getItem() != null && item.stackSize > 0 && item != gun) {
						break;
					}
				}
				if (item == null) {
					world.playSoundAtEntity(player, "random.click", 1.0F, 2.0f);
					if (!world.isRemote) {
						player.addChatMessage(new ChatComponentText("\u00A7cCan't find any ammo in the 3rd inventory row."));
					}
					return gun;
				}
			} else if ("ROCKET".equals(ability)) {
				if (player.inventory.consumeInventoryItem(Items.gunpowder)) {
					world.playSoundAtEntity(player, "mob.enderdragon.hit", 1.0F, (itemRand.nextFloat() * 0.4F + 1.2F));
					if (!world.isRemote) {
						Entity owner = null;
						world.createExplosion(owner, player.posX, player.posY, player.posZ, 0.4f, false);
						gun.damageItem(6, player);
					}
				} else {
					world.playSoundAtEntity(player, "random.click", 1.0F, 2.0f);
					if (!world.isRemote) {
						player.addChatMessage(new ChatComponentText("\u00A7cCan't find any gunpowder."));
					}
				}
				return gun;
			} else if ("TORCHES".equals(ability)) {
				if (player.inventory.consumeInventoryItem(Item.getItemFromBlock(Blocks.torch))) {
					fire(gun, -5, world, player);
				} else {
					world.playSoundAtEntity(player, "random.click", 1.0F, 2.0f);
					if (!world.isRemote) {
						player.addChatMessage(new ChatComponentText("\u00A7cCan't find any torches."));
					}
				}
				return gun;
			} else if ("SPUD".equals(ability)) {
				if (player.inventory.consumeInventoryItem(Items.poisonous_potato)) {
					fire(gun, -6, world, player);
				} else if (player.inventory.consumeInventoryItem(Items.potato)) {
					fire(gun, -8, world, player);
				} else if (player.inventory.consumeInventoryItem(Items.baked_potato)) {
					fire(gun, -7, world, player);
				} else {
					world.playSoundAtEntity(player, "random.click", 1.0F, 2.0f);
					if (!world.isRemote) {
						player.addChatMessage(new ChatComponentText("\u00A7cCan't potate without any potatoes."));
					}
				}
				return gun;
			} else if ("POTIONS".equals(ability)) {
				int leSlot = find(player.inventory, Items.potionitem);
				if (leSlot != -1) {
					fire(gun, leSlot, world, player);
				} else {
					world.playSoundAtEntity(player, "random.click", 1.0F, 2.0f);
					if (!world.isRemote) {
						player.addChatMessage(new ChatComponentText("\u00A7cCan't find any potions."));
					}
				}
				return gun;
			} else {
				while (item == null || item.getItem() == null || item.stackSize == 0 || item == gun) {
					if (iter++ > 2000) {
						world.playSoundAtEntity(player, "random.click", 1.0F, 2.0f);
						if (!world.isRemote) {
							player.addChatMessage(new ChatComponentText("\u00A7cCan't find any ammo."));
						}
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

	private int find(InventoryPlayer inventory, Item item) {
		for (int i = 0; i < inventory.mainInventory.length; ++i) {
			if (inventory.mainInventory[i] != null
					&& inventory.mainInventory[i].getItem() == item) {
				return i;
			}
		}

		return -1;
	}

	private void fire(ItemStack gun, int slot, World world, EntityPlayer player) {
		world.playSoundAtEntity(player, "mob.enderdragon.hit", 1.0F, (itemRand.nextFloat() * 0.4F + 1.2F));
		if (world.isRemote) return;
		EntityKahurProjectile proj = new EntityKahurProjectile(world, player);
		ItemStack copy;
		if (slot == -5) {
			copy = new ItemStack(Blocks.torch);
		} else if (slot == -6) {
			copy = new ItemStack(Items.poisonous_potato);
		} else if (slot == -7) {
			copy = new ItemStack(Items.baked_potato);
		} else if (slot == -8) {
			copy = new ItemStack(Items.potato);
		} else {
			copy = player.inventory.decrStackSize(slot, 1);
		}
		if (!FarragoMod.config.getBoolean("kahur.allowShootingMundaneItems")) {
			if (!(
					(FarragoMod.config.getBoolean("kahur.special.potato.enable") && copy.getItem() == Items.potato) ||
					(FarragoMod.config.getBoolean("kahur.special.poisonousPotato.enable") && copy.getItem() == Items.poisonous_potato) ||
					(FarragoMod.config.getBoolean("kahur.special.bakedPotato.enable") && copy.getItem() == Items.baked_potato) ||
					(FarragoMod.config.getBoolean("kahur.special.potion.enable") && copy.getItem() == Items.potionitem) ||
					(FarragoMod.config.getBoolean("kahur.special.tnt.enable") && copy.getItem() == Item.getItemFromBlock(Blocks.tnt)) ||
					(FarragoMod.config.getBoolean("kahur.special.torch.enable") && copy.getItem() == Item.getItemFromBlock(Blocks.torch)) ||
					(FarragoMod.config.getBoolean("kahur.special.enderPearl.enable") && copy.getItem() == Items.ender_pearl)
					)) {
				return;
			}
		}
		proj.setItem(copy);
		proj.setDamage(Masses.getMass(copy)+(Masses.getMagic(copy)*2f));
		gun.damageItem((int)Masses.getMagic(copy)+1, player);
		if (copy.getItem() == Items.gunpowder && FarragoMod.config.getBoolean("kahur.special.gunpowder.enable")) {
			Entity owner = null;
			world.createExplosion(owner, player.posX, player.posY, player.posZ, 0.4f, false);
			gun.damageItem(12, player);
		} else if (copy.getItem() == Items.arrow && FarragoMod.config.getBoolean("kahur.special.arrow.enable")) {
			world.spawnEntityInWorld(new EntityArrow(world, player, 0.8f));
		} else {
			world.spawnEntityInWorld(proj);
		}
	}
	
	@Override
	public boolean getIsRepairable(ItemStack a, ItemStack b) {
        return b.getItem() == Item.getItemFromBlock(Blocks.planks) ? true : super.getIsRepairable(a, b);
    }
}
