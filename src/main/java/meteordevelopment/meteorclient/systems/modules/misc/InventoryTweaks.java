/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.misc;

import meteordevelopment.meteorclient.events.entity.DropItemsEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ItemListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

public class InventoryTweaks extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgAutoDrop = settings.createGroup("Auto Drop");

    // General

    private final Setting<Boolean> mouseDragItemMove = sgGeneral.add(new BoolSetting.Builder()
            .name("mouse-drag-item-move")
            .description("Moving mouse over items while holding shift will transfer it to the other container.")
            .defaultValue(true)
            .build()
    );

    private final Setting<List<Item>> antiDropItems = sgGeneral.add(new ItemListSetting.Builder()
            .name("anti-drop-items")
            .description("Items to prevent dropping. Doesn't work in creative inventory screen.")
            .defaultValue(new ArrayList<>(0))
            .build()
    );

    // Auto Drop

    private final Setting<List<Item>> autoDropItems = sgAutoDrop.add(new ItemListSetting.Builder()
            .name("auto-drop-items")
            .description("Items to drop.")
            .defaultValue(new ArrayList<>(0))
            .build()
    );

    private final Setting<Boolean> autoDropExcludeHotbar = sgAutoDrop.add(new BoolSetting.Builder()
            .name("auto-drop-exclude-hotbar")
            .description("Whether or not to drop items from your hotbar.")
            .defaultValue(false)
            .build()
    );

    public InventoryTweaks() {
        super(Categories.Misc, "inventory-tweaks", "Various inventory related utilities.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        // Auto Drop
        if (mc.currentScreen instanceof HandledScreen<?> || autoDropItems.get().isEmpty()) return;

        for (int i = autoDropExcludeHotbar.get() ? 9 : 0; i < mc.player.getInventory().size(); i++) {
            if (autoDropItems.get().contains(mc.player.getInventory().getStack(i).getItem())) {
                InvUtils.drop().slot(i);
            }
        }
    }

    @EventHandler
    private void onDropItems(DropItemsEvent event) {
        if (antiDropItems.get().contains(event.itemStack.getItem())) event.cancel();
    }

    public boolean mouseDragItemMove() {
        return isActive() && mouseDragItemMove.get();
    }
}