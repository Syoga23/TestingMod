package net.syoga78.gloom_mod;

import java.awt.*;
import java.util.List;

import net.minecraft.client.gui.font.providers.UnihexProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.worldgen.DimensionTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<Float> LAST_HIT_DAMAGE = BUILDER
            .comment("Damage that will be dealt when player have 1 hp")
            .define("lastHitDamage", 2.0f);

    public static final ModConfigSpec.ConfigValue<Integer> DAMAGE_INTERVAL_TICKS = BUILDER
            .comment("Interval between hits")
            .define("damageInterval", 100);

    public static final ModConfigSpec.ConfigValue<Integer> DARKNESS_THRESHOLD = BUILDER
            .comment("Light level to die")
            .define("lightLevel", 1);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> DIMENSIONS_SAFE_LIST = BUILDER
            .comment("A list of dimensions that are safe from gloom.")
            .define("dimensions", List.of("minecraft:the_nether, minecraft:the_end"));


    static final ModConfigSpec SPEC = BUILDER.build();

}
