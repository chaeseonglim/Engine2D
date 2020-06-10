package com.lifejourney.engine2d;

import android.view.MotionEvent;

import androidx.annotation.NonNull;

public class Widget implements Comparable<Widget> {

    private final String LOG_TAG = "Widget";

    public Widget(Rect region, int layer) {
        this.region = region;
        this.layer = layer;
    }

    /**
     *
     */
    public void close() {
    }

    /**
     *
     * @param event
     * @return
     */
    public boolean onTouchEvent(MotionEvent event)
    {
        if (!isVisible()) {
            return false;
        }
        else if (captureInput) {
            return true;
        }
        else {
            return checkIfInputEventInRegion(event);
        }
    }

    protected boolean checkIfInputEventInRegion(MotionEvent event) {
        PointF touchedPos = Engine2D.GetInstance().translateScreenToWidgetCoord(
                new PointF(event.getX(), event.getY()));

        // FIXME: it should be checked differently for such as dragging event
        return region.includes(touchedPos);
    }

    /**
     *
     * @param other
     * @return
     */
    @Override
    public int compareTo(@NonNull Widget other) {
        if (other == this) {
            return 0;
        }
        else {
            return compareLayer(other);
        }
    }

    /**
     *
     * @param other
     * @return
     */
    private int compareLayer(Widget other) {
        if (!other.captureInput && this.captureInput) {
            return -1;
        }
        else if (other.captureInput && !this.captureInput) {
            return 1;
        }
        else return Integer.compare(other.layer, this.layer);
    }

    /**
     *
     */
    public void update() {
    }

    /**
     *
     */
    public void commit() {
        Rect viewport = Engine2D.GetInstance().getViewport();
        screenRegion = new RectF(region);
        screenRegion.offset(viewport.x, viewport.y);
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
     * @param captureInput
     */
    public void setCaptureInput(boolean captureInput) {
        this.captureInput = captureInput;
    }

    /**
     *
     * @return
     */
    public boolean getCaptureInput() {
        return captureInput;
    }

    private Rect region;
    private RectF screenRegion;
    private int layer = 0;
    private boolean visible = false;
    private boolean captureInput = false;
}
