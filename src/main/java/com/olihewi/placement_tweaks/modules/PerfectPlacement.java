package com.olihewi.placement_tweaks.modules;


import com.olihewi.placement_tweaks.PlacementTweaks;
import com.olihewi.placement_tweaks.config.ClientConfig;
import com.olihewi.placement_tweaks.config.CommonConfig;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class PerfectPlacement
{
  private static BlockPos lastBlockPos;
  private static Direction lastFace;
  private static BlockPos lastPlayerPos;

  public static final ResourceLocation DOUBLE_PLACEABLE = new ResourceLocation(PlacementTweaks.MOD_ID,"double_placeable");

  private static boolean MODULE_ENABLED = true;
  private static boolean CREATIVE_ONLY = false;
  private static boolean NO_DOUBLES = true;

  public static void initialize()
  {
    IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
    modBus.addListener(PerfectPlacement::OnConfigLoad);
    modBus.addListener(PerfectPlacement::OnConfigReload);
    MinecraftForge.EVENT_BUS.addListener(PerfectPlacement::OnClientTick);
  }

  private static void OnConfigLoad(ModConfig.Loading event)
  {
    MODULE_ENABLED = CommonConfig.PERFECT_PLACEMENT_ENABLED.get() && ClientConfig.PERFECT_PLACEMENT_ENABLED.get();
    CREATIVE_ONLY = CommonConfig.PERFECT_PLACEMENT_CREATIVE_ONLY.get();
    NO_DOUBLES = ClientConfig.PERFECT_PLACEMENT_REMOVE_DOUBLE_PLACEMENT.get();
  }

  private static void OnConfigReload(ModConfig.Reloading event)
  {
    MODULE_ENABLED = CommonConfig.PERFECT_PLACEMENT_ENABLED.get() && ClientConfig.PERFECT_PLACEMENT_ENABLED.get();
    CREATIVE_ONLY = CommonConfig.PERFECT_PLACEMENT_CREATIVE_ONLY.get();
    NO_DOUBLES = ClientConfig.PERFECT_PLACEMENT_REMOVE_DOUBLE_PLACEMENT.get();
  }

  private static void OnClientTick(TickEvent.ClientTickEvent event)
  {
    if (!MODULE_ENABLED || event.phase != TickEvent.Phase.START) return;
    Minecraft mc = Minecraft.getInstance();
    ClientPlayerEntity player = mc.player;
    if (mc.level == null || player == null) return;
    if (CREATIVE_ONLY && !player.isCreative()) return;
    int rightClickDelay = mc.rightClickDelay;
    RayTraceResult rayTraceResult = mc.hitResult;
    if (rayTraceResult != null && rayTraceResult.getType() == RayTraceResult.Type.BLOCK)
    {
      BlockRayTraceResult hit = (BlockRayTraceResult) rayTraceResult;
      BlockPos blockPos = hit.getBlockPos();
      Direction face = hit.getDirection();
      BlockPos playerPos = player.blockPosition();
      if (rightClickDelay > 0 && !blockPos.equals(lastBlockPos) && !blockPos.equals(lastBlockPos.relative(lastFace)))
      {
        mc.rightClickDelay = 0;
      }
      else if (NO_DOUBLES)
      {
        if (playerPos.relative(face.getOpposite()).equals(lastPlayerPos))
        {
          mc.rightClickDelay = 0;
        }
        else if (blockPos.equals(lastBlockPos) && face == lastFace)
        {
          Block block = player.level.getBlockState(blockPos).getBlock();
          if (!block.getTags().contains(DOUBLE_PLACEABLE) && !block.getTags().contains(StretchBuilding.STRETCH_BUILDABLE))
          {
            mc.rightClickDelay = 4;
          }
        }
      }
      lastBlockPos = blockPos.immutable();
      lastFace = face;
      lastPlayerPos = playerPos;
    }
  }
}
