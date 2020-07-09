package com.lifejourney.engine2d;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class TextSprite extends Sprite {

    private static String LOG_TAG = "TextSprite";

    public static class Builder extends Sprite.Builder<Builder> {
        // required parameter
        private String name;
        private String text;
        private float fontSize;

        // optional
        private int fontColor = Color.argb(255, 255, 255, 255);
        private int bgColor = Color.argb(0, 0, 0, 0);
        private Paint.Align textAlign = Paint.Align.LEFT;
        private String fontName;

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
        public Builder textAlign(Paint.Align textAlign) {
            this.textAlign = textAlign;
            return this;
        }
        public Builder fontName(String fontName) {
            this.fontName = fontName;
            return this;
        }
        public TextSprite build() {
            return new TextSprite(this);
        }
    };

    private TextSprite(Builder builder) {

        super(builder, false);
        name            = builder.name;
        text            = builder.text;
        fontSize        = builder.fontSize;
        fontColor       = builder.fontColor;
        bgColor         = builder.bgColor;
        textAlign       = builder.textAlign;
        if (builder.fontName != null) {
            ResourceManager resourceManager = Engine2D.GetInstance().getResourceManager();
            typeface = resourceManager.loadTypeface(builder.fontName);
        } else {
            typeface = Typeface.DEFAULT;
        }

        load();
    }

    private byte[] drawTextToByteArray(String text, Typeface typeface, float fontSize,
                                       int fontColor, Paint.Align align) {
        if (fontSize < 8.0f)
            fontSize = 8.0f;
        if (fontSize > 500.0f)
            fontSize = 500.0f;

        Paint textPaint = new Paint();
        textPaint.setTypeface(typeface);
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
        Bitmap textBitmap = Bitmap.createBitmap((int)size.width, (int)size.height, Bitmap.Config.ARGB_8888);
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

        ResourceManager resourceManager = Engine2D.GetInstance().getResourceManager();
        resourceManager.releaseTexture(asset);

        // Load texture
        rawBytes = drawTextToByteArray(text, typeface, fontSize, fontColor, textAlign);
        if (!resourceManager.loadTexture(asset, rawBytes, smooth)) {
            Log.e(LOG_TAG, "Failed to load texture");
            return false;
        } else {
            // Destroy and create sprite again
            nDestroySprite(id);
            id = nCreateSprite(asset, gridSize.width, gridSize.height);
            if (id == INVALID_ID) {
                Log.e(LOG_TAG, "Failed to create sprite");
                return false;
            }
        }

        return true;
    }

    /**
     *
     * @return
     */
    public void setText(String text) {

        this.text = text;
        load();
    }

    private String text;
    private float fontSize;
    private int fontColor;
    private int bgColor;
    private Paint.Align textAlign;
    private Typeface typeface;
}
