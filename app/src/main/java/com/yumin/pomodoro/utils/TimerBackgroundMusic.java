package com.yumin.pomodoro.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;

import com.yumin.pomodoro.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class TimerBackgroundMusic {
    private static final String TAG = "[TimerBackgroundMusic]";
    private Context context;
    private static TimerBackgroundMusic timerBackgroundMusic;
    private String currentPath;
    private boolean isPaused;
    private MediaPlayer mBackgroundMediaPlayer;

    private TimerBackgroundMusic(Context context) {
        this.context = context;
    }

    public static TimerBackgroundMusic getInstance(Context context){
        if (timerBackgroundMusic == null) {
            timerBackgroundMusic = new TimerBackgroundMusic(context);
        }
        return timerBackgroundMusic;
    }

    // 初始化一些数据
    private void initData() {
        mBackgroundMediaPlayer = null;
        isPaused = false;
        currentPath = null;
    }

    /**
     * 根据path路径播放背景音乐
     *
     * @param path
     *            :assets中的音频路径
     * @param isLoop
     *            :是否循环播放
     */
    public void playBackgroundMusic(String path, boolean isLoop) {
        if (currentPath == null) {
            LogUtil.logD(TAG,"playBackgroundMusic 000");
            // 这是第一次播放背景音乐--- it is the first time to play background music
            // 或者是执行end()方法后，重新被叫---or end() was called
            mBackgroundMediaPlayer = MediaPlayer.create(context, R.raw.sound_effect_clock);
            currentPath = path;
        } else {
            if (!currentPath.equals(path)) {
                LogUtil.logD(TAG,"playBackgroundMusic 001");
                // 播放一个新的背景音乐--- play new background music
                // 释放旧的资源并生成一个新的----release old resource and create a new one
                if (mBackgroundMediaPlayer != null) {
                    mBackgroundMediaPlayer.release();
                }
                mBackgroundMediaPlayer = MediaPlayer.create(context, R.raw.sound_effect_clock);
                // 记录这个路径---record the path
                currentPath = path;
            }
        }

        if (mBackgroundMediaPlayer == null) {
            LogUtil.logD(TAG,"playBackgroundMusic 002");
            LogUtil.logE(TAG, "playBackgroundMusic: background media player is null");
        } else {
            LogUtil.logD(TAG,"playBackgroundMusic 003");
            // 若果音乐正在播放或已近中断，停止它---if the music is playing or paused, stop it
//            mBackgroundMediaPlayer.stop();
//            mBackgroundMediaPlayer.setLooping(isLoop);
            try {
//                mBackgroundMediaPlayer.prepare();
//                mBackgroundMediaPlayer.seekTo(0);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    mBackgroundMediaPlayer.setOnDrmPreparedListener(new MediaPlayer.OnDrmPreparedListener() {
//                        @Override
//                        public void onDrmPrepared(MediaPlayer mp, int status) {
//                            mp.setAudioStreamType(AudioManager.STREAM_NOTIFICATION); // play the same volume as notification
//                            mp.start();
//                        }
//                    });
//                }
                this.isPaused = false;
            } catch (Exception e) {
                LogUtil.logE(TAG, "playBackgroundMusic: error state");
            }
        }
    }

    /**
     * 停止播放背景音乐
     */
    public void stopBackgroundMusic() {
        if (mBackgroundMediaPlayer != null) {
            mBackgroundMediaPlayer.stop();
            // should set the state, if not , the following sequence will be
            // error
            // play -> pause -> stop -> resume
            this.isPaused = false;
        }
    }

    /**
     * 暂停播放背景音乐
     */
    public void pauseBackgroundMusic() {
        if (mBackgroundMediaPlayer != null
                && mBackgroundMediaPlayer.isPlaying()) {
            mBackgroundMediaPlayer.pause();
            this.isPaused = true;
        }
    }

    /**
     * 继续播放背景音乐
     */
    public void resumeBackgroundMusic() {
        if (mBackgroundMediaPlayer != null && this.isPaused) {
            mBackgroundMediaPlayer.start();
            this.isPaused = false;
        }
    }

    /**
     * 重新播放背景音乐
     */
    public void rewindBackgroundMusic() {
        if (mBackgroundMediaPlayer != null) {
            mBackgroundMediaPlayer.stop();
            try {
                mBackgroundMediaPlayer.prepare();
                mBackgroundMediaPlayer.seekTo(0);
                mBackgroundMediaPlayer.start();
                this.isPaused = false;
            } catch (Exception e) {
                LogUtil.logE(TAG, "rewindBackgroundMusic: error state");
            }
        }
    }

    /**
     * 判断背景音乐是否正在播放
     *
     * @return：返回的boolean值代表是否正在播放
     */
    public boolean isBackgroundMusicPlaying() {
        boolean ret = false;
        if (mBackgroundMediaPlayer == null) {
            ret = false;
        } else {
            ret = mBackgroundMediaPlayer.isPlaying();
        }
        return ret;
    }

    /**
     * 结束背景音乐，并释放资源
     */
    public void end() {
        if (mBackgroundMediaPlayer != null) {
            mBackgroundMediaPlayer.release();
        }
        // 重新“初始化数据”
        initData();
    }

//    /**
//     * 得到背景音乐的“音量”
//     *
//     * @return
//     */
//    public float getBackgroundVolume() {
//        if (this.mBackgroundMediaPlayer != null) {
//            return (this.mLeftVolume + this.mRightVolume) / 2;
//        } else {
//            return 0.0f;
//        }
//    }

//    /**
//     * 设置背景音乐的音量
//     *
//     * @param volume
//     *            ：设置播放的音量，float类型
//     */
//    public void setBackgroundVolume(float volume) {
//        this.mLeftVolume = this.mRightVolume = volume;
//        if (this.mBackgroundMediaPlayer != null) {
//            this.mBackgroundMediaPlayer.setVolume(this.mLeftVolume,
//                    this.mRightVolume);
//        }
//    }

    /**
     * create mediaplayer for music
     *
     * @param fileName
     *            the path relative to assets
     * @return
     */
    private MediaPlayer createMediaPlayerFromAssets(String fileName) {
        MediaPlayer mediaPlayer = null;
        try {
            mediaPlayer = MediaPlayer.create(context, R.raw.sound_effect_clock);
        } catch (Exception e) {
            mediaPlayer = null;
            LogUtil.logE(TAG, "error: " + e.getMessage());
        }
        return mediaPlayer;
    }

    public Uri getRawUri(String filename) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + File.pathSeparator + File.separator + context.getPackageName() + "/raw/" + filename);
    }
}
