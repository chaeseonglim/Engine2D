package com.lifejourney.engine2d;

import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Objects;
import java.util.PriorityQueue;

public class World {

    private static final String LOG_TAG = "World";

    /**
     * Should be called from subclass
     * @param worldSize
     */
    protected void initCollisionPool(Size worldSize) {
        collidablePool = new CollidablePool(worldSize);
    }

    /**
     *
     */
    protected void close() {
        view.close();
        view = null;
    }

    /**
     *
     * @param event
     * @return
     */
    public boolean onTouchEvent(MotionEvent event)
    {
        PriorityQueue<Widget> widgetSorted = new PriorityQueue<>();
        for (Widget widget : widgets) {
            widgetSorted.offer(widget);
        }
        while (!widgetSorted.isEmpty()) {
            Widget widget = widgetSorted.poll();
            assert widget != null;
            if (widget.onTouchEvent(event)) {
                return true;
            }
        }
        if (view != null) {
            return view.onTouchEvent(event);
        }
        else {
            return false;
        }
    }

    /**
     *
     */
    public void update() {
        // Check the time elapsed since last update
        long currentTime = System.currentTimeMillis();
        accumulatedTime += currentTime - lastUpdateStartTime;
        lastUpdateStartTime = currentTime;

        // Prevent accumulatedTime goes up to infinite..
        if (accumulatedTime > 200)
            accumulatedTime = 200;

        long dt = (long) (1000.0f / desiredFPS);
        while (accumulatedTime > dt) {
            preupdate();
            updateViews();
            updateObjects();
            updateWidgets();
            postupdate();
            accumulatedTime -= dt;
        }
    }

    /**
     *
     */
    public void commit() {
        Engine2D.GetInstance().lockDraw();
        commitView();
        commitObjects();
        commitWidgets();
        Engine2D.GetInstance().unlockDraw();
    }

    /**
     *
     */
    protected void preupdate() {
    }

    /**
     *
     */
    protected void postupdate() {
    }

    /**
     *
     */
    private void updateViews() {
        view.update();
    }

    /**
     *
     */
    private void updateObjects() {
        PriorityQueue<Object> sortedObjects = new PriorityQueue<>();
        for (Object object : objects) {
            sortedObjects.offer(object);
        }
        while (!sortedObjects.isEmpty()) {
            Objects.requireNonNull(sortedObjects.poll()).update();
        }

        // Check collision
        if (collidablePool != null ) {
            collidablePool.checkCollision();
        }
    }

    /**
     *
     */
    private void updateWidgets() {
        for (Widget widget: widgets) {
            widget.update();
        }
    }

    /**
     *
     */
    private void commitView() {
        if (view != null) {
            view.commit();
        }
    }

    /**
     *
     */
    private void commitObjects() {
        for (Object object : objects) {
            object.commit();
        }
    }

    /**
     *
     */
    private void commitWidgets() {
        for (Widget widget: widgets) {
            widget.commit();
        }
    }

    /**
     *
     * @param object
     */
    public void addObject(Object object) {
        objects.add(object);
    }

    /**
     *
     * @param object
     */
    public void removeObject(Object object) {
        objects.remove(object);
    }

    /**
     *
     * @param object
     */
    public void addObject(CollidableObject object) {
        objects.add(object);
        collidablePool.addObject(object);
    }

    /**
     *
     * @param object
     */
    public void removeObject(CollidableObject object) {
        collidablePool.removeObject(object);
        objects.remove(object);
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

    /**
     *
     * @param view
     */
    protected void addView(View view) {
        this.view = view;
    }

    /**
     *
     * @param view
     */
    protected void removeView(View view) {
        this.view = null;
    }

    /**
     *
     * @return
     */
    protected View getView() {
        return view;
    }

    /**
     *
     * @param desiredFPS
     */
    public void setDesiredFPS(float desiredFPS) {
        this.desiredFPS = desiredFPS;
    }

    /**
     *
     * @return
     */
    public float getDesiredFPS() {
        return desiredFPS;
    }

    private float desiredFPS = 20.0f;
    private long accumulatedTime;
    private long lastUpdateStartTime = System.currentTimeMillis();

    private View view;
    private ArrayList<Object> objects = new ArrayList<>();
    private ArrayList<Widget> widgets = new ArrayList<>();
    private CollidablePool collidablePool;
}
