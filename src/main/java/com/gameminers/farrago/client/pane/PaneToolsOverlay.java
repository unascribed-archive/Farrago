package com.gameminers.farrago.client.pane;

import gminers.glasspane.GlassPane;
import gminers.glasspane.component.button.PaneButton;
import gminers.glasspane.event.PaneOverlayEvent;
import gminers.glasspane.listener.PaneEventHandler;
import gminers.kitchensink.Rendering;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import com.gameminers.farrago.FarragoMod;

import cpw.mods.fml.relauncher.ReflectionHelper;

public class PaneToolsOverlay extends GlassPane {
	private static final boolean DEBUG = false;
	private final PaneButton tools = new PaneButton("Tools");
	private boolean dirty = false;
	private final PaneTools toolPane = new PaneTools();
	public PaneToolsOverlay() {
		add(tools);
		tools.registerActivationListener(new Runnable() {
			@Override
			public void run() {
				toolPane.show();
			}
		});
	}
	@Override
	protected void doRender(int mouseX, int mouseY, float partialTicks) {
		if (getFocusedComponent() == tools) {
			// Glass Pane focus control is only really good when the entire screen is Glass Pane
			// It looks out of place when overlaying, and trying to mesh with, vanilla screens
			setFocusedComponent(null);
		}
		super.doRender(mouseX, mouseY, partialTicks);
		if (DEBUG || dirty) {
			GuiMainMenu mainMenu = (GuiMainMenu) Minecraft.getMinecraft().currentScreen;
			if (mainMenu == null) {
				FarragoMod.log.warn("Main menu is null");
				return;
			}
			List<GuiButton> buttons = ReflectionHelper.getPrivateValue(GuiScreen.class, mainMenu, 4);
			GuiButton singleplayer = null;
			for (GuiButton button : buttons) {
				if (I18n.format("menu.singleplayer").equals(button.displayString)) {
					singleplayer = button;
					break;
				}
			}
			if (singleplayer == null) {
				FarragoMod.log.error("Could not find the Singleplayer button on main menu; what kind of over-the-top mods do you have installed!?");
				return;
			}
			singleplayer.width = 98;
			tools.setX(singleplayer.xPosition+singleplayer.width+4);
			tools.setY(singleplayer.yPosition);
			tools.setWidth(98);
			tools.setHeight(20);
			dirty = false;
		}
		if (DEBUG) {
			int x = tools.getX()-1;
			Rendering.drawRect(x, 0, x+1, height, 0xFFFF0000);
			x -= 3;
			Rendering.drawRect(x, 0, x+1, height, 0xFFFFFF00);
		}
	}
	@Override
	protected void winch(int oldWidth, int oldHeight, int newWidth, int newHeight) {
		dirty = true;
	}
	@PaneEventHandler
	public void onOverlay(PaneOverlayEvent e) {
		dirty = true;
	}
}
