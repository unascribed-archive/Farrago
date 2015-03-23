package com.gameminers.farrago.client.tools;

import gminers.glasspane.GlassPane;
import gminers.glasspane.component.PaneImage;
import gminers.glasspane.component.PaneShadowPanel;
import gminers.glasspane.event.PaneDisplayEvent;
import gminers.glasspane.event.PaneHideEvent;
import gminers.glasspane.listener.PaneEventHandler;
import gminers.kitchensink.Rendering;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MovementInput;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.input.Mouse;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;


public class ToolBlockRenderer extends GlassPane {
	public static final class FakeSession extends Session {
		public FakeSession() {
			super("FarragoFakeWorld", new UUID(0, 0).toString().replace("-", ""), "definitely-a-session-token", "legacy");
		}
	}

	public static final class FakeNetManager extends NetworkManager {
		public FakeNetManager(boolean p_i45147_1_) {
			super(p_i45147_1_);
		}

		@Override
		public SocketAddress getSocketAddress() {
			return new InetSocketAddress("farrago.fakeworld", 47224);
		}
	}

	public static final class FakeNetHandler extends NetHandlerPlayClient {
		public FakeNetHandler(Minecraft p_i45061_1_, GuiScreen p_i45061_2_,
				NetworkManager p_i45061_3_) {
			super(p_i45061_1_, p_i45061_2_, p_i45061_3_);
		}

		@Override
		public void onDisconnect(IChatComponent p_147231_1_) {
			// haha no.
		}
	}
	private static final ResourceLocation LOGO = new ResourceLocation("farrago", "textures/misc/box.png");
	private WorldClient fakeWorld;
	private final Minecraft mc = Minecraft.getMinecraft();
	private int oldRenderDistance;
	public ToolBlockRenderer() {
		setName("Block Renderer");
		setScreenClearedBeforeDrawing(true);
		setShadowbox(null);
		final PaneShadowPanel shadow = new PaneShadowPanel();
		shadow.setAutoResizeHeight(true);
		shadow.setWidth(128);
		add(shadow);
	}
	
	@Override
	protected void doRender(int mouseX, int mouseY, float partialTicks) {
		if (fakeWorld != null) {
			mc.thePlayer.rotationPitch = mc.thePlayer.prevRotationPitch = 25;
			mc.thePlayer.rotationYaw = mc.thePlayer.prevRotationYaw = 225;
			mc.thePlayer.posX = mc.thePlayer.prevPosX = -0.5;
			mc.thePlayer.posY = mc.thePlayer.prevPosY = 65.5;
			mc.thePlayer.posZ = mc.thePlayer.prevPosZ = 1.5;
			mc.gameSettings.hideGUI = true;
			mc.entityRenderer.renderWorld(partialTicks, 0);
		}
		mc.entityRenderer.setupOverlayRendering();
		Rendering.drawCenteredString(mc.fontRenderer, mc.debug.split(",")[0], width/2, height-12, -1);
		PaneImage.render(LOGO, getWidth()-64, getHeight()-58, 0, 0, 64, 64, 256, 256, -1, 0.5f, false);
		super.doRender(mouseX, mouseY, partialTicks);
	}
	
	@Override
	protected void doTick() {
		super.doTick();
		if (orbitting) {
			System.out.println(Mouse.getDX()+", "+Mouse.getDY()+" - "+Mouse.getDWheel());
		} else {
			System.out.println(Mouse.getDWheel());
		}
	}
	
	@PaneEventHandler
	public void onDisplay(PaneDisplayEvent e) {
		oldRenderDistance = mc.gameSettings.renderDistanceChunks;
		mc.gameSettings.renderDistanceChunks = 3;
		fakeWorld = new WorldClient(new NetHandlerPlayClient(Minecraft.getMinecraft(), null, new NetworkManager(true)), new WorldSettings(new WorldInfo(new NBTTagCompound())), 0, EnumDifficulty.EASY, Minecraft.getMinecraft().mcProfiler);
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				fakeWorld.getChunkProvider().loadChunk(x, z);
			}
		}
		fakeWorld.setBlock(0, 64, 0, Blocks.lit_furnace);
		fakeWorld.getGameRules().setOrCreateGameRule("doDaylightCycle", "false");
		fakeWorld.setWorldTime(6000);
		mc.thePlayer = new EntityClientPlayerMP(mc, fakeWorld,
				new FakeSession(), new FakeNetHandler(mc, null, new FakeNetManager(true)),
				new FakeStatFileWriter());
		mc.thePlayer.sendQueue.getNetworkManager().setNetHandler(mc.thePlayer.sendQueue);
		mc.thePlayer.movementInput = new MovementInput();
		mc.playerController = new PlayerControllerMP(mc, mc.thePlayer.sendQueue);
		mc.renderViewEntity = mc.thePlayer;
		mc.theWorld = fakeWorld;
		mc.renderGlobal.setWorldAndLoadRenderers(fakeWorld);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@PaneEventHandler
	public void onHide(PaneHideEvent e) {
		mc.theWorld = null;
		mc.renderGlobal.setWorldAndLoadRenderers(null);
		mc.gameSettings.renderDistanceChunks = oldRenderDistance;
		MinecraftForge.EVENT_BUS.unregister(this);
	}
	
	@SubscribeEvent
	public void onRenderHand(RenderHandEvent e) {
		e.setCanceled(true);
	}
	
	@SubscribeEvent
	public void onFogColors(FogColors e) {
		e.red = 0.149019608f;
		e.green = 0.196078431f;
		e.blue = 0.219607843f;
	}
	
	@SubscribeEvent
	public void onFOV(FOVUpdateEvent e) {
		e.newfov = 0.818181818f;
	}
	
	private boolean orbitting = false;
	
	@Override
	protected void mouseDown(int mouseX, int mouseY, int button) {
		super.mouseDown(mouseX, mouseY, button);
		if (button == 1) {
			orbitting = true;
			Mouse.setGrabbed(true);
		}
	}
	
	@Override
	protected void mouseUp(int mouseX, int mouseY, int button) {
		super.mouseUp(mouseX, mouseY, button);
		if (button == 1) {
			orbitting = false;
			Mouse.setGrabbed(false);
		}
	}
	
}
