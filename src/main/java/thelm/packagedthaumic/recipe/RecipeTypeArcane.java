package thelm.packagedthaumic.recipe;

import java.awt.Color;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntLinkedOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IRecipeLayout;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.blocks.world.ore.ShardType;
import thelm.packagedauto.api.IRecipeInfo;
import thelm.packagedauto.api.IRecipeType;

public class RecipeTypeArcane implements IRecipeType {

	public static final RecipeTypeArcane INSTANCE = new RecipeTypeArcane();
	public static final ResourceLocation NAME = new ResourceLocation("packagedthaumic:arcane");
	public static final IntSet SLOTS;
	public static final List<String> CATEGORIES = Collections.singletonList("THAUMCRAFT_ARCANE_WORKBENCH");
	public static final Color COLOR = new Color(139, 139, 139);
	public static final Color COLOR_AIR = new Color(255, 255, 163);
	public static final Color COLOR_FIRE = new Color(255, 137, 74);
	public static final Color COLOR_WATER = new Color(116, 224, 253);
	public static final Color COLOR_EARTH = new Color(134, 210, 73);
	public static final Color COLOR_ORDER = new Color(225, 224, 241);
	public static final Color COLOR_ENTROPY = new Color(119, 119, 119);
	public static final Color COLOR_DISABLED = new Color(64, 64, 64);

	static {
		SLOTS = new IntLinkedOpenHashSet();
		for(int i = 3; i < 6; ++i) {
			for(int j = 3; j < 6; ++j) {
				SLOTS.add(9*i+j);
			}
		}
		SLOTS.add(22);
		SLOTS.add(29);
		SLOTS.add(33);
		SLOTS.add(47);
		SLOTS.add(51);
		SLOTS.add(58);
	}

	@Override
	public ResourceLocation getName() {
		return NAME;
	}

	@Override
	public String getLocalizedName() {
		return I18n.translateToLocal("recipe.packagedthaumic.arcane");
	}

	@Override
	public String getLocalizedNameShort() {
		return I18n.translateToLocal("recipe.packagedthaumic.arcane.short");
	}

	@Override
	public IRecipeInfo getNewRecipeInfo() {
		return new RecipeInfoArcane();
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
		for(Map.Entry<Integer, ? extends IGuiIngredient<ItemStack>> entry : ingredients.entrySet()) {
			IGuiIngredient<ItemStack> ingredient = entry.getValue();
			if(ingredient.isInput()) {
				if(index >= 9) {
					continue;
				}
				ItemStack displayed = entry.getValue().getDisplayedIngredient();
				if(displayed != null && !displayed.isEmpty()) {
					map.put(slotArray[index], displayed);
				}
				++index;
			}
			else {
				ItemStack displayed = entry.getValue().getDisplayedIngredient();
				if(displayed != null && !displayed.isEmpty() && displayed.getItem() instanceof IEssentiaContainerItem) {
					AspectList aspects = ((IEssentiaContainerItem)displayed.getItem()).getAspects(displayed);
					if(aspects != null && aspects.visSize() > 0) {
						Aspect aspect = aspects.getAspects()[0];
						int type = ShardType.getMetaByAspect(aspect);
						if(type >= 0 && type < 6) {
							map.put(slotArray[9+type], displayed);
						}
					}
				}
			}
		}
		return map;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Object getRepresentation() {
		return new ItemStack(BlocksTC.arcaneWorkbench);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Color getSlotColor(int slot) {
		if(!SLOTS.contains(slot) && slot != 85) {
			return COLOR_DISABLED;
		}
		switch(slot) {
		case 22: return COLOR_AIR;
		case 29: return COLOR_FIRE;
		case 33: return COLOR_WATER;
		case 47: return COLOR_EARTH;
		case 51: return COLOR_ORDER;
		case 58: return COLOR_ENTROPY;
		}
		return COLOR;
	}
}
