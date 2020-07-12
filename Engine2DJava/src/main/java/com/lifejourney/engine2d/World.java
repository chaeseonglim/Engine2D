package com.lifejourney.engine2d;

import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.PriorityQueue;

public class World {

    private static final String LOG_TAG = "World";

    /**
     * Should be called from subclass
     * @param worldSize
     */
    protected void initCollisionPool(Size worldSize, boolean response) {

        collidablePool = new CollidablePool(worldSize, response);
    }

    /**
     *
     */
    protected void close() {

        view.close();
        view = null;
    }

    public static Comparator<Controllable> controllableComparator = new Comparator<Controllable>() {

        public int compare(Controllable cont1, Controllable cont2){
            int result = Integer.compare(cont2.getLayer(), cont1.getLayer());
            if (result == 0) {
                return Float.compare(cont2.getDepth(), cont1.getDepth());
            }
            else {
                return result;
            }
        }
    };

    /**
     *
     * @param event
     * @return
     */
    public boolean onTouchEvent(MotionEvent event) {

        // controllables
        PriorityQueue<Controllable> controllableSorted =
                new PriorityQueue<>(controllables.size(), controllableComparator);
        for (Controllable controllable : controllables) {
            controllableSorted.offer(controllable);
        }
        while (!controllableSorted.isEmpty()) {
            Controllable controllable = controllableSorted.poll();
            assert controllable != null;
            if (controllable.onTouchEvent(event)) {
                return true;
            }
        }

        // view
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
            preUpdate();
            updateViews();
            updateObjects();
            updateWidgets();
            postUpdate();
            accumulatedTime -= dt;
        }
    }

    /**
     *
     */
    protected void preUpdate() {
    }

    /**
     *
     */
    protected void postUpdate() {
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
    protected void updateObjects() {

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
    public void commit() {

        Engine2D.GetInstance().lockDraw();
        commitView();
        commitObjects();
        commitWidgets();
        commitViewport();
        Engine2D.GetInstance().unlockDraw();
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
     */
    private void commitViewport() {

        Engine2D.GetInstance().commitViewport();
    }

    /**
     *
     * @param object
     */
    public void addObject(Object object) {

        if (object instanceof CollidableObject) {
            if (collidablePool != null ) {
                collidablePool.add((CollidableObject) object);
            }
        }
        if (object instanceof Controllable) {
            controllables.add((Controllable)object);
        }
        objects.add(object);
    }

    /**
     *
     * @param object
     */
    public void removeObject(Object object) {

        if (object instanceof CollidableObject) {
            if (collidablePool != null ) {
                collidablePool.remove((CollidableObject) object);
            }
        }
        if (object instanceof Controllable) {
            controllables.remove(object);
        }
        objects.remove(object);
    }

    /**
     *
     * @param widget
     */
    public void addWidget(Widget widget) {

        widgets.add(widget);
        controllables.add(widget);
    }

    /**
     *
     * @param widget
     */
    public void removeWidget(Widget widget) {

        widgets.remove(widget);
        controllables.remove(widget);
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

    private float desiredFPS = 30.0f;
    private long accumulatedTime;
    private long lastUpdateStartTime = System.currentTimeMillis();

    private View view;
    private ArrayList<Object> objects = new ArrayList<>();
    private ArrayList<Controllable> controllables = new ArrayList<>();
    private CollidablePool collidablePool;
    private ArrayList<Widget> widgets = new ArrayList<>();
}
