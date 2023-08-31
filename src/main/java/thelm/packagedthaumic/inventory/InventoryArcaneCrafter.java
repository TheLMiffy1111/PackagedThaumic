package thelm.packagedthaumic.inventory;

import java.util.stream.IntStream;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.CapabilityEnergy;
import thelm.packagedauto.inventory.InventoryTileBase;
import thelm.packagedthaumic.tile.TileArcaneCrafter;

public class InventoryArcaneCrafter extends InventoryTileBase {

	public final TileArcaneCrafter tile;

	public InventoryArcaneCrafter(TileArcaneCrafter tile) {
		super(tile, 17);
		this.tile = tile;
		slots = IntStream.rangeClosed(0, 15).toArray();
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		if(index == 16) {
			return stack.hasCapability(CapabilityEnergy.ENERGY, null);
		}
		return false;
	}

	@Override
	public int getField(int id) {
		switch(id) {
		case 0: return tile.remainingProgress;
		case 1: return tile.isWorking ? 1 : 0;
		case 2: return tile.getEnergyStorage().getEnergyStored();
		default: return 0;
		}
	}

	@Override
	public void setField(int id, int value) {
		switch(id) {
		case 0:
			tile.remainingProgress = value;
			break;
		case 1:
			tile.isWorking = value != 0;
			break;
		case 2:
			tile.getEnergyStorage().setEnergyStored(value);
			break;
		}
	}

	@Override
	public int getFieldCount() {
		return 3;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return false;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return tile.isWorking ? index == 15 : index != 16;
	}
}
