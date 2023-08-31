package thelm.packagedthaumic.client.renderer;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.client.renderers.models.ModelCube;
import thelm.packagedauto.client.RenderTimer;
import thelm.packagedthaumic.tile.TileInfusionCrafter;

public class RendererInfusionCrafter extends TileEntitySpecialRenderer<TileInfusionCrafter> {

	private static final ModelCube CUBE = new ModelCube(0);
	private static final ModelCube CUBE_OVER = new ModelCube(32);
	private static final ResourceLocation ARCANE = new ResourceLocation("thaumcraft:textures/blocks/infuser_normal.png");
	private static final ResourceLocation ANCIENT = new ResourceLocation("thaumcraft:textures/blocks/infuser_ancient.png");
	private static final ResourceLocation ELDRITCH = new ResourceLocation("thaumcraft:textures/blocks/infuser_eldritch.png");

	@Override
	public void render(TileInfusionCrafter te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		double stability = te.stability;
		int craftCount = te.craftCount;
		boolean structureValid = te.structureValid;
		boolean isWorking = te.isWorking;
		IInventory inv = te.getInventory();
		ItemStack stack = !inv.getStackInSlot(1).isEmpty() ? inv.getStackInSlot(1) : inv.getStackInSlot(0);
		float ticks = RenderTimer.INSTANCE.getTicks()+partialTicks;
		if(!stack.isEmpty()) {
			EntityItem entityitem = null;
			GlStateManager.pushMatrix();
			GlStateManager.translate(x+0.5, y-1.25, z+0.5);
			GlStateManager.scale(1.25, 1.25, 1.25);
			GlStateManager.rotate(ticks % 360, 0, 1, 0);
			ItemStack is = stack.copy();
			is.setCount(1);
			entityitem = new EntityItem(te.getWorld(), 0, 0, 0, is);
			entityitem.hoverStart = 0;
			RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
			rendermanager.renderEntity(entityitem, 0, 0, 0, 0, 0, false);
			GlStateManager.popMatrix();
		}
		GlStateManager.pushMatrix();
		ResourceLocation tex = ARCANE;
		IBlockState bs = te.getWorld().getBlockState(te.getPos().add(-1, -2, -1));
		if(bs.getBlock() == BlocksTC.pillarAncient) {
			tex = ANCIENT;
		}
		if(bs.getBlock() == BlocksTC.pillarEldritch) {
			tex = ELDRITCH;
		}
		bindTexture(tex);
		GlStateManager.translate(x+0.5, y+0.5, z+0.5);
		GlStateManager.rotate(ticks % 360, 0, 1, 0);
		GlStateManager.rotate(54.7356F, 1, 0, 1);
		double instability = Math.min(6, 1+(stability < 0 ? -stability*0.66 : 1)*(Math.min(craftCount, 50)/50F));
		double a1 = 0;
		double b1 = 0;
		double c1 = 0;
		int aa = 0;
		int bb = 0;
		int cc = 0;
		for(int a = 0; a < 2; ++a) {
			for(int b = 0; b < 2; ++b) {
				for(int c = 0; c < 2; ++c) {
					if(structureValid) {
						a1 = MathHelper.sin((ticks+a*10)/15)*0.006*instability;
						b1 = MathHelper.sin((ticks+b*10)/14)*0.006*instability;
						c1 = MathHelper.sin((ticks+c*10)/13)*0.006*instability;
					}
					aa = a == 0 ? -1 : 1;
					bb = b == 0 ? -1 : 1;
					cc = c == 0 ? -1 : 1;
					GlStateManager.pushMatrix();
					GlStateManager.translate(a1+aa*0.15, b1+bb*0.15, c1+cc*0.15);
					if(a > 0) {
						GlStateManager.rotate(90, a, 0, 0);
					}
					if(b > 0) {
						GlStateManager.rotate(90, 0, b, 0);
					}
					if(c > 0) {
						GlStateManager.rotate(90, 0, 0, c);
					}
					GlStateManager.scale(0.27, 0.27, 0.27);
					CUBE.render();
					GlStateManager.popMatrix();
				}
			}
		}
		if(structureValid) {
			GlStateManager.pushMatrix();
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.00392157F);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			for(int a = 0; a < 2; ++a) {
				for(int b = 0; b < 2; ++b) {
					for(int c = 0; c < 2; ++c) {
						a1 = MathHelper.sin((ticks+a*10)/15)*0.006*instability;
						b1 = MathHelper.sin((ticks+b*10)/14)*0.006*instability;
						c1 = MathHelper.sin((ticks+c*10)/13)*0.006*instability;
						aa = a == 0 ? -1 : 1;
						bb = b == 0 ? -1 : 1;
						cc = c == 0 ? -1 : 1;
						GlStateManager.pushMatrix();
						GlStateManager.translate(a1+aa*0.15, b1+bb*0.15, c1+cc*0.15);
						if(a > 0) {
							GlStateManager.rotate(90, a, 0, 0);
						}
						if(b > 0) {
							GlStateManager.rotate(90, 0, b, 0);
						}
						if(c > 0) {
							GlStateManager.rotate(90, 0, 0, c);
						}
						GlStateManager.scale(0.27, 0.27, 0.27);
						OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
						GlStateManager.color(0.8F, 0.1F, 1F, MathHelper.sin((ticks+a*2+b*3+c*4)/4)*0.1F+0.2F);
						CUBE_OVER.render();
						GlStateManager.color(1, 1, 1, 1);
						GlStateManager.popMatrix();
					}
				}
			}
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GlStateManager.disableBlend();
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
			GlStateManager.popMatrix();
		}
		if(isWorking) {
			GlStateManager.pushMatrix();
			int q = !Minecraft.getMinecraft().gameSettings.fancyGraphics ? 10 : 20;
			Tessellator tessellator = Tessellator.getInstance();
			float f1 = craftCount/500F;
			Random random = new Random(245L);
			RenderHelper.disableStandardItemLighting();
			GlStateManager.disableTexture2D();
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			GlStateManager.disableAlpha();
			GlStateManager.enableCull();
			GlStateManager.depthMask(false);
			GlStateManager.pushMatrix();
			for(int i = 0; i < q; ++i) {
				GlStateManager.rotate(random.nextFloat()*360, 1, 0, 0);
				GlStateManager.rotate(random.nextFloat()*360, 0, 1, 0);
				GlStateManager.rotate(random.nextFloat()*360, 0, 0, 1);
				GlStateManager.rotate(random.nextFloat()*360, 1, 0, 0);
				GlStateManager.rotate(random.nextFloat()*360, 0, 1, 0);
				GlStateManager.rotate(random.nextFloat()*360+f1*360, 0, 0, 1);
				tessellator.getBuffer().begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
				float f2 = random.nextFloat()*20+5;
				float f3 = random.nextFloat()*2+1;
				f2 /= 20/(Math.min(craftCount, 50)/50F);
				f3 /= 20/(Math.min(craftCount, 50)/50F);
				tessellator.getBuffer().pos(0, 0, 0).color(255, 255, 255, (int)(255*(1-f1))).endVertex();
				tessellator.getBuffer().pos(-0.866*f3, f2, -0.5*f3).color(255, 0, 255, 0).endVertex();
				tessellator.getBuffer().pos(0.866*f3, f2, -0.5*f3).color(255, 0, 255, 0).endVertex();
				tessellator.getBuffer().pos(0, f2, f3).color(255, 0, 255, 0).endVertex();
				tessellator.getBuffer().pos(-0.866*f3, f2, -0.5*f3).color(255, 0, 255, 0).endVertex();
				tessellator.draw();
			}
			GlStateManager.popMatrix();
			GlStateManager.depthMask(true);
			GlStateManager.disableCull();
			GlStateManager.enableAlpha();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GlStateManager.disableBlend();
			GlStateManager.shadeModel(GL11.GL_FLAT);
			GlStateManager.enableTexture2D();
			RenderHelper.enableStandardItemLighting();
			GlStateManager.color(1, 1, 1, 1);
			GlStateManager.popMatrix();
		}
		GlStateManager.popMatrix();
	}
}
