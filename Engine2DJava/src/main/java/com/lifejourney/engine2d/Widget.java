package com.lifejourney.engine2d;

import android.view.MotionEvent;

import java.util.ArrayList;

public abstract class Widget implements Controllable {

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
            return checkIfInputEventInRegion(event);
        }
    }

    protected boolean checkIfInputEventInRegion(MotionEvent event) {

        PointF touchedPos = Engine2D.GetInstance().translateScreenToWidgetCoord(
                new PointF(event.getX(), event.getY()));

        // FIXME: it should be checked differently for such as dragging events
        return region.includes(touchedPos);
    }

    /**
     *
     */
    public abstract void update();

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

    private Rect region;
    private RectF screenRegion;
    private int layer;
    private float depth;
    private boolean visible = false;
    private ArrayList<Sprite> sprites = new ArrayList<>();
}
