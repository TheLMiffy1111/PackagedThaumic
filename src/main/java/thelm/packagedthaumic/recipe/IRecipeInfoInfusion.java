package thelm.packagedthaumic.recipe;

import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import thelm.packagedauto.api.IRecipeInfo;

public interface IRecipeInfoInfusion extends IRecipeInfo {

	ItemStack getCenterInput();

	List<ItemStack> getPedestalInputs();

	AspectList getAspects();

	ItemStack getOutput();

	String getResearch();

	int getInstability();

	InfusionRecipe getRecipe();

	@Override
	default List<ItemStack> getOutputs() {
		return Collections.singletonList(getOutput());
	}
}
