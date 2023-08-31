package thelm.packagedthaumic.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thelm.packagedauto.block.BlockBase;
import thelm.packagedauto.tile.TileBase;
import thelm.packagedthaumic.PackagedThaumic;
import thelm.packagedthaumic.tile.TileCrucibleCrafter;

public class BlockCrucibleCrafter extends BlockBase {

	public static final BlockCrucibleCrafter INSTANCE = new BlockCrucibleCrafter();
	public static final Item ITEM_INSTANCE = new ItemBlock(INSTANCE).setRegistryName("packagedthaumic:crucible_crafter");
	public static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation("packagedthaumic:crucible_crafter#normal");

	protected BlockCrucibleCrafter() {
		super(Material.IRON);
		setHardness(15F);
		setResistance(25F);
		setSoundType(SoundType.METAL);
		setTranslationKey("packagedthaumic.crucible_crafter");
		setRegistryName("packagedthaumic:crucible_crafter");
		setCreativeTab(PackagedThaumic.CREATIVE_TAB);
	}

	@Override
	public TileBase createNewTileEntity(World worldIn, int meta) {
		return new TileCrucibleCrafter();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileEntity tileentity = world.getTileEntity(pos);
		if(tileentity instanceof TileCrucibleCrafter) {
			TileCrucibleCrafter crafter = (TileCrucibleCrafter)tileentity;
			if(!world.isRemote) {
				crafter.updateKnowledge(player);
			}
			if(player.isSneaking()) {
				if(!world.isRemote) {
					ITextComponent message = crafter.getMessage();
					if(message != null) {
						player.sendMessage(message);
					}
				}
				return true;
			}
		}
		return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity tileentity = world.getTileEntity(pos);
		if(tileentity instanceof TileCrucibleCrafter) {
			((TileCrucibleCrafter)tileentity).onBreak();
		}
		super.breakBlock(world, pos, state);
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels() {
		ModelLoader.setCustomModelResourceLocation(ITEM_INSTANCE, 0, MODEL_LOCATION);
	}
}
