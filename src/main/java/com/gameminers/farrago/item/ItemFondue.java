package com.gameminers.farrago.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.gameminers.farrago.FarragoMod;

public class ItemFondue extends ItemFood {
	private IIcon[] icons;
	private int[] healAmounts = {
			8,
			18,
			5,
			5,
			40
	};
	private String[] names = {
			"cheese",
			"chinese",
			"chocolate",
			"cider",
			"cyber_cider"
	};
	public ItemFondue() {
		super(0, false);
		setUnlocalizedName("fondue");
		setTextureName("farrago:fondue");
		setCreativeTab(FarragoMod.creativeTab);
		setHasSubtypes(true);
		setMaxStackSize(1);
	}
	public ItemStack onEaten(ItemStack p_77654_1_, World p_77654_2_, EntityPlayer p_77654_3_) {
        super.onEaten(p_77654_1_, p_77654_2_, p_77654_3_);
        return new ItemStack(FarragoMod.CAQUELON);
    }
	@Override
	public boolean hasEffect(ItemStack par1ItemStack, int pass) {
		return par1ItemStack.getItemDamage() == 4;
	}
	@Override
	public boolean getShareTag() {
		return false;
	}
	@Override
	public void onUpdate(ItemStack p_77663_1_, World p_77663_2_,
			Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
		if (!p_77663_2_.isRemote) return;
		if (p_77663_1_.getItemDamage() != 4) return;
		if (!FarragoMod.config.getBoolean("fondue.cyberCider.jingle")) return;
		if (p_77663_3_ instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)p_77663_3_;
			if (player.getCurrentEquippedItem() == p_77663_1_) {
				int ciderDecayTicks = 0;
				if (p_77663_1_.hasTagCompound() && p_77663_1_.getTagCompound().hasKey("CiderDecayTicks")) {
					ciderDecayTicks = p_77663_1_.getTagCompound().getInteger("CiderDecayTicks");
				}
				if (ciderDecayTicks > 0) {
					ciderDecayTicks--;
				} else {
					player.playSound("farrago:cyber_cider", 0.5f, 1.0f);
					ciderDecayTicks = 132;
				}
				NBTTagCompound compound = p_77663_1_.getTagCompound();
				if (compound == null) {
					compound = new NBTTagCompound();
					p_77663_1_.setTagCompound(compound);
				}
				compound.setInteger("CiderDecayTicks", ciderDecayTicks);
			}
		}
	}
	@Override
	public IIcon getIconFromDamage(int damage) {
		return icons[damage];
	}
	@Override
	public int func_150905_g(ItemStack p_150905_1_) {
		// getHealAmount
		return healAmounts[p_150905_1_.getItemDamage()];
	}
	@Override
	public float func_150906_h(ItemStack p_150906_1_) {
		// getSaturation
		return healAmounts[p_150906_1_.getItemDamage()]/10f;
	}
	@Override
	public String getUnlocalizedName(ItemStack p_77667_1_) {
		return "item."+names[p_77667_1_.getItemDamage()]+"_fondue";
	}
	@Override
	public void registerIcons(IIconRegister r) {
		icons = new IIcon[] {
				r.registerIcon("farrago:cheese_fondue"),
				r.registerIcon("farrago:chinese_fondue"),
				r.registerIcon("farrago:chocolate_fondue"),
				r.registerIcon("farrago:cider"),
				r.registerIcon("farrago:cyber_cider")
		};
	}
	
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		list.add(new ItemStack(item, 1, 0));
		list.add(new ItemStack(item, 1, 1));
		list.add(new ItemStack(item, 1, 2));
		list.add(new ItemStack(item, 1, 3));
		list.add(new ItemStack(item, 1, 4));
	}
}
