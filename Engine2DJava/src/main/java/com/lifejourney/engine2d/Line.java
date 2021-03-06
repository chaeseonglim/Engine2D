package com.lifejourney.engine2d;

import android.graphics.Color;
import android.util.Log;

public class Line {

    private static String LOG_TAG = "Line";

    public static class Builder {
        // required parameter
        private PointF begin;
        private PointF end;

        // optional
        private int layer = 0;
        private int color = Color.argb(255, 0, 0, 0);
        private float depth = 0.0f;
        private boolean visible = false;
        private float lineWidth = 1.0f;

        public Builder(PointF begin, PointF end) {
            this.begin = begin;
            this.end = end;
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
        public Builder lineWidth(float lineWidth) {
            this.lineWidth = lineWidth;
            return this;
        }
        public Line build() {
            return new Line(this);
        }
    };

    private Line(Builder builder) {
        begin       = builder.begin;
        end         = builder.end;
        color       = builder.color;
        layer       = builder.layer;
        depth       = builder.depth;
        visible     = builder.visible;
        lineWidth = builder.lineWidth;

        load();
    }

    /**
     *
     * @return
     */
    public boolean load() {
        id = nCreateLine(begin.x, begin.y, end.x, end.y,Color.red(color)/255.0f,
                Color.green(color)/255.0f, Color.blue(color)/255.0f,
                Color.alpha(color)/255.0f, layer, depth, lineWidth, visible);
        if (id == INVALID_ID) {
            Log.e(LOG_TAG, "Failed to create line");
            return false;
        }

        return true;
    }

    /**
     *
     */
    public void close() {
        if (id != INVALID_ID) {
            nDestroyLine(id);
            id = INVALID_ID;
        }
    }

    /**
     *
     */
    public void finalize() {
        if (id != INVALID_ID) {
            Log.w(LOG_TAG, "A line " + id + " is not properly closed");
            nDestroyLine(id);
        }
    }

    /**
     *
     */
    public void commit() {
        nSetProperties(id, begin.x, begin.y, end.x, end.y,
                Color.red(color), Color.green(color), Color.blue(color), Color.alpha(color),
                layer, depth, lineWidth, visible);
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
     * @param begin
     * @param vector
     */
    public void setPoints(PointF begin, Vector2D vector) {
        this.begin = begin;
        this.end = new PointF(begin.vectorize().add(vector));
    }

    /**
     *
     * @param begin
     * @param end
     */
    public void setPoints(PointF begin, PointF end) {
        this.begin = begin;
        this.end = end;
    }

    /**
     *
     * @param color
     */
    public void setColor(int color) {
        this.color = color;
    }

    /**
     *
     * @param lineWidth
     */
    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }

    private final int INVALID_ID = -1;

    private int id;
    private int layer;
    private PointF begin;
    private PointF end;
    private int color;
    private float depth;
    private float lineWidth;
    private boolean visible;

    private native int nCreateLine(float beginX, float beginY, float endX, float endY,
                                   float r, float g, float b, float a, int layer,
                                   float depth, float width, boolean visible);
    private native void nDestroyLine(int id);
    private native void nSetProperties(int id, float beginX, float beginY, float endX, float endY,
                                       float r, float g, float b, float a, int layer,
                                       float depth, float width, boolean visible);
}
