package thelm.packagedthaumic.tile;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.google.common.collect.Streams;

import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.IPlayerWarp.EnumWarpType;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.crafting.IInfusionStabiliser;
import thaumcraft.api.crafting.IInfusionStabiliserExt;
import thaumcraft.api.items.IGogglesDisplayExtended;
import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.api.potions.PotionVisExhaust;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.basic.BlockPillar;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXBlockArc;
import thelm.packagedauto.api.IPackageCraftingMachine;
import thelm.packagedauto.api.IRecipeInfo;
import thelm.packagedauto.api.MiscUtil;
import thelm.packagedauto.energy.EnergyStorage;
import thelm.packagedauto.tile.TileBase;
import thelm.packagedauto.tile.TileUnpackager;
import thelm.packagedthaumic.block.BlockMarkedPedestal;
import thelm.packagedthaumic.client.gui.GuiInfusionCrafter;
import thelm.packagedthaumic.container.ContainerInfusionCrafter;
import thelm.packagedthaumic.integration.appeng.networking.HostHelperTileInfusionCrafter;
import thelm.packagedthaumic.inventory.InventoryInfusionCrafter;
import thelm.packagedthaumic.network.packet.PacketSyncStability;
import thelm.packagedthaumic.recipe.IRecipeInfoInfusion;
import thelm.packagedthaumic.util.ThaumcraftHelper;

@Optional.InterfaceList({
	@Optional.Interface(iface="appeng.api.networking.IGridHost", modid="appliedenergistics2"),
	@Optional.Interface(iface="appeng.api.networking.security.IActionHost", modid="appliedenergistics2"),
})
public class TileInfusionCrafter extends TileBase implements ITickable, IPackageCraftingMachine, IAspectContainer, IGogglesDisplayExtended, IGridHost, IActionHost {

	public static final DecimalFormat STABILITY_FORMATTER = new DecimalFormat("#######.##");

	public static int energyCapacity = 5000;
	public static double essentiaTimeMultiplier = 1;
	public static double itemTimeMultiplier = 1;
	public static int energyUsage = 100;
	public static boolean requiresPillars = true;
	public static boolean drawMEEnergy = true;
	public static double itemParticleRate = 0.5;

	public boolean firstTick = true;
	public boolean structureValid = false;
	public IPlayerKnowledge ownerKnowledge = ThaumcraftCapabilities.KNOWLEDGE.getDefaultInstance();
	public boolean researchRequired = false;
	public AspectList aspects = new AspectList();
	public ArrayList<BlockPos> problemBlocks = new ArrayList<>();
	public int craftCount = 0;
	public double baseTimeMultiplier = 1;
	public double baseEssentiaTimeMultiplier = 1;
	public double stability = 25;
	public double stabilityGain = 0;
	public int instability = 0;
	public int requiredPedestals = 0;
	public double requiredStability = 0;
	public boolean isWorking = false;
	public int energyReq = 0;
	public int remainingProgress = 0;
	public IRecipeInfoInfusion currentRecipe;
	public List<BlockPos> pedestals = new ArrayList<>();

	public TileInfusionCrafter() {
		setInventory(new InventoryInfusionCrafter(this));
		setEnergyStorage(new EnergyStorage(this, energyCapacity));
		if(Loader.isModLoaded("appliedenergistics2")) {
			hostHelper = new HostHelperTileInfusionCrafter(this);
		}
	}

	@Override
	protected String getLocalizedName() {
		return I18n.translateToLocal("tile.packagedthaumic.infusion_crafter.name");
	}

	public ITextComponent getMessage() {
		ITextComponent message = new TextComponentTranslation("misc.packagedthaumic.owner", UsernameCache.getMap().getOrDefault(ownerUUID, ownerUUID.toString()));
		if(!isWorking) {
			getSurroundings();
			int usablePedestals = getEmptyPedestals().size();
			message.appendText("\n");
			message.appendSibling(new TextComponentTranslation("tile.packagedthaumic.infusion_crafter.pedestals.usable", usablePedestals));
			if(requiredPedestals > 0) {
				message.appendText("\n");
				message.appendSibling(new TextComponentTranslation("tile.packagedthaumic.infusion_crafter.pedestals.required", requiredPedestals));
			}
			if(requiredStability > 0) {
				message.appendText("\n");
				message.appendSibling(new TextComponentTranslation("tile.packagedthaumic.infusion_crafter.stability.required", STABILITY_FORMATTER.format(requiredStability)));
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
		if(firstTick) {
			firstTick = false;
			getSurroundings();
		}
		if(!world.isRemote) {
			if(world.getTotalWorldTime() % 20 == 0) {
				matchStructure();
			}
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
			else if(stability < 25) {
				stability = 25;
				PacketSyncStability.sync(this);
				markDirty();
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
		else {
			clientTick();
		}
	}

	@SideOnly(Side.CLIENT)
	protected void clientTick() {
		if(world.getTotalWorldTime() % 200 == 0) {
			getSurroundings();
		}
		if(isWorking) {
			if(craftCount == 0) {
				world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundsTC.infuserstart, SoundCategory.BLOCKS, 0.5F, 1F, false);
			}
			else if(craftCount == 0 || this.craftCount % 65 == 0) {
				world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundsTC.infuser, SoundCategory.BLOCKS, 0.5F, 1F, false);
			}
			++craftCount;
			FXDispatcher.INSTANCE.blockRunes(pos.getX(), pos.getY()-2, pos.getZ(), 0.5F+world.rand.nextFloat()*0.2F, 0.1F, 0.7F+world.rand.nextFloat()*0.3F, 25, -0.03F);
		}
		else if(craftCount > 0) {
			craftCount -= 2;
			craftCount = MathHelper.clamp(craftCount, 0, 50);
		}
		for(BlockPos pedestalPos : pedestals) {
			TileEntity tile = world.getTileEntity(pedestalPos);
			if(tile instanceof TileMarkedPedestal) {
				ItemStack is = ((TileMarkedPedestal)tile).getInventory().getStackInSlot(0);
				if(!is.isEmpty()) {
					if(world.rand.nextInt(6) == 0) {
						FXDispatcher.INSTANCE.drawInfusionParticles3(
								pedestalPos.getX()+world.rand.nextDouble(),
								pedestalPos.getY()+world.rand.nextDouble()+1,
								pedestalPos.getZ()+world.rand.nextDouble(),
								pos.getX(), pos.getY(), pos.getZ());
					}
					else {
						Item bi = is.getItem();
						int count = (int)itemParticleRate + world.rand.nextDouble() < (itemParticleRate % 1) ? 1 : 0;
						for(int i = 0; i < count; ++i) {
							if(bi instanceof ItemBlock) {
								FXDispatcher.INSTANCE.drawInfusionParticles2(
										pedestalPos.getX()+world.rand.nextDouble(),
										pedestalPos.getY()+world.rand.nextDouble()+1,
										pedestalPos.getZ()+world.rand.nextDouble(),
										pos, Block.getBlockFromItem(bi).getDefaultState(), is.getItemDamage());
							}
							else {
								FXDispatcher.INSTANCE.drawInfusionParticles1(
										pedestalPos.getX()+0.4+world.rand.nextDouble()*0.2,
										pedestalPos.getY()+1.23+world.rand.nextDouble()*0.2,
										pedestalPos.getZ()+0.4+world.rand.nextDouble()*0.2,
										pos, is);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public boolean acceptPackage(IRecipeInfo recipeInfo, List<ItemStack> stacks, EnumFacing facing) {
		if(!isBusy() && recipeInfo instanceof IRecipeInfoInfusion) {
			IRecipeInfoInfusion recipe = (IRecipeInfoInfusion)recipeInfo;
			if(structureValid) {
				if(ownerKnowledge != null && ThaumcraftHelper.INSTANCE.knowsResearchStrict(ownerKnowledge, recipe.getResearch())) {
					List<ItemStack> pedestalInputs = recipe.getPedestalInputs();
					List<BlockPos> emptyPedestals = getEmptyPedestals();
					getSurroundings();
					requiredPedestals = Math.max(requiredPedestals, pedestalInputs.size());
					requiredStability = Math.max(requiredStability, recipe.getInstability()/2.5);
					if(emptyPedestals.size() >= pedestalInputs.size() && stabilityGain > requiredStability) {
						pedestals.clear();
						pedestals.addAll(emptyPedestals.subList(0, pedestalInputs.size()));
						currentRecipe = recipe;
						researchRequired = false;
						isWorking = true;
						energyReq = remainingProgress = (int)(
								energyUsage*5*recipe.getAspects().visSize()*baseTimeMultiplier*baseEssentiaTimeMultiplier*essentiaTimeMultiplier+
								energyUsage*25*pedestalInputs.size()*baseTimeMultiplier*itemTimeMultiplier);
						inventory.setInventorySlotContents(0, recipe.getCenterInput());
						aspects.add(recipe.getAspects());
						instability = recipe.getInstability();
						for(int i = 0; i < pedestals.size(); ++i) {
							((TileMarkedPedestal)world.getTileEntity(pedestals.get(i))).getInventory().
							setInventorySlotContents(0, pedestalInputs.get(i).copy());
						}
						world.playSound(null, pos, SoundsTC.craftstart, SoundCategory.BLOCKS, 0.5F, 1F);
						syncTile(false);
						markDirty();
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
		return isWorking || !inventory.stacks.subList(0, 2).stream().allMatch(ItemStack::isEmpty);
	}

	protected void tickProcess() {
		if(pedestals.stream().map(world::getTileEntity).
				anyMatch(tile->!(tile instanceof TileMarkedPedestal) || tile.isInvalid())) {
			world.playSound(null, pos, SoundsTC.craftfail, SoundCategory.BLOCKS, 1F, 0.6F);
			endProcess();
		}
		else {
			int energy = energyStorage.extractEnergy(Math.min(energyUsage, remainingProgress), false);
			remainingProgress -= energy;
			if(world.getWorldTime() % 5 == 0) {
				getSurroundings();
				stability -= world.rand.nextDouble()*getStabilityLoss();
				stability += stabilityGain;
				stability = MathHelper.clamp(stability, -100, 25);
				if(stability < 0 && world.rand.nextInt(1500) < -stability) {
					if(world.rand.nextBoolean()) {
						switch(world.rand.nextInt(12)) {
						case 0: case 1: case 2:
							instabilityEventWarp();
							break;
						case 3: case 4: case 5:
							instabilityEventZap(false);
							break;
						case 6: case 7:
							instabilityEventZap(true);
							break;
						case 8: case 9:
							instabilityEventHarm(false);
							break;
						case 10:
							instabilityEventHarm(true);
							break;
						case 11:
							world.createExplosion(null, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 1.5F+world.rand.nextFloat(), false);
							break;
						}
					}
					else {
						AuraHelper.polluteAura(world, pos, 0.25F*(world.rand.nextInt(6)+5), true);
					}
					stability += 5+world.rand.nextDouble()*5;
					remainingProgress += energyUsage*5;
					remainingProgress = Math.min(remainingProgress, energyReq);
				}
				PacketSyncStability.sync(this);
				markDirty();
			}
		}
	}

	protected void finishProcess() {
		if(currentRecipe == null) {
			world.playSound(null, pos, SoundsTC.craftfail, SoundCategory.BLOCKS, 1F, 0.6F);
			endProcess();
			return;
		}
		if(pedestals.stream().map(world::getTileEntity).
				anyMatch(tile->!(tile instanceof TileMarkedPedestal) || tile.isInvalid())) {
			world.playSound(null, pos, SoundsTC.craftfail, SoundCategory.BLOCKS, 1F, 0.6F);
			endProcess();
			return;
		}
		for(BlockPos pedestalPos : pedestals) {
			IInventory pedestalInv = ((TileMarkedPedestal)world.getTileEntity(pedestalPos)).getInventory();
			pedestalInv.setInventorySlotContents(0, MiscUtil.getContainerItem(pedestalInv.getStackInSlot(0)));
		}
		aspects.aspects.clear();
		inventory.setInventorySlotContents(0, ItemStack.EMPTY);
		inventory.setInventorySlotContents(1, currentRecipe.getOutput());
		world.playSound(null, pos, SoundsTC.wand, SoundCategory.BLOCKS, 0.5F, 1F);
		world.playSound(null, pos, SoundsTC.poof, SoundCategory.BLOCKS, 0.4F, 1+(float)world.rand.nextGaussian()*0.05F);
		endProcess();
	}

	public void endProcess() {
		energyReq = 0;
		remainingProgress = 0;
		pedestals.stream().map(world::getTileEntity).
		filter(tile->tile instanceof TileMarkedPedestal && !tile.isInvalid()).
		forEach(tile->((TileMarkedPedestal)tile).spawnItem());
		pedestals.clear();
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
		instability = 0;
		syncTile(false);
		markDirty();
	}

	protected void getSurroundings() {
		TreeSet<BlockPos> positions = new TreeSet<>();
		Object2IntMap<Block> blockCount = new Object2IntOpenHashMap<>();
		problemBlocks.clear();
		baseTimeMultiplier = 1;
		baseEssentiaTimeMultiplier = 1;
		stabilityGain = 0;
		Streams.stream(BlockPos.getAllInBoxMutable(pos.add(-8, -7, -8), pos.add(8, 3, 8))).forEach(bp->{
			if(bp.getX() != 0 || bp.getZ() != 0) {
				Block blk = world.getBlockState(bp).getBlock();
				try {
					if(blk == Blocks.SKULL || blk instanceof IInfusionStabiliser && ((IInfusionStabiliser)blk).canStabaliseInfusion(world, bp)) {
						positions.add(bp.toImmutable());
					}
					if(blk == BlockMarkedPedestal.ANCIENT) {
						baseEssentiaTimeMultiplier -= 0.01;
					}
					if(blk == BlockMarkedPedestal.ELDRITCH) {
						baseEssentiaTimeMultiplier += 0.0025;
					}
				}
				catch(Exception e) {}
			}
		});
		while(!positions.isEmpty()) {
			BlockPos bp1 = positions.first();
			BlockPos bp2 = new BlockPos(pos.getX()*2-bp1.getX(), bp1.getY(), pos.getZ()*2-bp1.getZ());
			Block blk1 = world.getBlockState(bp1).getBlock();
			Block blk2 = world.getBlockState(bp2).getBlock();
			try {
				float amt1 = 0.1F;
				float amt2 = 0.1F;
				if(blk1 instanceof IInfusionStabiliserExt) {
					amt1 = ((IInfusionStabiliserExt)blk1).getStabilizationAmount(world, bp1);
				}
				if(blk2 instanceof IInfusionStabiliserExt) {
					amt2 = ((IInfusionStabiliserExt)blk2).getStabilizationAmount(world, bp2);
				}
				if(blk1 == blk2 && amt1 == amt2) {
					if(blk1 instanceof IInfusionStabiliserExt && ((IInfusionStabiliserExt)blk1).hasSymmetryPenalty(world, bp1, bp2)) {
						stabilityGain -= ((IInfusionStabiliserExt)blk1).getSymmetryPenalty(world, bp1);
						problemBlocks.add(bp1);
					}
					else {
						int c = blockCount.getInt(blk1);
						stabilityGain += amt1*Math.pow(0.75, c);
						blockCount.put(blk1, c+1);
					}
				}
				else {
					stabilityGain -= Math.max(amt1, amt2);
					problemBlocks.add(bp1);
				}
			}
			catch(Exception e) {}
			positions.remove(bp1);
			positions.remove(bp2);
		}
		if(world.getBlockState(pos.add(-1, -2, -1)).getBlock() == BlocksTC.pillarAncient &&
				world.getBlockState(pos.add(1, -2, -1)).getBlock() == BlocksTC.pillarAncient &&
				world.getBlockState(pos.add(1, -2, 1)).getBlock() == BlocksTC.pillarAncient &&
				world.getBlockState(pos.add(-1, -2, 1)).getBlock() == BlocksTC.pillarAncient) {
			baseTimeMultiplier -= 0.1;
			baseEssentiaTimeMultiplier -= 0.1;
			stabilityGain -= 0.1;
		}
		else if(world.getBlockState(pos.add(-1, -2, -1)).getBlock() == BlocksTC.pillarEldritch &&
				world.getBlockState(pos.add(1, -2, -1)).getBlock() == BlocksTC.pillarEldritch &&
				world.getBlockState(pos.add(1, -2, 1)).getBlock() == BlocksTC.pillarEldritch &&
				world.getBlockState(pos.add(-1, -2, 1)).getBlock() == BlocksTC.pillarEldritch) {
			baseTimeMultiplier -= 0.3;
			baseEssentiaTimeMultiplier += 0.05;
			stabilityGain += 0.2;
		}
		int[] xm = new int[] {-1, 1, 1, -1};
		int[] zm = new int[] {-1, -1, 1, 1};
		for(int a = 0; a < 4; ++a) {
			Block b = world.getBlockState(pos.add(xm[a], -3, zm[a])).getBlock();
			if(b == BlocksTC.matrixSpeed) {
				baseTimeMultiplier -= 0.1;
				baseEssentiaTimeMultiplier += 0.01;
			}
			if(b == BlocksTC.matrixCost) {
				baseTimeMultiplier += 0.1;
				baseEssentiaTimeMultiplier -= 0.02;
			}
		}
	}

	protected List<BlockPos> getEmptyPedestals() {
		return Streams.stream(BlockPos.getAllInBoxMutable(pos.add(-8, -7, -8), pos.add(8, 3, 8))).map(pos->{
			if(pos.getX() != 0 || pos.getY() != 0) {
				TileEntity tile = world.getTileEntity(pos);
				if(tile instanceof TileMarkedPedestal) {
					if(((TileMarkedPedestal)tile).getInventory().isEmpty()) {
						return pos.toImmutable();
					}
				}
			}
			return null;
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}

	protected void matchStructure() {
		boolean matches = !requiresPillars || 
				world.getBlockState(pos.add(-1, -2, -1)).getBlock() instanceof BlockPillar &&
				world.getBlockState(pos.add(1, -2, -1)).getBlock() instanceof BlockPillar &&
				world.getBlockState(pos.add(1, -2, 1)).getBlock() instanceof BlockPillar &&
				world.getBlockState(pos.add(-1, -2, 1)).getBlock() instanceof BlockPillar;
		if(matches != structureValid) {
			structureValid = matches;
			syncTile(false);
			markDirty();
		}
	}

	protected double getStabilityLoss() {
		double stabilityMod;
		if(stability > 12.5) {
			stabilityMod = 5;
		}
		else if(stability > 0) {
			stabilityMod = 6;
		}
		else if(stability > -25) {
			stabilityMod = 7;
		}
		else {
			stabilityMod = 8;
		}
		return instability/stabilityMod;
	}

	protected void instabilityEventWarp() {
		List<EntityPlayer> targets = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos).grow(10));
		if(targets != null && targets.size() > 0) {
			EntityPlayer target = targets.get(world.rand.nextInt(targets.size()));
			if(world.rand.nextFloat() < 0.25F) {
				ThaumcraftApi.internalMethods.addWarpToPlayer(target, 1, EnumWarpType.NORMAL);
			}
			else {
				ThaumcraftApi.internalMethods.addWarpToPlayer(target, 2+world.rand.nextInt(4), EnumWarpType.TEMPORARY);
			}
		}
	}

	protected void instabilityEventZap(boolean all) {
		List<EntityLivingBase> targets = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos).grow(10.0));
		if(targets != null && targets.size() > 0) {
			if(!all) {
				targets = Collections.singletonList(targets.get(world.rand.nextInt(targets.size())));
			}
			for(EntityLivingBase target : targets) {
				PacketHandler.INSTANCE.sendToAllAround(
						new PacketFXBlockArc(pos, target, 0.3F-world.rand.nextFloat()*0.1F, 0, 0.3F-world.rand.nextFloat()*0.1F),
						new TargetPoint(world.provider.getDimension(), pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 32));
				target.attackEntityFrom(DamageSource.MAGIC, 4+world.rand.nextInt(4));
			}
		}
	}

	protected void instabilityEventHarm(boolean all) {
		List<EntityLivingBase> targets = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos).grow(10));
		if(targets != null && targets.size() > 0) {
			if(!all) {
				targets = Collections.singletonList(targets.get(world.rand.nextInt(targets.size())));
			}
			for(EntityLivingBase target : targets) {
				if(world.rand.nextBoolean()) {
					target.addPotionEffect(new PotionEffect(PotionFluxTaint.instance, 120, 0, false, true));
				}
				else {
					PotionEffect pe = new PotionEffect(PotionVisExhaust.instance, 2400, 0, true, true);
					pe.getCurativeItems().clear();
					target.addPotionEffect(pe);
				}
			}
		}
	}

	protected void ejectItems() {
		int endIndex = isWorking ? 1 : 0;
		for(EnumFacing facing : EnumFacing.VALUES) {
			TileEntity tile = world.getTileEntity(pos.offset(facing));
			if(tile != null && !(tile instanceof TileUnpackager) && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite())) {
				IItemHandler itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
				for(int i = 1; i >= endIndex; --i) {
					ItemStack stack = inventory.getStackInSlot(i);
					if(stack.isEmpty()) {
						continue;
					}
					ItemStack stackRem = ItemHandlerHelper.insertItem(itemHandler, stack, false);
					inventory.setInventorySlotContents(i, stackRem);
				}
			}
		}
	}

	protected void chargeEnergy() {
		ItemStack energyStack = inventory.getStackInSlot(2);
		if(energyStack.hasCapability(CapabilityEnergy.ENERGY, null)) {
			int energyRequest = Math.min(energyStorage.getMaxReceive(), energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored());
			energyStorage.receiveEnergy(energyStack.getCapability(CapabilityEnergy.ENERGY, null).extractEnergy(energyRequest, false), false);
			if(energyStack.getCount() <= 0) {
				inventory.setInventorySlotContents(2, ItemStack.EMPTY);
			}
		}
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

	@Override
	public String[] getIGogglesText() {
		String stabilityKey = "stability.";
		if(stability > 12.5) {
			stabilityKey += "VERY_STABLE";
		}
		else if(stability > 0) {
			stabilityKey += "STABLE";
		}
		else if(stability > -25) {
			stabilityKey += "UNSTABLE";
		}
		else {
			stabilityKey += "VERY_UNSTABLE";
		}
		double loss = getStabilityLoss();
		return isWorking ? new String[] {
				"§l"+I18n.translateToLocal(stabilityKey),
				"§6§o"+STABILITY_FORMATTER.format(stabilityGain)+" "+I18n.translateToLocal("stability.gain"),
				"§c"+I18n.translateToLocal("stability.range")+"§o"+STABILITY_FORMATTER.format(loss)+" "+I18n.translateToLocal("stability.loss"),
		} : new String[] {
				"§l"+I18n.translateToLocal(stabilityKey),
				"§6§o"+STABILITY_FORMATTER.format(stabilityGain)+" "+I18n.translateToLocal("stability.gain"),
		};
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

	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos.add(0, -1, 0), pos.add(1, 1, 1));
	}

	public HostHelperTileInfusionCrafter hostHelper;

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
		remainingProgress = nbt.getInteger("Progress");
		energyReq = nbt.getInteger("EnergyReq");
		stability = nbt.getDouble("Stability");
		currentRecipe = null;
		if(nbt.hasKey("Recipe")) {
			NBTTagCompound tag = nbt.getCompoundTag("Recipe");
			IRecipeInfo recipe = MiscUtil.readRecipeFromNBT(tag);
			if(recipe instanceof IRecipeInfoInfusion) {
				currentRecipe = (IRecipeInfoInfusion)recipe;
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
		nbt.setInteger("Progress", remainingProgress);
		nbt.setInteger("EnergyReq", energyReq);
		nbt.setDouble("Stability", stability);
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
		isWorking = nbt.getBoolean("Working");
		instability = nbt.getInteger("Instability");
		structureValid = nbt.getBoolean("StructureValid");
		inventory.readFromNBT(nbt);
		aspects.readFromNBT(nbt);
		pedestals.clear();
		NBTTagList pedestalsTag = nbt.getTagList("Pedestals", 11);
		for(int i = 0; i < pedestalsTag.tagCount(); ++i) {
			int[] posArray = pedestalsTag.getIntArrayAt(i);
			BlockPos pos = new BlockPos(posArray[0], posArray[1], posArray[2]);
			pedestals.add(pos);
		}
	}

	@Override
	public NBTTagCompound writeSyncNBT(NBTTagCompound nbt) {
		super.writeSyncNBT(nbt);
		nbt.setBoolean("Working", isWorking);
		nbt.setInteger("Instability", instability);
		nbt.setBoolean("StructureValid", structureValid);
		inventory.writeToNBT(nbt);
		aspects.writeToNBT(nbt);
		NBTTagList injectorsTag = new NBTTagList();
		pedestals.stream().map(pos->new int[] {pos.getX(), pos.getY(), pos.getZ()}).
		forEach(arr->injectorsTag.appendTag(new NBTTagIntArray(arr)));
		nbt.setTag("Pedestals", injectorsTag);
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
		return new GuiInfusionCrafter(new ContainerInfusionCrafter(player.inventory, this));
	}

	@Override
	public Container getServerGuiElement(EntityPlayer player, Object... args) {
		return new ContainerInfusionCrafter(player.inventory, this);
	}
}
