package com.kana.study.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.kana.study.R;
import com.kana.study.model.GridItem;

import java.util.List;

/**
 * 五十音表 GridView 适配器（对照原版 g.java）
 * <p>
 * 三种 view type：
 *   0 = 全部播放头（ImageView 播放图标）
 *   1 = 行/列标题（单行 18sp 灰字）
 *   2 = 假名格子（假名 20sp + 罗马音 14sp，深色文字）
 */
public class KanaTableAdapter extends BaseAdapter {

    private static final int TYPE_ALL    = 0;
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_CELL   = 2;

    private final Context ctx;
    private final List<GridItem> data;
    private boolean showKatakana = false;

    public KanaTableAdapter(Context ctx, List<GridItem> data) {
        this.ctx = ctx;
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
            case TYPE_ALL -> getAllView(convertView, parent);
            case TYPE_HEADER -> getHeaderView(pos, convertView, parent);
            case TYPE_CELL -> getCellView(pos, convertView, parent);
            default -> convertView;
        };
    }

    private View getAllView(View v, ViewGroup parent) {
        if (v == null) {
            v = LayoutInflater.from(ctx).inflate(R.layout.item_grid_all, parent, false);
        }
        return v;
    }

    private View getHeaderView(int pos, View v, ViewGroup parent) {
        HeaderHolder h;
        if (v == null) {
            v = LayoutInflater.from(ctx).inflate(R.layout.item_grid_header, parent, false);
            h = new HeaderHolder();
            h.tv = v.findViewById(R.id.tv_title);
            v.setTag(h);
        } else {
            h = (HeaderHolder) v.getTag();
        }
        h.tv.setText(data.get(pos).title);
        return v;
    }

    private View getCellView(int pos, View v, ViewGroup parent) {
        CellHolder h;
        if (v == null) {
            v = LayoutInflater.from(ctx).inflate(R.layout.item_grid_cell, parent, false);
            h = new CellHolder();
            h.kana   = v.findViewById(R.id.tv_kana);
            h.romaji = v.findViewById(R.id.tv_romaji);
            v.setTag(h);
        } else {
            h = (CellHolder) v.getTag();
        }
        GridItem item = data.get(pos);
        if (item.enabled) {
            v.setBackgroundResource(R.drawable.bg_grid_cell);
            int c = ContextCompat.getColor(ctx, R.color.text1);
            h.kana.setTextColor(c);
            h.romaji.setTextColor(c);
        } else {
            v.setBackgroundResource(R.drawable.bg_grid_cell_disabled);
            int c = ContextCompat.getColor(ctx, R.color.text_disabled);
            h.kana.setTextColor(c);
            h.romaji.setTextColor(c);
        }
        if (item.kana != null) {
            h.kana.setText(showKatakana ? item.kana.katakana() : item.kana.hiragana());
            h.romaji.setText(item.kana.romaji());
        }
        return v;
    }

    static class HeaderHolder { TextView tv; }
    static class CellHolder { TextView kana, romaji; }
}
