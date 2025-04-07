package cc.cassian.raspberry.overlay;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.RaspberryMod;
import cc.cassian.raspberry.config.ModConfig;
import cc.cassian.raspberry.registry.RaspberryTags;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;
import java.util.stream.Stream;

public class OverlayHelpers {
    public static final int textureSize = 256;
    public static final ResourceLocation TEXTURE = RaspberryMod.locate("textures/gui/tooltip.png");

    public static void renderBackground(PoseStack poseStack, int windowWidth, int fontWidth, int xPlacement, int xOffset, int yPlacement, int textureOffset, int tooltipSize) {
        if (ModConfig.get().overlay_renderBackground) {
            final int yPlacementWithOffset = yPlacement-4;
            final int endCapOffset = 197;
            // render background
            GuiComponent.blit(poseStack,
                    xPlacement-xOffset-4, yPlacementWithOffset,
                    0, 0,
                    textureOffset, fontWidth+xOffset+4, tooltipSize,
                    OverlayHelpers.textureSize, OverlayHelpers.textureSize);
            // render endcap
            GuiComponent.blit(poseStack,
                    OverlayHelpers.getEndCapPlacement(windowWidth, fontWidth), yPlacementWithOffset,
                    0, endCapOffset,
                    textureOffset, 3, tooltipSize,
                    OverlayHelpers.textureSize, OverlayHelpers.textureSize);
        }
    }

    @SubscribeEvent
    public static void checkInventoryForOverlays(TickEvent.ClientTickEvent event){
        if ((ModConfig.get().overlay_compass_enable || ModConfig.get().overlay_clock_enable) && Minecraft.getInstance().level != null) {
            OverlayHelpers.checkInventoryForItems(Minecraft.getInstance().player);
        }
    }

    public static boolean playerHasPotions(Player player) {
        // Technically, we should check whether these are ambient,
        // but Map Atlases doesn't and still covers our overlay.
        // return Player.areAllEffectsAmbient(player.getActiveEffects());
        return !player.getActiveEffects().isEmpty();
    }

    public static void checkInventoryForItems(Player player) {
        if (ModConfig.get().overlay_compass_enable || ModConfig.get().overlay_clock_enable) {
            // Altimeters exist. Might put this back if we drop supplementaries.
            //var y = RaspberryTags.SHOWS_Y;
            //if (!ModCompat.SPELUNKERY && !ModCompat.CAVERNS_AND_CHASMS && !ModCompat.SUPPLEMENTARIES)
            //    y = RaspberryTags.SHOWS_XZ;
            var barometer = RaspberryTags.SHOWS_WEATHER;
            if (!ModCompat.CAVERNS_AND_CHASMS) {
                barometer = RaspberryTags.SHOWS_TIME;
            }
            var inventory = player.getInventory();
            if (ModConfig.get().overlay_requireItemInHand) {
                var main = player.getMainHandItem();
                var offhand = player.getOffhandItem();
                CompassOverlay.hasCompass = main.is(RaspberryTags.SHOWS_XZ) || offhand.is(RaspberryTags.SHOWS_XZ);
                CompassOverlay.hasDepthGauge = main.is(RaspberryTags.SHOWS_Y) || offhand.is(RaspberryTags.SHOWS_Y);
                ClockOverlay.hasClock = main.is(RaspberryTags.SHOWS_TIME) || offhand.is(RaspberryTags.SHOWS_TIME);
                ClockOverlay.hasBarometer = main.is(barometer) || offhand.is(barometer);
            } else {
                CompassOverlay.hasCompass = checkInventoryForItem(inventory, RaspberryTags.SHOWS_XZ);
                CompassOverlay.hasDepthGauge = checkInventoryForItem(inventory, RaspberryTags.SHOWS_Y);
                ClockOverlay.hasClock = checkInventoryForItem(inventory, RaspberryTags.SHOWS_TIME);
                ClockOverlay.hasBarometer = checkInventoryForItem(inventory, barometer);
            }
        } else {
            CompassOverlay.hasCompass = false;
            CompassOverlay.hasDepthGauge = false;
        }
    }

    public static Stream<ItemStack> getContents(ItemStack stack) {
        CompoundTag compoundtag = stack.getTag();
        if (compoundtag == null) {
            return Stream.empty();
        } else {
            if (compoundtag.contains("Items")) {
                ListTag listtag = compoundtag.getList("Items", 10);
                return listtag.stream().map(CompoundTag.class::cast).map(ItemStack::of);
            }
            else if (compoundtag.contains("BlockEntityTag")) {
                var compound = compoundtag.getCompound("BlockEntityTag");
                ListTag listtag = compound.getList("Items", 10);
                return listtag.stream().map(CompoundTag.class::cast).map(ItemStack::of);
            }
        }
        return Stream.empty();
    }


    public static boolean checkInventoryForItem(Inventory inventory, TagKey<Item> item) {
        if (inventory.contains(item)) {
            return true;
        }
        else return checkInventoryForStack(inventory, item, null) != ItemStack.EMPTY;
    }

    public static ItemStack checkInventoryForStack(Inventory inventory, TagKey<Item> key, Item item) {
        if (ModConfig.get().overlay_searchContainers && inventory.contains(RaspberryTags.CONTAINERS)) {
            for (ItemStack stack : inventory.items) {
                if (stack.is(RaspberryTags.CONTAINERS)) {
                    List<ItemStack> contents = getContents(stack).toList();
                    for (ItemStack content : contents) {
                        if (key != null && content.is(key))
                            return stack;
                        else if (item != null && content.is(item))
                            return stack;
                    }
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @SubscribeEvent
    public static void pickup(PlayerEvent.ItemPickupEvent event) {
        checkInventoryForItems(event.getEntity());
    }

    @SubscribeEvent
    public static void join(PlayerEvent.PlayerLoggedInEvent event) {
        checkInventoryForItems(event.getEntity());
    }

    @SubscribeEvent
    public static void toss(ItemTossEvent event) {
        checkInventoryForItems(event.getPlayer());
    }

    @SubscribeEvent
    public static void closeInventory(PlayerContainerEvent.Close event) {
        checkInventoryForItems(event.getEntity());
    }

    public static int getPlacement(int windowWidth, int fontWidth) {
        if (ModConfig.get().overlay_leftalign) {
            return 9;
        } else {
            return windowWidth-2-fontWidth;
        }
    }

    public static int getEndCapPlacement(int windowWidth, int fontWidth) {
        if (ModConfig.get().overlay_leftalign) {
            return fontWidth+8;
        } else {
            return windowWidth-4;
        }
    }
}
