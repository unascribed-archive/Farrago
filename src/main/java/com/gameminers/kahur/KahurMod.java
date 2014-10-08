package com.gameminers.kahur;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid="kahur",name="Kahur",version="0.2",dependencies="required-after:KitchenSink;after:GlassPane;after:*")
public class KahurMod {
	/*
	 * Damage is calculated based off of two values: Mass and Magic.
	 * 
	 * Mass is determined by the depth and contents of the crafting chain.
	 * An item with no way to craft it has a mass of 1.
	 * For example, a diamond has a mass of 1. A diamond block has a mass of 9.
	 * A gold ingot has a mass of 9 due to the nugget recipe, and a gold block has a mass of 81.
	 * If a crafting recipe makes multiple items, the mass of the used items is added and then divided by the amount made.
	 * Crafting trees deeper than 12 entries will be truncated to 12.
	 * 
	 * Magic is determined by enchantments and enchantment glow.
	 * If the item hasEffect, but no enchantments, magic += 12. (Assume it's a special item.)
	 * For every enchantment on the item, magic += (1 + enchantmentLevel)
	 * If the item has the generic.attackDamage attribute, add half of that to the magic.
	 * For every other attribute, add attributeValue/8 to the magic.
	 * 
	 * The Kahur takes damage equal to (1+magic) every shot.
	 * 
	 * Damage is equal to mass + (magic*2).
	 * 
	 * Drop chance is determined by mass and magic.
	 * For a given item, it's chance of dropping is 1 in max(1, 50-(mass+magic)).
	 * Probably not the best method. Needs research.
	 */
	public static Map<Long, ItemStack> protoStacks = Maps.newHashMap();
	public static Map<Long, List<Float>> mass = Maps.newHashMap();
	public static Map<Long, Float> bakedMass = Maps.newHashMap();
	static boolean baked = false;
	public static List<Runnable> tasks = Lists.newArrayList();
	public static ItemKahur KAHUR;
	@SidedProxy(clientSide="com.gameminers.kahur.ClientProxy",serverSide="com.gameminers.kahur.ServerProxy")
	public static Proxy proxy;
	public static CreativeTabs creativeTab = new CreativeTabs("kahur") {
		
		@Override
		public Item getTabIconItem() {
			return KAHUR;
		}
	};
	@EventHandler
	public void onInit(FMLInitializationEvent e) {
		KAHUR = new ItemKahur();
		GameRegistry.registerItem(KAHUR, "kahur");
		EntityRegistry.registerModEntity(EntityKahurProjectile.class, "kahurShot", 0, this, 64, 12, true);
		RenderingRegistry.registerEntityRenderingHandler(EntityKahurProjectile.class, new RenderKahurProjectile());
		for (WoodColor body : WoodColor.values()) {
			for (WoodColor drum : WoodColor.values()) {
				for (MineralColor pump : MineralColor.values()) {
					ItemStack kahur = new ItemStack(KAHUR);
					NBTTagCompound tag = new NBTTagCompound();
					tag.setString("KahurBodyMaterial", body.name());
					tag.setString("KahurDrumMaterial", drum.name());
					tag.setString("KahurPumpMaterial", pump.name());
					kahur.setTagCompound(tag);
					GameRegistry.addRecipe(kahur,
							"B  ",
							"PD ",
							" /B",
							'B', new ItemStack(Blocks.planks, 1, body.ordinal()),
							'D', new ItemStack(Blocks.planks, 1, drum.ordinal()),
							'P', pump.getMaterial(),
							'/', Items.stick);
					GameRegistry.addRecipe(kahur,
							"  B",
							" DP",
							"B/ ",
							'B', new ItemStack(Blocks.planks, 1, body.ordinal()),
							'D', new ItemStack(Blocks.planks, 1, drum.ordinal()),
							'P', pump.getMaterial(),
							'/', Items.stick);
				}
			}
		}
		FMLCommonHandler.instance().bus().register(this);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void onInformation(ItemTooltipEvent e) {
		if (e.showAdvancedItemTooltips) {
			float massF = getMass(e.itemStack);
			String massS = (massF <= 0 ? "\u00A7cERROR\u00A79" : ""+massF);
			float magic = getMagic(e.itemStack);
			if (magic > 0) {
				e.toolTip.add("\u00A79"+magic+" Kahur Magic");
			}
			e.toolTip.add("\u00A79"+massS+" Kahur Mass");
			e.toolTip.add("\u00A79All Kahur Masses:");
			for (float f : getMasses(e.itemStack, false)) {
				e.toolTip.add("\u00A79- "+f);
			}
		}
	}
	
	@SubscribeEvent
	public void onTick(TickEvent.ServerTickEvent e) {
		if (e.phase == Phase.END) {
			if (!tasks.isEmpty()) {
				tasks.get(0).run();
				tasks.remove(0);
			}
		}
	}
	
	@EventHandler
	public void onPostInit(FMLPostInitializationEvent e) {
		proxy.init();
	}
	
	public static long hashItemStack(ItemStack toHash) {
		long hash = 0;
		hash |= (toHash.getItemDamage() & Short.MAX_VALUE) << Short.SIZE;
		hash |= (Item.getIdFromItem(toHash.getItem()) & Short.MAX_VALUE);
		if (toHash.hasTagCompound()) {
			hash |= (toHash.getTagCompound().hashCode() << 32);
		}
		if (!baked) {
			protoStacks.put(hash, toHash);
		}
		return hash;
	}
	
	@SuppressWarnings("unchecked")
	static void calculateMass(Item i, int depth, int durability) {
		if (depth > 100) {
			throw new StackOverflowError();
		}
		for (IRecipe r : (List<IRecipe>)CraftingManager.getInstance().getRecipeList()) {
			if (r == null) continue;
			if (r.getRecipeOutput() == null) continue;
			if (r.getRecipeOutput().getItem() == i && (durability == 32767 || r.getRecipeOutput().getItemDamage() == durability)) {
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
			if (en.getValue().getItem() == i && (durability == 32767 || en.getValue().getItemDamage() == durability)) {
				ItemStack copy = en.getValue().copy();
				copy.stackSize = 1;
				updateMass(copy, getProtoMass(en.getKey(), depth)*0.75f);
			}
		}
		updateMass(new ItemStack(i, 1, 32767), 1.0f);
	}

	@SuppressWarnings("unchecked")
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
			System.out.println();
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
		List<Float> list = mass.get(is);
		if (list == null) {
			list = Lists.newArrayList();
			mass.put(hashItemStack(is), list);
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
			System.out.println("Baked mass of "+protoStacks.get(en.getKey())+" is "+bakedMass.get(en.getKey()));
		}
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
	
	public static float[] getMasses(ItemStack stack, boolean allowBaked) {
		goat.func_150996_a(stack.getItem());
		goat.setItemDamage(stack.getItemDamage());
		goat.setTagCompound(stack.getTagCompound());
		goat.stackSize = 1;
		long hash = hashItemStack(goat);
		if (baked && allowBaked) {
			if (bakedMass.containsKey(hash)) {
				return new float[] {bakedMass.get(hash)};
			}
		} else {
			if (mass.containsKey(hash)) {
				return array(mass.get(hash));
			}
		}
		goat.setTagCompound(null);
		hash = hashItemStack(goat);
		if (baked && allowBaked) {
			if (bakedMass.containsKey(hash)) {
				return new float[] {bakedMass.get(hash)};
			}
		} else {
			if (mass.containsKey(hash)) {
				return array(mass.get(hash));
			}
		}
		goat.setItemDamage(32767);
		hash = hashItemStack(goat);
		if (baked && allowBaked) {
			if (bakedMass.containsKey(hash)) {
				return new float[] {bakedMass.get(hash)};
			}
		} else {
			if (mass.containsKey(hash)) {
				return array(mass.get(hash));
			}
		}
		return new float[] { 0f };
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
		return avg == Float.NaN || avg == Float.NEGATIVE_INFINITY || avg == Float.POSITIVE_INFINITY ? 0f : avg;
	}
	
	private static float average(List<Float> list) {
		float total = 0f;
		for (Float f : list) {
			total += f;
		}
		float avg = total / list.size();
		return avg == Float.NaN || avg == Float.NEGATIVE_INFINITY || avg == Float.POSITIVE_INFINITY ? 0f : avg;
	}

	@SuppressWarnings("unchecked")
	public static float getMagic(ItemStack stack) {
		float magic = 0;
		Collection<Integer> enchantLevels = EnchantmentHelper.getEnchantments(stack).values();
		if (stack.hasEffect(0) && enchantLevels.isEmpty()) {
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
