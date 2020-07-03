package com.lifejourney.engine2d;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class OffsetCoord {

    private final static String LOG_TAG = "OffsetCoord";

    private static int HexSize = 0;

    /**
     *
     * @return
     */
    public static int GetHexSize() {
        return OffsetCoord.HexSize;
    }

    /**
     *
     * @param hexSize
     */
    public static void SetHexSize(int hexSize) {
        OffsetCoord.HexSize = hexSize;
    }


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

    public OffsetCoord(Point gameCoord) {
        fromGameCoord(gameCoord);
    }

    public OffsetCoord(PointF gameCoord) {
        fromGameCoord(gameCoord);
    }

    /**
     *
     * @return
     */
    public OffsetCoord clone() {
        return new OffsetCoord(x, y);
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

        x = cubeCoord.getX() + (cubeCoord.getZ() - (cubeCoord.getZ() & 1)) / 2;
        y = cubeCoord.getZ();
    }

    /**
     *
     * @return
     */
    public PointF toGameCoord() {

        return new PointF((float)(HexSize * SQRT3 * (x + 0.5 * (y & 1))), (float) HexSize * 3/2 * y);

    }

    /**
     *
     * @param gameCoord
     */
    public void fromGameCoord(PointF gameCoord) {

        fromCubeCoord(new CubeCoord(gameCoord));
    }

    /**
     *
     * @param gameCoord
     */
    public void fromGameCoord(Point gameCoord) {

        fromGameCoord(new PointF(gameCoord));
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
     * @param x
     * @param y
     */
    public void offset(int x, int y) {

        this.x += x;
        this.y += y;
    }

    /**
     *
     * @param offsetCoord
     * @return
     */
    public int getDistance(OffsetCoord offsetCoord) {

        return toCubeCoord().getDistance(offsetCoord.toCubeCoord());
    }

    /**
     *
     * @return
     */
    public ArrayList<OffsetCoord> getNeighbors() {

        ArrayList<OffsetCoord> neighbors = new ArrayList<>();
        // Note: Its order is from top left to clock-wise direction
        if ((y & 1) == 0) {
            neighbors.add(new OffsetCoord(x - 1, y - 1));
            neighbors.add(new OffsetCoord(x, y - 1));
            neighbors.add(new OffsetCoord(x + 1, y));
            neighbors.add(new OffsetCoord(x, y + 1));
            neighbors.add(new OffsetCoord(x - 1, y + 1));
            neighbors.add(new OffsetCoord(x - 1, y));
        }
        else {
            neighbors.add(new OffsetCoord(x, y - 1));
            neighbors.add(new OffsetCoord(x + 1, y - 1));
            neighbors.add(new OffsetCoord(x + 1, y));
            neighbors.add(new OffsetCoord(x + 1, y + 1));
            neighbors.add(new OffsetCoord(x, y + 1));
            neighbors.add(new OffsetCoord(x - 1, y));
        }
        return neighbors;
    }

    /**
     *
     * @param offsetCoord
     * @return
     */
    public CubeCoord.Direction getDirection(OffsetCoord offsetCoord) {

        return toCubeCoord().getDirection(offsetCoord.toCubeCoord());
    }

    private static final float SQRT3 = (float)Math.sqrt(3);

    private int x;
    private int y;
}
