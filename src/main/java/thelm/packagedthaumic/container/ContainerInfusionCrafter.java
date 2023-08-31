package thelm.packagedthaumic.container;

import net.minecraft.entity.player.InventoryPlayer;
import thelm.packagedauto.container.ContainerTileBase;
import thelm.packagedauto.slot.SlotBase;
import thelm.packagedauto.slot.SlotRemoveOnly;
import thelm.packagedthaumic.slot.SlotInfusionCrafterRemoveOnly;
import thelm.packagedthaumic.tile.TileInfusionCrafter;

public class ContainerInfusionCrafter extends ContainerTileBase<TileInfusionCrafter> {

	public ContainerInfusionCrafter(InventoryPlayer playerInventory, TileInfusionCrafter tile) {
		super(playerInventory, tile);
		addSlotToContainer(new SlotBase(inventory, 2, 8, 53));
		addSlotToContainer(new SlotInfusionCrafterRemoveOnly(tile, 0, 53, 35));
		addSlotToContainer(new SlotRemoveOnly(inventory, 1, 107, 35));
		setupPlayerInventory();
	}

	@Override
	public int getPlayerInvY() {
		return 91;
	}
}
