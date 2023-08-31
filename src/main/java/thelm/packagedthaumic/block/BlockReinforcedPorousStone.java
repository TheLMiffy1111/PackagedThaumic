package thelm.packagedthaumic.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.world.aura.AuraHandler;
import thelm.packagedauto.client.IModelRegister;
import thelm.packagedthaumic.PackagedThaumic;

public class BlockReinforcedPorousStone extends Block implements IModelRegister {

	public static final BlockReinforcedPorousStone INSTANCE = new BlockReinforcedPorousStone();
	public static final Item ITEM_INSTANCE = new ItemBlock(INSTANCE).setRegistryName("packagedthaumic:reinforced_porous_stone");
	public static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation("packagedthaumic:reinforced_porous_stone#normal");

	public BlockReinforcedPorousStone() {
		super(Material.ROCK);
		setHardness(3F);
		setSoundType(SoundType.STONE);
		setTranslationKey("packagedthaumic.reinforced_porous_stone");
		setRegistryName("packagedthaumic:reinforced_porous_stone");
		setCreativeTab(PackagedThaumic.CREATIVE_TAB);
		setTickRandomly(true);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if(!worldIn.isRemote && rand.nextInt(5) == 0) {
			float drainAmount = 0.05F*rand.nextFloat();
			float fluxPart = AuraHandler.getFlux(worldIn, pos)/AuraHandler.getTotalAura(worldIn, pos);
			if(rand.nextFloat() < fluxPart) {
				AuraHandler.drainFlux(worldIn, pos, drainAmount, false);
			}
			else {
				AuraHandler.drainVis(worldIn, pos, drainAmount, false);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels() {
		ModelLoader.setCustomModelResourceLocation(ITEM_INSTANCE, 0, MODEL_LOCATION);
	}
}
