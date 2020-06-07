package com.lifejourney.engine2d;

import android.util.Log;

public class Sprite {

    private static String LOG_TAG = "Sprite";

    public static class Builder {
        // required parameter
        private String asset;

        // optional
        private Point position = new Point(0, 0);
        private Size size = new Size(0, 0);
        private int layer = 0;
        private float depth = 0.0f;
        private float rotation = 0.0f;
        private float opaque = 1.0f;
        private float[] colorize = { 1.0f, 1.0f, 1.0f };
        private boolean visible = false;
        private Size gridSize = new Size(1, 1);
        private boolean smooth = true;

        public Builder(String asset) {
            this.asset = asset;
        }
        public Builder position(Point position) {
            this.position = position;
            return this;
        }
        public Builder size(Size size) {
            this.size = size;
            return this;
        }
        public Builder layer(int layer) {
            this.layer = layer;
            return this;
        }
        public Builder depth(float depth) {
            this.depth = depth;
            return this;
        }
        public Builder opaque(float opaque) {
            this.opaque = opaque;
            return this;
        }
        public Builder rotation(float rotation) {
            this.rotation = rotation;
            return this;
        }
        public Builder visible(boolean visible) {
            this.visible = visible;
            return this;
        }
        public Builder gridSize(Size gridSize) {
            this.gridSize = gridSize;
            return this;
        }
        public Builder smooth(boolean smooth) {
            this.smooth = smooth;
            return this;

        }
        public Builder colorize(float r, float g, float b) {
            this.colorize[0] = r;
            this.colorize[1] = g;
            this.colorize[2] = b;
            return this;
        }
        public Sprite build() {
            return new Sprite(this);
        }
    };

    private Sprite(Builder builder) {
        position    = builder.position;
        size        = builder.size;
        layer       = builder.layer;
        depth       = builder.depth;
        opaque      = builder.opaque;
        colorize    = builder.colorize;
        rotation    = builder.rotation;
        asset       = builder.asset;
        visible     = builder.visible;
        gridSize    = builder.gridSize;
        smooth      = builder.smooth;
        gridIndex   = new Point();

        load();
    }

    /**
     *
     * @return
     */
    public boolean load() {
        ResourceManager resourceManager = Engine2D.GetInstance().getResourceManager();
        if (!resourceManager.loadTexture(asset, smooth)) {
            Log.e(LOG_TAG, "Failed to load texture");
            return false;
        }

        id = nCreateSprite(asset, gridSize.width, gridSize.height);
        if (id == INVALID_ID) {
            Log.e(LOG_TAG, "Failed to create sprite");
            return false;
        }

        return true;
    }

    /**
     *
     */
    public void close() {
        if (id != INVALID_ID) {
            nDestroySprite(id);
            id = INVALID_ID;
        }
    }

    /**
     *
     */
    public void finalize() {
        if (id != INVALID_ID) {
            Log.w(LOG_TAG, "A sprite " + id + " is not properly closed");
            nDestroySprite(id);
        }
    }

    /**
     *
     */
    public void commit() {
        nSetProperties(id, position.x, position.y, size.width, size.height, layer, depth, opaque,
                colorize, rotation, visible, gridIndex.x, gridIndex.y);
    }

    /**
     *
     * @return
     */
    public Point getPos() {
        return position;
    }

    /**
     *
     * @param position
     */
    public void setPos(Point position) {
        this.position = position;
    }

    /**
     *
     * @return
     */
    public Size getSize() {
        return size;
    }

    /**
     *
     * @param size
     */
    public void setSize(Size size) {
        this.size = size;
    }

    /**
     *
     * @return
     */
    public int getLayer() {
        return layer;
    }

    /**
     *
     * @param layer
     */
    public void setLayer(int layer) {
        this.layer = layer;
    }

    /**
     *
     * @return
     */
    public float getDepth() {
        return depth;
    }

    /**
     *
     * @param depth
     */
    public void setDepth(float depth) {
        this.depth = depth;
    }

    /**
     *
     * @return
     */
    public float getRotation() {
        return rotation;
    }

    /**
     *
     * @param rotation
     */
    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    /**
     *
     * @return
     */
    public String getAsset() {
        return asset;
    }

    /**
     *
     */
    public void show() {
        this.visible = true;
    }

    /**
     *
     */
    public void hide() {
        this.visible = false;
    }

    /**
     *
     * @return
     */
    public boolean isVisible() {
        return this.visible;
    }

    /**
     *
     * @param visible
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     *
     * @return
     */
    public Size getGridSize() {
        return gridSize;
    }

    /**
     *
     * @return
     */
    public Point getGridIndex() {
        return gridIndex;
    }

    /**
     *
     * @param gridIndex
     */
    public void setGridIndex(Point gridIndex) {
        this.gridIndex = gridIndex;
    }

    /**
     *
     * @param r
     * @param g
     * @param b
     */
    public void setColorize(float r, float g, float b) {
        colorize[0] = r;
        colorize[1] = g;
        colorize[2] = b;
    }

    /**
     *
     * @param colorize
     */
    public void setColorize(float[] colorize) {
        this.colorize = colorize;
    }

    private final int INVALID_ID = -1;

    private int id;
    private int layer;
    private Point position;
    private Size size;
    private float opaque;
    private float rotation;
    private float depth;
    private String asset;
    private boolean visible;
    private boolean smooth;
    private Size gridSize;
    private Point gridIndex;
    private float[] colorize;

    private native int nCreateSprite(String asset, int gridCols, int gridRows);
    private native void nDestroySprite(int id);
    private native void nSetProperties(int id, int x, int y, int width, int height, int layer,
                                       float depth, float opaque, float[] colorize,
                                       float rotation, boolean visible,
                                       int gridCol, int gridRow);
}
