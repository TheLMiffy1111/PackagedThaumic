package thelm.packagedthaumic.recipe;

import java.awt.Color;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.apache.commons.lang3.ArrayUtils;

import com.buuz135.thaumicjei.ThaumcraftJEIPlugin;

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
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thelm.packagedauto.api.IRecipeInfo;
import thelm.packagedauto.api.IRecipeType;
import thelm.packagedthaumic.util.ThaumcraftHelper;

public class RecipeTypeCrucible implements IRecipeType {

	public static final RecipeTypeCrucible INSTANCE = new RecipeTypeCrucible();
	public static final ResourceLocation NAME = new ResourceLocation("packagedthaumic:crucible");
	public static final IntSet SLOTS;
	public static final List<String> CATEGORIES = Collections.singletonList("THAUMCRAFT_CRUCIBLE");
	public static final Color COLOR = new Color(139, 139, 139);
	public static final Color COLOR_CRYSTAL = new Color(139, 159, 179);
	public static final Color COLOR_DISABLED = new Color(64, 64, 64);
	
	static {
		SLOTS = new IntRBTreeSet();
		IntStream.range(0, 81).forEachOrdered(SLOTS::add);
	}
	
	@Override
	public ResourceLocation getName() {
		return NAME;
	}

	@Override
	public String getLocalizedName() {
		return I18n.translateToLocal("recipe.packagedthaumic.crucible");
	}

	@Override
	public String getLocalizedNameShort() {
		return I18n.translateToLocal("recipe.packagedthaumic.crucible.short");
	}
	
	@Override
	public IRecipeInfo getNewRecipeInfo() {
		return new RecipeInfoCrucible();
	}

	@Override
	public IntSet getEnabledSlots() {
		return SLOTS;
	}

	@Override
	public boolean canSetOutput() {
		return false;
	}

	@Override
	public boolean hasMachine() {
		return true;
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
		Map<Integer, ? extends IGuiIngredient<AspectList>> aspectIngs = recipeLayout.getIngredientsGroup(ThaumcraftJEIPlugin.ASPECT_LIST).getGuiIngredients();
		int index = 0;
		int[] slotArray = SLOTS.toIntArray();
		ArrayUtils.shift(slotArray, 0, 41, 1);
		for(Map.Entry<Integer, ? extends IGuiIngredient<ItemStack>> entry : ingredients.entrySet()) {
			IGuiIngredient<ItemStack> ingredient = entry.getValue();
			if(ingredient.isInput()) {
				ItemStack displayed = entry.getValue().getDisplayedIngredient();
				if(displayed != null && !displayed.isEmpty()) {
					map.put(slotArray[index], displayed);
				}
				++index;
			}
			if(index >= 1) {
				break;
			}
		}
		for(Map.Entry<Integer, ? extends IGuiIngredient<AspectList>> entry : aspectIngs.entrySet()) {
			IGuiIngredient<AspectList> ingredient = entry.getValue();
			if(ingredient.isInput()) {
				AspectList displayed = entry.getValue().getDisplayedIngredient();
				if(displayed != null && displayed.visSize() > 0) {
					List<ItemStack> crystals = ThaumcraftHelper.INSTANCE.makeClathrates(displayed);
					for(ItemStack crystal : crystals) {
						map.put(slotArray[index], crystal);
						++index;
						if(index >= 81) {
							break;
						}
					}
				}
			}
			if(index >= 81) {
				break;
			}
		}
		return map;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public Object getRepresentation() {
		return new ItemStack(BlocksTC.crucible);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Color getSlotColor(int slot) {
		if(slot < 81 && slot != 40) {
			return COLOR_CRYSTAL;
		}
		if(slot >= 81 && slot != 85) {
			return COLOR_DISABLED;
		}
		return COLOR;
	}
}
