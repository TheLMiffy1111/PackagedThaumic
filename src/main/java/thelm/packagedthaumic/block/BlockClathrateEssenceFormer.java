package thelm.packagedthaumic.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thelm.packagedauto.block.BlockBase;
import thelm.packagedauto.tile.TileBase;
import thelm.packagedthaumic.PackagedThaumic;
import thelm.packagedthaumic.tile.TileClathrateEssenceFormer;

public class BlockClathrateEssenceFormer extends BlockBase {

	public static final BlockClathrateEssenceFormer INSTANCE = new BlockClathrateEssenceFormer();
	public static final Item ITEM_INSTANCE = new ItemBlock(INSTANCE).setRegistryName("packagedthaumic:clathrate_essence_former");
	public static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation("packagedthaumic:clathrate_essence_former#normal");

	protected BlockClathrateEssenceFormer() {
		super(Material.IRON);
		setHardness(15F);
		setResistance(25F);
		setSoundType(SoundType.METAL);
		setTranslationKey("packagedthaumic.clathrate_essence_former");
		setRegistryName("packagedthaumic:clathrate_essence_former");
		setCreativeTab(PackagedThaumic.CREATIVE_TAB);
	}

	@Override
	public TileBase createNewTileEntity(World worldIn, int meta) {
		return new TileClathrateEssenceFormer();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels() {
		ModelLoader.setCustomModelResourceLocation(ITEM_INSTANCE, 0, MODEL_LOCATION);
	}
}
