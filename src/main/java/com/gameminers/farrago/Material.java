package com.gameminers.farrago;

import com.gameminers.farrago.item.gun.ItemKahur;
import com.gameminers.farrago.selector.NullSelector;
import com.gameminers.farrago.selector.Selector;
import com.typesafe.config.Config;

public class Material {
	public final String name, moniker;
	public final Selector ingot, block;
	public final int color;
	
	public final boolean validForKahur;
	public final int kahurDurability;
	public final ItemKahur.Ability kahurSpecial;
	public final boolean kahurMobs;
	public final boolean kahurDeterministic;
	
	public final boolean validForBelt;
	public final int beltRows;
	
	public Material(Config obj) {
		name = obj.getString("name");
		moniker = obj.hasPath("moniker") ? obj.getString("moniker") : null;
		ingot = obj.hasPath("ingotSelector") ? FarragoMod.parseSelector(obj.getString("ingotSelector")) : new NullSelector();
		block = obj.hasPath("blockSelector") ? FarragoMod.parseSelector(obj.getString("blockSelector")) : new NullSelector();
		color = Integer.parseInt(obj.getString("color"), 16) | 0xFF000000;
		if (obj.hasPath("kahurDurability")) {
			validForKahur = true;
			kahurDurability = obj.getInt("kahurDurability");
			kahurMobs = obj.hasPath("kahurMobs") && obj.getBoolean("kahurMobs");
			kahurDeterministic = obj.hasPath("kahurDeterministic") && obj.getBoolean("kahurDeterministic");
			kahurSpecial = obj.hasPath("kahurSpecial") ? ItemKahur.Ability.valueOf(obj.getString("kahurSpecial").toUpperCase()) : null;
		} else {
			validForKahur = false;
			kahurDurability = -1;
			kahurMobs = false;
			kahurDeterministic = false;
			kahurSpecial = null;
		}
		if (obj.hasPath("beltRows")) {
			validForBelt = true;
			beltRows = obj.getInt("beltRows");
		} else {
			validForBelt = false;
			beltRows = 0;
		}
	}
}
