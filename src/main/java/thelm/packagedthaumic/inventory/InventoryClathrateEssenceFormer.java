package thelm.packagedthaumic.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.CapabilityEnergy;
import thelm.packagedauto.inventory.InventoryTileBase;
import thelm.packagedthaumic.tile.TileClathrateEssenceFormer;

public class InventoryClathrateEssenceFormer extends InventoryTileBase {

	public final TileClathrateEssenceFormer tile;

	public InventoryClathrateEssenceFormer(TileClathrateEssenceFormer tile) {
		super(tile, 2);
		this.tile = tile;
		slots = new int[] {0};
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		if(index == 1) {
			return stack.hasCapability(CapabilityEnergy.ENERGY, null);
		}
		return false;
	}

	@Override
	public int getField(int id) {
		switch(id) {
		case 0: return tile.getEnergyStorage().getEnergyStored();
		default: return 0;
		}
	}

	@Override
	public void setField(int id, int value) {
		switch(id) {
		case 0:
			tile.getEnergyStorage().setEnergyStored(value);
			break;
		}
	}

	@Override
	public int getFieldCount() {
		return 1;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return false;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return index != 1;
	}
}
