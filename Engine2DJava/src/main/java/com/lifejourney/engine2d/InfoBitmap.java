package com.lifejourney.engine2d;

public class InfoBitmap {

    private static String LOG_TAG = "InfoBitmap";

    public InfoBitmap(android.graphics.Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    /**
     *
     * @return
     */
    public int[][] getInfoArray() {
        int[][] array = new int[getHeight()][getWidth()];
        int[] row = new int[getWidth()];
        for (int y = 0; y < bitmap.getHeight(); ++y) {
            bitmap.getPixels(row, 0, getWidth(), 0, y, getWidth(), 1);
            for (int x = 0; x < bitmap.getWidth(); ++x) {
                array[y][x] = row[x];
            }
        }

        return array;
    }

    /**
     *
     * @return
     */
    public int getWidth() {
        return bitmap.getWidth();
    }

    /**
     *
     * @return
     */
    public int getHeight() {
        return bitmap.getHeight();
    }

    private android.graphics.Bitmap bitmap;
}
