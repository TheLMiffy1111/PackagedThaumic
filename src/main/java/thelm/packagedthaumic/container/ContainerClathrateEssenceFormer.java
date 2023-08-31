package thelm.packagedthaumic.container;

import net.minecraft.entity.player.InventoryPlayer;
import thelm.packagedauto.container.ContainerTileBase;
import thelm.packagedauto.slot.SlotBase;
import thelm.packagedauto.slot.SlotRemoveOnly;
import thelm.packagedthaumic.tile.TileClathrateEssenceFormer;

public class ContainerClathrateEssenceFormer extends ContainerTileBase<TileClathrateEssenceFormer> {

	public ContainerClathrateEssenceFormer(InventoryPlayer playerInventory, TileClathrateEssenceFormer tile) {
		super(playerInventory, tile);
		addSlotToContainer(new SlotBase(inventory, 1, 8, 53));
		addSlotToContainer(new SlotRemoveOnly(inventory, 0, 80, 35));
		setupPlayerInventory();
	}

	@Override
	public int getPlayerInvY() {
		return 91;
	}
}
