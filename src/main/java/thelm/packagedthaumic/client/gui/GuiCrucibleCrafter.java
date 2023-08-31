package thelm.packagedthaumic.client.gui;

import net.minecraft.util.ResourceLocation;
import thelm.packagedauto.client.gui.GuiContainerTileBase;
import thelm.packagedthaumic.container.ContainerCrucibleCrafter;

public class GuiCrucibleCrafter extends GuiContainerTileBase<ContainerCrucibleCrafter> {

	public static final ResourceLocation BACKGROUND = new ResourceLocation("packagedthaumic:textures/gui/crucible_crafter.png");

	public GuiCrucibleCrafter(ContainerCrucibleCrafter container) {
		super(container);
		ySize = 173;
	}

	@Override
	protected ResourceLocation getBackgroundTexture() {
		return BACKGROUND;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		drawTexturedModalRect(guiLeft+75, guiTop+35, 176, 0, container.tile.getScaledProgress(22), 16);
		int scaledEnergy = container.tile.getScaledEnergy(40);
		drawTexturedModalRect(guiLeft+10, guiTop+10+40-scaledEnergy, 176, 16+40-scaledEnergy, 12, scaledEnergy);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		String s = container.inventory.getDisplayName().getUnformattedText();
		fontRenderer.drawString(s, Math.max(25, xSize/2 - fontRenderer.getStringWidth(s)/2), 6, 0xC0C0C0);
		fontRenderer.drawString(container.playerInventory.getDisplayName().getUnformattedText(), container.getPlayerInvX(), container.getPlayerInvY()-11, 0x404040);
		if(mouseX-guiLeft >= 10 && mouseY-guiTop >= 10 && mouseX-guiLeft <= 21 && mouseY-guiTop <= 49) {
			drawHoveringText(container.tile.getEnergyStorage().getEnergyStored()+" / "+container.tile.getEnergyStorage().getMaxEnergyStored()+" FE", mouseX-guiLeft, mouseY-guiTop);
		}
	}
}
