package com.lifejourney.engine2d;

import android.graphics.Color;

import java.util.ArrayList;

public class Rectangle {

    private static String LOG_TAG = "Rectangle";

    public static class Builder {
        // required parameter
        private RectF region;

        // optional
        private int layer = 0;
        private int color = Color.argb(255, 0, 0, 0);
        private float depth = 0.0f;
        private boolean visible = false;
        private float lineWidth = 1.0f;

        public Builder(RectF region) {
            this.region = region;
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
        public Rectangle build() {
            return new Rectangle(this);
        }
    };

    private Rectangle(Rectangle.Builder builder) {
        region      = builder.region;
        color       = builder.color;
        layer       = builder.layer;
        depth       = builder.depth;
        visible     = builder.visible;
        lineWidth   = builder.lineWidth;

        lines.add(new Line.Builder(region.topLeft().offset(0, -lineWidth/2),
                region.bottomLeft().offset(0, lineWidth/2)).color(color)
            .layer(layer).depth(depth).lineWidth(lineWidth).visible(visible).build());
        lines.add(new Line.Builder(region.topLeft().offset(-lineWidth/2, 0),
                region.topRight().offset(lineWidth/2, 0)).color(color)
                .layer(layer).depth(depth).lineWidth(lineWidth).visible(visible).build());
        lines.add(new Line.Builder(region.topRight().offset(0, -lineWidth/2),
                region.bottomRight().offset(0, lineWidth/2)).color(color)
                .layer(layer).depth(depth).lineWidth(lineWidth).visible(visible).build());
        lines.add(new Line.Builder(region.bottomLeft().offset(-lineWidth/2, 0),
                region.bottomRight().offset(lineWidth/2, 0)).color(color)
                .layer(layer).depth(depth).lineWidth(lineWidth).visible(visible).build());
    }

    /**
     *
     */
    public void close() {
        for (Line line: lines) {
            line.close();
        }
    }

    /**
     *
     */
    public void commit() {
        for (Line line: lines) {
            line.commit();
        }
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
        for (Line line: lines) {
            line.setLayer(layer);
        }
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
        for (Line line: lines) {
            line.setDepth(depth);
        }
    }

    /**
     *
     */
    public void show() {
        this.visible = true;
        for (Line line: lines) {
            line.show();
        }
    }

    /**
     *
     */
    public void hide() {
        this.visible = false;
        for (Line line: lines) {
            line.hide();
        }
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
        for (Line line: lines) {
            line.setVisible(visible);
        }
    }

    /**
     *
     * @param region
     */
    public void setRegion(RectF region) {
        this.region = region;
        lines.get(0).setPoints(region.topLeft().offset(0, -lineWidth/2),
                region.bottomLeft().offset(0, lineWidth/2));
        lines.get(1).setPoints(region.topLeft().offset(-lineWidth/2, 0),
                region.topRight().offset(lineWidth/2, 0));
        lines.get(2).setPoints(region.topRight().offset(0, -lineWidth/2),
                region.bottomRight().offset(0, lineWidth/2));
        lines.get(3).setPoints(region.bottomLeft().offset(-lineWidth/2, 0),
                region.bottomRight().offset(lineWidth/2, 0));
    }

    /**
     *
     * @param color
     */
    public void setColor(int color) {
        this.color = color;
        for (Line line: lines) {
            line.setColor(color);
        }
    }

    /**
     *
     * @param lineWidth
     */
    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
        for (Line line: lines) {
            line.setLineWidth(lineWidth);
        }
    }

    private RectF region;
    private ArrayList<Line> lines = new ArrayList<>();
    private int layer;
    private int color;
    private float depth;
    private float lineWidth;
    private boolean visible;
}
