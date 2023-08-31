package thelm.packagedthaumic.research;

import java.util.Objects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.research.IScanThing;
import thaumcraft.api.research.ScanningManager;

public class ScanMod implements IScanThing {

	public final String research;
	public final String modId;

	public ScanMod(String research, String modId) {
		this.research = Objects.requireNonNull(research);
		this.modId = Objects.requireNonNull(modId);
	}

	@Override
	public boolean checkThing(EntityPlayer player, Object obj) {
		if(obj instanceof Entity && !(obj instanceof EntityItem)) {
			ResourceLocation key = EntityList.getKey((Entity)obj);
			if(key != null) {
				return key.getNamespace().equalsIgnoreCase(modId);
			}
		}
		ItemStack stack = ScanningManager.getItemFromParms(player, obj);
		if(!stack.isEmpty()) {
			return stack.getItem().getRegistryName().getNamespace().equalsIgnoreCase(modId);
		}
		return false;
	}

	@Override
	public String getResearchKey(EntityPlayer player, Object obj) {
		return research;
	}
}
