package com.backpackinv;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class BackpackInv implements ModInitializer {
    public static final String MOD_ID = "backpackinv";

    /** Служебные предметы: нужны только чтобы нарисовать корпус и крышку рюкзака в руках. Получить их в игре нельзя. */
    public static Item BACKPACK_BODY;
    public static Item BACKPACK_LID;

    @Override
    public void onInitialize() {
        BACKPACK_BODY = register("backpack_body");
        BACKPACK_LID = register("backpack_lid");
    }

    private static Item register(String name) {
        return Registry.register(
                BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath(MOD_ID, name),
                new Item(new Item.Properties())
        );
    }
}
