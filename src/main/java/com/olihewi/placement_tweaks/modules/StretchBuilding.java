package com.olihewi.placement_tweaks.modules;

import com.olihewi.placement_tweaks.PlacementTweaks;
import com.olihewi.placement_tweaks.config.ClientConfig;
import com.olihewi.placement_tweaks.config.CommonConfig;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class StretchBuilding
{
  public static final ResourceLocation STRETCH_BUILDABLE = new ResourceLocation(PlacementTweaks.MOD_ID,"stretch_buildable");
  public static final ResourceLocation STRETCH_BUILDABLE_DOWN = new ResourceLocation(PlacementTweaks.MOD_ID,"stretch_buildable_down");

  private static boolean MODULE_ENABLED = true;
  private static int MAX_RANGE = 8;

  public static void initialize()
  {
    IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
    modBus.addListener(StretchBuilding::OnConfigLoad);
    modBus.addListener(StretchBuilding::OnConfigReload);
    MinecraftForge.EVENT_BUS.addListener(StretchBuilding::OnInteract);
  }

  private static void OnConfigLoad(ModConfig.Loading event)
  {
    MODULE_ENABLED = CommonConfig.STRETCH_BUILDING_ENABLED.get();
    MAX_RANGE = CommonConfig.STRETCH_BUILDING_RANGE.get();
  }

  private static void OnConfigReload(ModConfig.Reloading event)
  {
    MODULE_ENABLED = CommonConfig.STRETCH_BUILDING_ENABLED.get();
    MAX_RANGE = CommonConfig.STRETCH_BUILDING_RANGE.get();
  }

  private static void OnInteract(PlayerInteractEvent.RightClickBlock event)
  {
    PlayerEntity player = event.getPlayer();
    World world = player.level;
    ItemStack heldItem = event.getItemStack();
    BlockPos blockPos = event.getPos();
    BlockState blockState = player.level.getBlockState(blockPos);
    Block block = blockState.getBlock();
    if (player.isCrouching() || (!block.getTags().contains(STRETCH_BUILDABLE) && !block.getTags().contains(STRETCH_BUILDABLE_DOWN))) return;
    if (heldItem.getItem().equals(block.asItem()))
    {
      Direction direction = event.getFace();
      direction = direction == Direction.UP ? player.getDirection() : block.getTags().contains(STRETCH_BUILDABLE_DOWN) ? Direction.DOWN : Direction.UP;
      int i = 0;
      BlockPos.Mutable pos = blockPos.mutable().move(direction);
      while (i < MAX_RANGE)
      {
        if (!world.isClientSide && !World.isInWorldBounds(pos))
        {
          int j = world.getMaxBuildHeight();
          if (player instanceof ServerPlayerEntity && pos.getY() >= j)
          {
            SChatPacket schatpacket = new SChatPacket((new TranslationTextComponent("build.tooHigh", j)).withStyle(TextFormatting.RED), ChatType.GAME_INFO, Util.NIL_UUID);
            ((ServerPlayerEntity)player).connection.send(schatpacket);
          }
          break;
        }
        blockState = world.getBlockState(pos);
        if (!blockState.is(block) && event.getFace() != null)
        {
          BlockRayTraceResult newRayTrace = new BlockRayTraceResult(player.position(),event.getFace(),pos,false);
          BlockItemUseContext blockItemUseContext = new BlockItemUseContext(world, player, event.getHand(), heldItem, newRayTrace);
          if (blockState.canBeReplaced(blockItemUseContext))
          {
            BlockState newBlock = Block.byItem(heldItem.getItem()).getStateForPlacement(blockItemUseContext);
            if (newBlock != null && newBlock.canSurvive(world,pos))
            {
              world.setBlockAndUpdate(pos,newBlock);
              player.swing(event.getHand());
              world.playSound(null, pos, newBlock.getSoundType().getPlaceSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
              if (!player.isCreative())
              {
                heldItem.shrink(1);
              }
            }
          }
          break;
        }
        pos.move(direction);
        ++i;
      }
    }
  }
}
