package thelm.packagedthaumic.tile;

import java.util.HashSet;
import java.util.Set;

import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.tiles.essentia.TileSmelter;
import thelm.packagedauto.energy.EnergyStorage;
import thelm.packagedauto.tile.TileBase;
import thelm.packagedauto.tile.TileUnpackager;
import thelm.packagedthaumic.client.gui.GuiClathrateEssenceFormer;
import thelm.packagedthaumic.container.ContainerClathrateEssenceFormer;
import thelm.packagedthaumic.integration.appeng.networking.HostHelperTileClathrateEssenceFormer;
import thelm.packagedthaumic.inventory.InventoryClathrateEssenceFormer;
import thelm.packagedthaumic.item.ItemClathrateEssence;

@Optional.InterfaceList({
	@Optional.Interface(iface="appeng.api.networking.IGridHost", modid="appliedenergistics2"),
	@Optional.Interface(iface="appeng.api.networking.security.IActionHost", modid="appliedenergistics2"),
})
public class TileClathrateEssenceFormer extends TileBase implements ITickable, IEssentiaTransport, IGridHost, IActionHost {

	public static final Set<Class<?>> SMELTER_CLASSES;

	static {
		SMELTER_CLASSES = new HashSet<>();
		SMELTER_CLASSES.add(TileSmelter.class);
		if(Loader.isModLoaded("thaumadditions")) {
			try {
				SMELTER_CLASSES.add(Class.forName("org.zeith.thaumicadditions.tiles.TileAbstractSmelter"));
			}
			catch(Exception e) {}
		}
	}

	public static int energyCapacity = 5000;
	public static int energyUsage = 500;
	public static int tickInterval = 10;
	public static boolean drawMEEnergy = true;

	public TileClathrateEssenceFormer() {
		setInventory(new InventoryClathrateEssenceFormer(this));
		setEnergyStorage(new EnergyStorage(this, energyCapacity));
		if(Loader.isModLoaded("appliedenergistics2")) {
			hostHelper = new HostHelperTileClathrateEssenceFormer(this);
		}
	}

	@Override
	protected String getLocalizedName() {
		return I18n.translateToLocal("tile.packagedthaumic.clathrate_essence_former.name");
	}

	@Override
	public void update() {
		if(!world.isRemote) {
			if(world.getTotalWorldTime() % tickInterval == 0) {
				tickProcess();
			}
			chargeEnergy();
			if(world.getTotalWorldTime() % 8 == 0) {
				if(hostHelper != null && hostHelper.isActive()) {
					hostHelper.ejectItem();
					if(drawMEEnergy) {
						hostHelper.chargeEnergy();
					}
				}
				else {
					ejectItems();
				}
			}
		}
	}

	protected void tickProcess() {
		if(energyStorage.getEnergyStored() < energyUsage) {
			return;
		}
		ItemStack slotStack = inventory.getStackInSlot(0);
		Aspect slotAspect = null;
		if(slotStack.getItem() == ItemClathrateEssence.INSTANCE) {
			if(slotStack.getCount()+1 > slotStack.getMaxStackSize()) {
				return;
			}
			slotAspect = ItemClathrateEssence.INSTANCE.getAspect(slotStack);
		}
		else if(!slotStack.isEmpty()) {
			return;
		}
		for(EnumFacing facing : EnumFacing.VALUES) {
			TileEntity tile = ThaumcraftApiHelper.getConnectableTile(world, pos, facing);
			if(tile == null) {
				continue;
			}
			IEssentiaTransport transport = (IEssentiaTransport)tile;
			if(!transport.canOutputTo(facing.getOpposite())) {
				continue;
			}
			Aspect aspect = null;
			if(transport.getEssentiaAmount(facing.getOpposite()) > 0 &&
					transport.getSuctionAmount(facing.getOpposite()) < getSuctionAmount(facing) &&
					getSuctionAmount(facing) >= transport.getMinimumSuction()) {
				aspect = transport.getEssentiaType(facing.getOpposite());
			}
			if(aspect == null || slotAspect != null && slotAspect != aspect) {
				continue;
			}
			if(transport.takeEssentia(aspect, 1, facing) == 1) {
				energyStorage.extractEnergy(energyUsage, false);
				if(slotStack.isEmpty()) {
					inventory.setInventorySlotContents(0, ItemClathrateEssence.makeClathrate(aspect, 1));
				}
				else {
					slotStack.grow(1);
				}
				if(hostHelper != null && hostHelper.isActive()) {
					hostHelper.ejectItem();
				}
				else {
					ejectItems();
				}
				world.playSound(null, pos, SoundsTC.crystal, SoundCategory.BLOCKS, 0.2F, 1F);
				markDirty();
				return;
			}
		}
	}

	protected void ejectItems() {
		ItemStack stack = inventory.getStackInSlot(0);
		if(stack.isEmpty()) {
			return;
		}
		for(EnumFacing facing : EnumFacing.VALUES) {
			TileEntity tile = world.getTileEntity(pos.offset(facing));
			if(tile != null && !(tile instanceof TileUnpackager) &&
					SMELTER_CLASSES.stream().noneMatch(c->c.isAssignableFrom(tile.getClass())) &&
					tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite())) {
				IItemHandler itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
				for(int slot = 0; slot < itemHandler.getSlots(); ++slot) {
					ItemStack stackRem = itemHandler.insertItem(slot, stack, false);
					if(stackRem.getCount() < stack.getCount()) {
						stack = stackRem;
					}
					if(stack.isEmpty()) {
						break;
					}
				}
				inventory.setInventorySlotContents(0, stack);
			}
		}
	}

	protected void chargeEnergy() {
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
	public boolean isConnectable(EnumFacing side) {
		return true;
	}

	@Override
	public boolean canInputFrom(EnumFacing side) {
		return true;
	}

	@Override
	public boolean canOutputTo(EnumFacing side) {
		return false;
	}

	@Override
	public void setSuction(Aspect aspect, int amount) {}

	@Override
	public Aspect getSuctionType(EnumFacing side) {
		ItemStack slotStack = inventory.getStackInSlot(0);
		Aspect slotAspect = null;
		if(slotStack.getItem() == ItemClathrateEssence.INSTANCE) {
			slotAspect = ItemClathrateEssence.INSTANCE.getAspect(slotStack);
		}
		return slotAspect;
	}

	@Override
	public int getSuctionAmount(EnumFacing side) {
		ItemStack slotStack = inventory.getStackInSlot(0);
		return slotStack.getCount() >= slotStack.getMaxStackSize() ? 0 : 128;
	}

	@Override
	public int takeEssentia(Aspect aspect, int amount, EnumFacing side) {
		return 0;
	}

	@Override
	public int addEssentia(Aspect aspect, int amount, EnumFacing side) {
		return 0;
	}

	@Override
	public Aspect getEssentiaType(EnumFacing side) {
		return null;
	}

	@Override
	public int getEssentiaAmount(EnumFacing side) {
		return 0;
	}

	@Override
	public int getMinimumSuction() {
		return 0;
	}

	@Override
	public int getComparatorSignal() {
		if(!inventory.getStackInSlot(0).isEmpty()) {
			return 15;
		}
		return 0;
	}

	public HostHelperTileClathrateEssenceFormer hostHelper;

	@Override
	public void invalidate() {
		super.invalidate();
		if(hostHelper != null) {
			hostHelper.invalidate();
		}
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		if(hostHelper != null) {
			hostHelper.invalidate();
		}
	}

	@Optional.Method(modid="appliedenergistics2")
	@Override
	public IGridNode getGridNode(AEPartLocation dir) {
		return getActionableNode();
	}

	@Optional.Method(modid="appliedenergistics2")
	@Override
	public AECableType getCableConnectionType(AEPartLocation dir) {
		return AECableType.SMART;
	}

	@Optional.Method(modid="appliedenergistics2")
	@Override
	public void securityBreak() {
		world.destroyBlock(pos, true);
	}

	@Optional.Method(modid="appliedenergistics2")
	@Override
	public IGridNode getActionableNode() {
		return hostHelper.getNode();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if(hostHelper != null) {
			hostHelper.readFromNBT(nbt);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		if(hostHelper != null) {
			hostHelper.writeToNBT(nbt);
		}
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
		return new GuiClathrateEssenceFormer(new ContainerClathrateEssenceFormer(player.inventory, this));
	}

	@Override
	public Container getServerGuiElement(EntityPlayer player, Object... args) {
		return new ContainerClathrateEssenceFormer(player.inventory, this);
	}
}
