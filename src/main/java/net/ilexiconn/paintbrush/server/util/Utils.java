package net.ilexiconn.paintbrush.server.util;

public enum Utils {
    PAINT,
    FACE,
    BLOCK,
    SIZE;

    public Class<? extends Util> getUtilClass() {
        switch (this) {
            case PAINT:
                return Paint.class;
            case FACE:
                return PaintedFace.class;
            case BLOCK:
                return PaintedBlock.class;
            case SIZE:
                return PaintbrushSize.class;
        }
        return Util.class;
    }
}
