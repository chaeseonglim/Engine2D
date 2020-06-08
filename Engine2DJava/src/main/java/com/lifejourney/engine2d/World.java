package com.lifejourney.engine2d;

import android.util.Log;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class World {

    private static final String LOG_TAG = "World";

    /**
     *
     * @param worldSize
     */
    protected void initCollisionPool(Size worldSize) {
        collidablePool = new CollidablePool(worldSize);
    }

    /**
     *
     */
    protected void close() {
        mainView.close();
        for (View view : subViews) {
            view.close();
        }

        mainView = null;
        subViews.clear();
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
            postupdate();
            accumulatedTime -= dt;
        }
    }

    /**
     *
     */
    public void commit() {
        Engine2D.GetInstance().lockDraw();
        commitViews();
        commitObjects();
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
        Log.v(LOG_TAG, "updateView() enter");

        mainView.update();
        for (View view : subViews) {
            view.update();
        }

        Log.v(LOG_TAG, "updateView() leave");
    }

    /**
     *
     */
    private void updateObjects() {
        Log.v(LOG_TAG, "updateObject() enter");

        PriorityQueue<Object> updateQueue = new PriorityQueue<>();
        for (Object object : objects) {
            updateQueue.offer(object);
        }
        while (!updateQueue.isEmpty()) {
            updateQueue.poll().update();
        }

        Log.v(LOG_TAG, "collision detection enter");

        // Check collision
        collidablePool.checkCollision();

        Log.v(LOG_TAG, "collision detection leave");

        Log.v(LOG_TAG, "updateObject() leave");
    }

    /**
     *
     */
    private void commitViews() {
        mainView.commit();
        for (View view : subViews) {
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
    protected void addObject(CollidableObject object) {
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
     * @param view
     */
    protected void addMainView(View view) {
        mainView = view;
    }

    /**
     *
     * @param view
     */
    protected void removeMainView(View view) {
        mainView = null;
    }

    /**
     *
     * @return
     */
    protected View getMainView() {
        return mainView;
    }

    /**
     *
     * @param view
     */
    public void addSubView(View view) {
        subViews.add(view);
    }

    /**
     *
     * @param view
     */
    public void removeSubView(View view) {
        subViews.remove(view);
    }

    /**
     *
     * @return
     */
    public ArrayList<View> getSubViews() {
        return subViews;
    }

    /**
     *
     * @return
     */
    public float getDesiredFPS() {
        return desiredFPS;
    }

    /**
     *
     * @param desiredFPS
     */
    public void setDesiredFPS(float desiredFPS) {
        this.desiredFPS = desiredFPS;
    }

    private float desiredFPS = 20.0f;
    private long accumulatedTime;
    private long lastUpdateStartTime = System.currentTimeMillis();

    private View mainView;
    private ArrayList<View> subViews = new ArrayList<>();
    protected ArrayList<Object> objects = new ArrayList<>();
    private CollidablePool collidablePool;
}
