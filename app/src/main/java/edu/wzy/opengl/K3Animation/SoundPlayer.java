package edu.wzy.opengl.K3Animation;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;


import java.util.HashMap;

import edu.wzy.opengl.R;

public class SoundPlayer implements SoundPool.OnLoadCompleteListener {
    public static final int TYPE_COUNTDOWN = 1;
    public static final int TYPE_DRAW = 2;
    public static final int TYPE_START = 3;
    public static final int TYPE_SAY = 4;
    public static final int TYPE_N1 = 5;
    public static final int TYPE_N2 = 6;
    public static final int TYPE_N3 = 7;
    public static final int TYPE_N4 = 8;
    public static final int TYPE_N5 = 9;
    public static final int TYPE_N6 = 10;
    public static final int TYPE_GOODLUCK = 11;
    private SoundPool mSoundPool = null;
    private Context mContext = null;
    private int mAllLoaded = 0;
    private int mCurrentIndex = 0;
    private int mRepeatCount = 0;
    private HashMap<Integer, Integer> mSoundID = null;

    public SoundPlayer(Context context) {
        mContext = context;
        mSoundID = new HashMap<Integer, Integer>();
    }

    @Override
    public void onLoadComplete(SoundPool var1, int sampleID, int var3) {
        mSoundPool.play(sampleID, 1.0f, 1.0f, 0, mRepeatCount, 1.0f);
    }

    public void playSound(int type, int repeat) {
        if (mSoundPool == null) {
            mSoundPool = new SoundPool(3, AudioManager.STREAM_SYSTEM, 0);
            mSoundPool.setOnLoadCompleteListener(this);
        }
        mCurrentIndex = type;
        mRepeatCount = repeat;
        switch (type) {
            case TYPE_COUNTDOWN:
                if (mSoundID.get(TYPE_COUNTDOWN) == null) {
                    int resId = mSoundPool.load(mContext, R.raw.sound_cd, 1);
                    mSoundID.put(TYPE_COUNTDOWN, resId);
                    mAllLoaded++;
                } else {
                    mSoundPool.play(mSoundID.get(TYPE_COUNTDOWN), 1.0f, 1.0f, 0, repeat, 1.0f);
                }
                break;
            case TYPE_START:
                if (mSoundID.get(TYPE_START) == null) {
                    int resId = mSoundPool.load(mContext, R.raw.sound_start, 1);
                    mSoundID.put(TYPE_START, resId);
                    mAllLoaded++;
                } else {
                    mSoundPool.play(mSoundID.get(TYPE_START), 1.0f, 1.0f, 0, repeat, 1.0f);
                }
                break;
            case TYPE_DRAW:
                if (mSoundID.get(TYPE_DRAW) == null) {
                    int resId = mSoundPool.load(mContext, R.raw.sound_draw, 1);
                    mSoundID.put(TYPE_DRAW, resId);
                    mAllLoaded++;
                } else {
                    mSoundPool.play(mSoundID.get(TYPE_DRAW), 1.0f, 1.0f, 0, repeat, 1.0f);
                }
                break;
            case TYPE_SAY:
                if (mSoundID.get(TYPE_SAY) == null) {
                    int resId = mSoundPool.load(mContext, R.raw.sound_say, 1);
                    mSoundID.put(TYPE_SAY, resId);
                    mAllLoaded++;
                } else {
                    mSoundPool.play(mSoundID.get(TYPE_SAY), 1.0f, 1.0f, 0, repeat, 1.0f);
                }
                break;
            case TYPE_N1:
                if (mSoundID.get(TYPE_N1) == null) {
                    int resId = mSoundPool.load(mContext, R.raw.sound_n1, 1);
                    mSoundID.put(TYPE_N1, resId);
                    mAllLoaded++;
                } else {
                    mSoundPool.play(mSoundID.get(TYPE_N1), 1.0f, 1.0f, 0, repeat, 1.0f);
                }
                break;
            case TYPE_N2:
                if (mSoundID.get(TYPE_N2) == null) {
                    int resId = mSoundPool.load(mContext, R.raw.sound_n2, 1);
                    mSoundID.put(TYPE_N2, resId);
                    mAllLoaded++;
                } else {
                    mSoundPool.play(mSoundID.get(TYPE_N2), 1.0f, 1.0f, 0, repeat, 1.0f);
                }
                break;
            case TYPE_N3:
                if (mSoundID.get(TYPE_N3) == null) {
                    int resId = mSoundPool.load(mContext, R.raw.sound_n3, 1);
                    mSoundID.put(TYPE_N3, resId);
                    mAllLoaded++;
                } else {
                    mSoundPool.play(mSoundID.get(TYPE_N3), 1.0f, 1.0f, 0, repeat, 1.0f);
                }
                break;
            case TYPE_N4:
                if (mSoundID.get(TYPE_N4) == null) {
                    int resId = mSoundPool.load(mContext, R.raw.sound_n4, 1);
                    mSoundID.put(TYPE_N4, resId);
                    mAllLoaded++;
                } else {
                    mSoundPool.play(mSoundID.get(TYPE_N4), 1.0f, 1.0f, 0, repeat, 1.0f);
                }
                break;
            case TYPE_N5:
                if (mSoundID.get(TYPE_N5) == null) {
                    int resId = mSoundPool.load(mContext, R.raw.sound_n5, 1);
                    mSoundID.put(TYPE_N5, resId);
                    mAllLoaded++;
                } else {
                    mSoundPool.play(mSoundID.get(TYPE_N5), 1.0f, 1.0f, 0, repeat, 1.0f);
                }
                break;
            case TYPE_N6:
                if (mSoundID.get(TYPE_N6) == null) {
                    int resId = mSoundPool.load(mContext, R.raw.sound_n6, 1);
                    mSoundID.put(TYPE_N6, resId);
                    mAllLoaded++;
                } else {
                    mSoundPool.play(mSoundID.get(TYPE_N6), 1.0f, 1.0f, 0, repeat, 1.0f);
                }
                break;
            case TYPE_GOODLUCK:
                if (mSoundID.get(TYPE_GOODLUCK) == null) {
                    int resId = mSoundPool.load(mContext, R.raw.sound_goodluck, 1);
                    mSoundID.put(TYPE_GOODLUCK, resId);
                    mAllLoaded++;
                } else {
                    mSoundPool.play(mSoundID.get(TYPE_GOODLUCK), 1.0f, 1.0f, 0, repeat, 1.0f);
                }
                break;
        }
    }

    public void clearSoundPlayer() {
        if (mSoundPool != null) {
            mSoundPool.release();
            mAllLoaded = 0;
            mSoundID.clear();
        }
    }
}
