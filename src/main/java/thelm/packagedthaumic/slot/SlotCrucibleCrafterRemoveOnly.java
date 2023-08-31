package thelm.packagedthaumic.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import thelm.packagedauto.slot.SlotBase;
import thelm.packagedthaumic.tile.TileCrucibleCrafter;

//Code from CoFHCore
public class SlotCrucibleCrafterRemoveOnly extends SlotBase {

	public final TileCrucibleCrafter tile;

	public SlotCrucibleCrafterRemoveOnly(TileCrucibleCrafter tile, int index, int x, int y) {
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
