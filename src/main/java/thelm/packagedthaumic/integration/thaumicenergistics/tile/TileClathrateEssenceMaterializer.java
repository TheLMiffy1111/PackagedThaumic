package thelm.packagedthaumic.integration.thaumicenergistics.tile;

import java.util.Collections;
import java.util.List;

import appeng.api.AEApi;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.events.MENetworkCellArrayUpdate;
import appeng.api.networking.events.MENetworkChannelsChanged;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.api.networking.events.MENetworkStorageEvent;
import appeng.api.networking.security.IActionHost;
import appeng.api.storage.ICellContainer;
import appeng.api.storage.ICellInventory;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thelm.packagedauto.tile.TileBase;
import thelm.packagedthaumic.integration.thaumicenergistics.inventory.MEInventoryClathrateEssenceMaterializer;
import thelm.packagedthaumic.integration.thaumicenergistics.networking.HostHelperTileClathrateEssenceMaterializer;

// Code based on AE2 Fluid Crafting
public class TileClathrateEssenceMaterializer extends TileBase implements ITickable, IGridHost, IActionHost, ICellContainer {

	public static int energyUsage = 1000;

	public HostHelperTileClathrateEssenceMaterializer hostHelper = new HostHelperTileClathrateEssenceMaterializer(this);
	public MEInventoryClathrateEssenceMaterializer meInventory = new MEInventoryClathrateEssenceMaterializer(this);
	public boolean firstTick = true;
	public boolean prevActiveState = false;

	@Override
	protected String getLocalizedName() {
		return I18n.translateToLocal("tile.packagedthaumic.clathrate_essence_materializer.name");
	}

	@Override
	public void update() {
		if(firstTick) {
			firstTick = false;
			updateState();
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		hostHelper.invalidate();
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		hostHelper.invalidate();
	}

	@Override
	public IGridNode getGridNode(AEPartLocation dir) {
		return getActionableNode();
	}

	@Override
	public AECableType getCableConnectionType(AEPartLocation dir) {
		return AECableType.SMART;
	}

	@Override
	public void securityBreak() {
		world.destroyBlock(pos, true);
	}

	@Override
	public IGridNode getActionableNode() {
		return hostHelper.getNode();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<IMEInventoryHandler> getCellArray(IStorageChannel<?> channel) {
		if(hostHelper.isActive() && AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class) == channel) {
			return Collections.singletonList(meInventory.invHandler);
		}
		return Collections.emptyList();
	}

	@Override
	public int getPriority() {
		return Integer.MIN_VALUE;
	}

	@Override
	public void saveChanges(ICellInventory<?> cellInventory) {}

	@Override
	public void blinkCell(int slot) {}

	@MENetworkEventSubscribe
	public void onPowerUpdate(MENetworkPowerStatusChange event) {
		updateState();
	}

	@MENetworkEventSubscribe
	public void onChannelUpdate(MENetworkChannelsChanged event) {
		updateState();
	}

	@MENetworkEventSubscribe
	public void onStorageUpdate(MENetworkStorageEvent event) {
		updateState();
	}

	public void updateState() {
		if(!world.isRemote) {
			boolean isActive = hostHelper.isActive();
			if(isActive != prevActiveState) {
				prevActiveState = isActive;
				IGrid grid = hostHelper.getNode().getGrid();
				if(grid != null) {
					grid.postEvent(new MENetworkCellArrayUpdate());
				}
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		hostHelper.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		hostHelper.writeToNBT(nbt);
		return nbt;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public GuiContainer getClientGuiElement(EntityPlayer player, Object... args) {
		return null;
	}

	@Override
	public Container getServerGuiElement(EntityPlayer player, Object... args) {
		return null;
	}
}
