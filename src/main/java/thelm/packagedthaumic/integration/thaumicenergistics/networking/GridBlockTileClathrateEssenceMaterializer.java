package thelm.packagedthaumic.integration.thaumicenergistics.networking;

import appeng.api.AEApi;
import appeng.api.networking.IGrid;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.IMEMonitor;
import thaumicenergistics.api.storage.IAEEssentiaStack;
import thaumicenergistics.api.storage.IEssentiaStorageChannel;
import thelm.packagedauto.integration.appeng.networking.GridBlockTileBase;
import thelm.packagedthaumic.integration.thaumicenergistics.tile.TileClathrateEssenceMaterializer;

public class GridBlockTileClathrateEssenceMaterializer extends GridBlockTileBase<TileClathrateEssenceMaterializer> {

	public GridBlockTileClathrateEssenceMaterializer(TileClathrateEssenceMaterializer tile) {
		super(tile);
	}

	@Override
	public void gridChanged() {
		IGrid grid = tile.getActionableNode().getGrid();
		if(grid != null) {
			IStorageGrid storageGrid = grid.getCache(IStorageGrid.class);
			IEssentiaStorageChannel essentiaChannel = AEApi.instance().storage().getStorageChannel(IEssentiaStorageChannel.class);
			IMEMonitor<IAEEssentiaStack> essentiaInv = storageGrid.getInventory(essentiaChannel);
			essentiaInv.addListener(tile.meInventory, essentiaInv);
		}
	}
}
