package com.unascribed.farrago.damagesource;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;

public class DamageSourceBackfire extends DamageSource {
	private ItemStack source;
	public DamageSourceBackfire(String str, ItemStack source) {
		super(str);
		this.source = source;
	}
	
	public ItemStack getSource() {
		return source;
	}
	
	@Override
	public IChatComponent func_151519_b(EntityLivingBase p_151519_1_) {
		EntityLivingBase entitylivingbase1 = p_151519_1_.func_94060_bK();
        String s = "death.attack." + this.damageType;
        String s1 = s + ".player";
        return entitylivingbase1 != null && StatCollector.canTranslate(s1) ?
        		new ChatComponentTranslation(s1, p_151519_1_.func_145748_c_(), source.func_151000_E(), entitylivingbase1.func_145748_c_()):
        		new ChatComponentTranslation(s, p_151519_1_.func_145748_c_(), source.func_151000_E());
	}

}
