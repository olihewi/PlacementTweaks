package com.olihewi.placement_tweaks;

import com.olihewi.placement_tweaks.config.ClientConfig;
import com.olihewi.placement_tweaks.config.CommonConfig;
import com.olihewi.placement_tweaks.modules.PerfectPlacement;
import com.olihewi.placement_tweaks.modules.StretchBuilding;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod("placement_tweaks")
public class PlacementTweaks
{
    public static final String MOD_ID = "placement_tweaks";

    public PlacementTweaks()
    {
        // Load Configs
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC, "placement_tweaks-client.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC, "placement_tweaks-common.toml");

        PerfectPlacement.initialize();
        StretchBuilding.initialize();
    }
}
