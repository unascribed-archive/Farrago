package com.gameminers.farrago.client.init;

import gminers.glasspane.GlassPane;
import gminers.glasspane.HorzAlignment;
import gminers.glasspane.component.PaneImage;
import gminers.glasspane.component.progress.PaneProgressRing;
import gminers.glasspane.component.text.PaneLabel;
import gminers.glasspane.event.PaneDisplayEvent;
import gminers.glasspane.event.WinchEvent;
import gminers.glasspane.listener.PaneEventHandler;
import gminers.glasspane.shadowbox.PaneShadowbox;
import gminers.kitchensink.RandomPool;
import gminers.kitchensink.Rendering;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class InitScreen {
	private static String[] actions;
	private static String[] subjects;
	public static void init() {
		ResourceLocation actionsLoc = new ResourceLocation("farrago", "texts/actions.txt");
		ResourceLocation subjectsLoc = new ResourceLocation("farrago", "texts/subjects.txt");
		try {
			InputStream actionsIn = Minecraft.getMinecraft().getResourceManager().getResource(actionsLoc).getInputStream();
			actions = IOUtils.readLines(actionsIn).toArray(new String[0]);
			actionsIn.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			InputStream subjectsIn = Minecraft.getMinecraft().getResourceManager().getResource(subjectsLoc).getInputStream();
			subjects = IOUtils.readLines(subjectsIn).toArray(new String[0]);
			subjectsIn.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		final PaneProgressRing ring = new PaneProgressRing();
		ring.setOutlined(true);
		ring.setX(-1);
		ring.setY(-1);
		ring.setAutoResize(true);
		ring.setRelativeWidthOffset(2);
		ring.setRelativeHeightOffset(2);

		final PaneLabel derpLabel = new PaneLabel("Initializing Einstein buffers...");
		derpLabel.setAlignmentX(HorzAlignment.MIDDLE);
		derpLabel.setColor(0xCCCCCC);
		derpLabel.setShadow(false);
		derpLabel.setOutlined(true);
		derpLabel.setAutoResizeWidth(true);
		derpLabel.setRelativeWidth(1.0);

		final PaneLabel lessDerpLabel = new PaneLabel("(Preparing to initialize)");
		lessDerpLabel.setAlignmentX(HorzAlignment.MIDDLE);
		lessDerpLabel.setColor(0xCCCCCC);
		lessDerpLabel.setShadow(false);
		lessDerpLabel.setOutlined(true);
		lessDerpLabel.setSmall(true);
		lessDerpLabel.setAutoResizeWidth(true);
		lessDerpLabel.setRelativeWidth(1.0);

		final MutableBoolean go = new MutableBoolean(true);
		final InitThread initThread = new InitThread(lessDerpLabel, ring);
		final GlassPane takeoverPane = new GlassPane() {
			{
				add(ring, derpLabel, lessDerpLabel);
				setShadowbox(new PaneShadowbox() {
					private final ResourceLocation init = new ResourceLocation("farrago", "textures/misc/init.png");
					@Override
					public void winch() {
					}
					
					@Override
					public void tick() {
					}
					
					@Override
					public void render(int arg0, int arg1, float arg2) {
						Rendering.drawRect(0, 0, getWidth(), getHeight(), 0xFFEBEBEB);
						int side = Math.min(getWidth(), getHeight());
						PaneImage.render(init, (getWidth()/2)-((int)(side*1.77)/2), 0, 0, 0, (int)(side*1.77), getHeight(), 256, 256, -1, 1.0f, true);
					}
				});
			}

			@PaneEventHandler
			public void onWinch(final WinchEvent e) {
				derpLabel.setY((e.getNewHeight() / 2) + 76);
				lessDerpLabel.setY((e.getNewHeight() / 2) + 88);
			}

			@PaneEventHandler
			public void onDisplay(final PaneDisplayEvent e) {
				initThread.start();
			}
		};
		new Thread("Derp Label Derper") {
			{
				setDaemon(true);
			}

			@Override
			public void run() {
				while (go.booleanValue()) {
					derpLabel.setText(getRandomLoadingMessage());
					for (int i = 0; i < 4; i++) {
						derpLabel.setText(derpLabel.getText() + ".");
						try {
							sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}.start();
		takeoverPane.takeover();
		go.setValue(false);
	}
	private static final Random rand = RandomPool.createRandomFacade();
	public static String getRandomLoadingMessage() {
		return actions[rand.nextInt(actions.length)] + " " + subjects[rand.nextInt(subjects.length)];
	}
}
