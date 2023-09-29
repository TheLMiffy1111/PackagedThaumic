package thelm.packagedthaumic.proxy;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApi.BluePrint;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.IDustTrigger;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.Part;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ScanningManager;
import thaumcraft.api.research.theorycraft.TheorycraftManager;
import thaumcraft.common.blocks.basic.BlockPillar;
import thaumcraft.common.lib.crafting.DustTriggerMultiblock;
import thelm.packagedauto.api.RecipeTypeRegistry;
import thelm.packagedthaumic.block.BlockArcaneCrafter;
import thelm.packagedthaumic.block.BlockClathrateEssenceFormer;
import thelm.packagedthaumic.block.BlockCrucibleCrafter;
import thelm.packagedthaumic.block.BlockInfusionCrafter;
import thelm.packagedthaumic.block.BlockMarkedPedestal;
import thelm.packagedthaumic.block.BlockReinforcedPorousStone;
import thelm.packagedthaumic.block.BlockVirialArcaneCrafter;
import thelm.packagedthaumic.block.BlockVirialChamber;
import thelm.packagedthaumic.block.BlockVirialRechargePedestal;
import thelm.packagedthaumic.config.PackagedThaumicConfig;
import thelm.packagedthaumic.integration.thaumicenergistics.block.BlockClathrateEssenceMaterializer;
import thelm.packagedthaumic.integration.thaumicenergistics.tile.TileClathrateEssenceMaterializer;
import thelm.packagedthaumic.item.ItemClathrateEssence;
import thelm.packagedthaumic.item.ItemMisc;
import thelm.packagedthaumic.network.PacketHandler;
import thelm.packagedthaumic.recipe.RecipeTypeArcane;
import thelm.packagedthaumic.recipe.RecipeTypeCrucible;
import thelm.packagedthaumic.recipe.RecipeTypeInfusion;
import thelm.packagedthaumic.research.ScanMod;
import thelm.packagedthaumic.research.theorycraft.AidEncoder;
import thelm.packagedthaumic.research.theorycraft.CardEncoding;
import thelm.packagedthaumic.research.theorycraft.CardPackaging;
import thelm.packagedthaumic.tile.TileArcaneCrafter;
import thelm.packagedthaumic.tile.TileClathrateEssenceFormer;
import thelm.packagedthaumic.tile.TileCrucibleCrafter;
import thelm.packagedthaumic.tile.TileInfusionCrafter;
import thelm.packagedthaumic.tile.TileMarkedPedestal;
import thelm.packagedthaumic.tile.TileVirialArcaneCrafter;
import thelm.packagedthaumic.tile.TileVirialRechargePedestal;

public class CommonProxy {

	public void registerBlock(Block block) {
		ForgeRegistries.BLOCKS.register(block);
	}

	public void registerItem(Item item) {
		ForgeRegistries.ITEMS.register(item);
	}

	public void register(FMLPreInitializationEvent event) {
		registerConfig(event);
		registerBlocks();
		registerItems();
		registerModels();
		registerTileEntities();
		registerRecipeTypes();
		registerNetwork();
	}

	public void register(FMLInitializationEvent event) {
		registerColors();
		registerResearch();
		registerRecipes();
	}

	protected void registerConfig(FMLPreInitializationEvent event) {
		PackagedThaumicConfig.init(event.getSuggestedConfigurationFile());
	}

	protected void registerBlocks() {
		registerBlock(BlockReinforcedPorousStone.INSTANCE);
		registerBlock(BlockVirialChamber.INSTANCE);
		registerBlock(BlockClathrateEssenceFormer.INSTANCE);
		registerBlock(BlockArcaneCrafter.INSTANCE);
		registerBlock(BlockVirialArcaneCrafter.INSTANCE);
		registerBlock(BlockCrucibleCrafter.INSTANCE);
		registerBlock(BlockInfusionCrafter.INSTANCE);
		registerBlock(BlockMarkedPedestal.ARCANE);
		registerBlock(BlockMarkedPedestal.ANCIENT);
		registerBlock(BlockMarkedPedestal.ELDRITCH);
		registerBlock(BlockVirialRechargePedestal.INSTANCE);
		if(Loader.isModLoaded("thaumicenergistics")) {
			Supplier<Runnable> r = ()->()->{
				registerBlock(BlockClathrateEssenceMaterializer.INSTANCE);
			};
			r.get().run();
		}
	}

	protected void registerItems() {
		registerItem(BlockReinforcedPorousStone.ITEM_INSTANCE);
		registerItem(BlockVirialChamber.ITEM_INSTANCE);
		registerItem(BlockClathrateEssenceFormer.ITEM_INSTANCE);
		registerItem(BlockArcaneCrafter.ITEM_INSTANCE);
		registerItem(BlockVirialArcaneCrafter.ITEM_INSTANCE);
		registerItem(BlockCrucibleCrafter.ITEM_INSTANCE);
		registerItem(BlockInfusionCrafter.ITEM_INSTANCE);
		registerItem(BlockMarkedPedestal.ARCANE_ITEM);
		registerItem(BlockMarkedPedestal.ANCIENT_ITEM);
		registerItem(BlockMarkedPedestal.ELDRITCH_ITEM);
		registerItem(BlockVirialRechargePedestal.ITEM_INSTANCE);
		if(Loader.isModLoaded("thaumicenergistics")) {
			Supplier<Runnable> r = ()->()->{
				registerItem(BlockClathrateEssenceMaterializer.ITEM_INSTANCE);
			};
			r.get().run();
		}

		registerItem(ItemMisc.THAUMIC_PACKAGE_COMPONENT);
		registerItem(ItemMisc.POROUS_STONE_PLATE);
		registerItem(ItemClathrateEssence.INSTANCE);
	}

	protected void registerModels() {}

	protected void registerTileEntities() {
		GameRegistry.registerTileEntity(TileClathrateEssenceFormer.class, new ResourceLocation("packagedthaumic:clathrate_essence_former"));
		GameRegistry.registerTileEntity(TileArcaneCrafter.class, new ResourceLocation("packagedthaumic:arcane_crafter"));
		GameRegistry.registerTileEntity(TileVirialArcaneCrafter.class, new ResourceLocation("packagedthaumic:virial_arcane_crafter"));
		GameRegistry.registerTileEntity(TileCrucibleCrafter.class, new ResourceLocation("packagedthaumic:crucible_crafter"));
		GameRegistry.registerTileEntity(TileInfusionCrafter.class, new ResourceLocation("packagedthaumic:infusion_crafter"));
		GameRegistry.registerTileEntity(TileMarkedPedestal.class, new ResourceLocation("packagedthaumic:marked_pedestal"));
		GameRegistry.registerTileEntity(TileVirialRechargePedestal.class, new ResourceLocation("packagedthaumic:virial_recharge_pedestal"));
		if(Loader.isModLoaded("thaumicenergistics")) {
			Supplier<Runnable> r = ()->()->{
				GameRegistry.registerTileEntity(TileClathrateEssenceMaterializer.class, new ResourceLocation("packagedthaumic:clathrate_essence_materializer"));
			};
			r.get().run();
		}
	}

	protected void registerRecipeTypes() {
		RecipeTypeRegistry.registerRecipeType(RecipeTypeArcane.INSTANCE);
		RecipeTypeRegistry.registerRecipeType(RecipeTypeCrucible.INSTANCE);
		RecipeTypeRegistry.registerRecipeType(RecipeTypeInfusion.INSTANCE);
	}

	protected void registerNetwork() {
		PacketHandler.registerPackets();
	}

	protected void registerColors() {}

	protected void registerResearch() {
		ResearchCategories.registerCategory("PACKAGEDTHAUMIC", "f_MODPACKAGEDAUTO",
				new AspectList().
				add(Aspect.MECHANISM, 15).
				add(Aspect.CRAFT, 15).
				add(Aspect.MAGIC, 10).
				add(Aspect.ENERGY, 10).
				add(Aspect.AURA, 10).
				add(Aspect.PROTECT, 5),
				new ResourceLocation("packagedauto:textures/items/package_component.png"),
				new ResourceLocation("packagedthaumic:textures/gui/research_background.jpg"),
				new ResourceLocation("thaumcraft:textures/gui/gui_research_back_over.png"));
		TheorycraftManager.registerAid(AidEncoder.INSTANCE);
		TheorycraftManager.registerCard(CardEncoding.class);
		TheorycraftManager.registerCard(CardPackaging.class);
		ScanningManager.addScannableThing(new ScanMod("f_MODPACKAGEDAUTO", "packagedauto"));
		ScanningManager.addScannableThing(new ScanMod("f_MODAPPLIEDENERGISTICS2", "appliedenergistics2"));
		ThaumcraftApi.registerResearchLocation(new ResourceLocation("packagedthaumic:research/packaging"));
		if(Loader.isModLoaded("thaumicenergistics")) {
			ThaumcraftApi.registerResearchLocation(new ResourceLocation("packagedthaumic:research/packaging_energistics"));
		}
	}

	protected void registerRecipes() {
		ResourceLocation emptyLocation = new ResourceLocation("");
		Item component = Loader.isModLoaded("appliedenergistics2") ?
				thelm.packagedauto.item.ItemMisc.ME_PACKAGE_COMPONENT :
					thelm.packagedauto.item.ItemMisc.PACKAGE_COMPONENT;
		ThaumcraftApi.addArcaneCraftingRecipe(
				new ResourceLocation("packagedthaumic:thaumic_package_component"),
				new ShapedArcaneRecipe(
						emptyLocation,
						"PACKAGEDTHAUMIC_THAUMICPACKAGECOMPONENT",
						25,
						new AspectList().
						add(Aspect.FIRE, 1).
						add(Aspect.EARTH, 1).
						add(Aspect.ORDER, 1),
						new ItemStack(ItemMisc.THAUMIC_PACKAGE_COMPONENT),
						new Object[] {
								"BMB",
								"ACA",
								"BAB",
								'C', new ItemStack(component),
								'M', new ItemStack(ItemsTC.mind, 1, 0),
								'B', "plateBrass",
								'A', "gemAmber",
						}));
		ThaumcraftApi.addCrucibleRecipe(
				new ResourceLocation("packagedthaumic:reinforced_porous_stone"),
				new CrucibleRecipe(
						"PACKAGEDTHAUMIC_REINFORCEDPOROUSSTONE@3",
						new ItemStack(BlockReinforcedPorousStone.INSTANCE),
						new ItemStack(BlocksTC.stonePorous),
						new AspectList().
						add(Aspect.WATER, 5).
						add(Aspect.EARTH, 5).
						add(Aspect.PROTECT, 5)));
		ThaumcraftApi.addArcaneCraftingRecipe(
				new ResourceLocation("packagedthaumic:porous_stone_plate"),
				new ShapedArcaneRecipe(
						emptyLocation,
						"PACKAGEDTHAUMIC_POROUSSTONEPLATE",
						80,
						new AspectList().
						add(Aspect.AIR, 1).
						add(Aspect.FIRE, 1).
						add(Aspect.WATER, 1).
						add(Aspect.EARTH, 1),
						new ItemStack(ItemMisc.POROUS_STONE_PLATE, 8),
						new Object[] {
								"SSS",
								"SCS",
								"SSS",
								'S', new ItemStack(BlockReinforcedPorousStone.INSTANCE),
								'C', new ItemStack(BlocksTC.metalAlchemical),
						}));
		ThaumcraftApi.addInfusionCraftingRecipe(
				new ResourceLocation("packagedthaumic:clathrate_essence_former"),
				new InfusionRecipe(
						"PACKAGEDTHAUMIC_CLATHRATEESSENCEFORMER",
						new ItemStack(BlockClathrateEssenceFormer.INSTANCE),
						2,
						new AspectList().
						add(Aspect.COLD, 25).
						add(Aspect.MECHANISM, 15).
						add(Aspect.WATER, 10).
						add(Aspect.CRYSTAL, 5).
						add(Aspect.TRAP, 5),
						BlocksTC.everfullUrn,
						new Object[] {
								new ItemStack(BlocksTC.crucible),
								new ItemStack(ItemMisc.POROUS_STONE_PLATE),
								new ItemStack(ItemsTC.mechanismSimple),
								"plateBrass",
								new ItemStack(Blocks.PACKED_ICE),
								new ItemStack(ItemMisc.THAUMIC_PACKAGE_COMPONENT),
								new ItemStack(Blocks.PACKED_ICE),
								"plateBrass",
								new ItemStack(ItemsTC.morphicResonator),
								new ItemStack(ItemMisc.POROUS_STONE_PLATE),
						}));
		ThaumcraftApi.addInfusionCraftingRecipe(
				new ResourceLocation("packagedthaumic:virial_chamber"),
				new InfusionRecipe(
						"PACKAGEDTHAUMIC_VIRIALCHAMBER",
						new ItemStack(BlockVirialChamber.INSTANCE),
						8,
						new AspectList().
						add(Aspect.AURA, 50).
						add(Aspect.LIGHT, 25).
						add(Aspect.ENERGY, 25).
						add(Aspect.VOID, 25).
						add(Aspect.EXCHANGE, 25).
						add(Aspect.MECHANISM, 10),
						"nitor",
						new Object[] {
								"plateThaumium",
								new ItemStack(ItemMisc.POROUS_STONE_PLATE),
								new ItemStack(ItemsTC.mirroredGlass),
								"enderpearl",
								new ItemStack(ItemMisc.POROUS_STONE_PLATE),
								new ItemStack(ItemsTC.mechanismComplex),
								new ItemStack(ItemMisc.POROUS_STONE_PLATE),
								new ItemStack(ItemsTC.visResonator),
								"blockRedstone",
								new ItemStack(ItemMisc.POROUS_STONE_PLATE),
						}));
		ThaumcraftApi.addInfusionCraftingRecipe(
				new ResourceLocation("packagedthaumic:arcane_crafter"),
				new InfusionRecipe(
						"PACKAGEDTHAUMIC_ARCANECRAFTER",
						new ItemStack(BlockArcaneCrafter.INSTANCE),
						4,
						new AspectList().
						add(Aspect.CRAFT, 25).
						add(Aspect.MECHANISM, 15).
						add(Aspect.MAGIC, 10).
						add(Aspect.AURA, 5),
						new ItemStack(BlocksTC.arcaneWorkbench),
						new Object[] {
								new ItemStack(BlocksTC.arcaneWorkbenchCharger),
								"plateIron",
								new ItemStack(ItemsTC.mechanismSimple),
								"plateBrass",
								new ItemStack(ItemMisc.THAUMIC_PACKAGE_COMPONENT),
								"plateBrass",
								new ItemStack(ItemsTC.mechanismSimple),
								"plateIron",
						}));
		ThaumcraftApi.addInfusionCraftingRecipe(
				new ResourceLocation("packagedthaumic:virial_arcane_crafter"),
				new InfusionRecipe(
						"PACKAGEDTHAUMIC_VIRIALARCANECRAFTER",
						new ItemStack(BlockVirialArcaneCrafter.INSTANCE),
						6,
						new AspectList().
						add(Aspect.CRAFT, 15).
						add(Aspect.ENERGY, 15).
						add(Aspect.EXCHANGE, 15).
						add(Aspect.AURA, 15),
						new ItemStack(BlockArcaneCrafter.INSTANCE),
						new Object[] {
								new ItemStack(BlocksTC.visBattery),
								"blockRedstone",
								new ItemStack(BlockVirialChamber.INSTANCE),
								"blockRedstone",
						}));
		ThaumcraftApi.addInfusionCraftingRecipe(
				new ResourceLocation("packagedthaumic:virial_recharge_pedestal"),
				new InfusionRecipe(
						"PACKAGEDTHAUMIC_VIRIALRECHARGEPEDESTAL",
						new ItemStack(BlockVirialRechargePedestal.INSTANCE),
						6,
						new AspectList().
						add(Aspect.TOOL, 15).
						add(Aspect.ENERGY, 15).
						add(Aspect.EXCHANGE, 15).
						add(Aspect.AURA, 15),
						new ItemStack(BlocksTC.rechargePedestal),
						new Object[] {
								new ItemStack(BlocksTC.visBattery),
								"blockRedstone",
								new ItemStack(BlockVirialChamber.INSTANCE),
								"blockRedstone",
						}));
		ThaumcraftApi.addInfusionCraftingRecipe(
				new ResourceLocation("packagedthaumic:crucible_crafter"),
				new InfusionRecipe(
						"PACKAGEDTHAUMIC_CRUCIBLECRAFTER",
						new ItemStack(BlockCrucibleCrafter.INSTANCE),
						4,
						new AspectList().
						add(Aspect.CRAFT, 25).
						add(Aspect.MECHANISM, 15).
						add(Aspect.ALCHEMY, 10).
						add(Aspect.FIRE, 5).
						add(Aspect.ORDER, 5),
						new ItemStack(BlocksTC.crucible),
						new Object[] {
								new ItemStack(ItemsTC.morphicResonator),
								new ItemStack(ItemsTC.salisMundus),
								new ItemStack(ItemsTC.mechanismSimple),
								new ItemStack(BlocksTC.metalAlchemical),
								new ItemStack(ItemMisc.THAUMIC_PACKAGE_COMPONENT),
								new ItemStack(BlocksTC.metalAlchemical),
								new ItemStack(ItemsTC.mechanismSimple),
								new ItemStack(ItemsTC.salisMundus),
						}));
		ThaumcraftApi.addInfusionCraftingRecipe(
				new ResourceLocation("packagedthaumic:infusion_crafter"),
				new InfusionRecipe(
						"PACKAGEDTHAUMIC_INFUSIONCRAFTER",
						new ItemStack(BlockInfusionCrafter.INSTANCE),
						10,
						new AspectList().
						add(Aspect.CRAFT, 50).
						add(Aspect.MECHANISM, 25).
						add(Aspect.MOTION, 20).
						add(Aspect.ELDRITCH, 20).
						add(Aspect.ALCHEMY, 15).
						add(Aspect.ORDER, 10).
						add(Aspect.FIRE, 5).
						add(Aspect.AURA, 5),
						new ItemStack(BlocksTC.infusionMatrix),
						new Object[] {
								new ItemStack(BlocksTC.metalAlchemicalAdvanced),
								new ItemStack(BlocksTC.tubeBuffer),
								new ItemStack(ItemMisc.POROUS_STONE_PLATE),
								new ItemStack(ItemsTC.mind, 1, 1),
								new ItemStack(ItemsTC.mechanismComplex),
								new ItemStack(BlocksTC.mirror),
								new ItemStack(ItemMisc.POROUS_STONE_PLATE),
								new ItemStack(ItemsTC.salisMundus),
								new ItemStack(ItemMisc.THAUMIC_PACKAGE_COMPONENT),
								new ItemStack(ItemsTC.salisMundus),
								new ItemStack(ItemMisc.POROUS_STONE_PLATE),
								new ItemStack(BlocksTC.stabilizer),
								new ItemStack(ItemsTC.mechanismComplex),
								new ItemStack(ItemsTC.morphicResonator),
								new ItemStack(ItemMisc.POROUS_STONE_PLATE),
								new ItemStack(BlocksTC.tubeBuffer),
						}));
		ThaumcraftApi.addArcaneCraftingRecipe(
				new ResourceLocation("packagedthaumic:marked_arcane_pedestal"),
				new ShapedArcaneRecipe(
						emptyLocation,
						"PACKAGEDTHAUMIC_INFUSIONCRAFTER",
						50,
						new AspectList().
						add(Aspect.AIR, 1).
						add(Aspect.ORDER, 1).
						add(Aspect.ENTROPY, 1),
						new ItemStack(BlockMarkedPedestal.ARCANE),
						new Object[] {
								"IPI",
								"IMI",
								'P', new ItemStack(BlocksTC.pedestalArcane),
								'I', new ItemStack(BlocksTC.inlay),
								'M', new ItemStack(ItemsTC.mirroredGlass),
						}));
		ThaumcraftApi.addArcaneCraftingRecipe(
				new ResourceLocation("packagedthaumic:marked_ancient_pedestal"),
				new ShapedArcaneRecipe(
						emptyLocation,
						"PACKAGEDTHAUMIC_INFUSIONCRAFTERANCIENT",
						50,
						new AspectList().
						add(Aspect.AIR, 1).
						add(Aspect.ORDER, 1).
						add(Aspect.ENTROPY, 1),
						new ItemStack(BlockMarkedPedestal.ANCIENT),
						new Object[] {
								"IPI",
								"IMI",
								'P', new ItemStack(BlocksTC.pedestalAncient),
								'I', new ItemStack(BlocksTC.inlay),
								'M', new ItemStack(ItemsTC.mirroredGlass),
						}));
		ThaumcraftApi.addArcaneCraftingRecipe(
				new ResourceLocation("packagedthaumic:marked_eldritch_pedestal"),
				new ShapedArcaneRecipe(
						emptyLocation,
						"PACKAGEDTHAUMIC_INFUSIONCRAFTERELDRITCH",
						50,
						new AspectList().
						add(Aspect.AIR, 1).
						add(Aspect.ORDER, 1).
						add(Aspect.ENTROPY, 1),
						new ItemStack(BlockMarkedPedestal.ELDRITCH),
						new Object[] {
								"IPI",
								"IMI",
								'P', new ItemStack(BlocksTC.pedestalEldritch),
								'I', new ItemStack(BlocksTC.inlay),
								'M', new ItemStack(ItemsTC.mirroredGlass),
						}));
		Part crcr = new Part(BlockCrucibleCrafter.INSTANCE, null);
		Part cruc = new Part(BlocksTC.crucible, null);
		Part magm = new Part(Blocks.MAGMA, null);
		Part[][][] blueprintCrucible = new Part[][][] {
			{{crcr}},
			{{cruc}},
		};
		ThaumcraftApi.addMultiblockRecipeToCatalog(
				new ResourceLocation("packagedthaumic:crucible_crafter_structure"),
				new BluePrint(
						"PACKAGEDTHAUMIC_CRUCIBLECRAFTER",
						blueprintCrucible,
						new ItemStack[] {
								new ItemStack(BlocksTC.crucible),
								new ItemStack(BlockCrucibleCrafter.INSTANCE),
						}));
		Part incr = new Part(BlockInfusionCrafter.INSTANCE, null);
		Part arst = new Part(BlocksTC.stoneArcane, "AIR");
		Part arp1 = new Part(BlocksTC.stoneArcane, new ItemStack(BlocksTC.pillarArcane, 1, BlockPillar.calcMeta(EnumFacing.EAST)));
		Part arp2 = new Part(BlocksTC.stoneArcane, new ItemStack(BlocksTC.pillarArcane, 1, BlockPillar.calcMeta(EnumFacing.NORTH)));
		Part arp3 = new Part(BlocksTC.stoneArcane, new ItemStack(BlocksTC.pillarArcane, 1, BlockPillar.calcMeta(EnumFacing.SOUTH)));
		Part arp4 = new Part(BlocksTC.stoneArcane, new ItemStack(BlocksTC.pillarArcane, 1, BlockPillar.calcMeta(EnumFacing.WEST)));
		Part[][][] blueprintArcane = new Part[][][] {
			{{null, null, null}, {null, incr, null}, {null, null, null}},
			{{arst, null, arst}, {null, null, null}, {arst, null, arst}},
			{{arp1, null, arp2}, {null, null, null}, {arp3, null, arp4}},
		};
		IDustTrigger.registerDustTrigger(new DustTriggerMultiblock(
				"PACKAGEDTHAUMIC_INFUSIONCRAFTER",
				blueprintArcane));
		ThaumcraftApi.addMultiblockRecipeToCatalog(
				new ResourceLocation("packagedthaumic:infusion_crafter_altar"),
				new BluePrint(
						"PACKAGEDTHAUMIC_INFUSIONCRAFTER",
						blueprintArcane,
						new ItemStack[] {
								new ItemStack(BlocksTC.stoneArcane, 8),
								new ItemStack(BlockInfusionCrafter.INSTANCE),
						}));
		Part anst = new Part(BlocksTC.stoneAncient, "AIR");
		Part anp1 = new Part(BlocksTC.stoneAncient, new ItemStack(BlocksTC.pillarAncient, 1, BlockPillar.calcMeta(EnumFacing.EAST)));
		Part anp2 = new Part(BlocksTC.stoneAncient, new ItemStack(BlocksTC.pillarAncient, 1, BlockPillar.calcMeta(EnumFacing.NORTH)));
		Part anp3 = new Part(BlocksTC.stoneAncient, new ItemStack(BlocksTC.pillarAncient, 1, BlockPillar.calcMeta(EnumFacing.SOUTH)));
		Part anp4 = new Part(BlocksTC.stoneAncient, new ItemStack(BlocksTC.pillarAncient, 1, BlockPillar.calcMeta(EnumFacing.WEST)));
		Part[][][] blueprintAncient = new Part[][][] {
			{{null, null, null}, {null, incr, null}, {null, null, null}},
			{{anst, null, anst}, {null, null, null}, {anst, null, anst}},
			{{anp1, null, anp2}, {null, null, null}, {anp3, null, anp4}},
		};
		IDustTrigger.registerDustTrigger(new DustTriggerMultiblock(
				"PACKAGEDTHAUMIC_INFUSIONCRAFTERANCIENT",
				blueprintAncient));
		ThaumcraftApi.addMultiblockRecipeToCatalog(
				new ResourceLocation("packagedthaumic:infusion_crafter_altar_ancient"),
				new BluePrint(
						"PACKAGEDTHAUMIC_INFUSIONCRAFTERANCIENT",
						blueprintAncient,
						new ItemStack[] {
								new ItemStack(BlocksTC.stoneAncient, 8),
								new ItemStack(BlockInfusionCrafter.INSTANCE),
						}));
		Part elst = new Part(BlocksTC.stoneEldritchTile, "AIR");
		Part elp1 = new Part(BlocksTC.stoneEldritchTile, new ItemStack(BlocksTC.pillarEldritch, 1, BlockPillar.calcMeta(EnumFacing.EAST)));
		Part elp2 = new Part(BlocksTC.stoneEldritchTile, new ItemStack(BlocksTC.pillarEldritch, 1, BlockPillar.calcMeta(EnumFacing.NORTH)));
		Part elp3 = new Part(BlocksTC.stoneEldritchTile, new ItemStack(BlocksTC.pillarEldritch, 1, BlockPillar.calcMeta(EnumFacing.SOUTH)));
		Part elp4 = new Part(BlocksTC.stoneEldritchTile, new ItemStack(BlocksTC.pillarEldritch, 1, BlockPillar.calcMeta(EnumFacing.WEST)));
		Part[][][] blueprintEldritch = new Part[][][] {
			{{null, null, null}, {null, incr, null}, {null, null, null}},
			{{elst, null, elst}, {null, null, null}, {elst, null, elst}},
			{{elp1, null, elp2}, {null, null, null}, {elp3, null, elp4}},
		};
		IDustTrigger.registerDustTrigger(new DustTriggerMultiblock(
				"PACKAGEDTHAUMIC_INFUSIONCRAFTERELDRITCH",
				blueprintEldritch));
		ThaumcraftApi.addMultiblockRecipeToCatalog(
				new ResourceLocation("packagedthaumic:infusion_crafter_altar_eldritch"),
				new BluePrint(
						"PACKAGEDTHAUMIC_INFUSIONCRAFTERELDRITCH",
						blueprintEldritch,
						new ItemStack[] {
								new ItemStack(BlocksTC.stoneEldritchTile, 8),
								new ItemStack(BlockInfusionCrafter.INSTANCE),
						}));
		if(Loader.isModLoaded("thaumicenergistics")) {
			Supplier<Runnable> r = ()->()->{
				Item coalescenceCore = ForgeRegistries.ITEMS.getValue(new ResourceLocation("thaumicenergistics:coalescence_core"));
				Item fluixBlock = ForgeRegistries.ITEMS.getValue(new ResourceLocation("appliedenergistics2:fluix_block"));
				ThaumcraftApi.addInfusionCraftingRecipe(
						new ResourceLocation("packagedthaumic:clathrate_essence_materializer"),
						new InfusionRecipe(
								"PACKAGEDTHAUMIC_CLATHRATEESSENCEMATERIALIZER",
								new ItemStack(BlockClathrateEssenceMaterializer.INSTANCE),
								6,
								new AspectList().
								add(Aspect.EXCHANGE, 50).
								add(Aspect.MECHANISM, 25).
								add(Aspect.COLD, 15).
								add(Aspect.ALCHEMY, 10).
								add(Aspect.CRYSTAL, 10),
								new ItemStack(coalescenceCore),
								new Object[] {
										new ItemStack(ItemsTC.mechanismComplex),
										new ItemStack(BlockClathrateEssenceFormer.INSTANCE),
										new ItemStack(fluixBlock),
										new ItemStack(BlockClathrateEssenceFormer.INSTANCE),
										new ItemStack(ItemsTC.salisMundus),
										new ItemStack(BlockClathrateEssenceFormer.INSTANCE),
										new ItemStack(fluixBlock),
										new ItemStack(BlockClathrateEssenceFormer.INSTANCE),
								}));
			};
			r.get().run();
		}
	}
}
