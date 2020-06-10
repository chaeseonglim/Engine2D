package com.lifejourney.engine2d;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.Log;

import androidx.core.util.Pair;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class Sprite {

    private static String LOG_TAG = "Sprite";

    public static class Builder {
        // required parameter
        private String name;

        // optional
        private byte[] data = null;
        private String text = "";
        private float fontSize = 0.0f;
        private int fontColor = Color.argb(255, 255, 255, 255);
        private Paint.Align textAlign = Paint.Align.LEFT;
        private int bgColor = Color.argb(0, 0, 0, 0);
        private Point position = new Point(0, 0);
        private Size size = new Size(0, 0);
        private int layer = 0;
        private float depth = 0.0f;
        private float rotation = 0.0f;
        private float opaque = 1.0f;
        private float[] colorize = { 1.0f, 1.0f, 1.0f };
        private boolean visible = false;
        private Size gridSize = new Size(1, 1);
        private boolean smooth = true;

        public Builder(String name) {
            this.name = name;
        }
        public Builder(String name, byte[] data) {
            this.name = name;
            this.data = data;
        }
        public Builder(String name, String text, float fontSize, int fontColor, int bgColor, Paint.Align align) {
            this.name = name;
            this.text = text;
            this.fontSize = fontSize;
            this.fontColor = fontColor;
            this.bgColor = bgColor;
            this.textAlign = align;
        }
        public Builder position(Point position) {
            this.position = position;
            return this;
        }
        public Builder size(Size size) {
            this.size = size;
            return this;
        }
        public Builder layer(int layer) {
            this.layer = layer;
            return this;
        }
        public Builder depth(float depth) {
            this.depth = depth;
            return this;
        }
        public Builder opaque(float opaque) {
            this.opaque = opaque;
            return this;
        }
        public Builder rotation(float rotation) {
            this.rotation = rotation;
            return this;
        }
        public Builder visible(boolean visible) {
            this.visible = visible;
            return this;
        }
        public Builder gridSize(Size gridSize) {
            this.gridSize = gridSize;
            return this;
        }
        public Builder smooth(boolean smooth) {
            this.smooth = smooth;
            return this;

        }
        public Builder colorize(float r, float g, float b) {
            this.colorize[0] = r;
            this.colorize[1] = g;
            this.colorize[2] = b;
            return this;
        }
        public Sprite build() {
            return new Sprite(this);
        }
    };

    private Sprite(Builder builder) {
        position        = builder.position;
        size            = builder.size;
        rawData         = builder.data;
        text            = builder.text;
        fontSize        = builder.fontSize;
        fontColor       = builder.fontColor;
        bgColor         = builder.bgColor;
        textAlign       = builder.textAlign;
        layer           = builder.layer;
        depth           = builder.depth;
        opaque          = builder.opaque;
        colorize        = builder.colorize;
        rotation        = builder.rotation;
        name            = builder.name;
        visible         = builder.visible;
        gridSize        = builder.gridSize;
        smooth          = builder.smooth;
        animationList   = new ArrayList<>();
        animationList.add(new Pair<>(new Point(0, 0), 1));

        load();
    }

    private byte[] drawTextToByteArray(String text, float fontSize, int fontColor, Paint.Align align) {
        if (fontSize < 8.0f)
            fontSize = 8.0f;
        if (fontSize > 500.0f)
            fontSize = 500.0f;
        Paint textPaint = new Paint();
        textPaint.setTextSize(fontSize);
        textPaint.setFakeBoldText(false);
        textPaint.setAntiAlias(true);
        textPaint.setColor(fontColor);
        // If a hinting is available on the platform you are developing, you should enable it (uncomment the line below).
        //textPaint.setHinting(Paint.HINTING_ON);
        textPaint.setSubpixelText(true);
        textPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
        // Creates a new mutable bitmap, with 128px of width and height
        //int bitmapWidth = (int) (realTextWidth + 2.0f);
        //int bitmapHeight = (int) aFontSize + 2;
        Bitmap textBitmap = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888);
        textBitmap.eraseColor(bgColor);
        // Creates a new canvas that will draw into a bitmap instead of rendering into the screen
        Canvas bitmapCanvas = new Canvas(textBitmap);
        // Set start drawing position to [1, base_line_position]
        // The base_line_position may vary from one font to another but it usually is equal to 75% of font size (height).
        float y = textPaint.descent() - textPaint.ascent();

        if (align == Paint.Align.CENTER) {
            int lineSize = text.split("\n").length;
            y += (size.height - y * lineSize) / 2 - 5;
        }
        for (String line: text.split("\n")) {
            int x;
            float realTextWidth = textPaint.measureText(line);
            if (align == Paint.Align.LEFT) {
                x = 0;
            }
            else if (align == Paint.Align.CENTER) {
                x = (int) ((size.width - realTextWidth) / 2);

            }
            else {
                x = (int) (size.width - realTextWidth);
            }
            bitmapCanvas.drawText(line, x, y, textPaint);
            y += textPaint.descent() - textPaint.ascent();
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        textBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream) ;
        return stream.toByteArray();
    }

    /**
     *
     * @return
     */
    public boolean load() {
        if (!text.isEmpty()) {
            rawData = drawTextToByteArray(text, fontSize, fontColor, textAlign);
        }

        ResourceManager resourceManager = Engine2D.GetInstance().getResourceManager();
        if (rawData != null) {
            // In case of text, always refresh texture
            if (!text.isEmpty()) {
                resourceManager.releaseTexture(name);
            }
            if (!resourceManager.loadTexture(name, rawData, smooth)) {
                Log.e(LOG_TAG, "Failed to load texture");
                return false;
            }

            // release memory
            rawData = null;
        }
        else {
            if (!resourceManager.loadTexture(name, smooth)) {
                Log.e(LOG_TAG, "Failed to load texture");
                return false;
            }
        }

        id = nCreateSprite(name, gridSize.width, gridSize.height);
        if (id == INVALID_ID) {
            Log.e(LOG_TAG, "Failed to create sprite");
            return false;
        }

        return true;
    }

    /**
     *
     */
    public void close() {
        if (id != INVALID_ID) {
            nDestroySprite(id);
            id = INVALID_ID;
        }
    }

    /**
     *
     */
    public void finalize() {
        if (id != INVALID_ID) {
            Log.w(LOG_TAG, "A sprite " + id + " is not properly closed");
            nDestroySprite(id);
        }
    }

    /**
     *
     */
    public void commit() {
        Point grid = getNextGridIndex();
        nSetProperties(id, position.x, position.y, size.width, size.height, layer, depth, opaque,
                colorize, rotation, visible, grid.x, grid.y);
    }

    /**
     *
     * @return
     */
    public Point getPos() {
        return position;
    }

    /**
     *
     * @param position
     */
    public void setPos(Point position) {
        this.position = position;
    }

    /**
     *
     * @return
     */
    public Size getSize() {
        return size;
    }

    /**
     *
     * @param size
     */
    public void setSize(Size size) {
        this.size = size;
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
    public String getAsset() {
        return name;
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
    public Size getGridSize() {
        return gridSize;
    }

    /**
     *
     * @return
     */
    public Point getGridIndex() {
        if (animationList.size() <= currentAnimationIndex) {
            return new Point(0, 0);
        }
        return animationList.get(currentAnimationIndex).first;
    }

    private Point getNextGridIndex() {
        if (animationList.size() <= currentAnimationIndex) {
            return new Point(0, 0);
        }

        int maxStayingTime = animationList.get(currentAnimationIndex).second;

        if (++currentAnimationStayingTime >= maxStayingTime) {
            currentAnimationIndex = (currentAnimationIndex + 1) % animationList.size();
            currentAnimationStayingTime = 0;
        }

        return animationList.get(currentAnimationIndex).first;
    }

    /**
     *
     * @param gridIndex
     */
    public void setGridIndex(Point gridIndex) {
        animationList.clear();
        animationList.add(new Pair<>(gridIndex, 1));
        this.currentAnimationIndex = 0;
        this.currentAnimationStayingTime = 0;
    }

    /**
     *
     * @param animationList
     */
    public void setAnimationIndex(ArrayList<Pair<Point, Integer>> animationList) {
        this.animationList = animationList;
        this.currentAnimationIndex = 0;
        this.currentAnimationStayingTime = 0;
    }

    /**
     *
     * @param gridIndex
     * @param stayingTime
     */
    public void addAnimationIndex(Point gridIndex, int stayingTime) {
        this.animationList.add(new Pair<>(gridIndex, stayingTime));
        this.currentAnimationIndex = 0;
        this.currentAnimationStayingTime = 0;
    }

    /**
     *
     */
    public void clearAnimationIndex() {
        this.animationList.clear();
    }

    /**
     *
     * @param r
     * @param g
     * @param b
     */
    public void setColorize(float r, float g, float b) {
        colorize[0] = r;
        colorize[1] = g;
        colorize[2] = b;
    }

    /**
     *
     * @param colorize
     */
    public void setColorize(float[] colorize) {
        this.colorize = colorize;
    }

    private final int INVALID_ID = -1;

    private String name;
    private byte[] rawData;
    private String text;
    private float fontSize;
    private int fontColor;
    private int bgColor;
    private Paint.Align textAlign;
    private int id;
    private int layer;
    private Point position;
    private Size size;
    private float opaque;
    private float rotation;
    private float depth;
    private boolean visible;
    private boolean smooth;
    private Size gridSize;
    private float[] colorize;
    private ArrayList<Pair<Point, Integer>> animationList;
    private int currentAnimationIndex = 0;
    private int currentAnimationStayingTime = 0;

    private native int nCreateSprite(String asset, int gridCols, int gridRows);
    private native void nDestroySprite(int id);
    private native void nSetProperties(int id, int x, int y, int width, int height, int layer,
                                       float depth, float opaque, float[] colorize,
                                       float rotation, boolean visible,
                                       int gridCol, int gridRow);
}
