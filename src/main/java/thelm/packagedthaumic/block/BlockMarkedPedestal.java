package thelm.packagedthaumic.block;

import java.util.Random;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.crafting.IInfusionStabiliserExt;
import thaumcraft.client.fx.FXDispatcher;
import thelm.packagedauto.block.BlockBase;
import thelm.packagedauto.tile.TileBase;
import thelm.packagedthaumic.PackagedThaumic;
import thelm.packagedthaumic.tile.TileMarkedPedestal;

public class BlockMarkedPedestal extends BlockBase implements IInfusionStabiliserExt {

	public static final BlockMarkedPedestal ARCANE = new BlockMarkedPedestal("packagedthaumic:marked_arcane_pedestal");
	public static final BlockMarkedPedestal ANCIENT = new BlockMarkedPedestal("packagedthaumic:marked_ancient_pedestal");
	public static final BlockMarkedPedestal ELDRITCH = new BlockMarkedPedestal("packagedthaumic:marked_eldritch_pedestal");
	public static final Item ARCANE_ITEM = new ItemBlock(ARCANE).setRegistryName("packagedthaumic:marked_arcane_pedestal");
	public static final Item ANCIENT_ITEM = new ItemBlock(ANCIENT).setRegistryName("packagedthaumic:marked_ancient_pedestal");
	public static final Item ELDRITCH_ITEM = new ItemBlock(ELDRITCH).setRegistryName("packagedthaumic:marked_eldritch_pedestal");

	public BlockMarkedPedestal(String name) {
		super(Material.ROCK);
		setHardness(15F);
		setResistance(25F);
		setSoundType(SoundType.STONE);
		setTranslationKey(name.replace(':', '.'));
		setRegistryName(name);
		setCreativeTab(PackagedThaumic.CREATIVE_TAB);
	}

	@Override
	public TileBase createNewTileEntity(World worldIn, int meta) {
		return new TileMarkedPedestal();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return false;
	}

	@Override
	public boolean canStabaliseInfusion(World world, BlockPos pos) {
		return true;
	}

	@Override
	public float getStabilizationAmount(World world, BlockPos pos) {
		return world.getBlockState(pos).getBlock() == ELDRITCH ? 0.1F : 0;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		FXDispatcher.INSTANCE.blockRunes2(pos.getX(), pos.getY()-0.375, pos.getZ(), 1, 0, 0, 10, 0);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "normal"));
	}
}
