package com.gameminers.farrago.selector;

import net.minecraft.item.ItemStack;

public class CompoundSelector implements Selector {
	public static enum Mode {
		AND {
			@Override
			public boolean result(boolean a, boolean b) {
				return a && b;
			}
		},
		OR {
			@Override
			public boolean result(boolean a, boolean b) {
				return a || b;
			}
		};
		public abstract boolean result(boolean a, boolean b);
	}
	private final Selector a;
	private final Selector b;
	private final Mode mode;
	public CompoundSelector(Selector a, Selector b, Mode mode) {
		this.a = a;
		this.b = b;
		this.mode = mode;
	}

	@Override
	public Object getRepresentation() {
		return a.getRepresentation();
	}

	@Override
	public ItemStack getItemStackRepresentation() {
		return a.getItemStackRepresentation();
	}

	@Override
	public boolean itemStackMatches(ItemStack stack) {
		return mode.result(a.itemStackMatches(stack), b.itemStackMatches(stack));
	}

}
