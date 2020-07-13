package com.lifejourney.engine2d;

import android.view.MotionEvent;

import java.util.ArrayList;

public class Widget implements Controllable {

    private final String LOG_TAG = "Widget";

    public Widget(Rect region, int layer, float depth) {
        this.region = region;
        this.layer = layer;
        this.depth = depth;
    }

    /**
     *
     */
    public void close() {

        for (Sprite sprite: sprites) {
            sprite.close();
        }
        sprites = null;

        for (Widget widget: widgets) {
            widget.close();
        }
        widgets = null;
    }

    /**
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!isVisible()) {
            return false;
        }
        else {
            for (Widget widget: widgets) {
                if (widget.onTouchEvent(event)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     *
     * @param event
     * @return
     */
    protected boolean checkIfInputEventInRegion(MotionEvent event) {

        PointF touchedPos = Engine2D.GetInstance().translateScreenToWidgetCoord(
                new PointF(event.getX(), event.getY()));

        return region.includes(touchedPos);
    }

    /**
     *
     */
    public void update() {

        for (Widget widget: widgets) {
            widget.update();
        }
    }

    /**
     *
     */
    public void commit() {

        Rect viewport = Engine2D.GetInstance().getViewport();
        screenRegion = new RectF(region);
        screenRegion.offset(viewport.x, viewport.y);

        for (Sprite sprite: sprites) {
            sprite.setPosition(screenRegion.center().clone());
            sprite.commit();
        }

        for (Widget widget: widgets) {
            widget.commit();
        }
}

    /**
     *
     * @param region
     */
    public void setRegion(Rect region) {
        this.region = region;
    }

    /**
     *
     * @return
     */
    public Rect getRegion() {
        return region;
    }

    /**
     *
     * @return
     */
    public RectF getScreenRegion() {
        return screenRegion;
    }

    /**
     *
     */
    public void show() {
        setVisible(true);
    }

    /**
     *
     */
    public void hide() {
        setVisible(false);
    }

    /**
     *
     * @param visible
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
        for (Sprite sprite: sprites) {
            sprite.setVisible(visible);
        }
        for (Widget widget: widgets) {
            widget.setVisible(visible);
        }
    }

    /**
     *
     * @return
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     *
     * @return
     */
    @Override
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
    @Override
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
     * @param sprite
     */
    public void addSprite(Sprite sprite) {
        sprites.add(sprite);
    }

    /**
     *
     * @param sprite
     */
    public void removeSprite(Sprite sprite) {
        sprites.remove(sprite);
    }

    /**
     *
     * @param widget
     */
    public void addWidget(Widget widget) {
        widgets.add(widget);
    }

    /**
     *
     * @param widget
     */
    public void removeWidget(Widget widget) {
        widgets.remove(widget);
    }

    private Rect region;
    private RectF screenRegion;
    private int layer;
    private float depth;
    private boolean visible = false;
    private ArrayList<Sprite> sprites = new ArrayList<>();
    private ArrayList<Widget> widgets = new ArrayList<>();
}
