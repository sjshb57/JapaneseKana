package com.kana.study;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * 顺序播放队列。
 * <p>
 * play(romajiList) 从头顺序播放，每个播完自动播下一个。
 */
public class AudioPlayer {

    private static AudioPlayer instance;

    private final Context appContext;
    private final AudioManager audioManager;
    private AudioFocusRequest focusRequest;
    private boolean hasFocus;

    private MediaPlayer mp;
    private String[] queue;
    private int index;
    private boolean playing;

    public static AudioPlayer get(Context ctx) {
        if (instance == null) instance = new AudioPlayer(ctx.getApplicationContext());
        return instance;
    }

    private AudioPlayer(Context appContext) {
        this.appContext = appContext;
        this.audioManager = appContext.getSystemService(AudioManager.class);
    }

    public void play(String[] romajiList) {
        queue = romajiList;
        playing = true;
        index = 0;
        requestFocus();
        playCurrent();
    }

    private void next() {
        index++;
        playCurrent();
    }

    private void playCurrent() {
        if (index >= queue.length) {
            playing = false;
            abandonFocus();
            return;
        }
        String name = queue[index] + ".mp3";
        MediaPlayer player = player();
        player.reset();
        try (AssetFileDescriptor afd = appContext.getAssets().openFd(name)) {
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        } catch (IOException e) {
            throw new IllegalStateException("缺少音频 assets/" + queue[index] + ".mp3", e);
        }
        player.prepareAsync();
    }

    private MediaPlayer player() {
        if (mp == null) {
            mp = new MediaPlayer();
            mp.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build());
            mp.setOnPreparedListener(p -> {
                if (playing) p.start();
            });
            mp.setOnCompletionListener(p -> next());
        }
        return mp;
    }

    public void stop() {
        playing = false;
        if (mp != null) {
            mp.release();
            mp = null;
        }
        abandonFocus();
    }

    private void requestFocus() {
        if (hasFocus) return;
        if (focusRequest == null) {
            focusRequest = new AudioFocusRequest.Builder(
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                    .setAudioAttributes(new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build())
                    .setOnAudioFocusChangeListener(change -> {
                        if (change == AudioManager.AUDIOFOCUS_LOSS
                                || change == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) stop();
                    })
                    .build();
        }
        hasFocus = audioManager.requestAudioFocus(focusRequest)
                == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    private void abandonFocus() {
        if (!hasFocus) return;
        audioManager.abandonAudioFocusRequest(focusRequest);
        hasFocus = false;
    }
}
