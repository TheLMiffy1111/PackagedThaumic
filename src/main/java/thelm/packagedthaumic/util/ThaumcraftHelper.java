package thelm.packagedthaumic.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang3.ArrayUtils;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.crafting.IThaumcraftRecipe;
import thaumcraft.api.research.ResearchAddendum;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.api.research.ResearchStage;
import thelm.packagedthaumic.item.ItemClathrateEssence;

public class ThaumcraftHelper {

	public static final ThaumcraftHelper INSTANCE = new ThaumcraftHelper();

	private ThaumcraftHelper() {}

	public List<ItemStack> makeClathrates(AspectList aspects) {
		return makeClathrates(aspects, false);
	}

	public List<ItemStack> makeClathrates(AspectList aspects, boolean ignoreStackSize) {
		List<ItemStack> list = new ArrayList<>();
		for(Map.Entry<Aspect, Integer> entry : aspects.aspects.entrySet()) {
			Aspect aspect = entry.getKey();
			int amount = entry.getValue();
			if(aspect != null) {
				if(ignoreStackSize) {
					list.add(ItemClathrateEssence.makeClathrate(aspect, amount));
				}
				else {
					while(amount > 0) {
						ItemStack toAdd = ItemClathrateEssence.makeClathrate(aspect, 1);
						int limit = toAdd.getItem().getItemStackLimit(toAdd);
						toAdd.setCount(Math.min(amount, limit));
						list.add(toAdd);
						amount -= limit;
					}
				}
			}
		}
		return list;
	}

	public ResourceLocation getRecipeKey(IThaumcraftRecipe recipe) {
		return ThaumcraftApi.getCraftingRecipes().entrySet().stream().
				filter(entry->entry.getValue().equals(recipe)).findAny().
				map(Map.Entry::getKey).get();
	}

	public boolean knowsResearch(IPlayerKnowledge knowledge, String... research) {
		for(String r : research) {
			if(r.contains("&&")) {
				String[] rr = r.split("&&");
				if(!knowsResearch(knowledge, rr)) {
					return false;
				}
			}
			else if(r.contains("||")) {
				String[] rr = r.split("||");
				for(String str : rr) {
					if(knowsResearch(knowledge, str)) {
						return true;
					}
				}
			}
			else if(!knowledge.isResearchKnown(r)) {
				return false;
			}
		}
		return true;
	}

	public boolean knowsResearchStrict(IPlayerKnowledge knowledge, String... research) {
		for(String r : research) {
			if(r.contains("&&")) {
				String[] rr = r.split("&&");
				if(!knowsResearchStrict(knowledge, rr)) {
					return false;
				}
			}
			else if(r.contains("||")) {
				String[] rr = r.split("||");
				for(String str : rr) {
					if(knowsResearchStrict(knowledge, str)) {
						return true;
					}
				}
			}
			else if(r.contains("@") && !knowledge.isResearchKnown(r)) {
				return false;
			}
			else if(!knowledge.isResearchComplete(r)) {
				return false;
			}
		}
		return true;
	}

	private static final GameProfile PROFILE = new GameProfile(UUID.fromString("f3d87c7e-4395-4952-88b3-7be346ca6bf4"), "[PkTh]");
	private static EntityPlayer researchPlayer;

	public EntityPlayer getResearchFakePlayer(World world) {
		Objects.requireNonNull(world);
		// Simple research fake player, we don't need this fake player for anything else
		if(researchPlayer == null) {
			researchPlayer = new EntityPlayer(world, PROFILE) {
				@Override public boolean isSpectator() { return false; }
				@Override public boolean isCreative() { return false; }
			};
			IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(researchPlayer);
			for(ResearchCategory category : ResearchCategories.researchCategories.values()) {
				for(ResearchEntry entry : category.research.values()) {
					knowledge.addResearch(entry.getKey());
					String[] parents = ArrayUtils.nullToEmpty(entry.getParentsClean());
					for(String parent : parents) {
						knowledge.addResearch(parent);
					}
					ResearchStage[] stages = ArrayUtils.nullToEmpty(entry.getStages(), ResearchStage[].class);
					knowledge.setResearchStage(entry.getKey(), stages.length);
					for(ResearchStage stage : stages) {
						String[] researchReq = ArrayUtils.nullToEmpty(stage.getResearch());
						for(String research : researchReq) {
							knowledge.addResearch(cleanResearchKey(research));
						}
					}
					String[] siblings = ArrayUtils.nullToEmpty(entry.getSiblings());
					for(String sibling : siblings) {
						knowledge.addResearch(cleanResearchKey(sibling));
					}
					ResearchAddendum[] addenda = ArrayUtils.nullToEmpty(entry.getAddenda(), ResearchAddendum[].class);
					for(ResearchAddendum addendum : addenda) {
						String[] researchReq = ArrayUtils.nullToEmpty(addendum.getResearch());
						for(String research : researchReq) {
							knowledge.addResearch(cleanResearchKey(research));
						}
					}
				}
			}
		}
		else {
			researchPlayer.setWorld(world);
		}
		return researchPlayer;
	}

	public String cleanResearchKey(String research) {
		if(research.startsWith("~")) {
			research = research.substring(1);
		}
		int i = research.indexOf("@");
		if(i >= 0) {
			research = research.substring(0, i);
		}
		return research;
	}
}
