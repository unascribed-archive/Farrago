package com.unascribed.farrago.block;

import java.util.BitSet;
import java.util.List;
import java.util.Random;

import com.unascribed.farrago.FarragoMod;
import com.unascribed.farrago.tileentity.TileEntityTicker;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class BlockLightPipe extends Block {
	private static final boolean DEBUG = false;
	public static boolean rot = false;
	public static boolean rot2 = false;
	public static boolean inventory = false;
	private IIcon sideI;
	private IIcon poleI;
	private IIcon junctionI;
	private IIcon connectI;
	private BitSet sides = new BitSet(6);
	private BitSet connectSides = new BitSet(6);
	public BlockLightPipe() {
		super(Material.iron);setHarvestLevel("pickaxe", 1);
		setBlockName("light_pipe");
		setBlockTextureName("farrago:light_pipe");
		setCreativeTab(FarragoMod.creativeTab);
		setHardness(5.0f);
		setStepSound(soundTypeMetal);
		setResistance(10.0f);
		setBlockBounds(0f, 0f, 0f, 1f, 0.25f, 1f);
	}
	
	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileEntityTicker();
	}
	
	@Override
	public int tickRate(World world) {
		return 5;
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		if (!world.isRemote) {
			int meta = world.getBlockMetadata(x, y, z);
			int type = ((int) Math.ceil(meta/3f))-1;
			int time = (int) (world.getWorldTime() % 24000);
			boolean fizzle = false;
			switch (type) {
				case 1:
					fizzle = !(time > 0 && time < 12000);
					break;
				case 2:
					fizzle = !(time > 12000);
					break;
				case 3:
					fizzle = !(time > 14000);
					break;
			}
			if (fizzle) {
				world.setBlockMetadataWithNotify(x, y, z, 0, 3);
				world.playSoundEffect(x+0.5, y+0.5, z+0.5, "random.fizz", 0.1f, 0.5f);
				if (world instanceof WorldServer) {
					WorldServer ws = (WorldServer) world;
					ws.func_147487_a("smoke", x+0.5, y+0.5, z+0.5, 20, 0.2, 0.2, 0.2, 0);
				}
			} else {
				int base = type*3;
				int count = (meta == 0 ? 0 : meta-base);
				if (count == 0) return;
				debug("== "+x+", "+y+", "+z+" ==");
				int minSaw = -1;
				EnumFacing minDir = null;
				for (EnumFacing facing : EnumFacing.values()) {
					int xO = facing.getFrontOffsetX();
					int yO = facing.getFrontOffsetY();
					int zO = facing.getFrontOffsetZ();
					if (world.getBlock(x+xO, y+yO, z+zO) == this) {
						int tMeta = world.getBlockMetadata(x+xO, y+yO, z+zO);
						int tType = ((int) Math.ceil(tMeta/3f))-1;
						if (tMeta == 0 || tType == type) {
							debug(facing+" matches type");
							int tBase = tType*3;
							int tCount = (tMeta == 0 ? 0 : tMeta-tBase);
							if (tCount < count && (minSaw == -1 || tCount < minSaw)) {
								minSaw = tCount;
								minDir = facing;
							}
						} else {
							debug(facing+" does not match type");
						}
					}
				}
				if (minDir == null) {
					debug("nowhere to send to");
					return;
				}
				debug("sending "+minDir);
				int xO = minDir.getFrontOffsetX();
				int yO = minDir.getFrontOffsetY();
				int zO = minDir.getFrontOffsetZ();
				int mMeta = world.getBlockMetadata(x+xO, y+yO, z+zO);
				int mType, mBase, mCount;
				if (mMeta != 0) {
					mType = ((int) Math.ceil(mMeta/3f))-1;
					mBase = mType*3;
					mCount = (mMeta == 0 ? 0 : mMeta-mBase);
				} else {
					mType = type;
					mBase = base;
					mCount = 0;
				}
				debug("count: "+count);
				debug("mCount: "+mCount);
				debug("base: "+base);
				debug("mBase: "+mBase);
				if (mCount < 3) {
					count--;
					mCount++;
				}
				debug("post count: "+count);
				debug("post mCount: "+mCount);
				if (count == 0) {
					world.setBlockMetadataWithNotify(x, y, z, 0, 3);
				} else {
					world.setBlockMetadataWithNotify(x, y, z, base+count, 3);
				}
				if (mCount == 0) {
					world.setBlockMetadataWithNotify(x+xO, y+yO, z+zO, 0, 3);
				} else {
					world.setBlockMetadataWithNotify(x+xO, y+yO, z+zO, mBase+mCount, 3);
				}
			}
		}
	}
	
	private void debug(String msg) {
		if (DEBUG) {
			FarragoMod.log.info(msg);
		}
	}
	
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public int getRenderType() {
		return inventory ? 0 : FarragoMod.lightPipeRenderType;
	}
	
	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		int type = ((int) Math.ceil(meta/3f))-1;;
		int base = type*3;
		int count = (meta == 0 ? 0 : meta-base);
		return count*3;
	}
	
	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		if (rot) {
			return side == 0 || side == 1 ? sideI : rot2 ? sideI : poleI;
		} else {
			return side == 0 || side == 1 ? rot2 ? sideI : poleI : sideI;
		}
	}
	
	@Override
	public IIcon getIcon(int meta, int side) {
		return sideI;
	}
	
	@Override
	public void registerBlockIcons(IIconRegister registry) {
		sideI = registry.registerIcon("farrago:light_pipe");
		poleI = registry.registerIcon("farrago:light_pipe_r90");
		junctionI = registry.registerIcon("farrago:light_pipe_junction");
		connectI = registry.registerIcon("farrago:light_pipe_connect");
	}
	
	public IIcon getJunctionIcon() {
		return junctionI;
	}
	
	public IIcon getConnectIcon() {
		return connectI;
	}
	
	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
		return super.shouldSideBeRendered(world, x, y, z, side);
	}

	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB aabb, List list, Entity collider) {
		boolean junction = discover(world, x, y, z, sides, connectSides);
		if (junction) {
			setBlockBounds(0.3125f, 0.3125f, 0.3125f, 0.6875f, 0.6875f, 0.6875f);
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, collider);
		}
		if (sides.get(0)) {
			setBlockBounds(0.375f, 0f, 0.375f, 0.625f, 0.625f, 0.625f);
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, collider);
		}
		if (sides.get(1)) {
			setBlockBounds(0.375f, 0.375f, 0.375f, 0.625f, 1.0f, 0.625f);
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, collider);
		}
		if (sides.get(2)) {
			setBlockBounds(0.375f, 0.375f, 0f, 0.625f, 0.625f, 0.625f);
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, collider);
		}
		if (sides.get(3)) {
			setBlockBounds(0.375f, 0.375f, 0.375f, 0.625f, 0.625f, 1.0f);
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, collider);
		}
		if (sides.get(4)) {
			setBlockBounds(0f, 0.375f, 0.375f, 0.625f, 0.625f, 0.625f);
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, collider);
		}
		if (sides.get(5)) {
			setBlockBounds(0.375f, 0.375f, 0.375f, 1.0f, 0.625f, 0.625f);
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, collider);
		}
		if (connectSides.get(1)) {
			setBlockBounds(0.1875f, 0.8125f, 0.1875f, 0.8125f, 1.0f, 0.8125f);
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, collider);
		}
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		boolean junction = discover(world, x, y, z, sides, null);
		float x1, y1, z1, x2, y2, z2;
		if (junction) {
			x1 = y1 = z1 = 0.3125f;
			x2 = y2 = z2 = 0.6875f;
		} else {
			x1 = y1 = z1 = 0.375f;
			x2 = y2 = z2 = 0.625f;
		}
		if (sides.get(0)) {
			y1 = 0;
		}
		if (sides.get(1)) {
			y2 = 1;
		}
		if (sides.get(2)) {
			z1 = 0;
		}
		if (sides.get(3)) {
			z2 = 1;
		}
		if (sides.get(4)) {
			x1 = 0;
		}
		if (sides.get(5)) {
			x2 = 1;
		}
		setBlockBounds(x1, y1, z1, x2, y2, z2);
	}
	
	@Override
	public void setBlockBoundsForItemRender() {
		setBlockBounds(0.3125f, 0.3125f, 0.3125f, 0.6875f, 0.6875f, 0.6875f);
	}
	
	public static boolean discover(IBlockAccess world, int x, int y, int z, BitSet sides, BitSet connectSides) {
		for (EnumFacing facing : EnumFacing.values()) {
			sides.set(facing.ordinal(), world.getBlock(x+facing.getFrontOffsetX(), y+facing.getFrontOffsetY(), z+facing.getFrontOffsetZ()) == FarragoMod.LIGHT_PIPE);
		}
		if (connectSides != null) {
			connectSides.set(1, world.getBlock(x, y+1, z) == FarragoMod.COLLECTOR);
			sides.or(connectSides);
		} else {
			if (world.getBlock(x, y+1, z) == FarragoMod.COLLECTOR) {
				sides.set(1);
			}
		}
		boolean junction = sides.isEmpty() || sides.cardinality() > 1;
		if (sides.cardinality() == 2) {
			if (sides.get(0) && sides.get(1)) {
				junction = false;
			} else if (sides.get(2) && sides.get(3)) {
				junction = false;
			} else if (sides.get(4) && sides.get(5)) {
				junction = false;
			}
		}
		return junction;
	}

}

