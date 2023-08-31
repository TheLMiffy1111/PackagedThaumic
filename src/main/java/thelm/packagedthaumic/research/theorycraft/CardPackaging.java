package thelm.packagedthaumic.research.theorycraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.translation.I18n;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

public class CardPackaging extends TheorycraftCard {

	@Override
	public int getInspirationCost() {
		return 1;
	}

	@Override
	public String getResearchCategory() {
		return "PACKAGEDTHAUMIC";
	}

	@Override
	public String getLocalizedName() {
		return I18n.translateToLocal("card.packagedthaumic.packaging.name");
	}

	@Override
	public String getLocalizedText() {
		return I18n.translateToLocal("card.packagedthaumic.packaging.text");
	}

	@Override
	public boolean activate(EntityPlayer player, ResearchTableData data) {
		data.addTotal(getResearchCategory(), player.getRNG().nextInt(11)+15);
		return true;
	}
}
