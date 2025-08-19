package thelm.packagedthaumic.client.event;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import thelm.packagedauto.client.IModelRegister;
import thelm.packagedthaumic.client.renderer.RendererInfusionCrafter;
import thelm.packagedthaumic.client.renderer.RendererMarkedPedestal;
import thelm.packagedthaumic.client.renderer.RendererVirialRechargePedestal;
import thelm.packagedthaumic.event.CommonEventHandler;
import thelm.packagedthaumic.item.ItemClathrateEssence;
import thelm.packagedthaumic.tile.TileInfusionCrafter;
import thelm.packagedthaumic.tile.TileMarkedPedestal;
import thelm.packagedthaumic.tile.TileVirialRechargePedestal;

public class ClientEventHandler extends CommonEventHandler {

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
	public void onPreInit(FMLPreInitializationEvent event) {
		super.onPreInit(event);
		registerModels();
	}

	@Override
	public void onInit(FMLInitializationEvent event) {
		super.onInit(event);
		registerColors();
	}

	@Override
	protected void registerTileEntities() {
		super.registerTileEntities();
		ClientRegistry.bindTileEntitySpecialRenderer(TileInfusionCrafter.class, new RendererInfusionCrafter());
		ClientRegistry.bindTileEntitySpecialRenderer(TileMarkedPedestal.class, new RendererMarkedPedestal());
		ClientRegistry.bindTileEntitySpecialRenderer(TileVirialRechargePedestal.class, new RendererVirialRechargePedestal());
	}

	protected void registerModels() {
		for(IModelRegister model : modelRegisterList) {
			model.registerModels();
		}
	}

	protected void registerColors() {
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(ItemClathrateEssence.INSTANCE::getColor, ItemClathrateEssence.INSTANCE);
	}
}
