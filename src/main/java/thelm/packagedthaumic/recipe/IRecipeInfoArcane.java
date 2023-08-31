package thelm.packagedthaumic.recipe;

import java.util.Collections;
import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thelm.packagedauto.api.IRecipeInfo;

public interface IRecipeInfoArcane extends IRecipeInfo {

	AspectList getCrystals();

	ItemStack getOutput();

	String getResearch();

	int getVis();

	IArcaneRecipe getRecipe();

	InventoryCrafting getMatrix();

	@Override
	default List<ItemStack> getOutputs() {
		return Collections.singletonList(getOutput());
	}
}
