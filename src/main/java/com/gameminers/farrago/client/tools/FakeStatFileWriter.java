package com.gameminers.farrago.client.tools;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.IJsonSerializable;
import net.minecraft.util.JsonSerializableSet;

public class FakeStatFileWriter extends StatFileWriter {
	@Override
	public boolean canUnlockAchievement(Achievement p_77442_1_) {
		return false;
	}

	@Override
	public IJsonSerializable func_150870_b(StatBase p_150870_1_) {
		return new JsonSerializableSet();
	}

	@Override public void func_150871_b(EntityPlayer p_150871_1_, StatBase p_150871_2_, int p_150871_3_) {}

	@Override
	public IJsonSerializable func_150872_a(StatBase p_150872_1_, IJsonSerializable p_150872_2_) {
		return new JsonSerializableSet();
	}

	@Override public void func_150873_a(EntityPlayer p_150873_1_, StatBase p_150873_2_, int p_150873_3_) {}

	@Override
	public int func_150874_c(Achievement p_150874_1_) {
		return 0;
	}

	@Override
	public boolean hasAchievementUnlocked(Achievement p_77443_1_) {
		return false;
	}

	@Override
	public int writeStat(StatBase p_77444_1_) {
		return 0;
	}
}
