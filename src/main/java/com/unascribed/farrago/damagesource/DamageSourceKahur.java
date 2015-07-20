package com.unascribed.farrago.damagesource;

import com.unascribed.farrago.entity.EntityKahurProjectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;

public class DamageSourceKahur extends EntityDamageSourceIndirect {

	public DamageSourceKahur(String str, Entity proj, Entity shooter) {
		super(str, proj, shooter);
	}
	
	@Override
	public IChatComponent func_151519_b(EntityLivingBase p_151519_1_) {
		EntityKahurProjectile ekp = (EntityKahurProjectile) getSourceOfDamage();
		IChatComponent ichatcomponent = getEntity() == null ? this.damageSourceEntity.func_145748_c_() : getEntity().func_145748_c_();
        ItemStack itemstack = getEntity() instanceof EntityLivingBase ? ((EntityLivingBase)getEntity()).getHeldItem() : null;
        String s = "death.attack." + this.damageType;
        String s1 = s + ".item";
        return itemstack != null && itemstack.hasDisplayName() && StatCollector.canTranslate(s1) ?
        		new ChatComponentTranslation(s1, p_151519_1_.func_145748_c_(), ichatcomponent, ekp.getItem().func_151000_E(), itemstack.func_151000_E())
        		: new ChatComponentTranslation(s, p_151519_1_.func_145748_c_(), ichatcomponent, ekp.getItem().func_151000_E());
	}

}
