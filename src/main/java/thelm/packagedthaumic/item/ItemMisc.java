package thelm.packagedthaumic.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import thelm.packagedauto.client.IModelRegister;
import thelm.packagedthaumic.PackagedThaumic;

public class ItemMisc extends Item implements IModelRegister {

	public final ModelResourceLocation modelLocation;

	protected ItemMisc(String registryName, String unlocalizedName, String modelLocation, CreativeTabs creativeTab) {
		setRegistryName(registryName);
		setTranslationKey(unlocalizedName);
		this.modelLocation = new ModelResourceLocation(modelLocation);
		setCreativeTab(creativeTab);
	}

	@Override
	public void registerModels() {
		ModelLoader.setCustomModelResourceLocation(this, 0, modelLocation);
	}

	public static final ItemMisc THAUMIC_PACKAGE_COMPONENT = new ItemMisc("packagedthaumic:thaumic_package_component", "packagedthaumic.thaumic_package_component", "packagedthaumic:thaumic_package_component#inventory", PackagedThaumic.CREATIVE_TAB);
	public static final ItemMisc POROUS_STONE_PLATE = new ItemMisc("packagedthaumic:porous_stone_plate", "packagedthaumic.porous_stone_plate", "packagedthaumic:porous_stone_plate#inventory", PackagedThaumic.CREATIVE_TAB);
}