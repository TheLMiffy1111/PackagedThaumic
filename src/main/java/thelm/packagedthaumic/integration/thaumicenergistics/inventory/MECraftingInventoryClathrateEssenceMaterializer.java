package thelm.packagedthaumic.integration.thaumicenergistics.inventory;

import java.util.List;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.PowerUnits;
import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.me.storage.MEInventoryHandler;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumicenergistics.api.storage.IAEEssentiaStack;
import thaumicenergistics.api.storage.IEssentiaStorageChannel;
import thelm.packagedthaumic.integration.thaumicenergistics.tile.TileClathrateEssenceMaterializer;
import thelm.packagedthaumic.item.ItemClathrateEssence;

public class MECraftingInventoryClathrateEssenceMaterializer implements IMEInventory<IAEEssentiaStack> {

	public final TileClathrateEssenceMaterializer tile;
	public final MEInventoryHandler<IAEEssentiaStack> invHandler;
	private List<IAEItemStack> itemCache = null;

	public MECraftingInventoryClathrateEssenceMaterializer(TileClathrateEssenceMaterializer tile) {
		this.tile = tile;
		invHandler = new MEInventoryHandler<>(this, AEApi.instance().storage().getStorageChannel(IEssentiaStorageChannel.class));
		invHandler.setPriority(Integer.MAX_VALUE);
	}

	@Override
	public IAEEssentiaStack injectItems(IAEEssentiaStack input, Actionable mode, IActionSource source) {
		if(!tile.hostHelper.isActive()) {
			return input;
		}
		IGrid grid = tile.getActionableNode().getGrid();
		IStorageGrid storageGrid = grid.getCache(IStorageGrid.class);
		IItemStorageChannel itemChannel = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class);
		ICraftingGrid craftingGrid = grid.getCache(ICraftingGrid.class);
		Aspect aspect = input.getAspect();
		ItemStack stack = ItemClathrateEssence.makeClathrate(aspect, 1);
		IAEItemStack insert = itemChannel.createStack(stack);
		long requesting = Math.min(craftingGrid.requesting(insert), input.getStackSize());
		if(requesting == 0) {
			return input;
		}
		IEnergyGrid energyGrid = grid.getCache(IEnergyGrid.class);
		double conversion = PowerUnits.RF.convertTo(PowerUnits.AE, 1);
		double energyFactor = TileClathrateEssenceMaterializer.energyUsage*conversion;
		double availablePower = energyGrid.extractAEPower((requesting+0.5)*energyFactor, Actionable.SIMULATE, PowerMultiplier.CONFIG);
		long canInsert = (long)(availablePower/energyFactor);
		if(canInsert == 0) {
			return input;
		}
		IMEMonitor<IAEItemStack> itemInv = storageGrid.getInventory(itemChannel);
		insert.setStackSize(canInsert);
		IAEItemStack remaining = itemInv.injectItems(insert, mode, tile.hostHelper.source);
		if(mode == Actionable.MODULATE) {
			energyGrid.extractAEPower(canInsert*energyFactor, Actionable.MODULATE, PowerMultiplier.CONFIG);
		}
		return remaining != null ? input.copy().setStackSize(remaining.getStackSize()) : null;
	}

	@Override
	public IAEEssentiaStack extractItems(IAEEssentiaStack request, Actionable mode, IActionSource source) {
		return null;
	}

	@Override
	public IItemList<IAEEssentiaStack> getAvailableItems(IItemList<IAEEssentiaStack> out) {
		return out;
	}

	@Override
	public IStorageChannel<IAEEssentiaStack> getChannel() {
		return AEApi.instance().storage().getStorageChannel(IEssentiaStorageChannel.class);
	}
}
