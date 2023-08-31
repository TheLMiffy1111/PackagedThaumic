package thelm.packagedthaumic.container;

import net.minecraft.entity.player.InventoryPlayer;
import thelm.packagedauto.container.ContainerTileBase;
import thelm.packagedauto.slot.SlotBase;
import thelm.packagedthaumic.tile.TileVirialRechargePedestal;

public class ContainerVirialRechargePedestal extends ContainerTileBase<TileVirialRechargePedestal> {

	public ContainerVirialRechargePedestal(InventoryPlayer playerInventory, TileVirialRechargePedestal tile) {
		super(playerInventory, tile);
		addSlotToContainer(new SlotBase(inventory, 1, 8, 53));
		addSlotToContainer(new SlotBase(inventory, 0, 80, 35));
		setupPlayerInventory();
	}
}
