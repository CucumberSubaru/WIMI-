package com.backpackinv.client;

import com.backpackinv.BackpackInv;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.item.ItemStack;

public final class InventoryAnimation {
    public enum Phase { NONE, RAISING, OPEN, LOWERING }

    private static Phase phase = Phase.NONE;
    private static int ticks = 0;
    private static boolean bypass = false;
    private static BackpackInvConfig config = new BackpackInvConfig();
    private static ItemStack propStack;

    private InventoryAnimation() {}

    public static BackpackInvConfig config() {
        return config;
    }

    public static void reloadConfig() {
        config = BackpackInvConfig.load();
    }

    public static boolean isActive() {
        return phase != Phase.NONE;
    }

    public static ItemStack propStack() {
        if (propStack == null) {
            propStack = new ItemStack(BackpackInv.BACKPACK_PROP);
        }
        return propStack;
    }

    /** Нужно ли перехватить открытие инвентаря и сначала показать рюкзак. */
    public static boolean shouldIntercept() {
        if (bypass || !config.enabled) return false;
        if (phase != Phase.NONE && phase != Phase.LOWERING) return false;
        Minecraft mc = Minecraft.getInstance();
        return mc.player != null && mc.level != null && !mc.player.isSpectator();
    }

    public static void start() {
        reloadConfig();
        phase = Phase.RAISING;
        ticks = 0;
    }

    public static void tick(Minecraft mc) {
        if (phase == Phase.NONE) return;
        if (mc.player == null || mc.level == null) {
            phase = Phase.NONE;
            return;
        }
        switch (phase) {
            case RAISING:
                if (mc.screen != null) { // игрок открыл что-то другое (меню паузы и т.п.)
                    phase = Phase.NONE;
                    return;
                }
                ticks++;
                if (ticks >= Math.max(1, config.raiseTicks)) {
                    phase = Phase.OPEN;
                    bypass = true;
                    try {
                        mc.setScreen(new InventoryScreen(mc.player));
                    } finally {
                        bypass = false;
                    }
                }
                break;
            case OPEN:
                if (!(mc.screen instanceof InventoryScreen)) {
                    phase = Phase.LOWERING;
                    ticks = 0;
                }
                break;
            case LOWERING:
                ticks++;
                if (ticks >= Math.max(1, config.lowerTicks)) {
                    phase = Phase.NONE;
                }
                break;
            default:
                break;
        }
    }

    /** 0 = рюкзак за кадром, 1 = рюкзак полностью в руках. */
    public static float progress(float partialTick) {
        switch (phase) {
            case RAISING:
                return ease((ticks + partialTick) / Math.max(1, config.raiseTicks));
            case OPEN:
                return 1.0f;
            case LOWERING:
                return 1.0f - ease((ticks + partialTick) / Math.max(1, config.lowerTicks));
            default:
                return 0.0f;
        }
    }

    private static float ease(float t) {
        t = Math.max(0.0f, Math.min(1.0f, t));
        return t * t * (3.0f - 2.0f * t);
    }
}
