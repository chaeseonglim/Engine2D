package com.lifejourney.engine2d;

import java.util.ArrayList;
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

        spriteMap.clear();
        refreshMap.clear();
    }

    /**
     *
     * @return
     */
    private Rect getRegionToCache() {

        Rect cachedRegion = new Rect(Engine2D.GetInstance().getViewport());

        // Adding gaps to viewport for caching more sprites around
        cachedRegion.x = Math.max(0, cachedRegion.x - (int)tileSize.width * cacheMargin);
        cachedRegion.width += tileSize.width * cacheMargin * 2;
        cachedRegion.y = Math.max(0, cachedRegion.y - (int)tileSize.height * cacheMargin);
        cachedRegion.height += tileSize.height * cacheMargin * 2;
        return cachedRegion;
    }

    /**
     *
     * @param mapCoord
     * @return
     */
    protected abstract ArrayList<Sprite> getTileSprites(OffsetCoord mapCoord);

    /**
     *
     * @param mapCoord
     * @return
     */
    public void redrawTileSprite(OffsetCoord mapCoord) {

        refreshMap.put(mapCoord, true);
    }

    /**
     *
     * @param mapCoord
     */
    public void removeTileSprites(OffsetCoord mapCoord) {

        // Do nothing here - it's up to child class
    }

    /**
     *
     */
    private void removeSpritesOutsideOfScreen() {

        Rect regionToCache = getRegionToCache();

        Iterator<HashMap.Entry<OffsetCoord, ArrayList<Sprite>>> iter = spriteMap.entrySet().iterator();
        while (iter.hasNext()) {
            HashMap.Entry<OffsetCoord, ArrayList<Sprite>> entry = iter.next();
            OffsetCoord mapCoord = entry.getKey();
            RectF spriteRegion = new RectF(new PointF(mapCoord.toGameCoord()), tileSize);
            spriteRegion.offset(-tileSize.width/2, -tileSize.height/2);
            if (!Rect.intersects(regionToCache, new Rect(spriteRegion))) {
                removeTileSprites(mapCoord);
                iter.remove();
            }
        }
    }

    /**
     *
     */
    public void update() {

        // Clean up sprites at outside of screen
        removeSpritesOutsideOfScreen();

        // Build up sprites map
        Size mapSize = getMapSize();
        Rect cachedRegion = getRegionToCache();

        OffsetCoord topLeft = new OffsetCoord();
        OffsetCoord bottomRight = new OffsetCoord();
        topLeft.fromGameCoord(cachedRegion.topLeft());
        bottomRight.fromGameCoord(cachedRegion.bottomRight());

        for (int y = Math.max(topLeft.getY(), 0); y < Math.min(bottomRight.getY(), mapSize.height); ++y) {
            for (int x = Math.max(topLeft.getX(), 0); x < Math.min(bottomRight.getX(), mapSize.width); ++x) {
                OffsetCoord mapCoord = new OffsetCoord(x, y);
                ArrayList<Sprite> sprites = spriteMap.get(mapCoord);
                if (sprites == null || refreshMap.get(mapCoord) != null) {
                    spriteMap.put(mapCoord, getTileSprites(mapCoord));
                    refreshMap.remove(mapCoord);
                }
            }
        }
    }

    /**
     *
     */
    public void commit() {

        for (HashMap.Entry<OffsetCoord, ArrayList<Sprite>> entry : spriteMap.entrySet()) {
            ArrayList<Sprite> spriteList = entry.getValue();
            for (Sprite sprite: spriteList) {
                sprite.commit();
            }
        }
    }

    /**
     *
     * @param visible
     */
    public void setVisible(boolean visible) {

        for (HashMap.Entry<OffsetCoord, ArrayList<Sprite>> entry : spriteMap.entrySet()) {
            ArrayList<Sprite> spriteList = entry.getValue();
            for (Sprite sprite: spriteList) {
                sprite.setVisible(visible);
            }
        }
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
    public SizeF getTileSize() {

        return tileSize;
    }

    /**
     *
     * @param tileSize
     */
    public void setTileSize(SizeF tileSize) {

        this.tileSize = tileSize;
    }

    /**
     *
     * @return
     */
    public int[][] getMapData() {

        return mapData;
    }

    /**
     *
     * @return
     */
    public int getMapData(OffsetCoord mapCoord) {

        return mapData[mapCoord.getY()][mapCoord.getX()];
    }

    /**
     *
     * @param mapData
     */
    public void setMapData(int[][] mapData) {

        this.mapData = mapData;
    }

    /**
     *
     * @param hexSize
     */
    public void setHexSize(int hexSize) {

        OffsetCoord.SetHexSize(hexSize);
        CubeCoord.SetHexSize(hexSize);
        tileSize = new SizeF(SQRT3 * hexSize, 2 * hexSize);
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

    private int[][] mapData;
    private HashMap<OffsetCoord, ArrayList<Sprite>> spriteMap = new HashMap<>();
    private HashMap<OffsetCoord, Boolean> refreshMap = new HashMap<>();
    private Size mapSize;
    private SizeF tileSize;
    private int cacheMargin = 2;
}
