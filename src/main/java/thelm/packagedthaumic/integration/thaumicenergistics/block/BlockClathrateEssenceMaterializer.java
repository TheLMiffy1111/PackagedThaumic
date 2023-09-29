package thelm.packagedthaumic.integration.thaumicenergistics.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thelm.packagedauto.block.BlockBase;
import thelm.packagedauto.tile.TileBase;
import thelm.packagedthaumic.PackagedThaumic;
import thelm.packagedthaumic.integration.thaumicenergistics.tile.TileClathrateEssenceMaterializer;

public class BlockClathrateEssenceMaterializer extends BlockBase {

	public static final BlockClathrateEssenceMaterializer INSTANCE = new BlockClathrateEssenceMaterializer();
	public static final Item ITEM_INSTANCE = new ItemBlock(INSTANCE).setRegistryName("packagedthaumic:clathrate_essence_materializer");
	public static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation("packagedthaumic:clathrate_essence_materializer#normal");

	protected BlockClathrateEssenceMaterializer() {
		super(Material.IRON);
		setHardness(15F);
		setResistance(25F);
		setSoundType(SoundType.METAL);
		setTranslationKey("packagedthaumic.clathrate_essence_materializer");
		setRegistryName("packagedthaumic:clathrate_essence_materializer");
		setCreativeTab(PackagedThaumic.CREATIVE_TAB);
	}

	@Override
	public TileBase createNewTileEntity(World worldIn, int meta) {
		return new TileClathrateEssenceMaterializer();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels() {
		ModelLoader.setCustomModelResourceLocation(ITEM_INSTANCE, 0, MODEL_LOCATION);
	}
}
