package com.lifejourney.engine2d;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class ResourceManager {

    private static final String LOG_TAG = "ResourceManager";

    public ResourceManager(Context context) {
        this.context = context;
    }

    /**
     *
     * @param asset
     * @return
     */
    public Typeface loadTypeface(String asset) {
        return Typeface.createFromAsset(context.getAssets(), asset);
    }

    /**
     *
     * @param asset
     * @param smooth
     * @return
     */
    boolean loadTexture(String asset, boolean smooth) {
        boolean result;

        try {
            if (!nIsTextureLoaded(asset)) {
                InputStream is = context.getAssets().open(asset);
                byte[] fileBytes = new byte[is.available()];
                is.read(fileBytes);
                is.close();

                result = nLoadTexture(asset, fileBytes, smooth);
            }
            else {
                result = nAttachTexture(asset);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return result;
    }

    /**
     *
     * @param asset
     * @param data
     * @param smooth
     * @return
     */
    boolean loadTexture(String asset, byte[] data, boolean smooth) {
        boolean result = false;

        if (!nIsTextureLoaded(asset)) {
            result = nLoadTexture(asset, data, smooth);
        }
        else {
            result = nAttachTexture(asset);
        }

        return result;
    }

    /**
     *
     * @param asset
     */
    public void releaseTexture(String asset) {
        nReleaseTexture(asset);
    }

    /**
     *
     */
    public void releaseAll() {
        nReleaseAllTextures();
    }

    /**
     *
     * @param asset
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public InfoBitmap loadGrayscaleBitmap(String asset) {
        InfoBitmap infoBitmap = null;
        try {
            InputStream istr = context.getAssets().open(asset);
            BitmapFactory.Options option = new BitmapFactory.Options();
            option.outConfig = Bitmap.Config.ALPHA_8;
            infoBitmap = new InfoBitmap(BitmapFactory.decodeStream(istr, null, option));
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failed to load bitmap: " + asset);
            e.printStackTrace();
        }

        return infoBitmap;
    }

    /**
     *
     * @param asset
     */
    public void addMusic(int asset) {
        musicList.add(asset);
    }

    /**
     *
     * @return
     */
    public ArrayList<Integer> getMusicList() {
        return musicList;
    }

    /**
     *
     * @param asset
     */
    public void loadSoundEffect(String name, int asset) {
        SoundPool soundPool = Engine2D.GetInstance().getSoundPool();
        soundEffectMap.put(name, soundPool.load(context, asset, 1));
    }

    /**
     *
     */
    public void unloadSoundEffects() {
        SoundPool soundPool = Engine2D.GetInstance().getSoundPool();
        soundPool.release();
        soundEffectMap.clear();
    }

    /**
     *
     * @param name
     * @return
     */
    public int getSoundEffect(String name) {
        Integer id = soundEffectMap.get(name);
        if (id == null) {
            Log.e(LOG_TAG, "Failed to find sound effect " + name + "!!!");
            return -1;
        }
        return id;
    }

    private Context context;
    private HashMap<String, Integer> soundEffectMap = new HashMap<>(5);
    private ArrayList<Integer> musicList = new ArrayList<>();

    private native boolean nLoadTexture(String name, byte[] image, boolean smooth);
    private native boolean nAttachTexture(String name);
    private native void nReleaseTexture(String name);
    private native boolean nIsTextureLoaded(String name);
    private native void nReleaseAllTextures();
}
