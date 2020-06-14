package com.lifejourney.engine2d;

import java.util.HashMap;
import java.util.Iterator;

public abstract class HexTileMap {

    private final String LOG_TAG = "HexTileMap";

    public HexTileMap(int hexSize) {

        setHexSize(hexSize);
    }

    /**
     *
     */
    public void close() {

        for (HashMap.Entry<OffsetCoord, Sprite> entry: sprites.entrySet()) {
            entry.getValue().close();
        }
        sprites.clear();
    }

    /**
     *
     * @return
     */
    private Rect getRegionToCache() {

        Rect cachedRegion = new Rect(Engine2D.GetInstance().getViewport());

        // Adding gaps to viewport for caching more sprites around
        cachedRegion.x = Math.max(0, cachedRegion.x - tileSize.width * cacheMargin);
        cachedRegion.width += tileSize.width * cacheMargin * 2;
        cachedRegion.y = Math.max(0, cachedRegion.y - tileSize.height * cacheMargin);
        cachedRegion.height += tileSize.height * cacheMargin * 2;
        return cachedRegion;
    }

    /**
     *
     * @param offsetCoord
     * @return
     */
    protected abstract Sprite getTileSprite(OffsetCoord offsetCoord);

    /**
     *
     */
    private void removeUnusedSprites() {

        Rect regionToCache = getRegionToCache();

        Iterator<HashMap.Entry<OffsetCoord, Sprite>> iter = sprites.entrySet().iterator();
        while (iter.hasNext()) {
            HashMap.Entry<OffsetCoord, Sprite> entry = iter.next();
            OffsetCoord offsetCoord = entry.getKey();
            Rect spriteRegion = new Rect(offsetCoord.toScreenCoord(), tileSize);
            spriteRegion.offset(-tileSize.width/2, -tileSize.height/2);
            if (!Rect.intersects(regionToCache, spriteRegion)) {
                Sprite sprite = entry.getValue();
                sprite.close();
                iter.remove();
            }
        }
    }

    /**
     *
     */
    public void update() {

        // clean up unused spries
        removeUnusedSprites();

        // build up sprites
        Size mapSize = getMapSize();
        Rect cachedRegion = getRegionToCache();

        OffsetCoord topLeft = new OffsetCoord();
        OffsetCoord bottomRight = new OffsetCoord();
        topLeft.fromScreenCoord(cachedRegion.topLeft());
        bottomRight.fromScreenCoord(cachedRegion.bottomRight());

        for (int y = Math.max(topLeft.getY(), 0); y < Math.min(bottomRight.getY(), mapSize.height); ++y) {
            for (int x = Math.max(topLeft.getX(), 0); x < Math.min(bottomRight.getX(), mapSize.width); ++x) {
                OffsetCoord offsetCoord = new OffsetCoord(x, y);
                if (sprites.get(offsetCoord) == null) {
                    sprites.put(offsetCoord, getTileSprite(offsetCoord));
                }
            }
        }
    }

    /**
     *
     */
    public void commit() {

        for (HashMap.Entry<OffsetCoord, Sprite> entry : sprites.entrySet()) {
            entry.getValue().commit();
        }
    }

    /**
     *
     * @param visible
     */
    public void setVisible(boolean visible) {

        for (HashMap.Entry<OffsetCoord, Sprite> entry : sprites.entrySet()) {
            entry.getValue().setVisible(visible);
        }
    }

    /**
     *
     * @return
     */
    public Size getMapSize() {

        return mapSize;
    }

    /**
     *
     * @param size
     */
    public void setMapSize(Size size) {

        this.mapSize = size;
    }

    /**
     *
     * @return
     */
    public Size getTileSize() {

        return tileSize;
    }

    /**
     *
     * @param tileSize
     */
    public void setTileSize(Size tileSize) {

        this.tileSize = tileSize;
    }

    /**
     *
     * @return
     */
    public byte[][] getMapData() {

        return mapData;
    }

    /**
     *
     * @return
     */
    public byte getMapData(OffsetCoord offsetCoord) {

        return mapData[offsetCoord.getY()][offsetCoord.getX()];
    }

    /**
     *
     * @param mapData
     */
    public void setMapData(byte[][] mapData) {

        this.mapData = mapData;
    }

    /**
     *
     * @param offsetCoord
     * @param sprite
     */
    public void addSprite(OffsetCoord offsetCoord, Sprite sprite) {

        sprites.put(offsetCoord, sprite);
    }

    /**
     *
     * @param hexSize
     */
    public void setHexSize(int hexSize) {
        OffsetCoord.setHexSize(hexSize);
        CubeCoord.setHexSize(hexSize);

        tileSize = new Size((int) Math.round(SQRT3 * hexSize), 2 * hexSize);
    }

    /**
     *
     * @return
     */
    public int getCacheMargin() {
        return cacheMargin;
    }

    /**
     *
     * @param cacheMargin
     */
    public void setCacheMargin(int cacheMargin) {
        this.cacheMargin = cacheMargin;
    }

    private final static float SQRT3 = (float) Math.sqrt(3.0);

    private byte[][] mapData;
    private HashMap<OffsetCoord, Sprite> sprites = new HashMap<>();
    private Size mapSize;
    private Size tileSize;
    private int cacheMargin = 2;
}
