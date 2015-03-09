package com.gameminers.farrago.recipes;

import com.gameminers.farrago.FarragoMod;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class RecipeChromaticYttrium extends ShapedOreRecipe {
	public RecipeChromaticYttrium(Block result, Object... recipe) {
		this(new ItemStack(result), recipe);
	}
    public RecipeChromaticYttrium(Item result, Object... recipe) {
    	this(new ItemStack(result), recipe);
    }
    public RecipeChromaticYttrium(ItemStack result, Object... recipe) {
    	super(result, recipe);
    }
    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
    	ItemStack stack = getRecipeOutput().copy();
    	if (!stack.hasTagCompound()) {
    		stack.setTagCompound(new NBTTagCompound());
    	}
    	stack.getTagCompound().setInteger("Color", FarragoMod.VIVID_ORB.getColorFromItemStack(inv.getStackInSlot(4), 0));
    	return stack;
    }
}
