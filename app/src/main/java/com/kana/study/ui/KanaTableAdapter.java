package com.kana.study.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.kana.study.R;
import com.kana.study.data.GridItem;

import java.util.List;

/**
 * 五十音表 GridView 适配器，三种 view type 见 {@link GridItem}。
 */
public class KanaTableAdapter extends BaseAdapter {

    private final List<GridItem> data;
    private boolean showKatakana;

    public KanaTableAdapter(List<GridItem> data) {
        this.data = data;
    }

    public void setShowKatakana(boolean v) {
        if (showKatakana != v) {
            showKatakana = v;
            notifyDataSetChanged();
        }
    }

    @Override public int getCount() { return data.size(); }
    @Override public GridItem getItem(int pos) { return data.get(pos); }
    @Override public long getItemId(int pos) { return pos; }
    @Override public int getViewTypeCount() { return 3; }
    @Override public int getItemViewType(int pos) { return data.get(pos).type; }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        return switch (getItemViewType(pos)) {
            case GridItem.TYPE_ALL -> inflate(convertView, parent, R.layout.item_grid_all);
            case GridItem.TYPE_HEADER -> getHeaderView(pos, convertView, parent);
            case GridItem.TYPE_CELL -> getCellView(pos, convertView, parent);
            default -> throw new IllegalStateException("未知 view type: " + getItemViewType(pos));
        };
    }

    private static View inflate(View convertView, ViewGroup parent, int layout) {
        return convertView != null ? convertView
                : LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
    }

    private View getHeaderView(int pos, View v, ViewGroup parent) {
        HeaderHolder h;
        if (v == null) {
            v = inflate(null, parent, R.layout.item_grid_header);
            h = new HeaderHolder();
            h.tv = v.findViewById(R.id.tv_title);
            v.setTag(h);
        } else {
            h = (HeaderHolder) v.getTag();
        }
        h.tv.setText(data.get(pos).title(showKatakana));
        return v;
    }

    private View getCellView(int pos, View v, ViewGroup parent) {
        CellHolder h;
        if (v == null) {
            v = inflate(null, parent, R.layout.item_grid_cell);
            h = new CellHolder();
            h.kana   = v.findViewById(R.id.tv_kana);
            h.romaji = v.findViewById(R.id.tv_romaji);
            v.setTag(h);
        } else {
            h = (CellHolder) v.getTag();
        }
        GridItem item = data.get(pos);
        v.setBackgroundResource(item.enabled
                ? R.drawable.bg_grid_cell : R.drawable.bg_grid_cell_disabled);
        int color = ContextCompat.getColor(parent.getContext(),
                item.enabled ? R.color.text1 : R.color.text_disabled);
        h.kana.setTextColor(color);
        h.romaji.setTextColor(color);
        h.kana.setText(showKatakana ? item.kana.katakana() : item.kana.hiragana());
        h.romaji.setText(item.kana.romaji());
        return v;
    }

    static class HeaderHolder { TextView tv; }
    static class CellHolder { TextView kana, romaji; }
}
