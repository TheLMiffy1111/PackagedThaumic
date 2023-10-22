package thelm.packagedthaumic.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.IThaumcraftRecipe;
import thelm.packagedauto.api.IPackagePattern;
import thelm.packagedauto.api.IRecipeType;
import thelm.packagedauto.api.MiscUtil;
import thelm.packagedauto.util.PatternHelper;
import thelm.packagedthaumic.util.ThaumcraftHelper;

public class RecipeInfoCrucible implements IRecipeInfoCrucible {

	CrucibleRecipe recipe;
	ItemStack inputCatalyst = ItemStack.EMPTY;
	List<ItemStack> input = new ArrayList<>();
	ItemStack output;
	List<IPackagePattern> patterns = new ArrayList<>();

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		input.clear();
		output = ItemStack.EMPTY;
		patterns.clear();
		IThaumcraftRecipe recipe = ThaumcraftApi.getCraftingRecipes().get(new ResourceLocation(nbt.getString("Recipe")));
		inputCatalyst = new ItemStack(nbt.getCompoundTag("InputCatalyst"));
		if(recipe instanceof CrucibleRecipe) {
			this.recipe = (CrucibleRecipe)recipe;
			List<ItemStack> toCondense = new ArrayList<>(ThaumcraftHelper.INSTANCE.makeClathrates(this.recipe.getAspects()));
			toCondense.add(inputCatalyst);
			input.addAll(MiscUtil.condenseStacks(toCondense));
			output = this.recipe.getRecipeOutput();
			for(int i = 0; i*9 < input.size(); ++i) {
				patterns.add(new PatternHelper(this, i));
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		if(recipe != null) {
			nbt.setString("Recipe", ThaumcraftHelper.INSTANCE.getRecipeKey(recipe).toString());
		}
		NBTTagCompound inputCatalystTag = inputCatalyst.writeToNBT(new NBTTagCompound());
		nbt.setTag("InputCatalyst", inputCatalystTag);
		return nbt;
	}

	@Override
	public IRecipeType getRecipeType() {
		return RecipeTypeCrucible.INSTANCE;
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
	public ItemStack getCatalystInput() {
		return inputCatalyst.copy();
	}

	@Override
	public AspectList getAspects() {
		AspectList aspects = recipe.getAspects();
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
	public CrucibleRecipe getRecipe() {
		return recipe;
	}

	@Override
	public void generateFromStacks(List<ItemStack> input, List<ItemStack> output, World world) {
		recipe = null;
		inputCatalyst = ItemStack.EMPTY;
		this.input.clear();
		patterns.clear();
		AspectList aspects = new AspectList();
		int[] slotArray = RecipeTypeCrucible.SLOTS.toIntArray();
		ArrayUtils.shift(slotArray, 0, 41, 1);
		for(int i = 0; i < 81; ++i) {
			ItemStack toSet = input.get(slotArray[i]);
			if(!toSet.isEmpty()) {
				if(i == 0) {
					toSet.setCount(1);
					inputCatalyst = toSet.copy();
				}
				else if(toSet.getItem() instanceof IEssentiaContainerItem) {
					AspectList toAdd = ((IEssentiaContainerItem)toSet.getItem()).getAspects(toSet);
					if(toAdd != null) {
						for(Map.Entry<Aspect, Integer> entry : toAdd.aspects.entrySet()) {
							Aspect aspect = entry.getKey();
							int amount = entry.getValue();
							if(aspect != null) {
								aspects.add(aspect, amount*toSet.getCount());
							}
						}
					}
					else {
						input.set(slotArray[i], ItemStack.EMPTY);
					}
				}
				else {
					input.set(slotArray[i], ItemStack.EMPTY);
				}
			}
		}
		CrucibleRecipe recipe = null;
		int highest = 0;
		for(IThaumcraftRecipe rec : ThaumcraftApi.getCraftingRecipes().values()) {
			if(rec != null && rec instanceof CrucibleRecipe) {
				CrucibleRecipe cRec = (CrucibleRecipe)rec;
				if(cRec.getAspects() != null && cRec.matches(aspects, inputCatalyst)) {
					int result = cRec.getAspects().visSize();
					if(result > highest) {
						highest = result;
						recipe = cRec;
					}
				}
			}
		}
		if(recipe != null) {
			this.recipe = recipe;
			this.output = recipe.getRecipeOutput();
			List<ItemStack> crystals = ThaumcraftHelper.INSTANCE.makeClathrates(recipe.getAspects());
			for(int i = 0; i < 81; ++i) {
				if(i == 40) {
					continue;
				}
				input.set(i, ItemStack.EMPTY);
			}
			int slot = 0;
			for(ItemStack crystal : crystals) {
				input.set(slot, crystal);
				++slot;
				if(slot == 40) {
					++slot;
				}
				if(slot >= 81) {
					break;
				}
			}
			List<ItemStack> toCondense = new ArrayList<>(crystals);
			toCondense.add(inputCatalyst);
			this.input.addAll(MiscUtil.condenseStacks(toCondense));
			for(int i = 0; i*9 < this.input.size(); ++i) {
				patterns.add(new PatternHelper(this, i));
			}
			return;
		}
	}

	@Override
	public Int2ObjectMap<ItemStack> getEncoderStacks() {
		Int2ObjectMap<ItemStack> map = new Int2ObjectOpenHashMap<>();
		int[] slotArray = RecipeTypeCrucible.SLOTS.toIntArray();
		ArrayUtils.remove(slotArray, 40);
		map.put(40, inputCatalyst);
		List<ItemStack> crystals = ThaumcraftHelper.INSTANCE.makeClathrates(recipe.getAspects());
		for(int i = 0; i < crystals.size(); ++i) {
			map.put(slotArray[i], crystals.get(i));
		}
		return map;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof RecipeInfoCrucible) {
			RecipeInfoCrucible other = (RecipeInfoCrucible)obj;
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
