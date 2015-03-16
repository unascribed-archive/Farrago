package com.gameminers.farrago.selector;

import net.minecraft.item.ItemStack;

public class CompoundSelector implements Selector {
	public static enum Mode {
		AND {
			@Override
			public boolean result(boolean a, boolean b) {
				return a && b;
			}
			@Override
			public String getRepresentation() {
				return "&&";
			}
		},
		OR {
			@Override
			public boolean result(boolean a, boolean b) {
				return a || b;
			}
			@Override
			public String getRepresentation() {
				return "||";
			}
		};
		public abstract boolean result(boolean a, boolean b);
		public abstract String getRepresentation();
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
	
	@Override
	public String toString() {
		return a.toString() + " " + mode.getRepresentation() + " " + b.toString();
	}

}
