package thelm.packagedthaumic.recipe;

import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thelm.packagedauto.api.IRecipeInfo;

public interface IRecipeInfoCrucible extends IRecipeInfo {

	ItemStack getCatalystInput();
	
	AspectList getAspects();

	ItemStack getOutput();

	String getResearch();

	CrucibleRecipe getRecipe();

	@Override
	default List<ItemStack> getOutputs() {
		return Collections.singletonList(getOutput());
	}
}
