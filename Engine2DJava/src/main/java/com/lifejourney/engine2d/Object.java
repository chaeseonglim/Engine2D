package com.lifejourney.engine2d;

import androidx.core.util.Pair;

import java.util.ArrayList;

public class Object implements Comparable<Object> {

    private static String LOG_TAG = "Object";

    @SuppressWarnings("unchecked")
    public static class Builder<T extends Builder<T>> {
        // Required parameters
        protected PointF position;

        // Optional parameters
        protected float rotation = 0.0f;
        protected ArrayList<Pair<Sprite, Boolean>> sprites = new ArrayList<>();
        protected boolean visible = false;
        protected int updatePeriod = 1;
        protected int layer = 0;
        protected float depth = 0.0f;
        protected int priority = 0;

        public Builder(PointF position) {
            this.position = position;
        }
        public T rotation(float rotation) {
            this.rotation = rotation;
            return (T)this;
        }
        public T layer(int layer) {
            this.layer = layer;
            return (T)this;
        }
        public T depth(int depth) {
            this.depth = depth;
            return (T)this;
        }
        public T sprite(Sprite sprite) {
            this.sprites.add(new Pair<>(sprite, true));
            return (T)this;
        }
        public T sprite(Sprite sprite, boolean autoUpdate) {
            this.sprites.add(new Pair<>(sprite, autoUpdate));
            return (T)this;
        }
        public T visible(boolean visible) {
            this.visible = visible;
            return (T)this;
        }
        public T updatePeriod(int updatePeriod) {
            this.updatePeriod = updatePeriod;
            return (T)this;
        }
        public T priority(int priority) {
            this.priority = priority;
            return (T)this;
        }
        public Object build() {
            return new Object(this);
        }
    }

    protected Object(Builder builder) {

        position = builder.position;
        layer = builder.layer;
        depth = builder.depth;
        rotation = builder.rotation;
        visible = builder.visible;
        sprites = builder.sprites;
        updatePeriod = builder.updatePeriod;
        priority = builder.priority;
        updatePeriodLeft = (int) (Math.random()%updatePeriod);
    }

    /**
     *
     */
    public void close() {

        if (sprites != null) {
            for (Pair<Sprite, Boolean> pair: sprites) {
                pair.first.close();
            }
            sprites = null;
        }
    }

    /**
     *
     */
    public void update() {

        if (updatePeriodLeft == 0) {
            updatePeriodLeft = updatePeriod;
        }
        else {
            updatePeriodLeft--;
        }
    }

    /**
     *
     */
    public void commit() {

        if (sprites == null) {
            return;
        }

        for (int i = 0; i < sprites.size(); ++i) {
            Sprite sprite = sprites.get(i).first;
            if (sprites.get(i).second) {
                sprite.setPosition(position.clone());
                sprite.setDepth(depth + 0.1f*i);
                sprite.setLayer(layer);
                sprite.setRotation(rotation);
                sprite.setVisible(visible);
            }
            sprite.commit();
        }
    }

    /**
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(Object o) {

        if (o == this) {
            return 0;
        }
        else if (priority < o.priority) {
            return 1;
        }
        else {
            return -1;
        }
    }

    /**
     *
     * @return
     */
    public PointF getPosition() { return position; }

    /**
     *
     * @return
     */
    public Vector2D getPositionVector() {
        return new Vector2D(position.x, position.y);
    }

    /**
     *
     * @param position
     */
    public void setPosition(PointF position) {
        this.position = position;
    }

    /**
     *
     * @return
     */
    public float getRotation() {
        return rotation;
    }

    /**
     *
     * @param rotation
     */
    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    /**
     *
     * @return
     */
    public ArrayList<Pair<Sprite, Boolean>> getSprites() {
        return sprites;
    }

    /**
     *
     * @return
     */
    public Sprite getSprite(int i) {
        return sprites.get(i).first;
    }

    /**
     *
     * @param sprite
     */
    public void addSprite(Sprite sprite) {
        sprites.add(new Pair<>(sprite, true));
    }

    /**
     *
     * @param sprite
     */
    public void addSprite(Sprite sprite, boolean autoUpdate) {
        sprites.add(new Pair<>(sprite, autoUpdate));
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
     * @return
     */
    public PointF center() {
        return position;
    }

    /**
     *
     * @param updatePeriod
     */
    public void setUpdatePeriod(int updatePeriod) {
        this.updatePeriod = updatePeriod;
    }

    /**
     *
     * @return
     */
    public int getUpdatePeriod() {
        return updatePeriod;
    }

    /**
     *
     * @return
     */
    public int getUpdatePeriodLeft() {
        return updatePeriodLeft;
    }

    /**
     *
     * @param updatePeriodLeft
     */
    public void setUpdatePeriodLeft(int updatePeriodLeft) {
        this.updatePeriodLeft = updatePeriodLeft;
    }

    /**
     *
     * @return
     */
    public boolean isUpdatePossible() {
        return (updatePeriodLeft == 0);
    }

    /**
     *
     * @return
     */
    public boolean isDebugMode() {
        return debugMode;
    }

    /**
     *
     * @param debugMode
     */
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
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

    private PointF position;
    private int layer;
    private float depth;
    private float rotation;
    private boolean visible;
    private int priority;
    private ArrayList<Pair<Sprite, Boolean>> sprites;

    private int updatePeriod;
    private int updatePeriodLeft;

    private boolean debugMode = false;
}
