package thelm.packagedthaumic.tile;

import java.util.List;

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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thelm.packagedauto.api.IPackageCraftingMachine;
import thelm.packagedauto.api.IRecipeInfo;
import thelm.packagedauto.api.MiscUtil;
import thelm.packagedauto.energy.EnergyStorage;
import thelm.packagedauto.tile.TileBase;
import thelm.packagedauto.tile.TileUnpackager;
import thelm.packagedthaumic.client.gui.GuiVirialArcaneCrafter;
import thelm.packagedthaumic.container.ContainerVirialArcaneCrafter;
import thelm.packagedthaumic.integration.appeng.networking.HostHelperTileVirialArcaneCrafter;
import thelm.packagedthaumic.inventory.InventoryVirialArcaneCrafter;
import thelm.packagedthaumic.recipe.IRecipeInfoArcane;

@Optional.InterfaceList({
	@Optional.Interface(iface="appeng.api.networking.IGridHost", modid="appliedenergistics2"),
	@Optional.Interface(iface="appeng.api.networking.security.IActionHost", modid="appliedenergistics2"),
})
public class TileVirialArcaneCrafter extends TileBase implements ITickable, IPackageCraftingMachine, IGridHost, IActionHost {

	public static int energyCapacity = 50000;
	public static int energyPerVis = 1250;
	public static int energyUsage = 5000;
	public static double fluxLeakageChance = 0.002;
	public static boolean drawMEEnergy = false;

	public IPlayerKnowledge ownerKnowledge = ThaumcraftCapabilities.KNOWLEDGE.getDefaultInstance();
	public boolean researchRequired = false;
	public boolean isWorking = false;
	public int energyReq = 0;
	public int remainingProgress = 0;
	public IRecipeInfoArcane currentRecipe;

	public TileVirialArcaneCrafter() {
		setInventory(new InventoryVirialArcaneCrafter(this));
		setEnergyStorage(new EnergyStorage(this, energyCapacity));
		if(Loader.isModLoaded("appliedenergistics2")) {
			hostHelper = new HostHelperTileVirialArcaneCrafter(this);
		}
	}

	@Override
	protected String getLocalizedName() {
		return I18n.translateToLocal("tile.packagedthaumic.virial_arcane_crafter.name");
	}

	public ITextComponent getMessage() {
		ITextComponent message = new TextComponentTranslation("misc.packagedthaumic.owner", UsernameCache.getMap().getOrDefault(ownerUUID, ownerUUID.toString()));
		if(!isWorking) {
			if(researchRequired) {
				message.appendText("\n");
				message.appendSibling(new TextComponentTranslation("misc.packagedthaumic.research_required"));
			}
		}
		return message;
	}

	@Override
	public void setOwner(EntityPlayer owner) {
		super.setOwner(owner);
		updateKnowledge(owner);
	}

	public void updateKnowledge(EntityPlayer player) {
		if(player.getUniqueID().equals(ownerUUID)) {
			ownerKnowledge = ThaumcraftCapabilities.getKnowledge(player);
		}
	}

	@Override
	public void update() {
		if(!world.isRemote) {
			if(isWorking) {
				tickProcess();
				if(remainingProgress <= 0) {
					finishProcess();
					if(hostHelper != null && hostHelper.isActive()) {
						hostHelper.ejectItem();
					}
					else {
						ejectItems();
					}
				}
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

	@Override
	public boolean acceptPackage(IRecipeInfo recipeInfo, List<ItemStack> stacks, EnumFacing facing) {
		if(!isBusy() && recipeInfo instanceof IRecipeInfoArcane) {
			IRecipeInfoArcane recipe = (IRecipeInfoArcane)recipeInfo;
			if(ownerKnowledge != null && ownerKnowledge.isResearchKnown(recipe.getResearch())) {
				ItemStack slotStack = inventory.getStackInSlot(15);
				ItemStack outputStack = recipe.getOutput();
				if(slotStack.isEmpty() || slotStack.getItem() == outputStack.getItem() && slotStack.getItemDamage() == outputStack.getItemDamage() && ItemStack.areItemStackShareTagsEqual(slotStack, outputStack) && slotStack.getCount()+outputStack.getCount() <= outputStack.getMaxStackSize()) {
					currentRecipe = recipe;
					researchRequired = false;
					isWorking = true;
					energyReq = remainingProgress = recipe.getVis()*energyPerVis;
					for(int i = 0; i < 15; ++i) {
						inventory.setInventorySlotContents(i, recipe.getMatrix().getStackInSlot(i).copy());
					}
					markDirty();
					return true;
				}
			}
			else {
				researchRequired = true;
			}
		}
		return false;
	}

	@Override
	public boolean isBusy() {
		return isWorking || !inventory.stacks.subList(0, 15).stream().allMatch(ItemStack::isEmpty);
	}

	protected void tickProcess() {
		int energy = energyStorage.extractEnergy(Math.min(energyUsage, remainingProgress), false);
		remainingProgress -= energy;
		if(energy > 0 && world.rand.nextDouble() < fluxLeakageChance*energy/energyPerVis) {
			AuraHelper.polluteAura(world, pos, 1, true);
		}
	}

	protected void finishProcess() {
		if(currentRecipe == null) {
			endProcess();
			return;
		}
		if(inventory.getStackInSlot(15).isEmpty()) {
			inventory.setInventorySlotContents(15, currentRecipe.getOutput());
		}
		else {
			inventory.getStackInSlot(15).grow(currentRecipe.getOutput().getCount());
		}
		for(int i = 0; i < 9; ++i) {
			inventory.setInventorySlotContents(i, MiscUtil.getContainerItem(inventory.getStackInSlot(i)));
		}
		for(int i = 9; i < 15; ++i) {
			inventory.setInventorySlotContents(i, ItemStack.EMPTY);
		}
		endProcess();
	}

	public void endProcess() {
		remainingProgress = 0;
		isWorking = false;
		currentRecipe = null;
		markDirty();
	}

	protected void ejectItems() {
		int endIndex = isWorking ? 15 : 0;
		for(EnumFacing facing : EnumFacing.VALUES) {
			TileEntity tile = world.getTileEntity(pos.offset(facing));
			if(tile != null && !(tile instanceof TileUnpackager) && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite())) {
				IItemHandler itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
				boolean flag = true;
				for(int i = 15; i >= endIndex; --i) {
					ItemStack stack = inventory.getStackInSlot(i);
					if(stack.isEmpty()) {
						continue;
					}
					for(int slot = 0; slot < itemHandler.getSlots(); ++slot) {
						ItemStack stackRem = itemHandler.insertItem(slot, stack, false);
						if(stackRem.getCount() < stack.getCount()) {
							stack = stackRem;
							flag = false;
						}
						if(stack.isEmpty()) {
							break;
						}
					}
					inventory.setInventorySlotContents(i, stack);
					if(flag) {
						break;
					}
				}
			}
		}
	}

	protected void chargeEnergy() {
		ItemStack energyStack = inventory.getStackInSlot(16);
		if(energyStack.hasCapability(CapabilityEnergy.ENERGY, null)) {
			int energyRequest = Math.min(energyStorage.getMaxReceive(), energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored());
			energyStorage.receiveEnergy(energyStack.getCapability(CapabilityEnergy.ENERGY, null).extractEnergy(energyRequest, false), false);
			if(energyStack.getCount() <= 0) {
				inventory.setInventorySlotContents(16, ItemStack.EMPTY);
			}
		}
	}

	@Override
	public int getComparatorSignal() {
		if(isWorking) {
			return 1;
		}
		if(!inventory.stacks.subList(0, 16).stream().allMatch(ItemStack::isEmpty)) {
			return 15;
		}
		return 0;
	}

	public HostHelperTileVirialArcaneCrafter hostHelper;

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
		ownerKnowledge.deserializeNBT(nbt.getCompoundTag("OwnerKnowledge"));
		isWorking = nbt.getBoolean("Working");
		remainingProgress = nbt.getInteger("Progress");
		energyReq = nbt.getInteger("EnergyReq");
		currentRecipe = null;
		if(nbt.hasKey("Recipe")) {
			NBTTagCompound tag = nbt.getCompoundTag("Recipe");
			IRecipeInfo recipe = MiscUtil.readRecipeFromNBT(tag);
			if(recipe instanceof IRecipeInfoArcane) {
				currentRecipe = (IRecipeInfoArcane)recipe;
			}
		}
		if(hostHelper != null) {
			hostHelper.readFromNBT(nbt);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setTag("OwnerKnowledge", ownerKnowledge.serializeNBT());
		nbt.setBoolean("Working", isWorking);
		nbt.setInteger("Progress", remainingProgress);
		nbt.setInteger("EnergyReq", energyReq);
		if(currentRecipe != null) {
			NBTTagCompound tag = MiscUtil.writeRecipeToNBT(new NBTTagCompound(), currentRecipe);
			nbt.setTag("Recipe", tag);
		}
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

	public int getScaledProgress(int scale) {
		if(remainingProgress <= 0) {
			return 0;
		}
		return scale * (energyReq-remainingProgress) / energyReq;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public GuiContainer getClientGuiElement(EntityPlayer player, Object... args) {
		return new GuiVirialArcaneCrafter(new ContainerVirialArcaneCrafter(player.inventory, this));
	}

	@Override
	public Container getServerGuiElement(EntityPlayer player, Object... args) {
		return new ContainerVirialArcaneCrafter(player.inventory, this);
	}
}
