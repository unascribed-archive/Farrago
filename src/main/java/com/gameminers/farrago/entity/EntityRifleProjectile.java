package com.gameminers.farrago.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;

import com.gameminers.farrago.FarragoMod;
import com.gameminers.farrago.RifleMode;

public class EntityRifleProjectile extends EntityThrowable {
	public EntityRifleProjectile(World p_i1773_1_) {
        super(p_i1773_1_);
    }

    public EntityRifleProjectile(World world, EntityLivingBase shooter) {
        super(world, shooter);
        setSize(0.25F, 0.25F);
        setLocationAndAngles(shooter.posX, shooter.posY + (double)shooter.getEyeHeight(), shooter.posZ, shooter.rotationYaw, shooter.rotationPitch);
        posX -= (double)(MathHelper.cos(rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        posY -= 0.10000000149011612D;
        posZ -= (double)(MathHelper.sin(rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        setPosition(posX, posY, posZ);
        yOffset = 0.0F;
        float f = (rand.nextFloat()*0.8f)+0.2f;
        motionX = (double)(-MathHelper.sin(rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float)Math.PI) * f);
        motionZ = (double)(MathHelper.cos(rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float)Math.PI) * f);
        motionY = (double)(-MathHelper.sin((rotationPitch + func_70183_g()) / 180.0F * (float)Math.PI) * f);
        setThrowableHeading(motionX, motionY, motionZ, func_70182_d(), 0.0F);
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
    	return 0f;
    }
    
	@Override
	protected void onImpact(MovingObjectPosition pos) {
		if (!worldObj.isRemote) {
			switch (getMode()) {
				case DAMAGE: {
					if (pos.entityHit != null && pos.entityHit instanceof EntityLivingBase) {
						((EntityLivingBase)pos.entityHit).attackEntityFrom(new EntityDamageSourceIndirect("laser", this, getThrower()), 24f);
						ticksExisted += 10;
					} else {
						ticksExisted += 15;
					}
					break;
				}
				case AREA_DAMAGE: {
					worldObj.createExplosion(this, (int)pos.hitVec.xCoord, (int)pos.hitVec.yCoord, (int)pos.hitVec.zCoord, 4.0f, false);
					setDead();
					break;
				} 
				case EXPLOSION: {
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
				case PRECISION: {
					if (pos.typeOfHit == MovingObjectType.BLOCK && getThrower() instanceof EntityPlayerMP) {
						EntityPlayerMP player = ((EntityPlayerMP)getThrower());
						if (!worldObj.isAirBlock(pos.blockX, pos.blockY, pos.blockZ) && worldObj.canMineBlock(player, pos.blockX, pos.blockY, pos.blockZ)) {
							if (player.theItemInWorldManager.tryHarvestBlock(pos.blockX, pos.blockY, pos.blockZ)) {
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
						if (block == null || block.isAir(worldObj, posX, posY, posZ) || block.isReplaceable(worldObj, posX, posY, posZ) || !block.isCollidable()) {
							worldObj.setBlock(posX, posY, posZ, FarragoMod.GLOW);
							worldObj.setBlockMetadataWithNotify(posX, posY, posZ, pos.sideHit, 3);
							return;
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
		int foodLevel = player.getFoodStats().getFoodLevel();
		float sat = player.getFoodStats().getSaturationLevel();
		Block block = worldObj.getBlock(x, y, z);
        int meta = worldObj.getBlockMetadata(x, y, z);
        block.onBlockHarvested(worldObj, x, y, z, meta, player);
        boolean success = block.removedByPlayer(worldObj, player, x, y, z, true);

        if (success) {
            block.onBlockDestroyedByPlayer(worldObj, x, y, z, meta);
            block.harvestBlock(worldObj,player, x, y, z, meta);
            block.dropXpOnBlockBreak(worldObj, x, y, z, meta);
        }
        player.getFoodStats().setFoodLevel(foodLevel);
        player.getFoodStats().setFoodSaturationLevel(sat);
        return success;
	}

	public void setMode(RifleMode mode) {
		dataWatcher.updateObject(12, mode.name());
	}
	
	public RifleMode getMode() {
		try {
			return RifleMode.valueOf(dataWatcher.getWatchableObjectString(12));
		} catch (Exception e) {
			setMode(RifleMode.DAMAGE);
			return RifleMode.DAMAGE;
		}
	}
}