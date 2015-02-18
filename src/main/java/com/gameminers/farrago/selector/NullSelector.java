package com.gameminers.farrago.selector;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class NullSelector implements Selector {

	@Override
	public Object getRepresentation() {
		return null;
	}

	@Override
	public ItemStack getItemStackRepresentation() {
		return new ItemStack(Blocks.air);
	}

	@Override
	public boolean itemStackMatches(ItemStack stack) {
		return false;
	}

}
