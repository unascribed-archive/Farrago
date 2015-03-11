package com.gameminers.farrago.gen;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

import com.gameminers.farrago.FarragoMod;

import cpw.mods.fml.common.IWorldGenerator;

public class YttriumGenerator implements IWorldGenerator {
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.dimensionId == 0) {
        	System.out.println("Generating Yttrium");
        	Chunk c = world.getChunkFromChunkCoords(chunkX, chunkZ);
        	for (int i = 0; i < 45; i++) {
            	int x = random.nextInt(16);
            	int y = i > 30 ? random.nextInt(64) : random.nextInt(16);
            	int z = random.nextInt(16);
            	if (c.getBlock(x, y, z) == Blocks.stone) {
            		c.func_150807_a(x, y, z, FarragoMod.ORE, 0);
            	}
            }
        }
	}
}