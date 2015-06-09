package com.unascribed.farrago.selector;

import net.minecraft.item.ItemStack;

public interface Selector {
	Object getRepresentation();
	ItemStack getItemStackRepresentation();
	boolean itemStackMatches(ItemStack stack);
	String toString();
}
