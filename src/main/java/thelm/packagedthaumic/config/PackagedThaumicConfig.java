package thelm.packagedthaumic.config;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thelm.packagedthaumic.tile.TileArcaneCrafter;
import thelm.packagedthaumic.tile.TileClathrateEssenceFormer;
import thelm.packagedthaumic.tile.TileCrucibleCrafter;
import thelm.packagedthaumic.tile.TileInfusionCrafter;
import thelm.packagedthaumic.tile.TileVirialArcaneCrafter;
import thelm.packagedthaumic.tile.TileVirialRechargePedestal;

public class PackagedThaumicConfig {

	private PackagedThaumicConfig() {}

	public static Configuration config;

	public static void init(File file) {
		MinecraftForge.EVENT_BUS.register(PackagedThaumicConfig.class);
		config = new Configuration(file);
		config.load();
		init();
	}

	public static void init() {
		String category;
		category = "blocks.clathrate_essence_former";
		TileClathrateEssenceFormer.energyCapacity = config.get(category, "energy_capacity", TileClathrateEssenceFormer.energyCapacity, "How much FE the Essentia Clathrate Former should hold.", 0, Integer.MAX_VALUE).getInt();
		TileClathrateEssenceFormer.energyUsage = config.get(category, "energy_usage", TileClathrateEssenceFormer.energyUsage, "How much FE the Essentia Clathrate Former should use per operation.", 0, Integer.MAX_VALUE).getInt();
		TileClathrateEssenceFormer.tickInterval = config.get(category, "tick_interval", TileClathrateEssenceFormer.tickInterval, "How many ticks the Essentia Clathrate Former should take per operation.", 0, Integer.MAX_VALUE).getInt();
		TileClathrateEssenceFormer.drawMEEnergy = config.get(category, "draw_me_energy", TileClathrateEssenceFormer.drawMEEnergy, "Should the Essentia Clathrate Former draw energy from ME systems.").getBoolean();
		category = "blocks.arcane_crafter";
		TileArcaneCrafter.energyCapacity = config.get(category, "energy_capacity", TileArcaneCrafter.energyCapacity, "How much FE the Arcane Package Crafter should hold.", 0, Integer.MAX_VALUE).getInt();
		TileArcaneCrafter.energyReq = config.get(category, "energy_req", TileArcaneCrafter.energyReq, "How much FE the Arcane Package Crafter should use.", 0, Integer.MAX_VALUE).getInt();
		TileArcaneCrafter.energyUsage = config.get(category, "energy_usage", TileArcaneCrafter.energyUsage, "How much FE/t maximum the Arcane Package Crafter should use.", 0, Integer.MAX_VALUE).getInt();
		TileArcaneCrafter.drawMEEnergy = config.get(category, "draw_me_energy", TileArcaneCrafter.drawMEEnergy, "Should the Arcane Packager Crafter draw energy from ME systems.").getBoolean();
		category = "blocks.virial_arcane_crafter";
		TileVirialArcaneCrafter.energyCapacity = config.get(category, "energy_capacity", TileVirialArcaneCrafter.energyCapacity, "How much FE the Virial Arcane Package Crafter should hold.", 0, Integer.MAX_VALUE).getInt();
		TileVirialArcaneCrafter.energyPerVis = config.get(category, "energy_per_vis", TileVirialArcaneCrafter.energyPerVis, "How much FE the Virial Arcane Package Crafter should use per vis.", 1000, Integer.MAX_VALUE).getInt();
		TileVirialArcaneCrafter.energyUsage = config.get(category, "energy_usage", TileVirialArcaneCrafter.energyUsage, "How much FE/t maximum the Virial Arcane Package Crafter should use.", 0, Integer.MAX_VALUE).getInt();
		TileVirialArcaneCrafter.fluxLeakageChance = config.get(category, "flux_leakage_chance", TileVirialArcaneCrafter.fluxLeakageChance, "The base chance per vis that the Virial Arcane Package Crafter leaks flux.", 0, 1).getDouble();
		TileVirialArcaneCrafter.drawMEEnergy = config.get(category, "draw_me_energy", TileVirialArcaneCrafter.drawMEEnergy, "Should the Virial Arcane Packager Crafter draw energy from ME systems.").getBoolean();
		category = "blocks.crucible_crafter";
		TileCrucibleCrafter.energyCapacity = config.get(category, "energy_capacity", TileCrucibleCrafter.energyCapacity, "How much FE the Package Thaumatorium should hold.", 0, Integer.MAX_VALUE).getInt();
		TileCrucibleCrafter.timeMultiplier = config.get(category, "time_multiplier", TileCrucibleCrafter.timeMultiplier, "How much time the Package Thaumatorium should take compared to the Thaumatorium.", 0, Double.MAX_VALUE).getDouble();
		TileCrucibleCrafter.energyUsage = config.get(category, "energy_usage", TileCrucibleCrafter.energyUsage, "How much FE/t maximum the Package Thaumatorium should use.", 0, Integer.MAX_VALUE).getInt();
		TileCrucibleCrafter.requiresCrucible = config.get(category, "requires_crucible", TileCrucibleCrafter.requiresCrucible, "Should the Package Thaumatorium require a crucible below.").getBoolean();
		TileCrucibleCrafter.requiresHeat = config.get(category, "requires_heat", TileCrucibleCrafter.requiresHeat, "Should the Package Thaumatorium require a heat source below.").getBoolean();
		TileCrucibleCrafter.drawMEEnergy = config.get(category, "draw_me_energy", TileCrucibleCrafter.drawMEEnergy, "Should the Package Thaumatorium draw energy from ME systems.").getBoolean();
		category = "blocks.infusion_crafter";
		TileInfusionCrafter.energyCapacity = config.get(category, "energy_capacity", TileInfusionCrafter.energyCapacity, "How much FE the Package Runic Matrix should hold.", 0, Integer.MAX_VALUE).getInt();
		TileInfusionCrafter.essentiaTimeMultiplier = config.get(category, "essentia_time_multiplier", TileInfusionCrafter.essentiaTimeMultiplier, "How much time the Package Runic Matrix should take compared to the Runic Matrix per essentia.", 0, Double.MAX_VALUE).getDouble();
		TileInfusionCrafter.itemTimeMultiplier = config.get(category, "item_time_multiplier", TileInfusionCrafter.itemTimeMultiplier, "How much time the Package Runic Matrix should take compared to the Thaumatorium per item.", 0, Double.MAX_VALUE).getDouble();
		TileInfusionCrafter.energyUsage = config.get(category, "energy_usage", TileInfusionCrafter.energyUsage, "How much FE/t maximum the Package Runic Matrix should use.", 0, Integer.MAX_VALUE).getInt();
		TileInfusionCrafter.requiresPillars = config.get(category, "requires_pillars", TileInfusionCrafter.requiresPillars, "Should the Package Runic Matrix pillars below.").getBoolean();
		TileInfusionCrafter.drawMEEnergy = config.get(category, "draw_me_energy", TileInfusionCrafter.drawMEEnergy, "Should the Package Runic Matrix draw energy from ME systems.").getBoolean();
		category = "blocks.virial_recharge_pedestal";
		TileVirialRechargePedestal.energyCapacity = config.get(category, "energy_capacity", TileVirialRechargePedestal.energyCapacity, "How much FE the Virial Recharge Pedestal should hold.", 0, Integer.MAX_VALUE).getInt();
		TileVirialRechargePedestal.energyPerVis = config.get(category, "energy_per_vis", TileVirialRechargePedestal.energyPerVis, "How much FE the Virial Recharge Pedestal should use per vis.", 1000, Integer.MAX_VALUE).getInt();
		TileVirialRechargePedestal.tickInterval = config.get(category, "tick_interval", TileVirialRechargePedestal.tickInterval, "How many ticks the Virial Recharge Pedestal should take per operation.", 0, Integer.MAX_VALUE).getInt();
		TileVirialRechargePedestal.fluxLeakageChance = config.get(category, "flux_leakage_chance", TileVirialRechargePedestal.fluxLeakageChance, "The base chance per vis that the Virial Recharge Pedestal leaks flux.", 0, 1).getDouble();
		if(config.hasChanged()) {
			config.save();
		}
	}

	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event) {
		if(event.getModID().equals("packagedthaumic")) {
			init();
		}
	}
}
