package com.kana.study;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.kana.study.data.GridItem;
import com.kana.study.data.KanaData;
import com.kana.study.ui.KanaTableAdapter;
import com.kana.study.ui.SimpleTabStrip;

import java.util.List;

/**
 * 主界面：顶部标题栏（自适应状态栏）+ 40dp Tab（清音 / 浊音 / 拗音）+ ViewPager 三张表。
 * 右上角切换平假名 / 片假名。
 */
public class MainActivity extends Activity {

    private static final String STATE_KATAKANA = "show_katakana";
    private static final int PAGE_COUNT = 3;

    private final KanaTableAdapter[] adapters = new KanaTableAdapter[PAGE_COUNT];

    private boolean showKatakana;
    private TextView btnHiragana, btnKatakana;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            showKatakana = savedInstanceState.getBoolean(STATE_KATAKANA);
        }

        View titleBar = findViewById(R.id.title_bar);
        ViewCompat.setOnApplyWindowInsetsListener(titleBar, (v, insets) -> {
            v.setPadding(0, insets.getInsets(WindowInsetsCompat.Type.statusBars()).top, 0, 0);
            return insets;
        });

        btnHiragana = findViewById(R.id.switch_left);
        btnKatakana = findViewById(R.id.switch_right);
        btnHiragana.setOnClickListener(v -> setKanaType(false));
        btnKatakana.setOnClickListener(v -> setKanaType(true));
        updateSwitchUI();

        ViewPager pager = findViewById(R.id.kana_pager);
        pager.setAdapter(new KanaPagerAdapter());
        pager.setOffscreenPageLimit(PAGE_COUNT - 1);
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override public void onPageSelected(int position) {
                AudioPlayer.get(MainActivity.this).stop();
            }
        });

        SimpleTabStrip tabStrip = findViewById(R.id.tab_strip);
        tabStrip.setupWithViewPager(pager, new String[]{
                getString(R.string.tab_qing),
                getString(R.string.tab_zhuo),
                getString(R.string.tab_ao)});
    }

    @Override
    protected void onPause() {
        super.onPause();
        AudioPlayer.get(this).stop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_KATAKANA, showKatakana);
    }

    private void setKanaType(boolean katakana) {
        if (showKatakana == katakana) return;
        showKatakana = katakana;
        updateSwitchUI();
        for (KanaTableAdapter a : adapters) {
            if (a != null) a.setShowKatakana(showKatakana);
        }
    }

    private void updateSwitchUI() {
        btnHiragana.setSelected(!showKatakana);
        btnKatakana.setSelected(showKatakana);
    }

    private final class KanaPagerAdapter extends PagerAdapter {

        @Override public int getCount() { return PAGE_COUNT; }

        @Override public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View page = LayoutInflater.from(container.getContext())
                    .inflate(R.layout.page_kana_grid, container, false);

            KanaData data = KanaData.get(MainActivity.this);
            List<GridItem> items;
            int columns;
            switch (position) {
                case 1 -> { items = data.buildZhuoTable(); columns = 6; }
                case 2 -> { items = data.buildAoTable();   columns = 4; }
                default -> { items = data.buildQingTable(); columns = 6; }
            }

            GridView grid = page.findViewById(R.id.grid);
            grid.setNumColumns(columns);

            KanaTableAdapter adapter = new KanaTableAdapter(items);
            adapter.setShowKatakana(showKatakana);
            adapters[position] = adapter;
            grid.setAdapter(adapter);

            final int basePadding = grid.getPaddingBottom();
            ViewCompat.setOnApplyWindowInsetsListener(grid, (v, insets) -> {
                int bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
                v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(),
                        basePadding + bottom);
                return insets;
            });

            grid.setOnItemClickListener((parent, view, pos, id) -> {
                GridItem item = items.get(pos);
                if (!item.enabled) return;
                AudioPlayer.get(MainActivity.this).play(item.type == GridItem.TYPE_CELL
                        ? new String[]{item.kana.romaji()}
                        : item.romaji);
            });

            container.addView(page);
            return page;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
            adapters[position] = null;
        }
    }
}
