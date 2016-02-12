package net.ilexiconn.paintbrush.server.api;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;

import java.util.List;

public class PaintbrushAPI {
    private static List<Block> ignoredBlockList = Lists.newArrayList();
    private static List<Class<?>> ignoredBlockTypeList = Lists.newArrayList();

    public static void registerIgnoredBlock(Block block) {
        ignoredBlockList.add(block);
    }

    public static void registerIgnoredBlockType(Class<?> type) {
        ignoredBlockTypeList.add(type);
    }

    public static boolean isBlockIgnored(Block block) {
        if (!ignoredBlockList.contains(block)) {
            for (Class<?> type : ignoredBlockTypeList) {
                if (type.isAssignableFrom(block.getClass())) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }
}
