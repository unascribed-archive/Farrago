package com.gameminers.farrago.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemVanillaDust extends Item {
	private String[] dustTypes = {
		"iron",
		"gold",
		"emerald",
		"diamond",
		"dorito"
	};
	private IIcon[] iconsByDamage = new IIcon[5];
	private IIcon iron;
	private IIcon gold;
	private IIcon emerald;
	private IIcon diamond;
	public ItemVanillaDust() {
		setUnlocalizedName("dust");
		setTextureName("farrago:dust");
		setCreativeTab(CreativeTabs.tabMaterials);
		setHasSubtypes(true);
		setMaxStackSize(64);
	}
	@Override
	public IIcon getIconFromDamage(int damage) {
		return damage >= 5 ? iron : iconsByDamage[damage];
	}
	@Override
	public String getUnlocalizedName(ItemStack p_77667_1_) {
		int damage = p_77667_1_.getItemDamage();
		return "item.dust_"+(damage >= 5 ? "iron" : dustTypes[damage]);
	}
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < 5; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}
	@Override
	public void addInformation(ItemStack p_77624_1_, EntityPlayer p_77624_2_,
			List p_77624_3_, boolean p_77624_4_) {
		super.addInformation(p_77624_1_, p_77624_2_, p_77624_3_, p_77624_4_);
		if (p_77624_1_.getItemDamage() == 4) {
			p_77624_3_.add(I18n.format("item.dust_dorito.description"));
		}
	}
	@Override
	public boolean hasEffect(ItemStack par1ItemStack, int pass) {
		return par1ItemStack.getItemDamage() == 4;
	}
	@Override
	public void registerIcons(IIconRegister registry) {
		iconsByDamage[0] = iron = registry.registerIcon("farrago:dust_iron");
		iconsByDamage[1] = gold = registry.registerIcon("farrago:dust_gold");
		iconsByDamage[2] = emerald = registry.registerIcon("farrago:dust_emerald");
		iconsByDamage[3] = diamond = registry.registerIcon("farrago:dust_diamond");
		iconsByDamage[4] = registry.registerIcon("farrago:dust_dorito");
	}
	
}
