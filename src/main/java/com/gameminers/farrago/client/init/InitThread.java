package com.gameminers.farrago.client.init;

import gminers.glasspane.component.progress.PaneProgressRing;
import gminers.glasspane.component.text.PaneLabel;
import net.minecraft.item.Item;

import com.gameminers.farrago.Masses;
import com.gameminers.farrago.client.render.RenderKahurProjectile;
import com.gameminers.farrago.entity.EntityKahurProjectile;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class InitThread extends Thread {
	private final PaneLabel			infoLabel;
	private final PaneProgressRing	progress;

	public InitThread(final PaneLabel infoLabel, final PaneProgressRing progress) {
		super("Kahur Initialization Thread");
		setDaemon(true);
		setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(final Thread t, final Throwable e) {
				try {
					infoLabel.getGlassPane().hide();
				} catch (final Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		this.infoLabel = infoLabel;
		this.progress = progress;
	}

	@Override
	public void run() {
		progress.setMaximumProgress(GameData.getItemRegistry().getKeys().size());
		for (Object o : GameData.getItemRegistry()) {
			if (o instanceof Item) { // should always be true, but just to be sure
				addProgress(1);
				Item i = (Item) o;
				String n = Item.itemRegistry.getNameForObject(i);
				setText("Calculating item mass for "+n);
				try {
					Masses.calculateMass(i, 0, 32767);
				} catch (StackOverflowError error) {
					blink();
					continue;
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("Dazed and confused, but trying to continue");
					error("Unexpected error calculating mass for "+n+"; see console for details\nDazed and confused, but trying to continue");
					continue;
				}
			}
		}
		setText("Baking item masses");
		Masses.bake();
		RenderingRegistry.registerEntityRenderingHandler(EntityKahurProjectile.class, new RenderKahurProjectile());
		while (progress.getLagPercentage() < 0.95) {
			try {
				sleep(100);
			} catch (InterruptedException e) {
				break;
			}
		}
		infoLabel.getGlassPane().hide();
	}

	private void blink() {
		/*progress.setFilledColor(0xFFFF55);
		try {
			sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		progress.setFilledColor(0x55FF55);*/
	}

	private void error(String msg) {
		progress.setFilledColor(0xFF5555);
		setText(msg);
		try {
			sleep(2500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		progress.setFilledColor(0x55FF55);
	}

	private void addProgress(final int i) {
		progress.setProgress(progress.getProgress() + i);
	}

	private void setText(final String str) {
		infoLabel.setText(str);
	}
}