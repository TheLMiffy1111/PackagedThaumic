package thelm.packagedthaumic.recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IThaumcraftRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thelm.packagedauto.api.IPackagePattern;
import thelm.packagedauto.api.IRecipeType;
import thelm.packagedauto.api.MiscUtil;
import thelm.packagedauto.util.PatternHelper;
import thelm.packagedthaumic.util.ThaumcraftHelper;

public class RecipeInfoInfusion implements IRecipeInfoInfusion {

	InfusionRecipe recipe;
	ItemStack inputCenter = ItemStack.EMPTY;
	ArrayList<ItemStack> inputPedestal = new ArrayList<>();
	AspectList aspects = new AspectList();
	List<ItemStack> input = new ArrayList<>();
	ItemStack output;
	int instability;
	List<IPackagePattern> patterns = new ArrayList<>();

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		input.clear();
		output = ItemStack.EMPTY;
		patterns.clear();
		IThaumcraftRecipe recipe = ThaumcraftApi.getCraftingRecipes().get(new ResourceLocation(nbt.getString("Recipe")));
		inputCenter = new ItemStack(nbt.getCompoundTag("InputCenter"));
		MiscUtil.loadAllItems(nbt.getTagList("InputPedestal", 10), inputPedestal);
		aspects.readFromNBT(nbt, "Aspects");
		output = new ItemStack(nbt.getCompoundTag("Output"));
		instability = nbt.getInteger("Instability");
		if(recipe instanceof InfusionRecipe) {
			this.recipe = (InfusionRecipe)recipe;
			List<ItemStack> toCondense = new ArrayList<>(inputPedestal);
			toCondense.add(inputCenter);
			toCondense.addAll(ThaumcraftHelper.INSTANCE.makeClathrates(aspects));
			input.addAll(MiscUtil.condenseStacks(toCondense));
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
		NBTTagCompound inputCenterTag = inputCenter.writeToNBT(new NBTTagCompound());
		NBTTagList inputPedestalTag = MiscUtil.saveAllItems(new NBTTagList(), inputPedestal);
		nbt.setTag("InputCenter", inputCenterTag);
		nbt.setTag("InputPedestal", inputPedestalTag);
		aspects.writeToNBT(nbt, "Aspects");
		nbt.setTag("Output", output.writeToNBT(new NBTTagCompound()));
		nbt.setInteger("Instability", instability);
		return nbt;
	}

	@Override
	public IRecipeType getRecipeType() {
		return RecipeTypeInfusion.INSTANCE;
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
	public ItemStack getCenterInput() {
		return inputCenter.copy();
	}

	@Override
	public List<ItemStack> getPedestalInputs() {
		return Collections.unmodifiableList(inputPedestal);
	}

	@Override
	public AspectList getAspects() {
		return aspects.copy();
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
	public int getInstability() {
		return instability;
	}

	@Override
	public InfusionRecipe getRecipe() {
		return recipe;
	}

	@Override
	public void generateFromStacks(List<ItemStack> input, List<ItemStack> output, World world) {
		recipe = null;
		inputCenter = ItemStack.EMPTY;
		inputPedestal.clear();
		aspects.aspects.clear();
		this.input.clear();
		patterns.clear();
		if(world != null) {
			int[] slotArray = RecipeTypeInfusion.SLOTS.toIntArray();
			ArrayUtils.shift(slotArray, 0, 19, 1);
			for(int i = 0; i < 37; ++i) {
				ItemStack toSet = input.get(slotArray[i]);
				if(!toSet.isEmpty()) {
					toSet.setCount(1);
					if(i == 0) {
						inputCenter = toSet.copy();
					}
					else {
						inputPedestal.add(toSet.copy());
					}
				}
				else if(i == 0) {
					return;
				}
			}
			EntityPlayer fakePlayer = ThaumcraftHelper.INSTANCE.getResearchFakePlayer(world);
			InfusionRecipe recipe = ThaumcraftCraftingManager.findMatchingInfusionRecipe(inputPedestal, inputCenter, fakePlayer);
			if(recipe != null) {
				this.recipe = recipe;
				this.aspects = recipe.getAspects(fakePlayer, inputCenter, inputPedestal);
				if(this.aspects == null) {
					this.aspects = new AspectList();
				}
				Object outputObj = recipe.getRecipeOutput(fakePlayer, inputCenter, inputPedestal);
				this.instability = recipe.getInstability(fakePlayer, inputCenter, inputPedestal);
				this.output = getOutput(inputCenter, outputObj);
				List<ItemStack> crystals = ThaumcraftHelper.INSTANCE.makeClathrates(aspects);
				for(int i = 0; i < 81; ++i) {
					if(RecipeTypeInfusion.SLOTS.contains(i)) {
						continue;
					}
					input.set(i, ItemStack.EMPTY);
				}
				int slot = 0;
				for(ItemStack crystal : crystals) {
					input.set(slot, crystal);
					++slot;
					while(RecipeTypeInfusion.SLOTS.contains(slot)) {
						++slot;
					}
					if(slot >= 81) {
						break;
					}
				}
				List<ItemStack> toCondense = new ArrayList<>(inputPedestal);
				toCondense.add(inputCenter);
				toCondense.addAll(crystals);
				this.input.addAll(MiscUtil.condenseStacks(toCondense));
				for(int i = 0; i*9 < this.input.size(); ++i) {
					patterns.add(new PatternHelper(this, i));
				}
				return;
			}
		}
	}

	protected static ItemStack getOutput(ItemStack input, Object output) {
		if(output instanceof ItemStack) {
			ItemStack out = ((ItemStack)output).copy();
			if(input.isItemStackDamageable() && input.isItemDamaged() &&
					out.isItemStackDamageable() && !out.isItemDamaged()) {
				out.setItemDamage(out.getMaxDamage()*input.getItemDamage()/input.getMaxDamage());
			}
			return out;
		}
		if(output instanceof Object[]) {
			Object[] arr = (Object[])output;
			String key = arr[0].toString();
			NBTBase nbt = (NBTBase)arr[1];
			ItemStack out = input.copy();
			out.setTagInfo(key, nbt);
			return out;
		}
		if(output instanceof Enchantment) {
			Enchantment ench = (Enchantment)output;
			ItemStack out = input.copy();
			Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(out);
			map.put(ench, map.getOrDefault(ench, 0)+1);
			EnchantmentHelper.setEnchantments(map, out);
			return out;
		}
		return input;
	}

	@Override
	public Int2ObjectMap<ItemStack> getEncoderStacks() {
		Int2ObjectMap<ItemStack> map = new Int2ObjectOpenHashMap<>();
		int[] slotArray = RecipeTypeInfusion.SLOTS.toIntArray();
		ArrayUtils.remove(slotArray, 18);
		map.put(40, inputCenter);
		for(int i = 0; i < inputPedestal.size(); ++i) {
			map.put(slotArray[i], inputPedestal.get(i));
		}
		return map;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof RecipeInfoInfusion) {
			RecipeInfoInfusion other = (RecipeInfoInfusion)obj;
			return MiscUtil.recipeEquals(this, recipe, other, other.recipe);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return MiscUtil.recipeHashCode(this, recipe);
	}
}
