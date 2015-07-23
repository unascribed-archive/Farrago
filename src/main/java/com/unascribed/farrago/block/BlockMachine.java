package com.unascribed.farrago.block;

import java.util.List;
import java.util.Random;

import com.unascribed.farrago.FarragoMod;
import com.unascribed.farrago.tileentity.TileEntityCellFiller;
import com.unascribed.farrago.tileentity.TileEntityCombustor;
import com.unascribed.farrago.tileentity.TileEntityInventoryMachine;
import com.unascribed.farrago.tileentity.TileEntityMachine;
import com.unascribed.farrago.tileentity.TileEntityRadio;
import com.unascribed.farrago.tileentity.TileEntityScrapper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class BlockMachine extends BlockContainer implements NameDelegate {
	public class MachineIcons {
		public MachineIcons(IIcon top, IIcon bottom, IIcon side, IIcon front, IIcon frontOn) {
			this.top = top;
			this.bottom = bottom;
			this.side = side;
			this.front = front;
			this.frontOn = frontOn;
		}
		public MachineIcons(IIcon only) {
			front = only;
			top = bottom = side = frontOn = null;
		}
		public final IIcon top;
		public final IIcon bottom;
		public final IIcon side;
		public final IIcon front;
		public final IIcon frontOn;
	}

	public static boolean inventory;

	public String[] machineTypes = {
			"combustor",
			"scrapper",
			"cell_filler",
			"radio"
	};
	public Class[] tileEntities = {
			TileEntityCombustor.class,
			TileEntityScrapper.class,
			TileEntityCellFiller.class,
			TileEntityRadio.class
	};
	public MachineIcons[] icons = new MachineIcons[machineTypes.length];

	public BlockMachine() {
		super(Material.iron);
		setHarvestLevel("pickaxe", 1);
		setCreativeTab(FarragoMod.creativeTab);
		setHardness(5.0f);
		setStepSound(soundTypeMetal);
		setResistance(10.0f);
	}

    @Override
	public IIcon getIcon(int side, int meta) {
		return getIcon(null, meta, 0, 0, side);
    }
    
    @Override
    public IIcon getIcon(IBlockAccess world, int x,
    		int y, int z, int side) {
    	int facing;
    	int meta;
    	boolean on;
    	if (world == null) {
    		facing = 3;
    		meta = x;
    		on = false;
    	} else {
    		TileEntityMachine te = ((TileEntityMachine) world.getTileEntity(x, y, z));
    		facing = te.getDirection();
    		on = te.isOn();
    		meta = world.getBlockMetadata(x, y, z);
    	}
    	if (meta >= machineTypes.length) meta = 0;
        return side == 1 ? icons[meta].top : (side == 0 ? icons[meta].bottom : (side != facing ? icons[meta].side : on ? icons[meta].frontOn : icons[meta].front));
    }

    @Override
    public int getRenderType() {
    	return inventory ? 0 : FarragoMod.machineRenderType;
    }
    
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		if (meta >= machineTypes.length) meta = 0;
		try {
			return (TileEntity) tileEntities[meta].newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public Item getItem(World world, int x, int y, int z) {
		return Item.getItemFromBlock(this);
	}
	
	@Override
	public Item getItemDropped(int meta, Random rand, int fortune) {
		return Item.getItemFromBlock(this);
	}
	
	@Override
	public int damageDropped(int meta) {
		return meta;
	}
	
    @Override
	@SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister registry) {
    	for (int i = 0; i < machineTypes.length; i++) {
    		String s = machineTypes[i];
    		if (s.equals("radio")) {
    			icons[i] = new MachineIcons(registry.registerIcon("farrago:radio"));
    		} else {
	    		icons[i] = new MachineIcons(
	    				registry.registerIcon("farrago:"+s+"_top"),
	    				registry.registerIcon("farrago:"+s+"_bottom"),
	    				registry.registerIcon("farrago:"+s+"_side"),
	    				registry.registerIcon("farrago:"+s+"_front"),
	    				registry.registerIcon("farrago:"+s+"_front_on")
	    				);
    		}
    	}
    }
    
    @Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xOfs, float yOfs, float zOfs) {
        if (world.isRemote) {
            return true;
        } else {
            TileEntityMachine te = (TileEntityMachine)world.getTileEntity(x, y, z);
            
            if (te != null) {
                player.openGui(FarragoMod.inst, world.getBlockMetadata(x, y, z), world, x, y, z);
            } else {
            	player.addChatMessage(new ChatComponentText("This machine's tile entity is missing. You are advised to break and replace it."));
            }
            return true;
        }
    }

	@Override
	public String getUnlocalizedName(int meta) {
		if (meta >= machineTypes.length) meta = 0;
		return "tile.machine_"+machineTypes[meta];
	}
	
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < machineTypes.length; i++) {
    		list.add(new ItemStack(item, 1, i));
    	}
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

	@Override
	public boolean hasComparatorInputOverride() {
		return true;
	}

	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
		return Container.calcRedstoneFromInventory((IInventory) world.getTileEntity(x, y, z));
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
		int l = MathHelper.floor_double((double) (placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

		if (l == 0) {
			((TileEntityMachine) world.getTileEntity(x, y, z)).setDirection(2);
		}

		if (l == 1) {
			((TileEntityMachine) world.getTileEntity(x, y, z)).setDirection(5);
		}

		if (l == 2) {
			((TileEntityMachine) world.getTileEntity(x, y, z)).setDirection(3);
		}

		if (l == 3) {
			((TileEntityMachine) world.getTileEntity(x, y, z)).setDirection(4);
		}

		if (stack.hasDisplayName() && world.getTileEntity(x, y, z) instanceof TileEntityInventoryMachine) {
			((TileEntityInventoryMachine) world.getTileEntity(x, y, z)).setName(stack.getDisplayName());
		}
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);
		if (!world.isRemote) {
			Block block = world.getBlock(x, y, z - 1);
			Block block1 = world.getBlock(x, y, z + 1);
			Block block2 = world.getBlock(x - 1, y, z);
			Block block3 = world.getBlock(x + 1, y, z);
			byte facing = 3;

			if (block.func_149730_j() && !block1.func_149730_j()) {
				facing = 3;
			}

			if (block1.func_149730_j() && !block.func_149730_j()) {
				facing = 2;
			}

			if (block2.func_149730_j() && !block3.func_149730_j()) {
				facing = 5;
			}

			if (block3.func_149730_j() && !block2.func_149730_j()) {
				facing = 4;
			}

			((TileEntityMachine) world.getTileEntity(x, y, z)).setDirection(facing);
		}
	}
}
