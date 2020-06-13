package com.lifejourney.engine2d;

import androidx.annotation.Nullable;

public class OffsetCoord {

    public OffsetCoord() {
        this.x = 0;
        this.y = 0;
    }

    public OffsetCoord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public OffsetCoord(CubeCoord cubeCoord) {
        fromCubeCoord(cubeCoord);
    }

    public OffsetCoord(Point screenCoord) {
        fromScreenCoord(screenCoord);
    }

    /**
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(@Nullable java.lang.Object obj) {
        if (this != obj) {
            if (obj instanceof OffsetCoord) {
                return this.x == ((OffsetCoord) obj).x && this.y == ((OffsetCoord) obj).y;
            }
            else {
                return false;
            }
        }
        else {
            return true;
        }
    }

    @Override
    public int hashCode() {
        return x + (y << 5);
    }

    /**
     *
     * @return
     */
    public CubeCoord toCubeCoord() {
        CubeCoord cubeCoord = new CubeCoord();
        cubeCoord.fromOffsetCoord(this);
        return cubeCoord;
    }

    /**
     *
     * @param cubeCoord
     */
    public void fromCubeCoord(CubeCoord cubeCoord) {
        x = cubeCoord.getX() + (cubeCoord.getZ() - (cubeCoord.getZ() & 1));
        y = cubeCoord.getZ();
    }

    /**
     *
     * @return
     */
    public Point toScreenCoord() {
        return new Point((int) (hexSize * SQRT3 * (x + 0.5 * (y & 1))),
            hexSize * 3/2 * y);

    }

    /**
     *
     * @param screenCoord
     */
    public void fromScreenCoord(Point screenCoord) {
        fromCubeCoord(new CubeCoord(screenCoord));
    }

    /**
     *
     * @return
     */
    public int getX() {
        return x;
    }

    /**
     *
     * @param x
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     *
     * @return
     */
    public int getY() {
        return y;
    }

    /**
     *
     * @param y
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     *
     * @return
     */
    public static int getHexSize() {
        return OffsetCoord.hexSize;
    }

    /**
     *
     * @param hexSize
     */
    public static void setHexSize(int hexSize) {
        OffsetCoord.hexSize = hexSize;
    }

    private static final float SQRT3 = (float)Math.sqrt(3);
    private static int hexSize = 0;

    private int x;
    private int y;
}
