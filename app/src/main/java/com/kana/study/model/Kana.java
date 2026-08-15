package com.kana.study.model;

/**
 * 单个假名数据（对应原版 com.zhiyong.sunday.module.b.a）
 *
 * @param hiragana ping 平假名
 * @param katakana pian 片假名
 * @param romaji   luoma 罗马音
 */
public record Kana(String hiragana, String katakana, String romaji) {
}
