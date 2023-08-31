package thelm.packagedthaumic.container;

import net.minecraft.entity.player.InventoryPlayer;
import thelm.packagedauto.container.ContainerTileBase;
import thelm.packagedauto.slot.SlotBase;
import thelm.packagedauto.slot.SlotRemoveOnly;
import thelm.packagedthaumic.slot.SlotVirialArcaneCrafterRemoveOnly;
import thelm.packagedthaumic.tile.TileVirialArcaneCrafter;

public class ContainerVirialArcaneCrafter extends ContainerTileBase<TileVirialArcaneCrafter> {

	public ContainerVirialArcaneCrafter(InventoryPlayer playerInventory, TileVirialArcaneCrafter tile) {
		super(playerInventory, tile);
		addSlotToContainer(new SlotBase(inventory, 16, 8, 81));
		for(int i = 0; i < 3; ++i) {
			for(int j = 0; j < 3; ++j) {
				addSlotToContainer(new SlotVirialArcaneCrafterRemoveOnly(tile, i*3+j, 72+j*18, 45+i*18));
			}
		}
		addSlotToContainer(new SlotVirialArcaneCrafterRemoveOnly(tile, 9, 90, 17));
		addSlotToContainer(new SlotVirialArcaneCrafterRemoveOnly(tile, 10, 50, 40));
		addSlotToContainer(new SlotVirialArcaneCrafterRemoveOnly(tile, 11, 130, 40));
		addSlotToContainer(new SlotVirialArcaneCrafterRemoveOnly(tile, 12, 50, 86));
		addSlotToContainer(new SlotVirialArcaneCrafterRemoveOnly(tile, 13, 130, 86));
		addSlotToContainer(new SlotVirialArcaneCrafterRemoveOnly(tile, 14, 90, 109));
		addSlotToContainer(new SlotRemoveOnly(inventory, 15, 190, 63));
		setupPlayerInventory();
	}

	@Override
	public int getPlayerInvX() {
		return 29;
	}

	@Override
	public int getPlayerInvY() {
		return 140;
	}
}
