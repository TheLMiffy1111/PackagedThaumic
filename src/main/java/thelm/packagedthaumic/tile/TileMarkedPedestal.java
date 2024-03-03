package thelm.packagedthaumic.tile;

import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thelm.packagedauto.tile.TileBase;
import thelm.packagedthaumic.integration.appeng.networking.HostHelperTileMarkedPedestal;
import thelm.packagedthaumic.inventory.InventoryMarkedPedestal;

@Optional.InterfaceList({
	@Optional.Interface(iface="appeng.api.networking.IGridHost", modid="appliedenergistics2"),
	@Optional.Interface(iface="appeng.api.networking.security.IActionHost", modid="appliedenergistics2"),
})
public class TileMarkedPedestal extends TileBase implements ITickable, IGridHost, IActionHost {

	public boolean firstTick = true;
	
	public TileMarkedPedestal() {
		setInventory(new InventoryMarkedPedestal(this));
		if(Loader.isModLoaded("appliedenergistics2")) {
			hostHelper = new HostHelperTileMarkedPedestal(this);
		}
	}

	@Override
	protected String getLocalizedName() {
		return I18n.translateToLocal("tile.packagedthaumic.marked_pedestal.name");
	}

	@Override
	public void update() {
		if(firstTick) {
			firstTick = false;
			if(!world.isRemote && hostHelper != null) {
				hostHelper.isActive();
			}
		}
	}

	public void spawnItem() {
		if(hostHelper != null && hostHelper.isActive()) {
			hostHelper.ejectItem();
		}
		ItemStack stack = inventory.getStackInSlot(0);
		inventory.setInventorySlotContents(0, ItemStack.EMPTY);
		if(!stack.isEmpty()) {
			double dx = world.rand.nextFloat()/2+0.25;
			double dy = world.rand.nextFloat()/2+0.75;
			double dz = world.rand.nextFloat()/2+0.25;
			EntityItem entityitem = new EntityItem(world, pos.getX()+dx, pos.getY()+dy, pos.getZ()+dz, stack);
			entityitem.setDefaultPickupDelay();
			world.spawnEntity(entityitem);
		}
	}

	@Override
	public int getComparatorSignal() {
		return inventory.getStackInSlot(0).isEmpty() ? 0 : 15;
	}

	public HostHelperTileMarkedPedestal hostHelper;

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
