package thelm.packagedthaumic.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.CapabilityEnergy;
import thelm.packagedauto.inventory.InventoryTileBase;
import thelm.packagedthaumic.tile.TileInfusionCrafter;

public class InventoryInfusionCrafter extends InventoryTileBase {

	public final TileInfusionCrafter tile;

	public InventoryInfusionCrafter(TileInfusionCrafter tile) {
		super(tile, 3);
		this.tile = tile;
		slots = new int[] {0, 1};
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		if(index == 2) {
			return stack.hasCapability(CapabilityEnergy.ENERGY, null);
		}
		return false;
	}

	@Override
	public int getField(int id) {
		switch(id) {
		case 0: return tile.energyReq;
		case 1: return tile.remainingProgress;
		case 2: return tile.isWorking ? 1 : 0;
		case 3: return tile.getEnergyStorage().getEnergyStored();
		default: return 0;
		}
	}

	@Override
	public void setField(int id, int value) {
		switch(id) {
		case 0:
			tile.energyReq = value;
			break;
		case 1:
			tile.remainingProgress = value;
			break;
		case 2:
			tile.isWorking = value != 0;
			break;
		case 3:
			tile.getEnergyStorage().setEnergyStored(value);
			break;
		}
	}

	@Override
	public int getFieldCount() {
		return 4;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return false;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return tile.isWorking ? index == 1 : index != 2;
	}
}
