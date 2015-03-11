package com.gameminers.farrago.gen;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;

import com.gameminers.farrago.FarragoMod;

import cpw.mods.fml.common.IWorldGenerator;

public class XenotimeGenerator implements IWorldGenerator {
	private final WorldGenMinable xenotime = new WorldGenMinable(FarragoMod.ORE, 2, 24, Blocks.stone);
	private final WorldGenMinable apocite = new WorldGenMinable(FarragoMod.ORE, 1, 6, FarragoMod.ORE);
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.dimensionId == 0) {
        	Chunk c = world.getChunkFromChunkCoords(chunkX, chunkZ);
        	int x = random.nextInt(16);
        	int y = random.nextInt(64);
        	int z = random.nextInt(16);
        	if (c.getBlock(x, y, z) == Blocks.stone) {
        		xenotime.generate(world, random, (chunkX*16)+x, y, (chunkZ*16)+z);
        		apocite.generate(world, random, (chunkX*16)+x, y, (chunkZ*16)+z);
        	}
        }
	}
}