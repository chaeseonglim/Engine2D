package com.lifejourney.engine2d;

import java.util.HashMap;
import java.util.Iterator;

public abstract class HexTileMap {

    private final String LOG_TAG = "HexTileMap";

    public HexTileMap(int hexSize) {
        tileSize = new Size((int) Math.round(Math.sqrt(3) * hexSize), 2 * hexSize);
        sprites = new HashMap<>();
    }

    /**
     *
     */
    public void close() {
        for (HashMap.Entry<OffsetCoord, Sprite> entry: sprites.entrySet()) {
            entry.getValue().close();
        }
        sprites = new HashMap<>();
    }

    /**
     *
     * @return
     */
    private Rect getRegionToCache() {
        Rect cachedRegion = new Rect(Engine2D.GetInstance().getViewport());

        // Adding gaps to viewport for caching more sprites around
        cachedRegion.x = Math.max(0, cachedRegion.x - tileSize.width * 2);
        cachedRegion.width += tileSize.width * 4;
        cachedRegion.y = Math.max(0, cachedRegion.y - tileSize.height * 2);
        cachedRegion.height += tileSize.height * 4;
        return cachedRegion;
    }

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


    public void update() {

        OffsetCoord.setHexSize(tileSize.height / 2);
        CubeCoord.setHexSize(tileSize.height / 2);

        // clean up unused spries
        removeUnusedSprites();

        // build up sprites
        Size trackDataSize = getMapSize();
        Rect cachedRegion = getRegionToCache();

        OffsetCoord topLeft = new OffsetCoord();
        OffsetCoord bottomRight = new OffsetCoord();
        topLeft.fromScreenCoord(cachedRegion.topLeft());
        bottomRight.fromScreenCoord(cachedRegion.bottomRight());

        for (int y = Math.max(topLeft.getY(), 0); y < Math.min(bottomRight.getY(), trackDataSize.height); ++y) {
            for (int x = Math.max(topLeft.getX(), 0); x < Math.min(bottomRight.getX(), trackDataSize.width); ++x) {
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

    private byte[][] mapData;
    private HashMap<OffsetCoord, Sprite> sprites;
    private Size mapSize;
    private Size tileSize;
}
