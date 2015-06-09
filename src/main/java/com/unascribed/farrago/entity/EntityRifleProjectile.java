package com.unascribed.farrago.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.world.BlockEvent;

import com.typesafe.config.Config;
import com.unascribed.farrago.FarragoMod;
import com.unascribed.farrago.enums.RifleMode;

public class EntityRifleProjectile extends EntityThrowable {
	public EntityRifleProjectile(World p_i1773_1_) {
        super(p_i1773_1_);
    }

    public EntityRifleProjectile(World world, EntityLivingBase shooter, float speed, float spread) {
        super(world, shooter);
        setSize(0.25F, 0.25F);
        setLocationAndAngles(shooter.posX, shooter.posY + (double)shooter.getEyeHeight(), shooter.posZ, shooter.rotationYaw, shooter.rotationPitch);
        posX -= (double)(MathHelper.cos(rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        posY -= 0.10000000149011612D;
        posZ -= (double)(MathHelper.sin(rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        setPosition(posX, posY, posZ);
        yOffset = 0.0F;
        float f = 0.4f;
        motionX = (double)(-MathHelper.sin(rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float)Math.PI) * f);
        motionZ = (double)(MathHelper.cos(rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float)Math.PI) * f);
        motionY = (double)(-MathHelper.sin((rotationPitch + func_70183_g()) / 180.0F * (float)Math.PI) * f);
        setThrowableHeading(motionX, motionY, motionZ, speed, spread);
    }

    public EntityRifleProjectile(World p_i1775_1_, double p_i1775_2_, double p_i1775_4_, double p_i1775_6_) {
        super(p_i1775_1_, p_i1775_2_, p_i1775_4_, p_i1775_6_);
    }
    
    @Override
    protected void entityInit() {
    	dataWatcher.addObjectByDataType(12, 4);
    }
    
    @Override
    public void onUpdate() {
    	super.onUpdate();
    	FarragoMod.proxy.spawnRifleParticle(getMode(), this);
    	if (getMode() == RifleMode.BLAZE) {
    		if (!worldObj.isRemote && worldObj.isAirBlock((int)posX, (int)posY, (int)posZ) && rand.nextInt(14) == 1) {
				worldObj.setBlock((int)posX, (int)posY, (int)posZ, Blocks.fire);
			}
    	}
    	if (ticksExisted > (getMode() == null ? FarragoMod.config.getInt("minigun.projectileLifetime") : FarragoMod.config.getInt("rifle.projectile.lifetime"))) {
    		setDead();
    	}
    }
    
    @Override
    public boolean isInvisible() {
    	return true;
    }
    
    @Override
    protected float getGravityVelocity() {
    	return (float)getConfigSection().getDouble("gravity");
    }
    
	private Config getConfigSection() {
		RifleMode mode = getMode();
		if (mode == null) return FarragoMod.config.getConfig("minigun");
		return FarragoMod.config.getConfig("rifle.modes."+mode.name().toLowerCase());
	}

	@Override
	protected void onImpact(MovingObjectPosition pos) {
		if (!worldObj.isRemote) {
			int hitBlockX = pos.blockX;
			int hitBlockY = pos.blockY;
			int hitBlockZ = pos.blockZ;
			int targetBlockX = hitBlockX;
			int targetBlockY = hitBlockY;
			int targetBlockZ = hitBlockZ;
			switch (pos.sideHit) {
			case 0:
				// bottom
				targetBlockY -= 1;
				break;
			case 1:
				// top
				targetBlockY += 1;
				break;
			case 2:
				// east
				targetBlockZ -= 1;
				break;
			case 3:
				// west
				targetBlockZ += 1;
				break;
			case 4:
				// north
				targetBlockX -= 1;
				break;
			case 5:
				// south
				targetBlockX += 1;
				break;
			}
			Block hitBlock = worldObj.getBlock(hitBlockX, hitBlockY, hitBlockZ);
			Block targetBlock = worldObj.getBlock(targetBlockX, targetBlockY, targetBlockZ);
			if (getMode() == null) {
				int cost = 0;
				if (pos.entityHit != null && pos.entityHit instanceof EntityLivingBase) {
					((EntityLivingBase)pos.entityHit).attackEntityFrom(new EntityDamageSourceIndirect("laser", this, getThrower()), (float)FarragoMod.config.getDouble("minigun.damage"));
					cost = FarragoMod.getPassThruCost("minigun.passThruCost.entity");
				} else if (FarragoMod.config.getBoolean("minigun.breakPlants")) {
					if (hitBlock.getMaterial() == Material.plants || hitBlock.getMaterial() == Material.leaves ||
							hitBlock.isReplaceable(worldObj, hitBlockX, hitBlockY, hitBlockZ) || hitBlock.getMaterial() == Material.glass) {
						if (pos.typeOfHit == MovingObjectType.BLOCK && getThrower() instanceof EntityPlayerMP) {
							EntityPlayerMP player = ((EntityPlayerMP)getThrower());
							harvest(player, hitBlockX, hitBlockY, hitBlockZ);
							return;
						}
					}
					cost = FarragoMod.getPassThruCost("minigun.passThruCost.block");
				}
				applyCost(cost);
				return;
			}
			switch (getMode()) {
				case RIFLE: {
					if (pos.entityHit != null && pos.entityHit instanceof EntityLivingBase) {
						float damage = (float)getConfigSection().getDouble("damage");
						((EntityLivingBase)pos.entityHit).attackEntityFrom(new EntityDamageSourceIndirect("laser", this, getThrower()), damage);
					}
					break;
				}
				case SCATTER: {
					if (pos.entityHit != null && pos.entityHit instanceof EntityLivingBase) {
						float damage = (float)getConfigSection().getDouble("damage");
						((EntityLivingBase)pos.entityHit).attackEntityFrom(new EntityDamageSourceIndirect("laser", this, getThrower()), damage);
					}
					break;
				}
				case BAZOOKA: {
					float radius = (float)getConfigSection().getDouble("explosionRadius");
					worldObj.createExplosion(this, (int)pos.hitVec.xCoord, (int)pos.hitVec.yCoord, (int)pos.hitVec.zCoord, radius, false);
					break;
				} 
				case BLAZE: {
					if (pos.entityHit != null && pos.entityHit instanceof EntityLivingBase) {
						((EntityLivingBase)pos.entityHit).setFire(getConfigSection().getInt("fireTicks"));
						if (pos.entityHit instanceof EntityAnimal) {
							float damage = (float)getConfigSection().getDouble("damageToAnimals");
							((EntityLivingBase)pos.entityHit).attackEntityFrom(new EntityDamageSourceIndirect("laser", this, getThrower()), damage);
						} else {
							float damage = (float)getConfigSection().getDouble("damage");
							((EntityLivingBase)pos.entityHit).attackEntityFrom(new EntityDamageSourceIndirect("laser", this, getThrower()), damage);
						}
					} else {
						if (getConfigSection().getBoolean("setsFiresOnBlockHit") && targetBlock == null || targetBlock.isAir(worldObj, targetBlockX, targetBlockY, targetBlockZ) ||
								targetBlock.isReplaceable(worldObj, targetBlockX, targetBlockY, targetBlockZ) || !targetBlock.isCollidable()) {
							worldObj.setBlock(targetBlockX, targetBlockY, targetBlockZ, Blocks.fire);
						}
						if (getConfigSection().getBoolean("ignitesTnt") && hitBlock == Blocks.tnt) {
							((BlockTNT)Blocks.tnt).func_150114_a(worldObj, hitBlockX, hitBlockY, hitBlockZ, 1, getThrower());
							worldObj.setBlockToAir(hitBlockX, hitBlockY, hitBlockZ);
						}
					}
					break;
				}
				case EXPLOSIVE: {
					float radius = (float)getConfigSection().getDouble("explosionRadius");
					worldObj.createExplosion(this, pos.hitVec.xCoord, pos.hitVec.yCoord, pos.hitVec.zCoord, radius, true);
					break;
				} 
				case MINING: {
					if (pos.typeOfHit == MovingObjectType.BLOCK && getThrower() instanceof EntityPlayerMP) {
						EntityPlayerMP player = ((EntityPlayerMP)getThrower());
						for (int x = pos.blockX-1; x <= pos.blockX+1; x++) {
							for (int y = pos.blockY-1; y <= pos.blockY+1; y++) {
								for (int z = pos.blockZ-1; z <= pos.blockZ+1; z++) {
									if (!worldObj.isAirBlock(x, y, z) && worldObj.canMineBlock(player, x, y, z)) {
										if (harvest(player, x, y, z)) {
											ticksExisted += FarragoMod.getPassThruCost("rifle.modes.mining.harvestCost.harvestable");
										} else {
											ticksExisted += FarragoMod.getPassThruCost("rifle.modes.mining.harvestCost.unharvestable");
										}
									}
								}
							}
						}
					}
					return;
				}
				case PRECISION_MINING: {
					if (pos.typeOfHit == MovingObjectType.BLOCK && getThrower() instanceof EntityPlayerMP) {
						EntityPlayerMP player = ((EntityPlayerMP)getThrower());
						if (!worldObj.isAirBlock(pos.blockX, pos.blockY, pos.blockZ) && worldObj.canMineBlock(player, pos.blockX, pos.blockY, pos.blockZ)) {
							if (harvest(player, pos.blockX, pos.blockY, pos.blockZ)) {
								ticksExisted += FarragoMod.getPassThruCost("rifle.modes.precision_mining.harvestCost.harvestable");
							} else {
								ticksExisted += FarragoMod.getPassThruCost("rifle.modes.precision_mining.harvestCost.unharvestable");
							}
						}
					}
					return;
				}
				case GLOW: {
					if (pos.typeOfHit == MovingObjectType.BLOCK) {
						if (targetBlock == null || targetBlock.isAir(worldObj, targetBlockX, targetBlockY, targetBlockZ) ||
								targetBlock.isReplaceable(worldObj, targetBlockX, targetBlockY, targetBlockZ) || !targetBlock.isCollidable()) {
							worldObj.setBlock(targetBlockX, targetBlockY, targetBlockZ, FarragoMod.GLOW);
							worldObj.setBlockMetadataWithNotify(targetBlockX, targetBlockY, targetBlockZ, pos.sideHit, 3);
						}
					}
					break;
				}
				case TELEPORT: {
					if (pos.entityHit != null) {
			            pos.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 0.0F);
			        }

			        for (int i = 0; i < 32; ++i) {
			            worldObj.spawnParticle("portal", posX, posY + rand.nextDouble() * 2.0D, posZ, rand.nextGaussian(), 0.0D, rand.nextGaussian());
			        }

			        if (!worldObj.isRemote) {
			            if (getThrower() != null && getThrower() instanceof EntityPlayerMP) {
			                EntityPlayerMP entityplayermp = (EntityPlayerMP)getThrower();

		                    EnderTeleportEvent event = new EnderTeleportEvent(entityplayermp, posX, posY, posZ, 2.5F);
		                    if (!MinecraftForge.EVENT_BUS.post(event)) {
			                    if (getThrower().isRiding()) {
			                        getThrower().mountEntity((Entity)null);
			                    }

			                    getThrower().setPositionAndUpdate(event.targetX, event.targetY, event.targetZ);
			                    getThrower().fallDistance = 0.0F;
			                    getThrower().attackEntityFrom(DamageSource.fall, event.attackDamage);
		                    }
			            }
			        }
			        setDead();
					return;
				}
			}
			worldObj.playSoundAtEntity(this, "farrago:laser_impact", 1.0f, 1.0f);
			int cost;
			if (pos.typeOfHit == MovingObjectType.ENTITY) {
				cost = FarragoMod.getPassThruCost(getConfigSection(), "passThruCost.entity");
			} else {
				cost = FarragoMod.getPassThruCost(getConfigSection(), "passThruCost.block");
			}
			applyCost(cost);
		}
	}

	private void applyCost(int cost) {
		if (cost == -1) {
			setDead();
		} else {
			ticksExisted += cost;
		}
	}

	private boolean harvest(EntityPlayerMP player, int x, int y, int z) {
		BlockEvent.BreakEvent event = ForgeHooks.onBlockBreakEvent(worldObj, player.theItemInWorldManager.getGameType(), player, x, y, z);
		if (event.isCanceled()) {
			return false;
		} else {
			Block block = worldObj.getBlock(x, y, z);
	        int meta = worldObj.getBlockMetadata(x, y, z);
	        if (block.getBlockHardness(worldObj, x, y, z) < 0) return false;
	        worldObj.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (worldObj.getBlockMetadata(x, y, z) << 12));
	        block.onBlockHarvested(worldObj, x, y, z, meta, player);
	        boolean success = block.removedByPlayer(worldObj, player, x, y, z, true);
	
	        if (success) {
	            block.onBlockDestroyedByPlayer(worldObj, x, y, z, meta);
	            block.harvestBlock(worldObj,player, x, y, z, meta);
	            block.dropXpOnBlockBreak(worldObj, x, y, z, event.getExpToDrop() != 0 ? event.getExpToDrop() : block.getExpDrop(worldObj, meta, 0));
	        }
	        return success;
		}
	}

	public void setMode(RifleMode mode) {
		if (mode == null) {
			dataWatcher.updateObject(12, "null");
		} else {
			dataWatcher.updateObject(12, mode.name());
		}
	}
	
	public RifleMode getMode() {
		if ("null".equals(dataWatcher.getWatchableObjectString(12))) return null;
		try {
			return RifleMode.valueOf(dataWatcher.getWatchableObjectString(12));
		} catch (Exception e) {
			setMode(RifleMode.RIFLE);
			return RifleMode.RIFLE;
		}
	}
}
