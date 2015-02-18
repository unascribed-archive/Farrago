package com.gameminers.farrago.selector;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemSelector implements Selector {
	private ItemStack item;
	public ItemSelector(Item item) {
		this.item = new ItemStack(item, 1, 32767);
	}

	public ItemSelector(Block item) {
		this.item = new ItemStack(item, 1, 32767);
	}
	
	public ItemSelector(ItemStack item) {
		this.item = item;
	}

	@Override
	public Object getRepresentation() {
		return item;
	}

	@Override
	public ItemStack getItemStackRepresentation() {
		return item;
	}

	@Override
	public boolean itemStackMatches(ItemStack stack) {
		return ItemStack.areItemStacksEqual(item, stack);
	}

}
