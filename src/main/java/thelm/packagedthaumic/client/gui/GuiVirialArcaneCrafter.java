package thelm.packagedthaumic.client.gui;

import net.minecraft.util.ResourceLocation;
import thelm.packagedauto.client.gui.GuiContainerTileBase;
import thelm.packagedthaumic.container.ContainerVirialArcaneCrafter;

public class GuiVirialArcaneCrafter extends GuiContainerTileBase<ContainerVirialArcaneCrafter> {

	public static final ResourceLocation BACKGROUND = new ResourceLocation("packagedthaumic:textures/gui/virial_arcane_crafter.png");

	public GuiVirialArcaneCrafter(ContainerVirialArcaneCrafter container) {
		super(container);
		xSize = 218;
		ySize = 222;
	}

	@Override
	protected ResourceLocation getBackgroundTexture() {
		return BACKGROUND;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		drawTexturedModalRect(guiLeft+158, guiTop+63, 218, 0, container.tile.getScaledProgress(22), 16);
		int scaledEnergy = container.tile.getScaledEnergy(40);
		drawTexturedModalRect(guiLeft+10, guiTop+38+40-scaledEnergy, 218, 16+40-scaledEnergy, 12, scaledEnergy);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		String s = container.inventory.getDisplayName().getUnformattedText();
		fontRenderer.drawString(s, xSize/2 - fontRenderer.getStringWidth(s)/2, 6, 0x404040);
		fontRenderer.drawString(container.playerInventory.getDisplayName().getUnformattedText(), container.getPlayerInvX(), container.getPlayerInvY()-11, 0x404040);
		if(mouseX-guiLeft >= 10 && mouseY-guiTop >= 38 && mouseX-guiLeft <= 21 && mouseY-guiTop <= 77) {
			drawHoveringText(container.tile.getEnergyStorage().getEnergyStored()+" / "+container.tile.getEnergyStorage().getMaxEnergyStored()+" FE", mouseX-guiLeft, mouseY-guiTop);
		}
	}
}
