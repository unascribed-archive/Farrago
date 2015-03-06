package com.gameminers.farrago.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityBlunderbussProjectile extends EntityThrowable {
	public EntityBlunderbussProjectile(World p_i1773_1_) {
        super(p_i1773_1_);
    }

    public EntityBlunderbussProjectile(World world, EntityLivingBase shooter) {
        super(world, shooter);
        this.setSize(0.25F, 0.25F);
        this.setLocationAndAngles(shooter.posX, shooter.posY + (double)shooter.getEyeHeight(), shooter.posZ, shooter.rotationYaw, shooter.rotationPitch);
        this.posX -= (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        this.posY -= 0.10000000149011612D;
        this.posZ -= (double)(MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.yOffset = 0.0F;
        float f = (rand.nextFloat()*0.8f)+0.2f;
        this.motionX = (double)(-MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI) * f);
        this.motionZ = (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI) * f);
        this.motionY = (double)(-MathHelper.sin((this.rotationPitch + this.func_70183_g()) / 180.0F * (float)Math.PI) * f);
        this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, this.func_70182_d(), 25.0F);
    }

    public EntityBlunderbussProjectile(World p_i1775_1_, double p_i1775_2_, double p_i1775_4_, double p_i1775_6_) {
        super(p_i1775_1_, p_i1775_2_, p_i1775_4_, p_i1775_6_);
    }
    
	@Override
	protected void onImpact(MovingObjectPosition pos) {
		setDead();
		if (!worldObj.isRemote) {
			if (pos.entityHit != null && pos.entityHit instanceof EntityLivingBase) {
				((EntityLivingBase)pos.entityHit).attackEntityFrom(DamageSource.causeMobDamage(getThrower()), (rand.nextFloat()*1f)+0.6f);
				((EntityLivingBase)pos.entityHit).hurtResistantTime = 1;
			}
			if (worldObj instanceof WorldServer) {
				((WorldServer)worldObj).func_147487_a("smoke", pos.hitVec.xCoord, pos.hitVec.yCoord, pos.hitVec.zCoord, 1, 0.2f, 0.2f, 0.2f, 0f);
				((WorldServer)worldObj).playSoundAtEntity(this, "step.stone", 1.0f, 0.3f);
			}
		}
	}
}
