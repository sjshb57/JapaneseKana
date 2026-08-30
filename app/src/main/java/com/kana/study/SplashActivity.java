package com.kana.study;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.activity.ComponentActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * 启动屏：1200ms 后跳 MainActivity，期间屏蔽返回键
 * （OnBackPressedCallback 方式，兼容 Android 14+ 的 predictive back）。
 */
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends ComponentActivity {

    private static final long SPLASH_DURATION_MS = 1200L;

    private final Runnable jump = () -> {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    };

    private View root;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        root = findViewById(R.id.splash_root);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        root.postDelayed(jump, SPLASH_DURATION_MS);
    }

    @Override
    protected void onPause() {
        super.onPause();
        root.removeCallbacks(jump);
    }
}
