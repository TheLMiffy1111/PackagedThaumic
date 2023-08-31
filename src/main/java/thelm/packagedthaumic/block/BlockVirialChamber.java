package thelm.packagedthaumic.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thelm.packagedauto.client.IModelRegister;
import thelm.packagedthaumic.PackagedThaumic;

public class BlockVirialChamber extends Block implements IModelRegister {

	public static final BlockVirialChamber INSTANCE = new BlockVirialChamber();
	public static final Item ITEM_INSTANCE = new ItemBlock(INSTANCE).setRegistryName("packagedthaumic:virial_chamber");
	public static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation("packagedthaumic:virial_chamber#normal");

	public BlockVirialChamber() {
		super(Material.ROCK);
		setHardness(3F);
		setSoundType(SoundType.STONE);
		setTranslationKey("packagedthaumic.virial_chamber");
		setRegistryName("packagedthaumic:virial_chamber");
		setCreativeTab(PackagedThaumic.CREATIVE_TAB);
		setTickRandomly(true);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels() {
		ModelLoader.setCustomModelResourceLocation(ITEM_INSTANCE, 0, MODEL_LOCATION);
	}
}
