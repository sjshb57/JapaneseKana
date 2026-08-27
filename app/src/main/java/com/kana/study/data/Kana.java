package com.kana.study.data;

/**
 * 单个假名数据
 *
 * @param hiragana ping 平假名
 * @param katakana pian 片假名
 * @param romaji   luoma 罗马音
 */
public record Kana(String hiragana, String katakana, String romaji) {
}
