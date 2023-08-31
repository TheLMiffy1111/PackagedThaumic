package thelm.packagedthaumic.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.CapabilityEnergy;
import thaumcraft.api.items.IRechargable;
import thelm.packagedauto.inventory.InventoryTileBase;
import thelm.packagedthaumic.tile.TileVirialRechargePedestal;

public class InventoryVirialRechargePedestal extends InventoryTileBase {

	public final TileVirialRechargePedestal tile;

	public InventoryVirialRechargePedestal(TileVirialRechargePedestal tile) {
		super(tile, 2);
		this.tile = tile;
		slots = new int[] {0};
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack ret = super.decrStackSize(index, count);
		syncTile(false);
		return ret;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack ret = super.removeStackFromSlot(index);
		syncTile(false);
		return ret;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		super.setInventorySlotContents(index, stack);
		syncTile(false);
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		if(index == 0) {
			return stack.getItem() instanceof IRechargable;
		}
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
		return index != 1;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return index != 1;
	}
}
