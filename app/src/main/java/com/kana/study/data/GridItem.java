package com.kana.study.data;

/**
 * GridView 单元数据。
 * <p>
 * TYPE_ALL    左上角的「全部播放」
 * TYPE_HEADER 行 / 列标题，label + suffix（"あ" + "段"），带一组罗马音用于批量播放
 * TYPE_CELL   实际假名格子
 */
public final class GridItem {

    public static final int TYPE_ALL    = 0;
    public static final int TYPE_HEADER = 1;
    public static final int TYPE_CELL   = 2;

    public final int type;
    public final Kana label;        // header 的标题假名，跟着平 / 片假名切换
    public final String suffix;     // "段" 或 "行"
    public final String[] romaji;   // all / header 批量播放的罗马音
    public final Kana kana;         // cell 的假名
    public final boolean enabled;   // false = 现代日语中不存在的位置

    private GridItem(int type, Kana label, String suffix, String[] romaji, Kana kana, boolean enabled) {
        this.type = type;
        this.label = label;
        this.suffix = suffix;
        this.romaji = romaji;
        this.kana = kana;
        this.enabled = enabled;
    }

    static GridItem all(String[] romaji) {
        return new GridItem(TYPE_ALL, null, null, romaji, null, true);
    }

    static GridItem header(Kana label, String suffix, String[] romaji) {
        return new GridItem(TYPE_HEADER, label, suffix, romaji, null, true);
    }

    static GridItem cell(Kana kana, boolean enabled) {
        return new GridItem(TYPE_CELL, null, null, null, kana, enabled);
    }

    /** header 的显示文字，如「あ段」/「ア段」 */
    public String title(boolean katakana) {
        return (katakana ? label.katakana() : label.hiragana()) + suffix;
    }
}
