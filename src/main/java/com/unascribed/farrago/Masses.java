package com.unascribed.farrago;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.ReflectionHelper;

public class Masses {
	public static final Map<Long, List<Float>> mass = Maps.newHashMap();
	public static final Map<Long, Float> bakedMass = Maps.newHashMap();
	static boolean baked = false;
	public static void calculateMass(Item i, int depth, int durability) {
		if (!FarragoMod.config.getBoolean("kahur.calculateMasses")) {
			updateMass(new ItemStack(i, 1, OreDictionary.WILDCARD_VALUE), 1.0f);
			return;
		}
		if (depth > 100) {
			throw new StackOverflowError();
		}
		for (IRecipe r : (List<IRecipe>)CraftingManager.getInstance().getRecipeList()) {
			if (r == null) continue;
			if (r.getRecipeOutput() == null) continue;
			if (r.getRecipeOutput().getItem() == i && (durability == OreDictionary.WILDCARD_VALUE || r.getRecipeOutput().getItemDamage() == durability)) {
				float mass = 0f;
				try {
					if (r instanceof ShapedRecipes) {
						ShapedRecipes shaped = (ShapedRecipes) r;
						/*int count = 0;
						for (ItemStack is : shaped.recipeItems) {
							if (is == null || is.getItem() == null || is.stackSize == 0) continue;
							count++;
						}
						if (count <= 1) continue;*/
						for (ItemStack is : shaped.recipeItems) {
							if (is == null) continue;
							mass += getProtoMass(is, depth);
						}
					} else if (r instanceof ShapelessRecipes) {
						ShapelessRecipes shapeless = (ShapelessRecipes) r;
						/*int count = 0;
						for (ItemStack is : (List<ItemStack>)shapeless.recipeItems) {
							if (is == null || is.getItem() == null || is.stackSize == 0) continue;
							count++;
						}
						if (count <= 1) continue;*/
						for (ItemStack is : (List<ItemStack>)shapeless.recipeItems) {
							if (is == null) continue;
							mass += getProtoMass(is, depth);
						}
					} else if (r instanceof ShapedOreRecipe) {
						ShapedOreRecipe shaped = (ShapedOreRecipe) r;
						mass += processOreRecipe(shaped.getInput(), depth);
					} else if (r instanceof ShapelessOreRecipe) {
						ShapelessOreRecipe shapeless = (ShapelessOreRecipe) r;
						mass += processOreRecipe(shapeless.getInput().toArray(), depth);
					}
				} finally {
					// commit what information we managed to get
					mass /= r.getRecipeOutput().stackSize;
					ItemStack copy = r.getRecipeOutput().copy();
					copy.stackSize = 1;
					updateMass(copy, mass);
				}
			}
		}
		for (Map.Entry<ItemStack, ItemStack> en : ((Map<ItemStack, ItemStack>)FurnaceRecipes.smelting().getSmeltingList()).entrySet()) {
			if (en.getValue().getItem() == i && (durability == OreDictionary.WILDCARD_VALUE || en.getValue().getItemDamage() == durability)) {
				ItemStack copy = en.getValue().copy();
				copy.stackSize = 1;
				updateMass(copy, getProtoMass(en.getKey(), depth)*0.75f);
			}
		}
		updateMass(new ItemStack(i, 1, OreDictionary.WILDCARD_VALUE), 1.0f);
	}

	
	static float processOreRecipe(Object[] input, int depth) {
		float mass = 0f;
		/*int count = 0;
		for (Object o : input) {
			if (o == null) continue;
			count++;
		}
		if (count <= 1) return 0f;*/
		for (Object o : input) {
			if (o instanceof String) {
				float totalMass = 0f;
				float divisor = 0f;
				for (ItemStack is : OreDictionary.getOres((String)o)) {
					if (is == null) continue;
					mass += getProtoMass(is, depth);
					divisor++;
				}
				if (divisor > 0 && totalMass > 0) {
					mass += totalMass / divisor;
				}
			} else if (o instanceof ItemStack) {
				mass += getProtoMass((ItemStack)o, depth);
			} else if (o instanceof List) {
				for (ItemStack is : (List<ItemStack>)o) {
					if (is == null) continue;
					mass += getProtoMass(is, depth);
				}
			} else if (o != null) {
				System.err.println("Unknown object "+o+" ("+o.getClass()+") found in ore recipe");
			}
		}
		return mass;
	}

	static float getProtoMass(ItemStack is, int depth) {
		float isMass = getMass(is);
		if (isMass <= 0f) {
			calculateMass(is.getItem(), depth+1, is.getItemDamage());
			isMass = getMass(is);
		}
		if (isMass <= 0f) {
			isMass = 1f;
		}
		return isMass;
	}

	static void updateMass(ItemStack is, float newMass) {
		if (Float.isInfinite(newMass) || Float.isNaN(newMass) || newMass <= 0) {
			newMass = 0;
		}
		List<Float> list = mass.get(FarragoMod.hashItemStack(is));
		if (list == null) {
			list = Lists.newArrayList();
			mass.put(FarragoMod.hashItemStack(is), list);
		}
		if (newMass <= 0) {
			newMass = 1;
		}
		list.add(newMass);
	}
	
	public static void bake() {
		bakedMass.clear();
		for (Map.Entry<Long, List<Float>> en : mass.entrySet()) {
			bakedMass.put(en.getKey(), average(en.getValue()));
		}
		mass.clear();
		baked = true;
	}

	private static final UUID field_111210_e = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	
	private static final ItemStack goat = new ItemStack(Items.apple);
	
	public static float getMass(ItemStack stack) {
		if (baked) {
			return getMasses(stack, true)[0];
		} else {
			return average(getMasses(stack, true));
		}
	}
	
	private static boolean idioticModNag = true;
	
	public static float[] getMasses(ItemStack stack, boolean allowBaked) {
		if (stack == null) {
			if (idioticModNag) {
				FarragoMod.log.error("Some poorly coded mod is stuffing nulls into the crafting manager! This message will only be shown once.");
				idioticModNag = false;
			}
			return array(0);
		}
		goat.func_150996_a(stack.getItem());
		goat.setItemDamage(stack.getItemDamage());
		goat.setTagCompound(stack.getTagCompound());
		goat.stackSize = 1;
		long hash = FarragoMod.hashItemStack(goat);
		if (baked && allowBaked) {
			if (bakedMass.containsKey(hash)) {
				return array(bakedMass.get(hash));
			}
		} else {
			if (mass.containsKey(hash)) {
				return array(mass.get(hash));
			}
		}
		goat.setTagCompound(null);
		hash = FarragoMod.hashItemStack(goat);
		if (baked && allowBaked) {
			if (bakedMass.containsKey(hash)) {
				return array(bakedMass.get(hash));
			}
		} else {
			if (mass.containsKey(hash)) {
				return array(mass.get(hash));
			}
		}
		ReflectionHelper.setPrivateValue(ItemStack.class, goat, OreDictionary.WILDCARD_VALUE, 5);
		hash = FarragoMod.hashItemStack(goat);
		if (baked && allowBaked) {
			if (bakedMass.containsKey(hash)) {
				return array(bakedMass.get(hash));
			}
		} else {
			if (mass.containsKey(hash)) {
				return array(mass.get(hash));
			}
		}
		if (baked) {
			return array(0.25f);
		} else {
			return array(0f);
		}
	}

	private static float[] array(float... f) {
		return f;
	}


	private static float[] array(List<Float> list) {
		float[] array = new float[list.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = list.get(i);
		}
		return array;
	}

	private static float average(float[] array) {
		float total = 0f;
		for (Float f : array) {
			total += f;
		}
		float avg = total / array.length;
		return Float.isNaN(avg) || avg == Float.NEGATIVE_INFINITY || avg == Float.POSITIVE_INFINITY ? 0f : avg;
	}
	
	private static float average(List<Float> list) {
		float total = 0f;
		for (Float f : list) {
			total += f;
		}
		float avg = total / list.size();
		return Float.isNaN(avg) || avg == Float.NEGATIVE_INFINITY || avg == Float.POSITIVE_INFINITY ? 0f : avg;
	}

	
	public static float getMagic(ItemStack stack) {
		float magic = 0;
		Collection<Integer> enchantLevels = EnchantmentHelper.getEnchantments(stack).values();
		if ((stack.getItem() == Items.golden_apple && stack.getItemDamage() == 1) || stack.getItem() == Items.nether_star) {
			magic += 12;
		}
		magic += enchantLevels.size();
		for (Integer i : enchantLevels) {
			if (i != null && i > 0) {
				magic += i;
			}
		}
		for (Map.Entry entry : (Collection<Map.Entry>)stack.getAttributeModifiers().entries()) {
			String attr = (String) entry.getKey();
			AttributeModifier mod = (AttributeModifier)entry.getValue();
            double amt = mod.getAmount();

            if (mod.getID().equals(field_111210_e)) {
                amt += (double)EnchantmentHelper.func_152377_a(stack, EnumCreatureAttribute.UNDEFINED);
            }

            double amt2;

            if (mod.getOperation() != 1 && mod.getOperation() != 2) {
                amt2 = amt;
            } else {
                amt2 = amt * 100.0D;
            }
            if (attr.equals("generic.attackDamage")) {
            	magic += amt2/2f;
            } else {
            	magic += amt2/8f;
            }
		}
		if (stack.getItem() instanceof ItemFood) {
			magic += ((ItemFood)stack.getItem()).func_150906_h(stack);
		}
		return magic;
	}

}
