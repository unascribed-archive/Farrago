package com.gameminers.farrago.block;

import java.util.Random;

import net.minecraft.block.BlockFurnace;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.gameminers.farrago.FarragoMod;
import com.gameminers.farrago.tileentity.TileEntityCombustor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCombustor extends BlockFurnace {

	private IIcon bottom;
	private IIcon top;
	private IIcon front;

	public BlockCombustor() {
		super(false);
		setHarvestLevel("pickaxe", 1);
		setCreativeTab(FarragoMod.creativeTab);
		setBlockName("combustor");
		setHardness(5.0f);
		setStepSound(soundTypeMetal);
		setResistance(10.0f);
	}

	@SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
		meta = meta & 0x7;
        return side == 1 ? this.top : (side == 0 ? this.bottom : (side != meta ? this.blockIcon : this.front));
    }

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityCombustor();
	}
	
	@Override
	public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_,
			int p_149694_4_) {
		return Item.getItemFromBlock(this);
	}
	
	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_,
			int p_149650_3_) {
		return Item.getItemFromBlock(this);
	}
	
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister p_149651_1_) {
        blockIcon = p_149651_1_.registerIcon("farrago:combustor_side");
        front = p_149651_1_.registerIcon("farrago:combustor_front");
        top = p_149651_1_.registerIcon("farrago:combustor_top");
        bottom = p_149651_1_.registerIcon("farrago:combustor_bottom");
    }
    
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xOfs, float yOfs, float zOfs) {
        if (world.isRemote) {
            return true;
        } else {
            TileEntityCombustor te = (TileEntityCombustor)world.getTileEntity(x, y, z);

            if (te != null) {
                player.openGui(FarragoMod.inst, 0, world, x, y, z);
            }

            return true;
        }
    }
}

