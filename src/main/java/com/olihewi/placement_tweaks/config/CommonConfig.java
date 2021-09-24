package com.olihewi.placement_tweaks.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig
{
  public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
  public static final ForgeConfigSpec SPEC;

  public static final ForgeConfigSpec.ConfigValue<Boolean> PERFECT_PLACEMENT_ENABLED;
  public static final ForgeConfigSpec.ConfigValue<Boolean> PERFECT_PLACEMENT_CREATIVE_ONLY;

  public static final ForgeConfigSpec.ConfigValue<Boolean> STRETCH_BUILDING_ENABLED;
  public static final ForgeConfigSpec.ConfigValue<Integer> STRETCH_BUILDING_RANGE;

  static
  {
    BUILDER.push("Perfect Placement");
    PERFECT_PLACEMENT_ENABLED = BUILDER.define("Enabled",true);
    PERFECT_PLACEMENT_CREATIVE_ONLY = BUILDER.define("Only use Perfect Placement in Creative Mode",false);
    BUILDER.pop();

    BUILDER.push("Stretch Building");
    STRETCH_BUILDING_ENABLED = BUILDER.define("Enabled",true);
    STRETCH_BUILDING_RANGE = BUILDER.define("Maximum Range", 8);
    BUILDER.pop();

    SPEC = BUILDER.build();
  }
}
