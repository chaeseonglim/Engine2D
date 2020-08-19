package com.lifejourney.engine2d;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;
import android.view.Surface;

public class Engine2D implements MediaPlayer.OnCompletionListener {

    // Used to load the 'RacingFever' library on application startup.
    static {
        System.loadLibrary("Engine2D");
    }

    private Engine2D() {}
    private static class Singleton {
        private static final Engine2D instance = new Engine2D();
    }

    public static Engine2D GetInstance() {
        return Singleton.instance;
    }

    private static final long ONE_MS_IN_NS = 1000000;
    private static final long ONE_S_IN_NS = 1000 * ONE_MS_IN_NS;

    private static final String LOG_TAG = "Engine2D";

    /**
     *
     * @param activity
     */
    public void initEngine(Activity activity) {
        if (!initialized) {
            this.activity = activity;

            // Initialize Engine
            nEngineInit(activity);

            // Initialize sound
            AudioAttributes attrs = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(20)
                    .setAudioAttributes(attrs)
                    .build();

            // Initialize resource manager
            resourceManager = new ResourceManager(activity.getApplicationContext());

            // Initialize collision detector
            collisionDetector = new CollisionDetector();

            initialized = true;
        }
    }

    /**
     *
     */
    public void finalizeEngine() {
        if (initialized) {

            // Finalize resource manager
            resourceManager.releaseAll();

            // Finalize sound
            soundPool.release();

            // Finalize Engine
            nEngineFinalize();

            initialized = false;
        }
    }

    /**
     *
     * @param surface
     * @param width
     * @param height
     */
    public void setSurface(Surface surface, int width, int height) {
        nEngineSetSurface(surface, width, height);
        screenSize = new Size(width, height);
    }

    /**
     *
     */
    public void clearSurface() {
        nEngineClearSurface();
    }

    /**
     *
     * @param viewport
     */
    public void setViewport(Rect viewport) {
        this.viewport = viewport;
    }

    /**
     *
     * @return
     */
    public Rect getViewport() {
        return viewport;
    }

    /**
     *
     */
    public void commitViewport() {
        nEngineSetViewport(viewport.x, viewport.y, viewport.width, viewport.height);
    }

    /**
     *
     */
    public void start() {
        nEngineStart();
    }

    /**
     *
     */
    public void stop() {
        nEngineStop();
    }

    /**
     *
     */
    void lockDraw() {
        nEngineLockDraw();
    }

    /**
     *
     */
    void unlockDraw() {
        nEngineUnlockDraw();
    }

    /**
     *
     * @return
     */
    public float getAverageFps() {
        return nEngineGetAverageFps();
    }

    /**
     *
     * @param pt
     * @return
     */
    public PointF translateScreenToGamePosition(PointF pt) {
        return new PointF(pt.x / screenSize.width * viewport.width + viewport.x,
            pt.y / screenSize.height * viewport.height + viewport.y);
    }

    /**
     *
     * @param pt
     * @return
     */
    public PointF translateScreenToWidgetPosition(PointF pt) {
        return new PointF(pt.x / screenSize.width * viewport.width,
                pt.y / screenSize.height * viewport.height);
    }

    /**
     *
     * @return
     */
    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    /**
     *
     * @return
     */
    public CollisionDetector getCollisionDetector() {
        return collisionDetector;
    }

    /**
     *
     * @return
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     *
     * @return
     */
    public Activity getActivity() {
        return activity;
    }

    /**
     *
     */
    public void playMusic(float volume) {
        if (!isMusicEnabled()) {
            return;
        }

        if (musicPlayer != null) {
            if (resourceManager.getMusicList().size() == 1) {
                musicPlayer.setLooping(true);
                if (!musicPlayer.isPlaying()) {
                    musicPlayer.start();
                }
                return;
            } else {
                musicPlayer.release();
            }
        }

        if (resourceManager.getMusicList().size() == 0) {
            Log.e(LOG_TAG, "There's no music added in resource manager!!");
            return;
        }

        musicVolume = volume;

        musicPlayer = MediaPlayer.create(activity, resourceManager.getMusicList().get(currentMusicIndex));
        musicPlayer.setLooping(false);
        musicPlayer.setVolume(volume, volume);
        musicPlayer.setOnCompletionListener(this);
        musicPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                musicPlayer.start();
            }
        });
    }

    /**
     *
     */
    public void stopMusic() {
        if (musicPlayer != null) {
            musicPlayer.stop();
            musicPlayer.release();
            musicPlayer = null;
        }
    }

    /**
     *
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        if (musicPlayer != null) {
            Log.i(LOG_TAG, "Music is finished!!!");
            musicPlayer.release();
        }

        Log.i(LOG_TAG, "Let me play next music");

        currentMusicIndex = (currentMusicIndex + 1) % resourceManager.getMusicList().size();
        musicPlayer = MediaPlayer.create(activity, resourceManager.getMusicList().get(currentMusicIndex));
        musicPlayer.setLooping(false);
        musicPlayer.setOnCompletionListener(this);
        musicPlayer.setVolume(musicVolume, musicVolume);
        musicPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (musicEnabled) {
                    musicPlayer.start();
                }
            }
        });
    }

    /**
     *
     * @return
     */
    public SoundPool getSoundPool() {
        return soundPool;
    }

    /**
     *
     * @param name
     */
    public void playSoundEffect(String name, float volume) {
        if (soundEffectEnabled) {
            soundPool.play(resourceManager.getSoundEffect(name), volume, volume,
                    1, 0, 1.0f);
        }
    }

    /**
     *
     * @param musicEnabled
     */
    public void enableMusic(boolean musicEnabled) {
        boolean prevMusicEnabled = this.musicEnabled;
        this.musicEnabled = musicEnabled;
        if (prevMusicEnabled != this.musicEnabled) {
            if (musicEnabled) {
                playMusic(musicVolume);
            } else {
                stopMusic();
            }
        }
    }

    /**
     *
     * @return
     */
    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    /**
     *
     * @param soundEffectEnabled
     */
    public void enableSoundEffect(boolean soundEffectEnabled) {
        this.soundEffectEnabled = soundEffectEnabled;
    }

    /**
     *
     * @return
     */
    public boolean isSoundEffectEnabled() {
        return soundEffectEnabled;
    }

    /**
     *
     * @param key
     * @param value
     */
    public void savePreference(String key, String value) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();

    }

    /**
     *
     * @param key
     * @param value
     */
    public void savePreference(String key, int value) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public String loadPreference(String key, String defaultValue) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getString(key, defaultValue);
    }

    /**
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public int loadPreference(String key, int defaultValue) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getInt(key, defaultValue);
    }

    /**
     *
     * @param resource
     * @return
     */
    public String getString(int resource) {
        return activity.getString(resource);
    }

    /**
     *
     * @param rgb
     */
    public void setBackgroundColor(int rgb) {
        nEngineSetBackgroundColor((int)Color.red(rgb), (int)Color.green(rgb), (int)Color.blue(rgb));
    }

    private Activity activity;
    private Size screenSize;
    private Rect viewport;
    private ResourceManager resourceManager;
    private CollisionDetector collisionDetector;
    private boolean initialized = false;
    private MediaPlayer musicPlayer = null;
    private int currentMusicIndex = 0;
    private float musicVolume = 0.5f;
    private SoundPool soundPool = null;
    private boolean musicEnabled = true;
    private boolean soundEffectEnabled = true;

    private native void nEngineInit(Activity activity);
    private native void nEngineFinalize();
    private native void nEngineSetSurface(Surface surface, int width, int height);
    private native void nEngineClearSurface();
    private native void nEngineStart();
    private native void nEngineStop();
    private native void nEngineSetAutoSwapInterval(boolean enabled);
    private native float nEngineGetAverageFps();
    private native int nEngineGetSwappyStats(int stat, int bin);
    private native void nEngineSetViewport(int x, int y, int width, int height);
    private native void nEngineSetBackgroundColor(int r, int g, int b);
    private native void nEngineLockDraw();
    private native void nEngineUnlockDraw();
}
