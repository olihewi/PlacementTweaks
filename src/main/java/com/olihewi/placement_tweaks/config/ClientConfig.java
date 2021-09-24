package com.olihewi.placement_tweaks.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig
{
  public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
  public static final ForgeConfigSpec SPEC;

  public static final ForgeConfigSpec.ConfigValue<Boolean> PERFECT_PLACEMENT_ENABLED;
  public static final ForgeConfigSpec.ConfigValue<Boolean> PERFECT_PLACEMENT_REMOVE_DOUBLE_PLACEMENT;

  static
  {
    BUILDER.push("Perfect Placement");
    PERFECT_PLACEMENT_ENABLED = BUILDER.define("Enabled",true);
    PERFECT_PLACEMENT_REMOVE_DOUBLE_PLACEMENT = BUILDER.define("Remove Double Placements", true);
    BUILDER.pop();

    SPEC = BUILDER.build();
  }
}
