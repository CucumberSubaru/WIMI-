package com.backpackinv.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class BackpackInvClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        InventoryAnimation.reloadConfig();
        ClientTickEvents.END_CLIENT_TICK.register(InventoryAnimation::tick);
    }
}
