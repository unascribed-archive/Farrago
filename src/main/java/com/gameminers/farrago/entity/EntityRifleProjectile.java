package com.gameminers.farrago.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
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

import com.gameminers.farrago.FarragoMod;
import com.gameminers.farrago.enums.RifleMode;

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
    	if (ticksExisted > 250) {
    		setDead();
    	}
    }
    
    @Override
    public boolean isInvisible() {
    	return true;
    }
    
    @Override
    protected float getGravityVelocity() {
    	if (getMode() == RifleMode.TELEPORT) return 0.015f;
    	return 0f;
    }
    
	@Override
	protected void onImpact(MovingObjectPosition pos) {
		if (!worldObj.isRemote) {
			switch (getMode()) {
				case RIFLE: {
					if (pos.entityHit != null && pos.entityHit instanceof EntityLivingBase) {
						((EntityLivingBase)pos.entityHit).attackEntityFrom(new EntityDamageSourceIndirect("laser", this, getThrower()), 8f);
						ticksExisted += 10;
					} else {
						ticksExisted += 15;
					}
					break;
				}
				case SCATTER: {
					if (pos.entityHit != null && pos.entityHit instanceof EntityLivingBase) {
						((EntityLivingBase)pos.entityHit).attackEntityFrom(new EntityDamageSourceIndirect("laser", this, getThrower()), 6f);
						ticksExisted += 15;
					} else {
						ticksExisted += 20;
					}
					break;
				}
				case BAZOOKA: {
					worldObj.createExplosion(this, (int)pos.hitVec.xCoord, (int)pos.hitVec.yCoord, (int)pos.hitVec.zCoord, 3.5f, false);
					setDead();
					break;
				} 
				case BLAZE: {
					if (pos.entityHit != null && pos.entityHit instanceof EntityLivingBase) {
						((EntityLivingBase)pos.entityHit).setFire(40);
						((EntityLivingBase)pos.entityHit).attackEntityFrom(new EntityDamageSourceIndirect("laser", this, getThrower()), 7f);
						ticksExisted += 20;
					} else {
						ticksExisted += 25;
					}
					break;
				}
				case EXPLOSIVE: {
					worldObj.createExplosion(this, (int)pos.hitVec.xCoord, (int)pos.hitVec.yCoord, (int)pos.hitVec.zCoord, 6.0f, true);
					setDead();
					break;
				} 
				case MINING: {
					if (pos.typeOfHit == MovingObjectType.BLOCK && getThrower() instanceof EntityPlayerMP) {
						EntityPlayerMP player = ((EntityPlayerMP)getThrower());
						for (int x = pos.blockX-1; x <= pos.blockX+1; x++) {
							for (int y = pos.blockY-1; y <= pos.blockY+1; y++) {
								for (int z = pos.blockZ-1; z <= pos.blockZ+1; z++) {
									if (!worldObj.isAirBlock(x, y, z) && worldObj.canMineBlock(player, x, y, z)) {
										harvest(player, x, y, z);
										ticksExisted += 5;
									}
								}
							}
						}
					}
					break;
				}
				case PRECISION_MINING: {
					if (pos.typeOfHit == MovingObjectType.BLOCK && getThrower() instanceof EntityPlayerMP) {
						EntityPlayerMP player = ((EntityPlayerMP)getThrower());
						if (!worldObj.isAirBlock(pos.blockX, pos.blockY, pos.blockZ) && worldObj.canMineBlock(player, pos.blockX, pos.blockY, pos.blockZ)) {
							if (harvest(player, pos.blockX, pos.blockY, pos.blockZ)) {
								ticksExisted += 2;
							} else {
								ticksExisted += 5;
							}
						}
					}
					break;
				}
				case GLOW: {
					if (pos.typeOfHit == MovingObjectType.BLOCK) {
						int posX = pos.blockX;
						int posY = pos.blockY;
						int posZ = pos.blockZ;
						switch (pos.sideHit) {
						case 0:
							// bottom
							posY -= 1;
							break;
						case 1:
							// top
							posY += 1;
							break;
						case 2:
							// east
							posZ -= 1;
							break;
						case 3:
							// west
							posZ += 1;
							break;
						case 4:
							// north
							posX -= 1;
							break;
						case 5:
							// south
							posX += 1;
							break;
						}
						Block block = worldObj.getBlock(posX, posY, posZ);
						setDead();
						if (block == null || block.isAir(worldObj, posX, posY, posZ) || block.isReplaceable(worldObj, posX, posY, posZ) || !block.isCollidable()) {
							worldObj.setBlock(posX, posY, posZ, FarragoMod.GLOW);
							worldObj.setBlockMetadataWithNotify(posX, posY, posZ, pos.sideHit, 3);
							return;
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
			            setDead();
			        }
					break;
				}
			}
			worldObj.playSoundAtEntity(this, "farrago:laser_impact", 1.0f, 1.0f);
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
		dataWatcher.updateObject(12, mode.name());
	}
	
	public RifleMode getMode() {
		try {
			return RifleMode.valueOf(dataWatcher.getWatchableObjectString(12));
		} catch (Exception e) {
			setMode(RifleMode.RIFLE);
			return RifleMode.RIFLE;
		}
	}
}
