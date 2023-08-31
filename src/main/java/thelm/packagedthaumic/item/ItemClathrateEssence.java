package thelm.packagedthaumic.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thelm.packagedauto.client.IModelRegister;
import thelm.packagedthaumic.PackagedThaumic;

public class ItemClathrateEssence extends Item implements IEssentiaContainerItem, IModelRegister {

	public static final ItemClathrateEssence INSTANCE = new ItemClathrateEssence();
	public static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation("packagedthaumic:clathrate_essence#inventory");

	protected ItemClathrateEssence() {
		setRegistryName("packagedthaumic:clathrate_essence");
		setTranslationKey("packagedthaumic.clathrate_essence");
		setCreativeTab(PackagedThaumic.CREATIVE_TAB);
	}

	public static ItemStack makeClathrate(Aspect aspect, int amount) {
		ItemStack stack = new ItemStack(INSTANCE, amount);
		if(aspect != null) {
			INSTANCE.setAspect(stack, aspect);
		}
		return stack;
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if(isInCreativeTab(tab)) {
			for(Aspect aspect : Aspect.aspects.values()) {
				ItemStack stack = new ItemStack(this);
				setAspect(stack, aspect);
				items.add(stack);
			}
		}
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		AspectList aspects = getAspects(stack);
		if(aspects != null && !aspects.aspects.isEmpty()) {
			return String.format(super.getItemStackDisplayName(stack), aspects.getAspects()[0].getName());
		}
		return super.getItemStackDisplayName(stack);
	}

	@Override
	public AspectList getAspects(ItemStack stack) {
		if(stack.hasTagCompound()) {
			Aspect aspect = getAspect(stack);
			if(aspect == null) {
				return null;
			}
			return new AspectList().add(aspect, 1);
		}
		return null;
	}

	public Aspect getAspect(ItemStack stack) {
		if(stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();
			return Aspect.getAspect(nbt.getString("Aspect"));
		}
		return null;
	}

	@Override
	public void setAspects(ItemStack stack, AspectList aspects) {
		if(aspects == null || aspects.size() == 0) {
			return;
		}
		setAspect(stack, aspects.getAspects()[0]);
	}

	public void setAspect(ItemStack stack, Aspect aspect) {
		if(aspect == null) {
			return;
		}
		if(!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		NBTTagCompound nbt = stack.getTagCompound();
		nbt.setString("Aspect", aspect.getTag());
	}

	@Override
	public boolean ignoreContainedAspects() {
		return false;
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		if(!world.isRemote && !stack.hasTagCompound()) {
			setAspect(stack, (Aspect)Aspect.aspects.values().toArray()[world.rand.nextInt(Aspect.aspects.size())]);
		}
		super.onUpdate(stack, world, entity, slot, selected);
	}

	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) {
		if(!world.isRemote && !stack.hasTagCompound()) {
			setAspect(stack, (Aspect)Aspect.aspects.values().toArray()[world.rand.nextInt(Aspect.aspects.size())]);
		}
	}

	public int getColor(ItemStack stack, int tintIndex) {
		if(tintIndex == 0) {
			Aspect aspect = getAspect(stack);
			if(aspect != null) {
				return aspect.getColor();
			}
		}
		return 0xFFFFFF;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels() {
		ModelLoader.setCustomModelResourceLocation(this, 0, MODEL_LOCATION);
	}
}
