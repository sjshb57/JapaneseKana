package com.kana.study.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.kana.study.R;
import com.kana.study.view.SimpleTabStrip;

import java.util.ArrayList;
import java.util.List;

/**
 * 五十音图主页（对应原版 k.java）
 *   - 顶部 title_bar（48dp 内容 + 自适应状态栏高度）
 *   - 中间 40dp Tab（清音/浊音/拗音）
 *   - 内层 ViewPager 切 3 个子 Fragment
 */
public class KanaMainFragment extends Fragment {

    private boolean showKatakana = false;
    private TextView btnHiragana, btnKatakana;
    private TabPagerAdapter pagerAdapter;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_kana_main, container, false);

        View titleBar = root.findViewById(R.id.title_bar);
        ViewCompat.setOnApplyWindowInsetsListener(titleBar, (v, insets) -> {
            int top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            v.setPadding(0, top, 0, 0);
            return WindowInsetsCompat.CONSUMED;
        });

        ViewPager pager = root.findViewById(R.id.kana_pager);
        SimpleTabStrip tabStrip = root.findViewById(R.id.tab_strip);
        btnHiragana = root.findViewById(R.id.switch_left);
        btnKatakana = root.findViewById(R.id.switch_right);

        pagerAdapter = new TabPagerAdapter(getChildFragmentManager());
        pagerAdapter.add(KanaGridFragment.newInstance(0));
        pagerAdapter.add(KanaGridFragment.newInstance(1));
        pagerAdapter.add(KanaGridFragment.newInstance(2));
        pager.setAdapter(pagerAdapter);
        pager.setOffscreenPageLimit(2);

        tabStrip.setupWithViewPager(pager, new String[]{"清音", "浊音", "拗音"});

        btnHiragana.setOnClickListener(v -> setKanaType(false));
        btnKatakana.setOnClickListener(v -> setKanaType(true));
        updateSwitchUI();

        return root;
    }

    private void setKanaType(boolean katakana) {
        if (showKatakana == katakana) return;
        showKatakana = katakana;
        updateSwitchUI();
        for (int i = 0; i < pagerAdapter.getCount(); i++) {
            Fragment f = pagerAdapter.getFragment(i);
            if (f instanceof KanaGridFragment) {
                ((KanaGridFragment) f).setShowKatakana(showKatakana);
            }
        }
    }

    private void updateSwitchUI() {
        int primary = ContextCompat.getColor(requireContext(), R.color.primary);
        int white   = ContextCompat.getColor(requireContext(), R.color.white);
        if (!showKatakana) {
            btnHiragana.setBackgroundResource(R.drawable.bg_switch_left_selected);
            btnHiragana.setTextColor(primary);
            btnKatakana.setBackgroundResource(R.drawable.bg_switch_right_normal);
            btnKatakana.setTextColor(white);
        } else {
            btnHiragana.setBackgroundResource(R.drawable.bg_switch_left_normal);
            btnHiragana.setTextColor(white);
            btnKatakana.setBackgroundResource(R.drawable.bg_switch_right_selected);
            btnKatakana.setTextColor(primary);
        }
    }

    static class TabPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> list = new ArrayList<>();
        TabPagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }
        void add(Fragment f) { list.add(f); }
        Fragment getFragment(int pos) { return list.get(pos); }
        @NonNull
        @Override public Fragment getItem(int pos) { return list.get(pos); }
        @Override public int getCount() { return list.size(); }
    }
}
