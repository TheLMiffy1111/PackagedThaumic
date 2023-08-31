package thelm.packagedthaumic.proxy;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import thelm.packagedauto.client.IModelRegister;
import thelm.packagedthaumic.client.renderer.RendererInfusionCrafter;
import thelm.packagedthaumic.client.renderer.RendererMarkedPedestal;
import thelm.packagedthaumic.client.renderer.RendererVirialRechargePedestal;
import thelm.packagedthaumic.item.ItemClathrateEssence;
import thelm.packagedthaumic.tile.TileInfusionCrafter;
import thelm.packagedthaumic.tile.TileMarkedPedestal;
import thelm.packagedthaumic.tile.TileVirialRechargePedestal;

public class ClientProxy extends CommonProxy {

	private static List<IModelRegister> modelRegisterList = new ArrayList<>();

	@Override
	public void registerBlock(Block block) {
		super.registerBlock(block);
		if(block instanceof IModelRegister) {
			modelRegisterList.add((IModelRegister)block);
		}
	}

	@Override
	public void registerItem(Item item) {
		super.registerItem(item);
		if(item instanceof IModelRegister) {
			modelRegisterList.add((IModelRegister)item);
		}
	}

	@Override
	protected void registerModels() {
		for(IModelRegister model : modelRegisterList) {
			model.registerModels();
		}
	}

	@Override
	protected void registerTileEntities() {
		super.registerTileEntities();
		ClientRegistry.bindTileEntitySpecialRenderer(TileInfusionCrafter.class, new RendererInfusionCrafter());
		ClientRegistry.bindTileEntitySpecialRenderer(TileMarkedPedestal.class, new RendererMarkedPedestal());
		ClientRegistry.bindTileEntitySpecialRenderer(TileVirialRechargePedestal.class, new RendererVirialRechargePedestal());
	}

	@Override
	protected void registerColors() {
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(ItemClathrateEssence.INSTANCE::getColor, ItemClathrateEssence.INSTANCE);
	}
}
