package com.unascribed.farrago.recipes;

import com.unascribed.farrago.FarragoMod;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class RecipeChromatic extends ShapedOreRecipe {
	public RecipeChromatic(Block result, Object... recipe) {
		this(new ItemStack(result), recipe);
	}
    public RecipeChromatic(Item result, Object... recipe) {
    	this(new ItemStack(result), recipe);
    }
    public RecipeChromatic(ItemStack result, Object... recipe) {
    	super(result, recipe);
    }
    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
    	ItemStack orb = null;
    	for (int i = 0; i < inv.getSizeInventory(); i++) {
    		ItemStack stack = inv.getStackInSlot(i);
    		if (stack == null) continue;
    		if (stack.getItem() == FarragoMod.VIVID_ORB) {
    			orb = stack;
    			break;
    		}
    	}
    	if (orb != null) {
    		ItemStack stack = getRecipeOutput().copy();
        	if (!stack.hasTagCompound()) {
        		stack.setTagCompound(new NBTTagCompound());
        	}
    		stack.getTagCompound().setInteger("Color", FarragoMod.VIVID_ORB.getColorFromItemStack(orb, 0));
    		return stack;
    	} else {
    		return null;
    	}
    }
}
