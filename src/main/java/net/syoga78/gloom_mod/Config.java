package net.syoga78.gloom_mod;

import java.awt.*;
import java.util.List;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<Float> LAST_HIT_DAMAGE = BUILDER
            .comment("Damage that will be dealt when player have 1 hp")
            .translation("config.gloom_mod.lastHitDamage")
            .define("lastHitDamage", 2.0f);

    public static final ModConfigSpec.ConfigValue<Integer> DAMAGE_INTERVAL_TICKS = BUILDER
            .comment("Interval between hits")
            .translation("config.gloom_mod.damageInterval")
            .define("damageInterval", 100);

    public static final ModConfigSpec.ConfigValue<Integer> DARKNESS_THRESHOLD = BUILDER
            .comment("Light level threshold after which you will start taking damage")
            .translation("config.gloom_mod.lightLevel")
            .define("lightLevel", 1);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> DIMENSIONS_SAFE_LIST = BUILDER
            .comment("A list of dimensions that are safe from gloom.")
            .translation("config.gloom_mod.dimensions")
            .define("dimensions", List.of("minecraft:the_nether", "minecraft:the_end"), o -> o instanceof String);


    static final ModConfigSpec SPEC = BUILDER.build();

}
