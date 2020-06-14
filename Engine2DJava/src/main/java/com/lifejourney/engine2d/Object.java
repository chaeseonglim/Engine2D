package com.lifejourney.engine2d;

import java.util.ArrayList;

public class Object implements Comparable<Object> {

    private static String LOG_TAG = "Object";

    @SuppressWarnings("unchecked")
    public static class Builder<T extends Builder<T>> {
        // Required parameters
        protected PointF position;

        // Optional parameters
        protected float rotation = 0.0f;
        protected ArrayList<Sprite> sprites = new ArrayList<>();
        protected ArrayList<Boolean> spritesAutoUpdate = new ArrayList<>();
        protected boolean visible = false;
        protected int updatePeriod = 1;
        protected int priority = 0;

        public Builder(PointF position) {
            this.position = position;
        }
        public T rotation(float rotation) {
            this.rotation = rotation;
            return (T)this;
        }
        public T sprite(Sprite sprite) {
            this.sprites.add(sprite);
            this.spritesAutoUpdate.add(true);
            return (T)this;
        }
        public T sprite(Sprite sprite, boolean autoUpdate) {
            this.sprites.add(sprite);
            this.spritesAutoUpdate.add(autoUpdate);
            return (T)this;
        }
        public T sprites(ArrayList<Sprite> sprites) {
            this.sprites = sprites;
            for (int i = 0; i < sprites.size(); ++i) {
                this.spritesAutoUpdate.add(true);
            }
            return (T)this;
        }
        public T sprites(ArrayList<Sprite> sprites, ArrayList<Boolean> spritesAutoUpdate) {
            this.sprites = sprites;
            this.spritesAutoUpdate = spritesAutoUpdate;
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
        rotation = builder.rotation;
        visible = builder.visible;
        sprites = builder.sprites;
        spritesAutoUpdate = builder.spritesAutoUpdate;
        updatePeriod = builder.updatePeriod;
        priority = builder.priority;
        updatePeriodLeft = (int) (Math.random()%updatePeriod);
    }

    /**
     *
     */
    public void close() {

        if (sprites != null) {
            for (Sprite sprite: sprites) {
                sprite.close();
            }
            sprites = null;
            spritesAutoUpdate = null;
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
            Sprite sprite = sprites.get(i);
            if (spritesAutoUpdate.get(i)) {
                sprite.setPosition(new Point(position));
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
     * @param sprites
     */
    public void setSprites(ArrayList<Sprite> sprites) {
        this.sprites = sprites;
        this.spritesAutoUpdate = new ArrayList<>();
        for (int i = 0; i < sprites.size(); ++i) {
            this.spritesAutoUpdate.add(true);
        }
    }

    /**
     *
     * @param sprites
     */
    public void setSprites(ArrayList<Sprite> sprites, ArrayList<Boolean> spritesAutoUpdate) {
        this.sprites = sprites;
        this.spritesAutoUpdate = spritesAutoUpdate;
    }

    /**
     *
     * @return
     */
    public ArrayList<Sprite> getSprites() {
        return sprites;
    }

    /**
     *
     * @return
     */
    public Sprite getSprite(int i) {
        return sprites.get(i);
    }

    /**
     *
     * @param sprite
     */
    public void addSprite(Sprite sprite) {
        this.sprites.add(sprite);
        this.spritesAutoUpdate.add(true);
    }

    /**
     *
     * @param sprite
     */
    public void addSprite(Sprite sprite, boolean autoUpdate) {
        this.sprites.add(sprite);
        this.spritesAutoUpdate.add(autoUpdate);
    }

    /**
     *
     * @param index
     * @param autoUpdate
     */
    public void setSpritesAutoUpdate(int index, boolean autoUpdate) {
        spritesAutoUpdate.set(index, autoUpdate);
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

    private PointF position;
    private float rotation;
    private boolean visible;
    private int priority;
    private ArrayList<Sprite> sprites;
    private ArrayList<Boolean> spritesAutoUpdate;

    private int updatePeriod;
    private int updatePeriodLeft;

    private boolean debugMode = false;
}
