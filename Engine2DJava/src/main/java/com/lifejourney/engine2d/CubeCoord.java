package com.lifejourney.engine2d;

import android.annotation.SuppressLint;

import androidx.annotation.Nullable;

public class CubeCoord {

    private final String LOG_TAG = "CubeCoord";

    private static int HexSize = 0;

    /**
     *
     * @return
     */
    public static int GetHexSize() {
        return HexSize;
    }

    /**
     *
     * @param hexSize
     */
    public static void SetHexSize(int hexSize) {
        CubeCoord.HexSize = hexSize;
    }

    public CubeCoord() {
        this.x = this.y = this.z = 0;
    }

    @SuppressLint("Assert")
    public CubeCoord(int x, int y, int z) {
        assert (x + y + z) == 0;

        this.x = x;
        this.y = y;
        this.z = z;
    }

    public CubeCoord(float x, float y, float z) {
        round(x, y, z);
    }

    public CubeCoord(OffsetCoord offsetCoord) {
        fromOffsetCoord(offsetCoord);
    }

    public CubeCoord(PointF gameCoord) {
        fromGameCoord(gameCoord);
    }

    /**
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(@Nullable java.lang.Object obj) {

        if (this != obj) {
            if (obj instanceof CubeCoord) {
                return this.x == ((CubeCoord) obj).x && this.y == ((CubeCoord) obj).y &&
                        this.z == ((CubeCoord) obj).y;
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

        return x + (y << 5) + (z << 10);
    }

    /**
     *
     * @param x
     * @param y
     * @param z
     */
    private void round(float x, float y, float z) {

        float rx = Math.round(x);
        float ry = Math.round(y);
        float rz = Math.round(z);

        float xDiff = Math.abs(rx - x);
        float yDiff = Math.abs(ry - y);
        float zDiff = Math.abs(rz - z);

        if (xDiff > yDiff && xDiff > zDiff) {
            rx = - ry - rz;
        }
        else if (yDiff > zDiff) {
            ry = - rx - rz;
        }
        else {
            rz = -rx - ry;
        }

        this.x = (int)rx;
        this.y = (int)ry;
        this.z = (int)rz;
    }

    /**
     *
     * @return
     */
    public OffsetCoord toOffsetCoord() {

        return new OffsetCoord(x + (z - (z & 1)) / 2, z);
    }

    /**
     *
     * @param offsetCoord
     */
    public void fromOffsetCoord(OffsetCoord offsetCoord) {

        x = offsetCoord.getX() - (offsetCoord.getY() - (offsetCoord.getY() & 1)) / 2;
        z = offsetCoord.getY();
        y = -x-z;
        HexSize = offsetCoord.GetHexSize();
    }

    /**
     *
     * @return
     */
    public PointF toGameCoord() {

        return new PointF(HexSize * (SQRT3 * x + SQRT3 / 2 * z),
                HexSize * (3.0f / 2 * z));

    }

    /**
     *
     * @param pt
     */
    public void fromGameCoord(PointF pt) {

        float x = (SQRT3 / 3 * pt.x - 1.0f / 3 * pt.y) / HexSize;
        float z = (2.0f / 3 * pt.y) / HexSize;
        float y = -x-z;

        round(x, y, z);
    }

    /**
     *
     * @param x
     * @param y
     * @param z
     */
    @SuppressLint("Assert")
    public void setPos(int x, int y, int z) {

        assert (x + y + z) == 0;

        this.x = x;
        this.y = y;
        this.z = z;
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
    public int getZ() {
        return z;
    }

    /**
     *
     * @param z
     */
    public void setZ(int z) {
        this.z = z;
    }

    /**
     *
     * @param cubeCoord
     * @return
     */
    public int getDistance(CubeCoord cubeCoord) {

        return (Math.abs(x - cubeCoord.x) + Math.abs(y - cubeCoord.y) + Math.abs(z - cubeCoord.z)) / 2;
    }

    /**
     *
     * @param cubeCoord
     * @return
     */
    public boolean isAdjacent(CubeCoord cubeCoord) {
        int xDiff = Math.abs(x - cubeCoord.x);
        int yDiff = Math.abs(y - cubeCoord.y);
        int zDiff = Math.abs(z - cubeCoord.z);

        return (Math.max(Math.max(xDiff, yDiff),zDiff) == 1);
    }

    public enum Direction {
        TOP_LEFT,
        TOP_RIGHT,
        RIGHT,
        BOTTOM_RIGHT,
        BOTTOM_LEFT,
        LEFT,
        UNKNOWN
    }

    /**
     *
     * @param cubeCoord
     * @return
     */
    public Direction getDirection(CubeCoord cubeCoord) {
        int xDiff = cubeCoord.x - x;
        int yDiff = cubeCoord.y - y;
        int zDiff = cubeCoord.z - z;
        if (xDiff == 0 && yDiff > 0 && zDiff < 0) {
            return Direction.TOP_LEFT;
        }
        else if (xDiff > 0 && yDiff == 0 && zDiff < 0) {
            return Direction.TOP_RIGHT;
        }
        else if (xDiff == 0 && yDiff < 0 && zDiff > 0) {
            return Direction.BOTTOM_RIGHT;
        }
        else if (xDiff < 0 && yDiff == 0 && zDiff > 0) {
            return Direction.BOTTOM_LEFT;
        }
        else if (xDiff < 0 && yDiff > 0 && zDiff == 0) {
            return Direction.LEFT;
        }
        else if (xDiff > 0 && yDiff < 0 && zDiff == 0) {
            return Direction.RIGHT;
        }
        else {
            return Direction.UNKNOWN;
        }
    }

    private static final float SQRT3 = (float)Math.sqrt(3);

    private int x;
    private int y;
    private int z;
}
