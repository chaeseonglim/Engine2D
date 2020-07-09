package com.lifejourney.engine2d;

import android.util.Log;

import androidx.core.util.Pair;

import java.util.ArrayList;

public class Sprite {

    private static String LOG_TAG = "Sprite";

    public static class Builder<T extends Builder<T>> {
        // required parameter
        private String name;

        // optional
        private String asset;
        private byte[] rawBytes = null;
        private PointF position = new PointF();
        private PointF positionOffset = new PointF();
        private SizeF size = new SizeF();
        private int layer = 0;
        private float depth = 0.0f;
        private float rotation = 0.0f;
        private float opaque = 1.0f;
        private float[] colorize = { 1.0f, 1.0f, 1.0f };
        private boolean visible = false;
        private Size gridSize = new Size(1, 1);
        private boolean smooth = true;

        public Builder(String asset) {
            this.name = this.asset = asset;
        }
        public Builder(String name, String asset) {
            this.name = name;
            this.asset = asset;
        }
        public Builder(String name, byte[] rawBytes) {
            this.name = this.asset = name;
            this.rawBytes = rawBytes;
        }
        public T position(PointF position) {
            this.position = position;
            return (T)this;
        }
        public T positionOffset(PointF positionOffset) {
            this.positionOffset = positionOffset;
            return (T)this;
        }
        public T size(SizeF size) {
            this.size = size;
            return (T)this;
        }
        public T layer(int layer) {
            this.layer = layer;
            return (T)this;
        }
        public T depth(float depth) {
            this.depth = depth;
            return (T)this;
        }
        public T opaque(float opaque) {
            this.opaque = opaque;
            return (T)this;
        }
        public T rotation(float rotation) {
            this.rotation = rotation;
            return (T)this;
        }
        public T visible(boolean visible) {
            this.visible = visible;
            return (T)this;
        }
        public T gridSize(int cols, int rows) {
            this.gridSize = new Size(cols, rows);
            return (T)this;
        }
        public T smooth(boolean smooth) {
            this.smooth = smooth;
            return (T)this;

        }
        public T colorize(float r, float g, float b) {
            this.colorize[0] = r;
            this.colorize[1] = g;
            this.colorize[2] = b;
            return (T)this;
        }
        public Sprite build() {
            return new Sprite(this, true);
        }
    };

    protected Sprite(Builder builder, boolean needLoad) {
        name            = builder.name;
        asset           = builder.asset;
        rawBytes        = builder.rawBytes;
        position        = builder.position;
        positionOffset  = builder.positionOffset;
        size            = builder.size;
        layer           = builder.layer;
        depth           = builder.depth;
        opaque          = builder.opaque;
        colorize        = builder.colorize;
        rotation        = builder.rotation;
        visible         = builder.visible;
        gridSize        = builder.gridSize;
        smooth          = builder.smooth;
        animation = new ArrayList<>();
        animation.add(new Pair<>(new Point(0, 0), 1));

        if (needLoad) {
            load();
        }
    }

    /**
     *
     * @return
     */
    public boolean load() {

        ResourceManager resourceManager = Engine2D.GetInstance().getResourceManager();
        if (rawBytes != null) {
            if (!resourceManager.loadTexture(asset, rawBytes, smooth)) {
                Log.e(LOG_TAG, "Failed to load texture");
                return false;
            }

            // release memory
            rawBytes = null;
        }
        else {
            if (!resourceManager.loadTexture(asset, smooth)) {
                Log.e(LOG_TAG, "Failed to load texture");
                return false;
            }
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

        Point grid = getNextGridIndex();
        nSetProperties(id, position.x + positionOffset.x, position.y + positionOffset.y,
                size.width, size.height, layer, depth, opaque,
                colorize, rotation, visible, grid.x, grid.y);
    }

    /**
     *
     * @return
     */
    public PointF getPosition() {
        return position;
    }

    /**
     *
     * @param position
     */
    public void setPosition(PointF position) {
        this.position = position;
    }

    /**
     *
     * @return
     */
    public SizeF getSize() {
        return size;
    }

    /**
     *
     * @param size
     */
    public void setSize(SizeF size) {
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
        if (animation.size() <= currentAnimationIndex) {
            return new Point(0, 0);
        }
        return animation.get(currentAnimationIndex).first;
    }

    private Point getNextGridIndex() {
        if (animation.size() <= currentAnimationIndex) {
            return new Point(0, 0);
        }

        int maxStayingTime = animation.get(currentAnimationIndex).second;

        if (++currentAnimationStayingTime >= maxStayingTime) {
            if (currentAnimationIndex + 1 >= animation.size()) {
                if (animationWrap) {
                    currentAnimationIndex = 0;
                }
            }
            else {
                currentAnimationIndex++;
            }
            currentAnimationStayingTime = 0;
        }

        return animation.get(currentAnimationIndex).first;
    }

    /**
     *
     * @param cols
     * @param rows
     */
    public void setGridIndex(int cols, int rows) {
        animation.clear();
        animation.add(new Pair<>(new Point(cols, rows), 1));
        this.currentAnimationIndex = 0;
        this.currentAnimationStayingTime = 0;
    }

    /**
     *
     * @param animation
     */
    public void setAnimation(ArrayList<Pair<Point, Integer>> animation) {
        this.animation = animation;
        this.currentAnimationIndex = 0;
        this.currentAnimationStayingTime = 0;
    }

    /**
     *
     * @param cols
     * @param rows
     * @param stayingTime
     */
    public void addAnimationFrame(int cols, int rows, int stayingTime) {
        this.animation.add(new Pair<>(new Point(cols, rows), stayingTime));
        this.currentAnimationIndex = 0;
        this.currentAnimationStayingTime = 0;
    }

    /**
     *
     */
    public void clearAnimation() {
        this.animation.clear();
    }

    /**
     *
     * @return
     */
    public ArrayList<Pair<Point, Integer>> getAnimation() {
        return animation;
    }

    /**
     *
     * @param animationWrap
     */
    public void setAnimationWrap(boolean animationWrap) {
        this.animationWrap = animationWrap;
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

    /**
     *
     * @return
     */
    public float getOpaque() {
        return opaque;
    }

    /**
     *
     * @param opaque
     */
    public void setOpaque(float opaque) {
        this.opaque = opaque;
    }

    /**
     *
     * @return
     */
    public PointF getPositionOffset() {
        return positionOffset;
    }

    /**
     *
     * @param positionOffset
     */
    public void setPositionOffset(PointF positionOffset) {
        this.positionOffset = positionOffset;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    protected final int INVALID_ID = -1;

    protected String name;
    protected String asset;
    protected byte[] rawBytes;
    protected int id;
    protected int layer;
    protected PointF position;
    protected SizeF size;
    protected float opaque;
    protected float rotation;
    protected float depth;
    protected boolean visible;
    protected boolean smooth;
    protected Size gridSize;
    protected float[] colorize;
    protected PointF positionOffset;
    protected ArrayList<Pair<Point, Integer>> animation;
    protected int currentAnimationIndex = 0;
    protected int currentAnimationStayingTime = 0;
    protected boolean animationWrap = false;

    protected native int nCreateSprite(String asset, int gridCols, int gridRows);
    protected native void nDestroySprite(int id);
    protected native void nSetProperties(int id, float x, float y, float width, float height, int layer,
                                       float depth, float opaque, float[] colorize,
                                       float rotation, boolean visible,
                                       int gridCol, int gridRow);
}
