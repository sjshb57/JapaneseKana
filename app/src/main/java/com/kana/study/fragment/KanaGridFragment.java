package com.kana.study.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kana.study.R;
import com.kana.study.adapter.KanaTableAdapter;
import com.kana.study.model.GridItem;
import com.kana.study.utils.AudioPlayer;
import com.kana.study.utils.KanaData;

import java.util.List;

/**
 * 单个清/浊/拗音页（对应原版 b.java）
 */
public class KanaGridFragment extends Fragment {

    private static final String ARG_TYPE = "type"; // 0=清音, 1=浊音, 2=拗音

    private KanaTableAdapter adapter;

    public static KanaGridFragment newInstance(int type) {
        KanaGridFragment f = new KanaGridFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_TYPE, type);
        f.setArguments(b);
        return f;
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_kana_grid, container, false);

        Bundle args = getArguments();
        int kanaType = args != null ? args.getInt(ARG_TYPE, 0) : 0;

        KanaData d = KanaData.get();
        List<GridItem> data;
        int columns = switch (kanaType) {
            case 1 -> {
                data = d.buildZhuoTable();
                yield 6;
            }
            case 2 -> {
                data = d.buildAoTable();
                yield 4;
            }
            default -> {
                data = d.buildQingTable();
                yield 6;
            }
        };

        GridView gv = root.findViewById(R.id.grid);
        gv.setNumColumns(columns);
        adapter = new KanaTableAdapter(requireContext(), data);
        gv.setAdapter(adapter);

        gv.setOnItemClickListener((parent, view, pos, id) -> {
            GridItem item = data.get(pos);
            if (!item.enabled) return;
            AudioPlayer player = AudioPlayer.get();
            if (item.type == 2 && item.kana != null) {
                // 单个假名
                player.setQueue(new String[]{item.kana.romaji()});
                player.play();
            } else if (item.romajiList != null && item.romajiList.length > 0) {
                // 全部播放头 / 行标题 / 列标题：批量播放
                player.setQueue(item.romajiList);
                player.play();
            }
        });

        return root;
    }

    /** 由父 Fragment 调用，切换平/片假名显示 */
    public void setShowKatakana(boolean show) {
        if (adapter != null) adapter.setShowKatakana(show);
    }

    @Override public void onPause() {
        super.onPause();
        AudioPlayer.get().stop();
    }
}
