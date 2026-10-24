package com.backpackinv;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class BackpackInv implements ModInitializer {
    public static final String MOD_ID = "backpackinv";

    /** Служебный предмет: нужен только чтобы нарисовать модель рюкзака в руках. Его нельзя получить в игре. */
    public static Item BACKPACK_PROP;

    @Override
    public void onInitialize() {
        BACKPACK_PROP = Registry.register(
                BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "backpack_prop"),
                new Item(new Item.Properties())
        );
    }
}
