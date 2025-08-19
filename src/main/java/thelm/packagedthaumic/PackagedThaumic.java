package thelm.packagedthaumic;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thelm.packagedthaumic.block.BlockCrucibleCrafter;
import thelm.packagedthaumic.event.CommonEventHandler;

@Mod(
		modid = PackagedThaumic.MOD_ID,
		name = PackagedThaumic.NAME,
		version = PackagedThaumic.VERSION,
		dependencies = PackagedThaumic.DEPENDENCIES,
		guiFactory = PackagedThaumic.GUI_FACTORY
		)
public class PackagedThaumic {

	public static final String MOD_ID = "packagedthaumic";
	public static final String NAME = "PackagedThaumic";
	public static final String VERSION = "1.12.2-0@VERSION@";
	public static final String DEPENDENCIES = "required-after:packagedauto@[1.12.2-1.0.13.48,);required-after:thaumcraft;";
	public static final String GUI_FACTORY = "thelm.packagedthaumic.client.gui.GuiPackagedThaumicConfigFactory";
	public static final CreativeTabs CREATIVE_TAB = new CreativeTabs("packagedthaumic") {
		@SideOnly(Side.CLIENT)
		@Override
		public ItemStack createIcon() {
			return new ItemStack(BlockCrucibleCrafter.INSTANCE);
		}
	};
	@SidedProxy(
			clientSide = "thelm.packagedthaumic.client.event.ClientEventHandler",
			serverSide = "thelm.packagedthaumic.event.CommonEventHandler",
			modId = PackagedThaumic.MOD_ID)
	public static CommonEventHandler proxy;

	@EventHandler
	public void onPreInit(FMLPreInitializationEvent event) {
		proxy.onPreInit(event);
	}

	@EventHandler
	public void onInit(FMLInitializationEvent event) {
		proxy.onInit(event);
	}
}
