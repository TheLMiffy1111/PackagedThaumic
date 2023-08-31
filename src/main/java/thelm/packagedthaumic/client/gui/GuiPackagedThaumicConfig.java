package thelm.packagedthaumic.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import thelm.packagedthaumic.config.PackagedThaumicConfig;

public class GuiPackagedThaumicConfig extends GuiConfig {

	public GuiPackagedThaumicConfig(GuiScreen parent) {
		super(parent, getConfigElements(), "packagedthaumic", false, false, getAbridgedConfigPath(PackagedThaumicConfig.config.toString()));
	}

	private static List<IConfigElement> getConfigElements() {
		ArrayList<IConfigElement> list = new ArrayList<>();
		for(String category : PackagedThaumicConfig.config.getCategoryNames()) {
			list.add(new ConfigElement(PackagedThaumicConfig.config.getCategory(category)));
		}
		return list;
	}
}