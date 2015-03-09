package com.gameminers.farrago.selector;

import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.google.common.collect.Lists;

public class ItemSelector implements Selector {
	private ItemStack item;
	private boolean lenientTag;
	private ItemStack goat = new ItemStack(Items.apple);
	public ItemSelector(Item item) {
		this(new ItemStack(item, 1, 32767));
	}

	public ItemSelector(Block item) {
		this(new ItemStack(item, 1, 32767));
	}
	
	public ItemSelector(ItemStack item) {
		this(item, false);
	}

	public ItemSelector(ItemStack item, boolean lenientTag) {
		this.item = item;
		this.lenientTag = lenientTag;
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
		goat.func_150996_a(stack.getItem());
		goat.setItemDamage(item.getItemDamage() == 32767 ? 32767 : stack.getItemDamage());
		goat.setTagCompound(item.hasTagCompound() && stack.hasTagCompound() ? (NBTTagCompound)stack.getTagCompound().copy() : null);
		ItemStack comp = goat;
		if (lenientTag && item.hasTagCompound() && comp.hasTagCompound()) {
			NBTTagCompound tag = comp.getTagCompound();
			List<String> toRemove = Lists.newArrayList();
			for (String key : (Set<String>)tag.func_150296_c()) {
				if (item.getTagCompound().hasKey(key)) continue;
				toRemove.add(key);
			}
			for (String s : toRemove) {
				tag.removeTag(s);
			}
		}
		comp.stackSize = item.stackSize;
		return ItemStack.areItemStacksEqual(item, comp);
	}

}
