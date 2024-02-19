package thelm.packagedthaumic.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.common.blocks.world.ore.ShardType;
import thaumcraft.common.container.InventoryArcaneWorkbench;
import thaumcraft.common.items.resources.ItemCrystalEssence;
import thelm.packagedauto.api.IPackagePattern;
import thelm.packagedauto.api.IRecipeType;
import thelm.packagedauto.api.MiscUtil;
import thelm.packagedauto.container.ContainerEmpty;
import thelm.packagedauto.util.PatternHelper;

public class RecipeInfoArcane implements IRecipeInfoArcane {

	IArcaneRecipe recipe;
	List<ItemStack> input = new ArrayList<>();
	InventoryCrafting matrix = new InventoryArcaneWorkbench(null, new ContainerEmpty());
	ItemStack output;
	List<IPackagePattern> patterns = new ArrayList<>();

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		input.clear();
		output = ItemStack.EMPTY;
		patterns.clear();
		List<ItemStack> matrixList = new ArrayList<>();
		MiscUtil.loadAllItems(nbt.getTagList("Matrix", 10), matrixList);
		for(int i = 0; i < 15 && i < matrixList.size(); ++i) {
			matrix.setInventorySlotContents(i, matrixList.get(i));
		}
		IRecipe rec = CraftingManager.getRecipe(new ResourceLocation(nbt.getString("Recipe")));
		if(rec instanceof IArcaneRecipe) {
			recipe = (IArcaneRecipe)rec;
		}
		if(recipe != null) {
			input.addAll(MiscUtil.condenseStacks(matrix));
			output = recipe.getCraftingResult(matrix).copy();
			for(int i = 0; i*9 < input.size(); ++i) {
				patterns.add(new PatternHelper(this, i));
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		if(recipe != null) {
			nbt.setString("Recipe", recipe.getRegistryName().toString());
		}
		List<ItemStack> matrixList = new ArrayList<>();
		for(int i = 0; i < 15; ++i) {
			matrixList.add(matrix.getStackInSlot(i));
		}
		NBTTagList matrixTag = MiscUtil.saveAllItems(new NBTTagList(), matrixList);
		nbt.setTag("Matrix", matrixTag);
		return nbt;
	}

	@Override
	public IRecipeType getRecipeType() {
		return RecipeTypeArcane.INSTANCE;
	}

	@Override
	public boolean isValid() {
		return recipe != null;
	}

	@Override
	public List<IPackagePattern> getPatterns() {
		return Collections.unmodifiableList(patterns);
	}

	@Override
	public AspectList getCrystals() {
		AspectList aspects = recipe.getCrystals();
		return aspects != null ? aspects.copy() : new AspectList();
	}

	@Override
	public List<ItemStack> getInputs() {
		return Collections.unmodifiableList(input);
	}

	@Override
	public ItemStack getOutput() {
		return output.copy();
	}

	@Override
	public String getResearch() {
		return recipe.getResearch();
	}

	@Override
	public int getVis() {
		return recipe.getVis();
	}

	@Override
	public IArcaneRecipe getRecipe() {
		return recipe;
	}

	@Override
	public InventoryCrafting getMatrix() {
		return matrix;
	}

	@Override
	public void generateFromStacks(List<ItemStack> input, List<ItemStack> output, World world) {
		recipe = null;
		this.input.clear();
		patterns.clear();
		if(world != null) {
			int[] slotArray = RecipeTypeArcane.SLOTS.toIntArray();
			for(int i = 0; i < 9; ++i) {
				ItemStack toSet = input.get(slotArray[i]);
				toSet.setCount(1);
				matrix.setInventorySlotContents(i, toSet.copy());
			}
			for(int i = 9; i < 15; ++i) {
				ItemStack toSet = input.get(slotArray[i]);
				if(toSet.getItem() instanceof ItemCrystalEssence) {
					AspectList aspects = ((ItemCrystalEssence)toSet.getItem()).getAspects(toSet);
					if(aspects != null) {
						Aspect aspect = aspects.getAspects()[0];
						if(ShardType.getMetaByAspect(aspect) == i-9) {
							matrix.setInventorySlotContents(i, toSet.copy());
							continue;
						}
					}
				}
				input.set(slotArray[i], ItemStack.EMPTY);
			}
			for(IRecipe recipe : (Iterable<IRecipe>)CraftingManager.REGISTRY)  {
				if(recipe instanceof IArcaneRecipe && recipe.matches(matrix, world)) {
					this.recipe = (IArcaneRecipe)recipe;
					AspectList aspects = this.recipe.getCrystals();
					if(aspects == null) {
						aspects = new AspectList();
					}
					for(int i = 9; i < 15; ++i) {
						Aspect aspect = ShardType.byMetadata(i-9).getAspect();
						int amount = aspects.getAmount(aspect);
						ItemStack crystal = amount > 0 ? ThaumcraftApiHelper.makeCrystal(aspect, amount) : ItemStack.EMPTY;
						matrix.setInventorySlotContents(i, crystal);
						input.set(slotArray[i], crystal.copy());
					}
					this.input.addAll(MiscUtil.condenseStacks(matrix));
					this.output = recipe.getCraftingResult(matrix).copy();
					for(int i = 0; i*9 < this.input.size(); ++i) {
						patterns.add(new PatternHelper(this, i));
					}
					return;
				}
			}
		}
		matrix.clear();
	}

	@Override
	public Int2ObjectMap<ItemStack> getEncoderStacks() {
		Int2ObjectMap<ItemStack> map = new Int2ObjectOpenHashMap<>();
		int[] slotArray = RecipeTypeArcane.SLOTS.toIntArray();
		for(int i = 0; i < 15; ++i) {
			map.put(slotArray[i], matrix.getStackInSlot(i));
		}
		return map;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof RecipeInfoArcane) {
			RecipeInfoArcane other = (RecipeInfoArcane)obj;
			for(int i = 0; i < input.size(); ++i) {
				if(!ItemStack.areItemStacksEqualUsingNBTShareTag(input.get(i), other.input.get(i))) {
					return false;
				}
			}
			return recipe.equals(other.recipe);
		}
		return false;
	}

	@Override
	public int hashCode() {
		Object[] toHash = new Object[2];
		Object[] inputArray = new Object[input.size()];
		for(int i = 0; i < input.size(); ++i) {
			ItemStack stack = input.get(i);
			inputArray[i] = new Object[] {stack.getItem(), stack.getItemDamage(), stack.getCount(), stack.getTagCompound()};
		}
		toHash[0] = recipe;
		toHash[1] = inputArray;
		return Arrays.deepHashCode(toHash);
	}
}
