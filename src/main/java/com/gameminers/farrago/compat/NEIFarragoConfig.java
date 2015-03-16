package com.gameminers.farrago.compat;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;

import com.gameminers.farrago.FarragoMod;

import cpw.mods.fml.common.Mod;

public class NEIFarragoConfig implements IConfigureNEI {

	@Override
	public String getName() {
		return FarragoMod.class.getAnnotation(Mod.class).name();
	}

	@Override
	public String getVersion() {
		return FarragoMod.class.getAnnotation(Mod.class).version();
	}

	@Override
	public void loadConfig() {
		API.hideItem(new ItemStack(FarragoMod.UNDEFINED, 1, OreDictionary.WILDCARD_VALUE));
	}

}
