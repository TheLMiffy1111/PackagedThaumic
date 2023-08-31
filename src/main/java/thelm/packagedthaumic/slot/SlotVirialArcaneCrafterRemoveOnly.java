package thelm.packagedthaumic.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import thelm.packagedauto.slot.SlotBase;
import thelm.packagedthaumic.tile.TileVirialArcaneCrafter;

//Code from CoFHCore
public class SlotVirialArcaneCrafterRemoveOnly extends SlotBase {

	public final TileVirialArcaneCrafter tile;

	public SlotVirialArcaneCrafterRemoveOnly(TileVirialArcaneCrafter tile, int index, int x, int y) {
		super(tile.getInventory(), index, x, y);
		this.tile = tile;
	}

	@Override
	public boolean canTakeStack(EntityPlayer playerIn) {
		return !tile.isWorking;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return false;
	}
}
