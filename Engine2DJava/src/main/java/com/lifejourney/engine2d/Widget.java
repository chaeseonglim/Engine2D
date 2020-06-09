package com.lifejourney.engine2d;

import android.view.MotionEvent;

public class Widget {

    Widget(Rect region) {
        this.region = region;
    }

    /**
     *
     */
    void close() {

    }

    /**
     *
     * @param event
     * @return
     */
    boolean onTouchEvent(MotionEvent event)
    {
        // FIXME: it should be checked differently for such as dragging event
        return region.includes(new PointF(event.getX(), event.getY()));
    }

    void update() {
    }

    void commit() {
        Rect viewport = Engine2D.GetInstance().getViewport();
        screenRegion = new RectF(region);
        screenRegion.offset(viewport.x, viewport.y);
    }

    public RectF getScreenRegion() {
        return screenRegion;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void show() {
        setVisible(true);
    }

    public void hide() {
        setVisible(false);
    }

    public boolean isVisible() {
        return visible;
    }

    private Rect region;
    private RectF screenRegion;
    private boolean visible = false;
}
