package net.ilexiconn.paintbrush.client;

import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ilexiconn.paintbrush.server.util.BlockPos;
import net.ilexiconn.paintbrush.server.util.Paint;
import net.ilexiconn.paintbrush.server.util.PaintedBlock;
import net.ilexiconn.paintbrush.server.util.PaintedFace;
import net.minecraft.util.EnumFacing;

import java.util.List;

@SideOnly(Side.CLIENT)
public class PaintbrushDataClient {
    private static List<PaintedBlock> paintedBlocks = Lists.newArrayList();

    public static Paint getPaint(BlockPos pos, EnumFacing facing, int x, int y) {
        PaintedBlock paintedBlock = getPaintedBlock(pos);

        if (paintedBlock != null) {
            PaintedFace paintedFace = paintedBlock.getPaintedFace(facing);

            if (paintedFace != null) {
                return paintedFace.getPaint(x, y);
            }
        }

        return null;
    }

    public static PaintedBlock getPaintedBlock(BlockPos pos) {
        for (PaintedBlock paintedBlock : paintedBlocks) {
            if (pos.equals(paintedBlock.pos)) {
                return paintedBlock;
            }
        }

        return null;
    }

    public static void addPaintedBlock(PaintedBlock paintedBlock) {
        paintedBlocks.add(paintedBlock);
    }

    public static void addPaintedFace(PaintedBlock paintedBlock, PaintedFace paintedFace) {
        paintedBlock.paintedFaceList.add(paintedFace);
    }

    public static void addPaint(PaintedFace paintedFace, Paint paint) {
        paintedFace.paintList.add(paint);
    }

    public static List<PaintedBlock> getPaintedBlocks() {
        return paintedBlocks;
    }
}
