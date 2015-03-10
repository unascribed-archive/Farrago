package com.gameminers.farrago.block;

import gminers.kitchensink.Strings;

import java.util.List;

import net.minecraft.block.BlockCompressed;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import com.gameminers.farrago.FarragoMod;

import cpw.mods.fml.common.registry.GameRegistry;

public class BlockResource extends BlockCompressed {
	private String[] resourceTypes = {
		"nether_star",
		"yttrium",
		"yttrium_copper",
		"copper",
		"ender_pearl"
	};
	private MapColor[] resourceColors = {
		MapColor.snowColor,
		MapColor.ironColor,
		MapColor.obsidianColor,
		MapColor.adobeColor,
		MapColor.cyanColor
	};
	private int[] harvestLevels = {
			3,
			2,
			2,
			1,
			1
	};
	private IIcon[] iconsByDamage = new IIcon[resourceTypes.length];
	public BlockResource() {
		super(null);
		setCreativeTab(FarragoMod.creativeTab);
		setHardness(5.0F);
		setResistance(10.0F);
		setStepSound(soundTypeMetal);
		for (int i = 0; i < harvestLevels.length; i++) {
			setHarvestLevel("pickaxe", harvestLevels[i], i);
		}
	}
	
	@Override
	public int damageDropped(int meta) {
		return meta;
	}
	
	@Override
	public MapColor getMapColor(int meta) {
		return meta >= resourceTypes.length ? resourceColors[0] : resourceColors[meta];
	}
	
	@Override
	public IIcon getIcon(int side, int meta) {
		return meta >= resourceTypes.length ? iconsByDamage[0] : iconsByDamage[meta];
	}
	
	public String getUnlocalizedName(int meta) {
		return "tile.block_"+(meta >= resourceTypes.length ? resourceTypes[0] : resourceTypes[meta]);
	}
	
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < resourceTypes.length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}
	@Override
	public void registerBlockIcons(IIconRegister registry) {
		for (int i = 0; i < resourceTypes.length; i++) {
			iconsByDamage[i] = registry.registerIcon("farrago:block_"+resourceTypes[i]);
		}
	}
	public void registerOres() {
		for (int i = 0; i < resourceTypes.length; i++) {
			OreDictionary.registerOre("block"+Strings.formatTitleCase(resourceTypes[i]).replace(" ", ""), new ItemStack(this, 1, i));
		}
	}

	public void registerRecipes() {
		for (int i = 0; i < resourceTypes.length; i++) {
			String s = resourceTypes[i];
			String nm = "ingot"+Strings.formatTitleCase(s).replace(" ", "");
			List<ItemStack> li = OreDictionary.getOres(nm);
			if (li == null || li.isEmpty()) {
				nm = "gem"+Strings.formatTitleCase(s).replace(" ", "");
				li = OreDictionary.getOres(nm);
			}
			ItemStack stack = li.get(0).copy();
			stack.stackSize = 9;
			GameRegistry.addRecipe(new ShapelessOreRecipe(stack, new ItemStack(this, 1, i)));
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(this, 1, i), nm, nm, nm, nm, nm, nm, nm, nm, nm));
		}
	}
}
