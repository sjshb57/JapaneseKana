package com.kana.study;

import android.app.Application;

import com.kana.study.utils.AudioPlayer;
import com.kana.study.utils.KanaData;

/**
 * 全局 Application（对照原版 com.zhiyong.sunday.AppApplication）。
 * 在进程启动时初始化数据和音频播放器。
 */
public class AppApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        KanaData.get().load(this);
        AudioPlayer.get().init(this);
    }
}
