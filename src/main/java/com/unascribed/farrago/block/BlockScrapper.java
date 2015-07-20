package com.unascribed.farrago.block;

import java.util.Random;

import com.unascribed.farrago.FarragoMod;
import com.unascribed.farrago.tileentity.TileEntityInventoryMachine;
import com.unascribed.farrago.tileentity.TileEntityMachine;
import com.unascribed.farrago.tileentity.TileEntityScrapper;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Deprecated
public class BlockScrapper extends Block {

	private IIcon bottom;
	private IIcon top;
	private IIcon front;
	private IIcon frontOn;

	public BlockScrapper() {
		super(Material.cactus);
		setBlockName("scrapper");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		boolean on = (meta & 0x8) == 0x8;
		meta = meta & 0x7;
		return side == 1 ? this.top : (side == 0 ? this.bottom : (side != meta ? this.blockIcon : (on ? frontOn : front)));
	}

	@Override
	public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_) {
		return Item.getItemFromBlock(FarragoMod.MACHINE);
	}

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
		return Item.getItemFromBlock(FarragoMod.MACHINE);
	}

	@Override
	public int damageDropped(int p_149692_1_) {
		return 1;
	}
	
	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileEntityScrapper();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xOfs, float yOfs, float zOfs) {
		if (world.isRemote) {
			return true;
		} else {
			FarragoMod.log.info("Migrating Scrapper at "+x+", "+y+", "+z+" to new unified machine block");
			TileEntityMachine te = ((TileEntityMachine)world.getTileEntity(x, y, z));
			te.setDirection(world.getBlockMetadata(x, y, z));
			world.setBlock(x, y, z, FarragoMod.MACHINE);
			world.setBlockMetadataWithNotify(x, y, z, 1, 2);
			
			return true;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister p_149651_1_) {
		blockIcon = p_149651_1_.registerIcon("farrago:scrapper_side");
		front = p_149651_1_.registerIcon("farrago:scrapper_front");
		frontOn = p_149651_1_.registerIcon("farrago:scrapper_front_on");
		top = p_149651_1_.registerIcon("farrago:scrapper_top");
		bottom = p_149651_1_.registerIcon("farrago:scrapper_bottom");
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		TileEntityMachine te = (TileEntityMachine) world.getTileEntity(x, y, z);

		if (te != null && te instanceof TileEntityInventoryMachine) {
			TileEntityInventoryMachine inv = (TileEntityInventoryMachine) te;
			for (int i = 0; i < inv.getSizeInventory(); ++i) {
				ItemStack itemstack = inv.getStackInSlot(i);

				if (itemstack != null) {
					float f = world.rand.nextFloat() * 0.8F + 0.1F;
					float f1 = world.rand.nextFloat() * 0.8F + 0.1F;
					float f2 = world.rand.nextFloat() * 0.8F + 0.1F;

					while (itemstack.stackSize > 0) {
						int count = world.rand.nextInt(21) + 10;

						if (count > itemstack.stackSize) {
							count = itemstack.stackSize;
						}

						itemstack.stackSize -= count;
						EntityItem entityitem = new EntityItem(world,
								(double) ((float) x + f),
								(double) ((float) y + f1),
								(double) ((float) z + f2),
								new ItemStack(itemstack.getItem(), count, itemstack.getItemDamage()));

						if (itemstack.hasTagCompound()) {
							entityitem.getEntityItem().setTagCompound(
									(NBTTagCompound) itemstack.getTagCompound()
											.copy());
						}

						double spread = 0.05;
						entityitem.motionX = world.rand.nextGaussian() * spread;
						entityitem.motionY = world.rand.nextGaussian() * spread + 0.2;
						entityitem.motionZ = world.rand.nextGaussian() * spread;
						world.spawnEntityInWorld(entityitem);
					}
				}
			}

			world.func_147453_f(x, y, z, block);
		}

		super.breakBlock(world, x, y, z, block, meta);
	}
}

