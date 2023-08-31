package thelm.packagedthaumic.container;

import net.minecraft.entity.player.InventoryPlayer;
import thelm.packagedauto.container.ContainerTileBase;
import thelm.packagedauto.slot.SlotBase;
import thelm.packagedauto.slot.SlotRemoveOnly;
import thelm.packagedthaumic.slot.SlotCrucibleCrafterRemoveOnly;
import thelm.packagedthaumic.tile.TileCrucibleCrafter;

public class ContainerCrucibleCrafter extends ContainerTileBase<TileCrucibleCrafter> {

	public ContainerCrucibleCrafter(InventoryPlayer playerInventory, TileCrucibleCrafter tile) {
		super(playerInventory, tile);
		addSlotToContainer(new SlotBase(inventory, 2, 8, 53));
		addSlotToContainer(new SlotCrucibleCrafterRemoveOnly(tile, 0, 53, 35));
		addSlotToContainer(new SlotRemoveOnly(inventory, 1, 107, 35));
		setupPlayerInventory();
	}

	@Override
	public int getPlayerInvY() {
		return 91;
	}
}
