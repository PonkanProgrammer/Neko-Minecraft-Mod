package com.neko.catcustomizer;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;

public class NekoCatCustomizerMod implements ModInitializer {
    public static final String MOD_ID = "nekocatcustomizer";

    private static final List<CatVariant> VARIANTS = new ArrayList<>();
    private static final List<DyeColor> DYE_COLORS = List.of(DyeColor.values());

    public static final Item CAT_STYLER = Registry.register(
            Registries.ITEM,
            Identifier.of(MOD_ID, "cat_styler"),
            new Item(new Item.Settings().maxCount(1).maxDamage(96))
    );

    @Override
    public void onInitialize() {
        refreshVariants();

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient() || !(entity instanceof CatEntity cat)) {
                return ActionResult.PASS;
            }

            ItemStack held = player.getStackInHand(hand);

            if (held.isOf(CAT_STYLER)) {
                if (player.isSneaking()) {
                    randomizeAll(cat, world.getRandom());
                    notifyPlayer(player, "猫の見た目をランダム変更しました！");
                } else {
                    cycleVariant(cat);
                    notifyPlayer(player, "猫の模様を切り替えました。しゃがみ右クリックでランダム変更できます。", cat.getVariant());
                }

                if (!player.isCreative()) {
                    held.damage(1, player, p -> p.sendToolBreakStatus(hand));
                }

                return ActionResult.SUCCESS;
            }

            DyeColor dye = mapDyeToColor(held);
            if (dye == null) {
                return ActionResult.PASS;
            }

            if (!cat.isTamed()) {
                notifyPlayer(player, "首輪の色変更は手懐けた猫だけです。");
                return ActionResult.PASS;
            }

            cat.setCollarColor(dye);
            held.decrementUnlessCreative(1, player);
            notifyPlayer(player, "首輪の色を変更しました: " + dye.getName());
            return ActionResult.SUCCESS;
        });
    }

    private void refreshVariants() {
        VARIANTS.clear();
        Registries.CAT_VARIANT.stream().forEach(VARIANTS::add);
    }

    private static void cycleVariant(CatEntity cat) {
        if (VARIANTS.isEmpty()) {
            return;
        }

        int currentIndex = VARIANTS.indexOf(cat.getVariant());
        int nextIndex = (currentIndex + 1) % VARIANTS.size();
        cat.setVariant(VARIANTS.get(nextIndex));
    }

    private static void randomizeAll(CatEntity cat, Random random) {
        if (!VARIANTS.isEmpty()) {
            cat.setVariant(VARIANTS.get(random.nextInt(VARIANTS.size())));
        }

        if (cat.isTamed()) {
            cat.setCollarColor(DYE_COLORS.get(random.nextInt(DYE_COLORS.size())));
        }
    }

    private static DyeColor mapDyeToColor(ItemStack stack) {
        if (stack.isOf(Items.WHITE_DYE)) return DyeColor.WHITE;
        if (stack.isOf(Items.ORANGE_DYE)) return DyeColor.ORANGE;
        if (stack.isOf(Items.MAGENTA_DYE)) return DyeColor.MAGENTA;
        if (stack.isOf(Items.LIGHT_BLUE_DYE)) return DyeColor.LIGHT_BLUE;
        if (stack.isOf(Items.YELLOW_DYE)) return DyeColor.YELLOW;
        if (stack.isOf(Items.LIME_DYE)) return DyeColor.LIME;
        if (stack.isOf(Items.PINK_DYE)) return DyeColor.PINK;
        if (stack.isOf(Items.GRAY_DYE)) return DyeColor.GRAY;
        if (stack.isOf(Items.LIGHT_GRAY_DYE)) return DyeColor.LIGHT_GRAY;
        if (stack.isOf(Items.CYAN_DYE)) return DyeColor.CYAN;
        if (stack.isOf(Items.PURPLE_DYE)) return DyeColor.PURPLE;
        if (stack.isOf(Items.BLUE_DYE)) return DyeColor.BLUE;
        if (stack.isOf(Items.BROWN_DYE)) return DyeColor.BROWN;
        if (stack.isOf(Items.GREEN_DYE)) return DyeColor.GREEN;
        if (stack.isOf(Items.RED_DYE)) return DyeColor.RED;
        if (stack.isOf(Items.BLACK_DYE)) return DyeColor.BLACK;
        return null;
    }

    private static void notifyPlayer(net.minecraft.entity.player.PlayerEntity player, String message) {
        notifyPlayer(player, message, null);
    }

    private static void notifyPlayer(net.minecraft.entity.player.PlayerEntity player, String message, CatVariant variant) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            String suffix = "";
            if (variant != null) {
                suffix = " (variant=" + Registries.CAT_VARIANT.getId(variant) + ")";
            }
            serverPlayer.sendMessage(Text.literal("[NekoCat] " + message + suffix), true);
        }
    }
}
