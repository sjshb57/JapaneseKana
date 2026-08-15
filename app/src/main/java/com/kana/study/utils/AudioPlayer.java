package com.kana.study.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

/**
 * 顺序播放队列（对应原版 com.zhiyong.sunday.module.common.c.c）
 * <p>
 * 调用 setQueue(romajiList) 设置队列，
 * play() 从头开始顺序播放，每个播放完自动 next()。
 */
public class AudioPlayer {

    private static AudioPlayer instance;

    private Context appContext;
    private MediaPlayer mp;
    private String[] queue;
    private int index = -1;
    private boolean prepared = false;

    public static synchronized AudioPlayer get() {
        if (instance == null) instance = new AudioPlayer();
        return instance;
    }

    public void init(Context ctx) {
        this.appContext = ctx.getApplicationContext();
    }

    public void setQueue(String[] romajiList) {
        this.queue = romajiList;
    }

    /** 从队列开头开始播 */
    public void play() {
        index = 0;
        playCurrent();
    }

    /** 播下一个 */
    public void next() {
        index++;
        playCurrent();
    }

    private void playCurrent() {
        if (queue == null || index < 0 || index >= queue.length) {
            release();
            return;
        }
        release();
        mp = new MediaPlayer();
        try {
            AssetFileDescriptor afd = appContext.getAssets().openFd(queue[index] + ".mp3");
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mp.setOnPreparedListener(player -> {
                prepared = true;
                player.start();
            });
            mp.setOnCompletionListener(player -> next());
            mp.prepareAsync();
        } catch (Exception ignored) {
            next(); // 跳过有问题的文件
        }
    }

    public void stop() {
        if (mp != null && prepared) {
            try { mp.stop(); } catch (Exception ignored) {}
        }
    }

    public void release() {
        prepared = false;
        if (mp != null) {
            try { mp.release(); } catch (Exception ignored) {}
            mp = null;
        }
    }
}
