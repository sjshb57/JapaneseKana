package com.kana.study.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.kana.study.R;

/**
 * 顶部 Tab 控件（对应原版 PagerSlidingTabStrip 的简化实现）。
 * <p>
 * 视觉：
 *   - 白色背景
 *   - 横向 3 个等宽 TextView，16sp
 *   - 未选灰字 #999999，选中绿字 primary
 *   - 底部 3dp 绿色短横线，宽度 = 文字宽度，跟随当前 page 平滑移动
 *   - 无下划线、无分隔条
 */
public class SimpleTabStrip extends LinearLayout {

    private ViewPager viewPager;
    private int currentPosition = 0;
    private float currentOffset = 0f;

    private final Paint indicatorPaint = new Paint();
    private final int indicatorHeightPx;

    private final int colorTextNormal;
    private final int colorTextSelected;

    public SimpleTabStrip(Context ctx) { this(ctx, null); }
    public SimpleTabStrip(Context ctx, AttributeSet a) {
        super(ctx, a);
        setOrientation(HORIZONTAL);
        setWillNotDraw(false);

        indicatorHeightPx = dp();

        colorTextNormal   = ContextCompat.getColor(ctx, R.color.tab_text_normal);
        colorTextSelected = ContextCompat.getColor(ctx, R.color.primary);

        indicatorPaint.setColor(colorTextSelected);
        indicatorPaint.setStyle(Paint.Style.FILL);
        indicatorPaint.setAntiAlias(true);
    }

    public void setupWithViewPager(@NonNull ViewPager vp, String[] titles) {
        this.viewPager = vp;
        removeAllViews();
        for (int i = 0; i < titles.length; i++) addTab(i, titles[i]);
        updateTextColors(vp.getCurrentItem());
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override public void onPageScrolled(int pos, float off, int pxOff) {
                currentPosition = pos;
                currentOffset = off;
                invalidate();
            }
            @Override public void onPageSelected(int pos) { updateTextColors(pos); }
            @Override public void onPageScrollStateChanged(int state) {}
        });
    }

    private void addTab(int index, String title) {
        TextView tv = new TextView(getContext());
        tv.setText(title);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tv.setTextColor(colorTextNormal);
        tv.setSingleLine(true);
        tv.setOnClickListener(v -> {
            if (viewPager != null) viewPager.setCurrentItem(index);
        });
        addView(tv, new LayoutParams(0, LayoutParams.MATCH_PARENT, 1f));
    }

    private void updateTextColors(int selectedPos) {
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            if (v instanceof TextView) {
                ((TextView) v).setTextColor(i == selectedPos ? colorTextSelected : colorTextNormal);
            }
        }
        currentPosition = selectedPos;
        currentOffset = 0f;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int count = getChildCount();
        if (count == 0) return;

        int h = getHeight();

        // 短横线指示器：长度 = 文字宽度，跟当前 tab 居中
        float[] curRange = textRange(getChildAt(currentPosition));
        float left  = curRange[0];
        float right = curRange[1];

        if (currentOffset > 0f && currentPosition < count - 1) {
            float[] nextRange = textRange(getChildAt(currentPosition + 1));
            left  = curRange[0] + currentOffset * (nextRange[0] - curRange[0]);
            right = curRange[1] + currentOffset * (nextRange[1] - curRange[1]);
        }

        canvas.drawRect(left, h - indicatorHeightPx, right, h, indicatorPaint);
    }

    /** 返回 tab 内文字的左右绝对坐标（在父 TabStrip 坐标系内） */
    private float[] textRange(View tabView) {
        if (!(tabView instanceof TextView tv)) {
            return new float[]{tabView.getLeft(), tabView.getRight()};
        }
        float textWidth = tv.getPaint().measureText(tv.getText().toString());
        float pad = (tv.getWidth() - textWidth) / 2f;
        return new float[]{tabView.getLeft() + pad, tabView.getRight() - pad};
    }

    private int dp() {
        return (int) (3 * getResources().getDisplayMetrics().density + 0.5f);
    }
}
