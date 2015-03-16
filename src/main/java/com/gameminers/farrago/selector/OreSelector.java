package com.gameminers.farrago.selector;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.commons.lang3.ArrayUtils;

public class OreSelector implements Selector {
	private final String ore;
	private final Selector fallback;
	
	
	private boolean oresPresent = false;
	private boolean oresChecked = false;
	private int oreId;
	private ItemStack oreRepresentation;
	
	public OreSelector(String ore) {
		this.ore = ore;
		this.fallback = new NullSelector();
	}

	public OreSelector(String ore, Selector fallback) {
		this.ore = ore;
		this.fallback = fallback;
	}

	@Override
	public Object getRepresentation() {
		return isOrePresent() ? ore : fallback.getRepresentation();
	}

	@Override
	public ItemStack getItemStackRepresentation() {
		if (isOrePresent()) {
			return oreRepresentation;
		}
		return fallback.getItemStackRepresentation();
	}

	@Override
	public boolean itemStackMatches(ItemStack stack) {
		return isOrePresent() ? ArrayUtils.contains(OreDictionary.getOreIDs(stack), oreId) : fallback.itemStackMatches(stack);
	}
	
	
	
	public String getOre() {
		return ore;
	}
	
	public int getOreId() {
		return oreId;
	}
	
	public Selector getFallback() {
		return fallback;
	}
	
	public boolean isOrePresent() {
		if (oresChecked) return oresPresent;
		List<ItemStack> ores = OreDictionary.getOres(ore);
		if (ores.isEmpty()) {
			oresPresent = false;
		} else {
			oresPresent = true;
			oreRepresentation = ores.get(0);
		}
		oresChecked = true;
		return oresPresent;
	}

	@Override
	public String toString() {
		return "#"+ore+"("+fallback+")";
	}

}
