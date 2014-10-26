package com.gameminers.farrago.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.gameminers.farrago.container.ContainerScrapper;
import com.gameminers.farrago.tileentity.TileEntityScrapper;

public class GuiScrapperInventory extends GuiContainer {

	private static final ResourceLocation combustorGuiTextures = new ResourceLocation("farrago", "textures/gui/container/scrapper.png");
    private TileEntityScrapper tileScrapper;

    public GuiScrapperInventory(InventoryPlayer p_i1091_1_, TileEntityScrapper p_i1091_2_) {
        super(new ContainerScrapper(p_i1091_1_, p_i1091_2_));
        this.tileScrapper = p_i1091_2_;
    }

    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
        String s = this.tileScrapper.hasCustomInventoryName() ? this.tileScrapper.getInventoryName() : I18n.format(this.tileScrapper.getInventoryName(), new Object[0]);
        this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
    }

    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(combustorGuiTextures);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

        if (this.tileScrapper.isBurning()) {
            int i1 = this.tileScrapper.getBurnTimeRemainingScaled(13);
            this.drawTexturedModalRect(k + 45, l + 35 + 12 - i1, 176, 12 - i1, 14, i1 + 1);
        }
        int i1 = this.tileScrapper.getCookProgressScaled(24);
        this.drawTexturedModalRect(k + 67, l + 17, 176, 14, i1 + 1, 16);
        
    }

}
