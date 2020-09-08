package com.lifejourney.engine2d;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import androidx.core.util.Pair;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.UUID;

public class TextSprite extends Sprite {

    private static String LOG_TAG = "TextSprite";

    public static class Builder extends Sprite.Builder<Builder> {
        // required parameter
        private String text;
        private float fontSize;

        // optional
        private int fontColor = Color.argb(255, 255, 255, 255);
        private int bgColor = Color.argb(0, 0, 0, 0);
        private String fontName = null;
        private Layout.Alignment horizontalAlign = Layout.Alignment.ALIGN_NORMAL;
        private Layout.Alignment verticalAlign = Layout.Alignment.ALIGN_NORMAL;
        private boolean shadow = false;
        private int shadowColor = Color.argb(0, 0, 0, 0);
        private float shadowDepth = 1.0f;

        public Builder(String name, String text, float fontSize) {
            super(name);
            this.text = text;
            this.fontSize = fontSize;
        }
        public Builder fontColor(int fontColor) {
            this.fontColor = fontColor;
            return this;
        }
        public Builder bgColor(int bgColor) {
            this.bgColor = bgColor;
            return this;
        }
        public Builder fontName(String fontName) {
            this.fontName = fontName;
            return this;
        }
        public Builder horizontalAlign(Layout.Alignment horizontalAlign) {
            this.horizontalAlign = horizontalAlign;
            return this;
        }
        public Builder verticalAlign(Layout.Alignment verticalAlign) {
            this.verticalAlign = verticalAlign;
            return this;
        }
        public Builder shadow(int shadowColor, float shadowDepth) {
            this.shadow = true;
            this.shadowColor = shadowColor;
            this.shadowDepth = shadowDepth;
            return this;
        }
        public TextSprite build() {
            return new TextSprite(this);
        }
    };

    private TextSprite(Builder builder) {
        super(builder, false);
        text            = builder.text;
        fontSize        = builder.fontSize;
        fontColor       = builder.fontColor;
        bgColor         = builder.bgColor;
        horizontalAlign = builder.horizontalAlign;
        verticalAlign   = builder.verticalAlign;
        shadow          = builder.shadow;
        shadowColor     = builder.shadowColor;
        shadowDepth     = builder.shadowDepth;
        if (builder.fontName != null) {
            ResourceManager resourceManager = Engine2D.GetInstance().getResourceManager();
            typeface = resourceManager.loadTypeface(builder.fontName);
        } else {
            typeface = Typeface.DEFAULT;
        }
        originalAsset   = asset;

        load();
    }

    @Override
    public void close() {
        super.close();

        // Delete all lazy deleted sprites
        ListIterator<Pair<Integer, Integer>> iit = spriteIDsToLazyDelete.listIterator();
        while (iit.hasNext()) {
            Pair<Integer, Integer> item = iit.next();
            if (item.second == 1) {
                nDestroySprite(item.first);
                iit.remove();
            } else {
                iit.set(new Pair<>(item.first, item.second - 1));
            }
        }
    }

    @Override
    public void commit() {
        Iterator<Pair<Integer, Integer>> it = spriteIDsToLazyDelete.iterator();
        while (it.hasNext()) {
            nSetProperties(it.next().first,
                    position.x + positionOffset.x, position.y + positionOffset.y,
                    size.width, size.height, layer, depth, opaque,
                    colorize, rotation, false, 0, 0);
        }

        super.commit();

        // Delete prev sprite after new sprite is drawn to prevent blinking
        ListIterator<Pair<Integer, Integer>> iit = spriteIDsToLazyDelete.listIterator();
        while (iit.hasNext()) {
            Pair<Integer, Integer> item = iit.next();
            if (item.second == 1) {
                nDestroySprite(item.first);
                iit.remove();
            } else {
                iit.set(new Pair<>(item.first, item.second - 1));
            }
        }
    }

    /**
     *
     * @return
     */
    public boolean load() {
        if (!asset.equals(originalAsset)) {
            ResourceManager resourceManager = Engine2D.GetInstance().getResourceManager();
            resourceManager.releaseTexture(asset);
        }

        asset = originalAsset + UUID.randomUUID();

        rawBytes = drawTextToByteArray();
        if (id != INVALID_ID) {
            spriteIDsToLazyDelete.add(new Pair<>(id, 1));
            id = INVALID_ID;
        }

        return super.load();
    }

    /**
     *
     * @return
     */
    private byte[] drawTextToByteArray() {
        TextPaint textPaint = new TextPaint();
        textPaint.setTypeface(typeface);
        textPaint.setTextSize(fontSize);
        textPaint.setFakeBoldText(false);
        textPaint.setAntiAlias(true);

        Bitmap textBitmap = Bitmap.createBitmap((int)size.width, (int)size.height, Bitmap.Config.ARGB_8888);
        textBitmap.eraseColor(bgColor);

        // Creates a new canvas that will draw into a bitmap instead of rendering into the screen
        Canvas canvas = new Canvas(textBitmap);
        StaticLayout textLayout = new StaticLayout(text, textPaint, canvas.getWidth(),
                horizontalAlign, 1.0f, 0.0f, false);
        canvas.save();

        // calculate x and y position where your text will be placed
        if (verticalAlign == Layout.Alignment.ALIGN_CENTER) {
            canvas.translate(0, (size.height - textLayout.getHeight()) / 2);
        } else if (verticalAlign == Layout.Alignment.ALIGN_OPPOSITE) {
            canvas.translate(0, size.height - textLayout.getHeight());
        }

        if (shadow) {
            textPaint.setColor(shadowColor);
            canvas.translate(0, shadowDepth);
            textLayout.draw(canvas);
        }

        textPaint.setColor(fontColor);
        canvas.translate(0, -shadowDepth);
        textLayout.draw(canvas);

        canvas.restore();

        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        textBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream) ;
        return stream.toByteArray();
    }

    /**
     *
     * @return
     */
    public void setText(String text) {
        this.text = text;
        load();
    }

    /**
     *
     * @return
     */
    public void setFontColor(int fontColor) {
        this.fontColor = fontColor;
    }

    /**
     *
     * @param shadowColor
     * @param shadowDepth
     */
    public void setShadow(int shadowColor, float shadowDepth) {
        this.shadow = true;
        this.shadowColor = shadowColor;
        this.shadowDepth = shadowDepth;
    }

    private String text;
    private String originalAsset;
    private float fontSize;
    private int fontColor;
    private int bgColor;
    private Layout.Alignment horizontalAlign;
    private Layout.Alignment verticalAlign;
    private Typeface typeface;
    private ArrayList<Pair<Integer, Integer>> spriteIDsToLazyDelete = new ArrayList<>();
    private boolean shadow;
    private int shadowColor;
    private float shadowDepth;
}
