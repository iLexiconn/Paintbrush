package net.ilexiconn.paintbrush.server.util;

public enum Utils {
    PAINT,
    FACE,
    BLOCK;

    public Class<? extends Util> getUtilClass() {
        switch (this) {
            case PAINT:
                return Paint.class;
            case FACE:
                return PaintedFace.class;
            case BLOCK:
                return PaintedBlock.class;
        }
        return Util.class;
    }
}
