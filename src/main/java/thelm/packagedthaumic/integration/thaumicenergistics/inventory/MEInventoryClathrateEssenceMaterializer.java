package thelm.packagedthaumic.integration.thaumicenergistics.inventory;

import java.util.ArrayList;
import java.util.List;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.PowerUnits;
import appeng.api.networking.IGrid;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IBaseMonitor;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IMEMonitorHandlerReceiver;
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

public class MEInventoryClathrateEssenceMaterializer implements IMEInventory<IAEItemStack>, IMEMonitorHandlerReceiver<IAEEssentiaStack> {

	public final TileClathrateEssenceMaterializer tile;
	public final MEInventoryHandler<IAEItemStack> invHandler;
	private List<IAEItemStack> itemCache = null;

	public MEInventoryClathrateEssenceMaterializer(TileClathrateEssenceMaterializer tile) {
		this.tile = tile;
		invHandler = new MEInventoryHandler<>(this, AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
		invHandler.setPriority(Integer.MIN_VALUE);
	}

	@Override
	public IAEItemStack injectItems(IAEItemStack input, Actionable mode, IActionSource source) {
		return input;
	}

	@Override
	public IAEItemStack extractItems(IAEItemStack request, Actionable mode, IActionSource source) {
		if(!tile.hostHelper.isActive()) {
			return null;
		}
		ItemStack definition = request.getDefinition();
		if(definition.getItem() != ItemClathrateEssence.INSTANCE) {
			return null;
		}
		Aspect aspect = ItemClathrateEssence.INSTANCE.getAspect(definition);
		if(aspect == null) {
			return null;
		}
		IGrid grid = tile.getActionableNode().getGrid();
		IStorageGrid storageGrid = grid.getCache(IStorageGrid.class);
		IEssentiaStorageChannel essentiaChannel = AEApi.instance().storage().getStorageChannel(IEssentiaStorageChannel.class);
		IMEMonitor<IAEEssentiaStack> essentiaInv = storageGrid.getInventory(essentiaChannel);
		IAEEssentiaStack essentiaRequest = essentiaChannel.createStack(aspect).setStackSize(request.getStackSize());
		IAEEssentiaStack possible = essentiaInv.extractItems(essentiaRequest.copy(), Actionable.SIMULATE, tile.hostHelper.source);
		long canRetrieve = possible != null ? possible.getStackSize() : 0;
		if(canRetrieve == 0) {
			return null;
		}
		IEnergyGrid energyGrid = grid.getCache(IEnergyGrid.class);
		double conversion = PowerUnits.RF.convertTo(PowerUnits.AE, 1);
		double energyFactor = TileClathrateEssenceMaterializer.energyUsage*conversion;
		double availablePower = energyGrid.extractAEPower((canRetrieve+0.5)*energyFactor, Actionable.SIMULATE, PowerMultiplier.CONFIG);
		long canExtract = (long)(availablePower/energyFactor);
		if(canExtract == 0) {
			return null;
		}
		possible.setStackSize(canExtract);
		IAEEssentiaStack extracted = essentiaInv.extractItems(possible, mode, tile.hostHelper.source);
		if(mode == Actionable.MODULATE) {
			energyGrid.extractAEPower(canExtract*energyFactor, Actionable.MODULATE, PowerMultiplier.CONFIG);
		}
		return extracted != null ? request.copy().setStackSize(extracted.getStackSize()) : null;
	}

	@Override
	public IItemList<IAEItemStack> getAvailableItems(IItemList<IAEItemStack> out) {
		if(itemCache == null) {
			IGrid grid = tile.getActionableNode().getGrid();
			if(grid != null) {
				itemCache = new ArrayList<>();
				IStorageGrid storageGrid = grid.getCache(IStorageGrid.class);
				IItemStorageChannel itemChannel = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class);
				IEssentiaStorageChannel essentiaChannel = AEApi.instance().storage().getStorageChannel(IEssentiaStorageChannel.class);
				IMEMonitor<IAEEssentiaStack> essentiaInv = storageGrid.getInventory(essentiaChannel);
				for(IAEEssentiaStack essentia : essentiaInv.getStorageList()) {
					Aspect aspect = essentia.getAspect();
					ItemStack stack = ItemClathrateEssence.makeClathrate(aspect, 1);
					itemCache.add(itemChannel.createStack(stack).setStackSize(essentia.getStackSize()));
				}
			}
		}
		if(itemCache != null) {
			for(IAEItemStack stack : itemCache) {
				out.addStorage(stack);
			}
		}
		return out;
	}

	@Override
	public IStorageChannel<IAEItemStack> getChannel() {
		return AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class);
	}

	@Override
	public boolean isValid(Object verificationToken) {
		IGrid grid = tile.getActionableNode().getGrid();
		if(grid != null) {
			IStorageGrid storageGrid = grid.getCache(IStorageGrid.class);
			IEssentiaStorageChannel essentiaChannel = AEApi.instance().storage().getStorageChannel(IEssentiaStorageChannel.class);
			IMEMonitor<IAEEssentiaStack> essentiaInv = storageGrid.getInventory(essentiaChannel);
			return essentiaInv == verificationToken;
		}
		return false;
	}

	@Override
	public void onListUpdate() {}

	@Override
	public void postChange(IBaseMonitor<IAEEssentiaStack> monitor, Iterable<IAEEssentiaStack> change, IActionSource source) {
		if(itemCache != null) {
			itemCache.clear();
			itemCache = null;
		}
		IGrid grid = tile.getActionableNode().getGrid();
		if(grid != null) {
			IItemStorageChannel itemChannel = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class);
			List<IAEItemStack> mappedChanges = new ArrayList<>();
			for(IAEEssentiaStack essentia : change) {
				Aspect aspect = essentia.getAspect();
				ItemStack stack = ItemClathrateEssence.makeClathrate(aspect, 1);
				IAEItemStack itemStack = itemChannel.createStack(stack).setStackSize(essentia.getStackSize());
				mappedChanges.add(itemStack);
			}
			IStorageGrid storageGrid = grid.getCache(IStorageGrid.class);
			storageGrid.postAlterationOfStoredItems(itemChannel, mappedChanges, tile.hostHelper.source);
		}
	}
}
