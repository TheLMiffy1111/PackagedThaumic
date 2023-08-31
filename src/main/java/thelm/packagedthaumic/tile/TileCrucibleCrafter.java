package thelm.packagedthaumic.tile;

import java.util.List;

import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
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
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thelm.packagedauto.api.IPackageCraftingMachine;
import thelm.packagedauto.api.IRecipeInfo;
import thelm.packagedauto.api.MiscUtil;
import thelm.packagedauto.energy.EnergyStorage;
import thelm.packagedauto.tile.TileBase;
import thelm.packagedauto.tile.TileUnpackager;
import thelm.packagedthaumic.client.gui.GuiCrucibleCrafter;
import thelm.packagedthaumic.container.ContainerCrucibleCrafter;
import thelm.packagedthaumic.integration.appeng.networking.HostHelperTileCrucibleCrafter;
import thelm.packagedthaumic.inventory.InventoryCrucibleCrafter;
import thelm.packagedthaumic.recipe.IRecipeInfoCrucible;
import thelm.packagedthaumic.util.ThaumcraftHelper;

@Optional.InterfaceList({
	@Optional.Interface(iface="appeng.api.networking.IGridHost", modid="appliedenergistics2"),
	@Optional.Interface(iface="appeng.api.networking.security.IActionHost", modid="appliedenergistics2"),
})
public class TileCrucibleCrafter extends TileBase implements ITickable, IPackageCraftingMachine, IAspectContainer, IGridHost, IActionHost {

	public static int energyCapacity = 5000;
	public static double timeMultiplier = 0.5;
	public static int energyUsage = 100;
	public static boolean requiresCrucible = true;
	public static boolean requiresHeat = true;
	public static boolean drawMEEnergy = true;

	public boolean hasCrucible = false;
	public boolean heated = false;
	public IPlayerKnowledge ownerKnowledge = ThaumcraftCapabilities.KNOWLEDGE.getDefaultInstance();
	public boolean researchRequired = false;
	public AspectList aspects = new AspectList();
	public boolean isWorking = false;
	public int energyReq = 0;
	public int remainingProgress = 0;
	public IRecipeInfoCrucible currentRecipe;

	public TileCrucibleCrafter() {
		setInventory(new InventoryCrucibleCrafter(this));
		setEnergyStorage(new EnergyStorage(this, energyCapacity));
		if(Loader.isModLoaded("appliedenergistics2")) {
			hostHelper = new HostHelperTileCrucibleCrafter(this);
		}
	}

	@Override
	protected String getLocalizedName() {
		return I18n.translateToLocal("tile.packagedthaumic.crucible_crafter.name");
	}

	public ITextComponent getMessage() {
		ITextComponent message = new TextComponentTranslation("misc.packagedthaumic.owner", UsernameCache.getMap().getOrDefault(ownerUUID, ownerUUID.toString()));
		if(!isWorking) {
			if(!hasCrucible()) {
				message.appendText("\n");
				message.appendSibling(new TextComponentTranslation("tile.packagedthaumic.crucible_crafter.crucible_required"));
			}
			else {
				if(!hasHeat()) {
					message.appendText("\n");
					message.appendSibling(new TextComponentTranslation("tile.packagedthaumic.crucible_crafter.heat_required"));
				}
				if(researchRequired) {
					message.appendText("\n");
					message.appendSibling(new TextComponentTranslation("misc.packagedthaumic.research_required"));
				}
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
			if(world.getTotalWorldTime() % 40 == 0) {
				heated = hasHeat();
			}
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
		if(!isBusy() && recipeInfo instanceof IRecipeInfoCrucible) {
			IRecipeInfoCrucible recipe = (IRecipeInfoCrucible)recipeInfo;
			if(hasCrucible()) {
				if(ownerKnowledge != null && ThaumcraftHelper.INSTANCE.knowsResearchStrict(ownerKnowledge, recipe.getResearch())) {
					ItemStack slotStack = inventory.getStackInSlot(1);
					ItemStack outputStack = recipe.getOutput();
					if(slotStack.isEmpty() || slotStack.getItem() == outputStack.getItem() && slotStack.getItemDamage() == outputStack.getItemDamage() && ItemStack.areItemStackShareTagsEqual(slotStack, outputStack) && slotStack.getCount()+outputStack.getCount() <= outputStack.getMaxStackSize()) {
						currentRecipe = recipe;
						researchRequired = false;
						isWorking = true;
						energyReq = remainingProgress = (int)(energyUsage*5*recipe.getAspects().visSize()*timeMultiplier);
						inventory.setInventorySlotContents(0, recipe.getCatalystInput());
						aspects.add(recipe.getAspects());
						markDirty();
						syncTile(false);
						return true;
					}
				}
				else {
					researchRequired = true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean isBusy() {
		return isWorking || !inventory.stacks.subList(0, 1).stream().allMatch(ItemStack::isEmpty);
	}

	protected void tickProcess() {
		if(world.getTotalWorldTime() % 8 == 0 && !hasCrucible()) {
			endProcess();
		}
		if(heated) {
			int energy = energyStorage.extractEnergy(Math.min(energyUsage, remainingProgress), false);
			remainingProgress -= energy;
		}
	}

	protected void finishProcess() {
		if(currentRecipe == null) {
			endProcess();
			return;
		}
		if(inventory.getStackInSlot(1).isEmpty()) {
			inventory.setInventorySlotContents(1, currentRecipe.getOutput());
		}
		else {
			inventory.getStackInSlot(1).grow(currentRecipe.getOutput().getCount());
		}
		aspects.aspects.clear();
		inventory.setInventorySlotContents(0, MiscUtil.getContainerItem(inventory.getStackInSlot(0)));
		world.playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.25F, 2.6F+(world.rand.nextFloat()-world.rand.nextFloat())*0.8F);
		endProcess();
	}

	public void endProcess() {
		energyReq = 0;
		remainingProgress = 0;
		isWorking = false;
		currentRecipe = null;
		//int essentia = aspects.visSize();
		//if(essentia > 0) {
		//	AuraHelper.polluteAura(world, pos, essentia*0.125F, true);
		//	int f = aspects.getAmount(Aspect.FLUX);
		//	if(f > 0) {
		//		AuraHelper.polluteAura(world, pos, essentia*0.375F, false);
		//	}
		//	world.playSound(null, pos, SoundsTC.spill, SoundCategory.BLOCKS, 0.2F, 1F);
		//}
		aspects.aspects.clear();
		syncTile(false);
		markDirty();
	}

	protected void ejectItems() {
		int endIndex = isWorking ? 1 : 0;
		for(EnumFacing facing : EnumFacing.VALUES) {
			TileEntity tile = world.getTileEntity(pos.offset(facing));
			if(tile != null && !(tile instanceof TileUnpackager) && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite())) {
				IItemHandler itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
				boolean flag = true;
				for(int i = 1; i >= endIndex; --i) {
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
		int prevStored = energyStorage.getEnergyStored();
		ItemStack energyStack = inventory.getStackInSlot(2);
		if(energyStack.hasCapability(CapabilityEnergy.ENERGY, null)) {
			int energyRequest = Math.min(energyStorage.getMaxReceive(), energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored());
			energyStorage.receiveEnergy(energyStack.getCapability(CapabilityEnergy.ENERGY, null).extractEnergy(energyRequest, false), false);
			if(energyStack.getCount() <= 0) {
				inventory.setInventorySlotContents(2, ItemStack.EMPTY);
			}
		}
	}

	protected boolean hasCrucible() {
		return !requiresCrucible || world.getBlockState(pos.down()).getBlock() == BlocksTC.crucible;
	}

	protected boolean hasHeat() {
		if(!requiresHeat) {
			return true;
		}
		IBlockState state = world.getBlockState(pos.down(requiresCrucible ? 2 : 1));
		Material mat = state.getMaterial();
		Block bi = state.getBlock();
		return mat == Material.LAVA || mat == Material.FIRE || BlocksTC.nitor.containsValue(bi) || bi == Blocks.MAGMA;
	}

	@Override
	public AspectList getAspects() {
		return aspects;
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

	public void onBreak() {
		if(!world.isRemote && isWorking) {
			endProcess();
		}
	}

	@Override
	public int getComparatorSignal() {
		if(isWorking) {
			return 1;
		}
		if(!inventory.stacks.subList(0, 2).stream().allMatch(ItemStack::isEmpty)) {
			return 15;
		}
		return 0;
	}

	public HostHelperTileCrucibleCrafter hostHelper;

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
			if(recipe instanceof IRecipeInfoCrucible) {
				currentRecipe = (IRecipeInfoCrucible)recipe;
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

	@Override
	public void readSyncNBT(NBTTagCompound nbt) {
		super.readSyncNBT(nbt);
		aspects.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeSyncNBT(NBTTagCompound nbt) {
		super.writeSyncNBT(nbt);
		aspects.writeToNBT(nbt);
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
		return new GuiCrucibleCrafter(new ContainerCrucibleCrafter(player.inventory, this));
	}

	@Override
	public Container getServerGuiElement(EntityPlayer player, Object... args) {
		return new ContainerCrucibleCrafter(player.inventory, this);
	}
}
