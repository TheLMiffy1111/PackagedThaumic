package thelm.packagedthaumic.research.theorycraft;

import net.minecraft.item.ItemStack;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;
import thaumcraft.api.research.theorycraft.TheorycraftCard;
import thelm.packagedauto.block.BlockEncoder;

public class AidEncoder implements ITheorycraftAid {

	public static final AidEncoder INSTANCE = new AidEncoder();
	public static final Class<TheorycraftCard>[] CARDS = new Class[] {CardEncoding.class};

	@Override
	public Object getAidObject() {
		return new ItemStack(BlockEncoder.INSTANCE);
	}

	@Override
	public Class<TheorycraftCard>[] getCards() {
		return CARDS;
	}
}
