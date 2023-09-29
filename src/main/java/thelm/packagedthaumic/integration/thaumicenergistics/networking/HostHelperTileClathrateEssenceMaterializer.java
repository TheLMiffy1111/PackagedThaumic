package thelm.packagedthaumic.integration.thaumicenergistics.networking;

import thelm.packagedauto.integration.appeng.networking.HostHelperTile;
import thelm.packagedthaumic.integration.thaumicenergistics.tile.TileClathrateEssenceMaterializer;

public class HostHelperTileClathrateEssenceMaterializer extends HostHelperTile<TileClathrateEssenceMaterializer> {

	public HostHelperTileClathrateEssenceMaterializer(TileClathrateEssenceMaterializer tile) {
		super(tile);
		gridBlock = new GridBlockTileClathrateEssenceMaterializer(tile);
	}
}
