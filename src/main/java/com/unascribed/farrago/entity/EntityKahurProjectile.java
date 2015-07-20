package com.unascribed.farrago.entity;

import java.util.Iterator;
import java.util.List;

import com.unascribed.farrago.FarragoMod;
import com.unascribed.farrago.damagesource.DamageSourceKahur;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

public class EntityKahurProjectile extends EntityThrowable {
	private float damage = 2;
	
	public EntityKahurProjectile(World p_i1773_1_) {
        super(p_i1773_1_);
    }

    public EntityKahurProjectile(World p_i1774_1_, EntityLivingBase p_i1774_2_) {
        super(p_i1774_1_, p_i1774_2_);
    }

    public EntityKahurProjectile(World p_i1775_1_, double p_i1775_2_, double p_i1775_4_, double p_i1775_6_) {
        super(p_i1775_1_, p_i1775_2_, p_i1775_4_, p_i1775_6_);
    }

    @Override
    protected void entityInit() {
    	dataWatcher.addObjectByDataType(12, 5);
    }
    
    public float getDamage() {
		return damage;
	}
    
    public ItemStack getItem() {
    	return dataWatcher.getWatchableObjectItemStack(12);
	}
    
    public void setDamage(float damage) {
		this.damage = damage;
	}
    
    public void setItem(ItemStack item) {
		dataWatcher.updateObject(12, item);
	}
    
    @Override
    public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
    	super.writeEntityToNBT(p_70014_1_);
    	if (getItem() != null) {
    		p_70014_1_.setTag("KahurItem", getItem().writeToNBT(new NBTTagCompound()));
    	}
    }
    
    @Override
    public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
    	super.readEntityFromNBT(p_70037_1_);
    	if (p_70037_1_.hasKey("KahurItem")) {
    		setItem(ItemStack.loadItemStackFromNBT(p_70037_1_.getCompoundTag("KahurItem")));
    	}
    }
    
    @Override
    public void onUpdate() {
    	super.onUpdate();
    	if (entity != null) {
    		entity.onUpdate();
    	}
    }
    
    @Override
    public void onEntityUpdate() {
    	super.onEntityUpdate();
    	if (entity != null) {
    		entity.onEntityUpdate();
    	}
    }
    
	@Override
	protected void onImpact(MovingObjectPosition pos) {
		float dropChance = 0.8f;
		setDead();
		if (!FarragoMod.config.getBoolean("kahur.special.disable")) {
			if (pos.entityHit != null) {
				if (pos.entityHit.isDead) return;
				if (pos.entityHit instanceof EntityLivingBase) {
					EntityLivingBase living = ((EntityLivingBase)pos.entityHit);
					if (FarragoMod.config.getBoolean("kahur.special.potato.enable") && getItem().getItem() == Items.potato && living instanceof EntityPlayerMP) {
						((EntityPlayerMP)living).getFoodStats().addStats(1, 0.2f);
						worldObj.playSoundAtEntity(living, "random.burp", 0.8f, 1.0f);
						dropChance = 0.0f;
					} else {
						living.attackEntityFrom(new DamageSourceKahur("kahur_shot", this, getThrower()), damage);
						dropChance = 0.4f;
						if (FarragoMod.config.getBoolean("kahur.special.poisonousPotato.enable") && getItem().getItem() == Items.poisonous_potato) {
							living.addPotionEffect(new PotionEffect(Potion.poison.getId(), 100, 1));
						} else if (FarragoMod.config.getBoolean("kahur.special.bakedPotato.enable") && getItem().getItem() == Items.baked_potato) {
							living.setFire(5);
						}
					}
				}
			}
			if (FarragoMod.config.getBoolean("kahur.special.potion.enable") && getItem().getItem() == Items.potionitem) {
				if (!this.worldObj.isRemote) {
					List list = Items.potionitem.getEffects(getItem());
	
					if (list != null && !list.isEmpty()) {
						AxisAlignedBB axisalignedbb = this.boundingBox.expand(4.0D,
								2.0D, 4.0D);
						List list1 = this.worldObj.getEntitiesWithinAABB(
								EntityLivingBase.class, axisalignedbb);
	
						if (list1 != null && !list1.isEmpty()) {
							Iterator iterator = list1.iterator();
	
							while (iterator.hasNext()) {
								EntityLivingBase entitylivingbase = (EntityLivingBase) iterator
										.next();
								double d0 = this
										.getDistanceSqToEntity(entitylivingbase);
	
								if (d0 < 16.0D) {
									double d1 = 1.0D - Math.sqrt(d0) / 4.0D;
	
									if (entitylivingbase == pos.entityHit) {
										d1 = 1.0D;
									}
	
									Iterator iterator1 = list.iterator();
	
									while (iterator1.hasNext()) {
										PotionEffect potioneffect = (PotionEffect) iterator1
												.next();
										int i = potioneffect.getPotionID();
	
										if (Potion.potionTypes[i].isInstant()) {
											Potion.potionTypes[i]
													.affectEntity(
															this.getThrower(),
															entitylivingbase,
															potioneffect
																	.getAmplifier(),
															d1);
										} else {
											int j = (int) (d1
													* (double) potioneffect
															.getDuration() + 0.5D);
	
											if (j > 20) {
												entitylivingbase
														.addPotionEffect(new PotionEffect(
																i,
																j,
																potioneffect
																		.getAmplifier()));
											}
										}
									}
								}
							}
						}
					}
	
					this.worldObj.playAuxSFX(2002, (int) Math.round(this.posX),
							(int) Math.round(this.posY),
							(int) Math.round(this.posZ), getItem().getItemDamage());
					dropChance = 0.0f;
				}
			}
			if (FarragoMod.config.getBoolean("kahur.special.tnt.enable") && Block.getBlockFromItem(getItem().getItem()) == Blocks.tnt) {
				if (!this.worldObj.isRemote) {
					worldObj.createExplosion(this, posX, posY, posZ, 2.7f, true);
				}
				return;
			}
			if (FarragoMod.config.getBoolean("kahur.special.torch.enable") && getItem().getItem() == Item.getItemFromBlock(Blocks.torch)) {
				if (worldObj.isRemote) return;
				int posX = pos.blockX;
				int posY = pos.blockY;
				int posZ = pos.blockZ;
				int meta = 0;
				switch (pos.sideHit) {
				case -1:
					// none
					return;
				case 0:
					// bottom
					return;
				case 1:
					// top
					posY += 1;
					meta = 5;
					break;
				case 2:
					// east
					posZ -= 1;
					meta = 4;
					break;
				case 3:
					// west
					posZ += 1;
					meta = 3;
					break;
				case 4:
					// north
					posX -= 1;
					meta = 2;
					break;
				case 5:
					// south
					posX += 1;
					meta = 1;
					break;
				}
				Block block = worldObj.getBlock(posX, posY, posZ);
				if (block == null || block.isAir(worldObj, posX, posY, posZ) || !block.isCollidable()) {
					worldObj.setBlock(posX, posY, posZ, Blocks.torch);
					worldObj.setBlockMetadataWithNotify(posX, posY, posZ, meta, 3);
					return;
				}
			}
			if (FarragoMod.config.getBoolean("kahur.special.enderPearl.enable") && getItem().getItem() == Items.ender_pearl) {
				if (!worldObj.isRemote) {
					if (getThrower() != null && getThrower() instanceof EntityPlayerMP) {
		                EntityPlayerMP entityplayermp = (EntityPlayerMP)getThrower();
		                if (entityplayermp.playerNetServerHandler.func_147362_b().isChannelOpen() && entityplayermp.worldObj == worldObj) {
		                    EnderTeleportEvent event = new EnderTeleportEvent(entityplayermp, posX, posY, posZ, 5.0F);
		                    if (!MinecraftForge.EVENT_BUS.post(event)) {
			                    if (getThrower().isRiding()) {
			                        getThrower().mountEntity((Entity)null);
			                    }
		
			                    getThrower().setPositionAndUpdate(event.targetX, event.targetY, event.targetZ);
			                    getThrower().fallDistance = 0.0F;
			                    getThrower().attackEntityFrom(DamageSource.fall, event.attackDamage);
			                    return;
		                    }
		                }
		            }
				}
			}
		}
		
		if (getItem().isItemStackDamageable()) {
			if (getThrower() != null) {
				getItem().damageItem(20, getThrower());
				if (getItem().stackSize == 0) {
					playSound("random.break", 0.8F, 0.8F + this.worldObj.rand.nextFloat() * 0.4F);
					return;
				}
			}
		}
		if (!worldObj.isRemote) {
			if (getItem().getItem().isItemTool(getItem()) || rand.nextFloat() < dropChance) {
				entityDropItem(getItem(), 0.2f);
				worldObj.playSoundAtEntity(this, "step.stone", 1.5f, 2.0f);
			} else {
				worldObj.playSoundAtEntity(this, "random.break", 0.8f, 2.0f);
			}
		}
	}

	private Entity entity;
	
	public Entity getDummyEntity() {
		if (entity == null && getItem() != null) {
			entity = new EntityItem(worldObj, posX, posY, posZ, getItem());
			Entity newEntity = getItem().getItem().createEntity(worldObj, entity, getItem());
			if (newEntity != null) {
				entity = newEntity;
			}
		}
		return entity;
	}
}
