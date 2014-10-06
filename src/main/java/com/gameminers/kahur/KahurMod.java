package com.gameminers.kahur;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid="kahur",name="Kahur",version="0.1")
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
	public static Map<Item, Integer> mass = Maps.newHashMap();
	public static List<Runnable> tasks = Lists.newArrayList();
	public static ItemKahur KAHUR;
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
		
	}
	
	private static final UUID field_111210_e = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	
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
            System.out.println(attr+": "+amt2);
            if (attr.equals("generic.attackDamage")) {
            	magic += amt2/2f;
            } else {
            	magic += amt2/8f;
            }
		}
		if (stack.getItem() instanceof ItemFood) {
			magic += ((ItemFood)stack.getItem()).func_150906_h(stack);
		}
		System.out.println("magic "+magic);
		return magic;
	}
}
