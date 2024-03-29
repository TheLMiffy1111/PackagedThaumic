package thelm.packagedthaumic.client.gui;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

public class GuiPackagedThaumicConfigFactory implements IModGuiFactory {

	@Override
	public void initialize(Minecraft minecraft) {}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return null;
	}

	@Override
	public boolean hasConfigGui() {
		return true;
	}

	@Override
	public GuiScreen createConfigGui(GuiScreen parent) {
		return new GuiPackagedThaumicConfig(parent);
	}
}
