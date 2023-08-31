package thelm.packagedthaumic.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import thelm.packagedauto.client.RenderTimer;
import thelm.packagedthaumic.tile.TileMarkedPedestal;

public class RendererMarkedPedestal extends TileEntitySpecialRenderer<TileMarkedPedestal> {

	@Override
	public void render(TileMarkedPedestal te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		ItemStack stack = te.getInventory().getStackInSlot(0);
		if(!stack.isEmpty()) {
			EntityItem entityitem = null;
			float ticks = RenderTimer.INSTANCE.getTicks()+partialTicks;
			GlStateManager.pushMatrix();
			GlStateManager.translate(x+0.5, y+0.75, z+0.5);
			GlStateManager.scale(1.25, 1.25, 1.25);
			GlStateManager.rotate(ticks % 360, 0, 1, 0);
			ItemStack is = stack.copy();
			is.setCount(1);
			entityitem = new EntityItem(Minecraft.getMinecraft().world, 0, 0, 0, is);
			entityitem.hoverStart = 0;
			RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
			rendermanager.renderEntity(entityitem, 0, 0, 0, 0, 0, false);
			GlStateManager.popMatrix();
		}
	}
}
