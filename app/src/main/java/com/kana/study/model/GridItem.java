package com.kana.study.model;

/**
 * GridView 单元数据（对应原版 com.zhiyong.sunday.module.b.j）
 * <p>
 * type 含义：
 *   0 = 全部播放头（左上角播放图标）
 *   1 = 行/列标题（含一组罗马音用于批量播放）
 *   2 = 实际假名格子
 */
public class GridItem {
    public final int type;           // 0/1/2
    public final String title;       // type=1 时的标题文字（如 "あ行"、"い段"）
    public final String[] romajiList; // type=0/1 批量播放用的罗马音数组
    public final Kana kana;          // type=2 时的假名数据
    public boolean enabled = true;   // false 表示日语里不存在的假名（如 yi/ye/wu/wi/we）

    public GridItem(int type, String title, String[] romajiList, Kana kana) {
        this.type = type;
        this.title = title;
        this.romajiList = romajiList;
        this.kana = kana;
    }
}
