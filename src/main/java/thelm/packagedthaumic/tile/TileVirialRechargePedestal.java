package thelm.packagedthaumic.tile;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;
import thelm.packagedauto.energy.EnergyStorage;
import thelm.packagedauto.tile.TileBase;
import thelm.packagedthaumic.client.gui.GuiVirialRechargePedestal;
import thelm.packagedthaumic.container.ContainerVirialRechargePedestal;
import thelm.packagedthaumic.inventory.InventoryVirialRechargePedestal;

public class TileVirialRechargePedestal extends TileBase implements ITickable, IAspectContainer {

	public static int energyCapacity = 50000;
	public static int energyPerVis = 1250;
	public static int tickInterval = 10;
	public static double fluxLeakageChance = 0.002;

	public TileVirialRechargePedestal() {
		setInventory(new InventoryVirialRechargePedestal(this));
		setEnergyStorage(new EnergyStorage(this, energyCapacity));
	}

	@Override
	protected String getLocalizedName() {
		return I18n.translateToLocal("tile.packagedthaumic.virial_recharge_pedestal.name");
	}

	@Override
	public void update() {
		if(!world.isRemote) {
			if(world.getTotalWorldTime() % tickInterval == 0) {
				tickProcess();
			}
			chargeEnergy();
		}
	}

	protected void tickProcess() {
		int maxCharge = Math.min(5, energyStorage.getEnergyStored()/energyPerVis);
		if(maxCharge > 0) {
			int charged = (int)RechargeHelper.rechargeItemBlindly(inventory.getStackInSlot(0), null, maxCharge);
			if(charged > 0) {
				energyStorage.extractEnergy(charged*energyPerVis, false);
				if(world.rand.nextDouble() < fluxLeakageChance*charged) {
					AuraHelper.polluteAura(world, pos, 1, true);
				}
				markDirty();
				syncTile(false);
			}
		}
	}

	protected void chargeEnergy() {
		int prevStored = energyStorage.getEnergyStored();
		ItemStack energyStack = inventory.getStackInSlot(1);
		if(energyStack.hasCapability(CapabilityEnergy.ENERGY, null)) {
			int energyRequest = Math.min(energyStorage.getMaxReceive(), energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored());
			energyStorage.receiveEnergy(energyStack.getCapability(CapabilityEnergy.ENERGY, null).extractEnergy(energyRequest, false), false);
			if(energyStack.getCount() <= 0) {
				inventory.setInventorySlotContents(1, ItemStack.EMPTY);
			}
		}
	}

	@Override
	public int getComparatorSignal() {
		ItemStack stack = inventory.getStackInSlot(0);
		if(stack.getItem() instanceof IRechargable) {
			return 1+(int)Math.floor(15*RechargeHelper.getChargePercentage(stack, null));
		}
		return 0;
	}

	@Override
	public AspectList getAspects() {
		ItemStack stack = inventory.getStackInSlot(0);
		if(stack.getItem() instanceof IRechargable) {
			int charge = RechargeHelper.getCharge(stack);
			return new AspectList().add(Aspect.ENERGY, charge);
		}
		else {
			return null;
		}
	}

	@Override
	public void setAspects(AspectList aspects) {}

	@Override
	public int addToContainer(Aspect aspect, int amount) {
		return 0;
	}

	@Override
	public boolean takeFromContainer(Aspect aspect, int amount) {
		return false;
	}

	@Override
	public boolean takeFromContainer(AspectList aspects) {
		return false;
	}

	@Override
	public boolean doesContainerContainAmount(Aspect aspect, int amount) {
		return false;
	}

	@Override
	public boolean doesContainerContain(AspectList aspects) {
		return false;
	}

	@Override
	public int containerContains(Aspect aspect) {
		return 0;
	}

	@Override
	public boolean doesContainerAccept(Aspect aspect) {
		return false;
	}

	@Override
	public void readSyncNBT(NBTTagCompound nbt) {
		super.readSyncNBT(nbt);
		inventory.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeSyncNBT(NBTTagCompound nbt) {
		super.writeSyncNBT(nbt);
		inventory.writeToNBT(nbt);
		return nbt;
	}

	public int getScaledEnergy(int scale) {
		if(energyStorage.getMaxEnergyStored() <= 0) {
			return 0;
		}
		return scale * energyStorage.getEnergyStored() / energyStorage.getMaxEnergyStored();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public GuiContainer getClientGuiElement(EntityPlayer player, Object... args) {
		return new GuiVirialRechargePedestal(new ContainerVirialRechargePedestal(player.inventory, this));
	}

	@Override
	public Container getServerGuiElement(EntityPlayer player, Object... args) {
		return new ContainerVirialRechargePedestal(player.inventory, this);
	}
}
