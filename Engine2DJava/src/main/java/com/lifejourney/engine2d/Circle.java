package com.lifejourney.engine2d;

import android.graphics.Color;
import android.util.Log;

public class Circle {

    private static String LOG_TAG = "Circle";

    public static class Builder {
        // required parameter
        private PointF center;
        private float radius;

        // optional
        private int layer = 0;
        private int color = Color.argb(255, 0, 0, 0);
        private float depth = 0.0f;
        private boolean visible = false;

        public Builder(PointF center, float radius) {
            this.center = center;
            this.radius = radius;
        }
        public Builder color(int color) {
            this.color = color;
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
        public Builder visible(boolean visible) {
            this.visible = visible;
            return this;
        }
        public Circle build() {
            return new Circle(this);
        }
    };

    private Circle(Builder builder) {
        center      = builder.center;
        radius      = builder.radius;
        color       = builder.color;
        layer       = builder.layer;
        depth       = builder.depth;
        visible     = builder.visible;

        load();
    }

    /**
     *
     * @return
     */
    public boolean load() {
        id = nCreateCircle(center.x, center.y, radius, Color.red(color)/255.0f,
                Color.green(color)/255.0f, Color.blue(color)/255.0f,
                Color.alpha(color)/255.0f, layer);
        if (id == INVALID_ID) {
            Log.e(LOG_TAG, "Failed to create circle");
            return false;
        }

        return true;
    }

    /**
     *
     */
    public void close() {
        if (id != INVALID_ID) {
            nDestroyCircle(id);
            id = INVALID_ID;
        }
    }

    /**
     *
     */
    public void finalize() {
        if (id != INVALID_ID) {
            Log.w(LOG_TAG, "A circle " + id + " is not properly closed");
            nDestroyCircle(id);
        }
    }

    /**
     *
     */
    public void commit() {
        nSetProperties(id, center.x, center.y, radius, Color.red(color)/255.0f,
                Color.green(color)/255.0f, Color.blue(color)/255.0f,
                Color.alpha(color)/255.0f, layer, depth, visible);
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
     * @param center
     */
    public void setCenter(PointF center) {
        this.center = center;
    }

    /**
     *
     * @param radius
     */
    public void setRadius(float radius) {
        this.radius = radius;
    }

    /**
     *
     * @param color
     */
    public void setColor(int color) {
        this.color = color;
    }

    private final int INVALID_ID = -1;

    private int id;
    private int layer;
    private PointF center;
    private float radius;
    private int color;
    private float depth;
    private boolean visible;

    private native int nCreateCircle(float centerX, float centerY, float radius,
                                     float r, float g, float b, float a, int layer);
    private native void nDestroyCircle(int id);
    private native void nSetProperties(int id, float centerX, float centerY, float radius,
                                       float r, float g, float b, float a, int layer,
                                       float depth, boolean visible);
}
