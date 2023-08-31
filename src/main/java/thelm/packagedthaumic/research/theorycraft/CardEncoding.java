package thelm.packagedthaumic.research.theorycraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import thaumcraft.api.items.IScribeTools;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;
import thaumcraft.common.tiles.crafting.TileResearchTable;

public class CardEncoding extends TheorycraftCard {

	@Override
	public boolean isAidOnly() {
		return true;
	}

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
		return I18n.translateToLocal("card.packagedthaumic.encoding.name");
	}

	@Override
	public String getLocalizedText() {
		return I18n.translateToLocal("card.packagedthaumic.encoding.text");
	}

	@Override
	public boolean activate(EntityPlayer player, ResearchTableData data) {
		if(data.table != null) {
			TileResearchTable table = (TileResearchTable)data.table;
			ItemStack scribeStack = table.getStackInSlot(0);
			ItemStack paperStack = table.getStackInSlot(1);
			if(scribeStack.getItem() instanceof IScribeTools &&
					scribeStack.getItemDamage() < scribeStack.getMaxDamage() &&
					paperStack.getItem() == Items.PAPER &&
					paperStack.getCount() > 0) {
				table.consumeInkFromTable();
				table.consumepaperFromTable();
				data.addTotal(getResearchCategory(), 25);
				return true;
			}
		}
		return false;
	}
}
