package thelm.packagedthaumic.recipe;

import java.awt.Color;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IRecipeLayout;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.blocks.BlocksTC;
import thelm.packagedauto.api.IRecipeInfo;
import thelm.packagedauto.api.IRecipeType;

public class RecipeTypeInfusion implements IRecipeType {

	public static final RecipeTypeInfusion INSTANCE = new RecipeTypeInfusion();
	public static final ResourceLocation NAME = new ResourceLocation("packagedthaumic:infusion");
	public static final IntSet SLOTS;
	public static final List<String> CATEGORIES = Collections.singletonList("THAUMCRAFT_INFUSION");
	public static final Color COLOR = new Color(139, 139, 139);
	public static final Color COLOR_CENTER = new Color(159, 139, 179);
	public static final Color COLOR_CRYSTAL = new Color(139, 159, 179);
	public static final Color COLOR_DISABLED = new Color(64, 64, 64);
	
	static {
		SLOTS = new IntRBTreeSet();
		for(int i = 2; i < 7; ++i) {
			for(int j = 2; j < 7; ++j) {
				SLOTS.add(9*i+j);
			}
		}
		for(int i = 3; i < 6; ++i) {
			SLOTS.add(9+i);
			SLOTS.add(63+i);
			SLOTS.add(9*i+1);
			SLOTS.add(9*i+7);
		}
	}
	
	@Override
	public ResourceLocation getName() {
		return NAME;
	}

	@Override
	public String getLocalizedName() {
		return I18n.translateToLocal("recipe.packagedthaumic.infusion");
	}

	@Override
	public String getLocalizedNameShort() {
		return I18n.translateToLocal("recipe.packagedthaumic.infusion.short");
	}
	
	@Override
	public IRecipeInfo getNewRecipeInfo() {
		return new RecipeInfoInfusion();
	}

	@Override
	public IntSet getEnabledSlots() {
		return SLOTS;
	}
	
	@Override
	public List<String> getJEICategories() {
		return CATEGORIES;
	}
	
	@Optional.Method(modid="jei")
	@Override
	public Int2ObjectMap<ItemStack> getRecipeTransferMap(IRecipeLayout recipeLayout, String category) {
		Int2ObjectMap<ItemStack> map = new Int2ObjectOpenHashMap<>();
		Map<Integer, ? extends IGuiIngredient<ItemStack>> ingredients = recipeLayout.getItemStacks().getGuiIngredients();
		int index = 0;
		int[] slotArray = SLOTS.toIntArray();
		ArrayUtils.shift(slotArray, 0, 19, 1);
		for(Map.Entry<Integer, ? extends IGuiIngredient<ItemStack>> entry : ingredients.entrySet()) {
			IGuiIngredient<ItemStack> ingredient = entry.getValue();
			if(ingredient.isInput()) {
				ItemStack displayed = entry.getValue().getDisplayedIngredient();
				if(displayed != null && !displayed.isEmpty()) {
					map.put(slotArray[index], displayed);
				}
				++index;
			}
			if(index >= 37) {
				break;
			}
		}
		return map;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public Object getRepresentation() {
		return new ItemStack(BlocksTC.infusionMatrix);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Color getSlotColor(int slot) {
		if(!SLOTS.contains(slot) && slot < 81) {
			return COLOR_CRYSTAL;
		}
		if(slot >= 81 && slot != 85) {
			return COLOR_DISABLED;
		}
		if(slot == 40) {
			return COLOR_CENTER;
		}
		return COLOR;
	}
}
