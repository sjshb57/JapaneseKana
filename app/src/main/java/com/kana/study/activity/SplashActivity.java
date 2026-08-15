package com.kana.study.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.kana.study.R;

/**
 * 启动屏（对照原版 com.zhiyong.sunday.module.splash.SplashActivity）
 * <p>
 * 1200ms 后自动跳转 MainActivity，期间禁用返回键（OnBackPressedCallback 方式，
 * 兼容 Android 14+ 的 predictive back gesture）。
 */
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION_MS = 1200L;

    private Handler handler;
    private Runnable jumpRunnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        View root = findViewById(R.id.splash_root);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
            }
        });

        handler = new Handler(Looper.getMainLooper());
        jumpRunnable = () -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(jumpRunnable, SPLASH_DURATION_MS);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(jumpRunnable);
    }
}