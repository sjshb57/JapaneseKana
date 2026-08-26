package com.kana.study.utils;

import android.content.Context;
import android.content.res.AssetManager;

import com.kana.study.model.GridItem;
import com.kana.study.model.Kana;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 假名表数据管理器（严格对照原版 com.zhiyong.sunday.module.b.n 的逻辑）
 * <p>
 * 数据三个 List：
 *   qing 清音 51 个（5*10 + 1 n）
 *   zhuo 浊音 25 个（5*5）
 *   ao   拗音 33 个（11*3）
 */
public class KanaData {

    private static KanaData instance;

    private final List<Kana> qing; // 清音 raw
    private final List<Kana> zhuo; // 浊音 raw
    private final List<Kana> ao;   // 拗音 raw

    public static KanaData get(Context ctx) {
        if (instance == null) instance = new KanaData(ctx.getAssets());
        return instance;
    }

    private KanaData(AssetManager assets) {
        try (InputStream is = assets.open("kana.json")) {
            JSONObject root = new JSONObject(new String(readAll(is), StandardCharsets.UTF_8));
            qing = parse(root.getJSONArray("qing"));
            zhuo = parse(root.getJSONArray("zhuo"));
            ao   = parse(root.getJSONArray("ao"));
        } catch (Exception e) {
            throw new IllegalStateException("assets/kana.json 读取失败", e);
        }
    }

    private static byte[] readAll(InputStream is) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(8192);
        byte[] buf = new byte[8192];
        int n;
        while ((n = is.read(buf)) != -1) out.write(buf, 0, n);
        return out.toByteArray();
    }

    private static List<Kana> parse(JSONArray arr) throws JSONException {
        List<Kana> list = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject o = arr.getJSONObject(i);
            list.add(new Kana(o.getString("ping"), o.getString("pian"), o.getString("luoma")));
        }
        return list;
    }

    /** 拗音的列标题（取自浊音表？实际是清音/浊音表中的 ya/yu/yo 列） */
    private String[] aoColumnTitles() {
        return new String[]{
                qing.get(35).hiragana(),  // ya
                qing.get(37).hiragana(),  // yu
                qing.get(39).hiragana()   // yo
        };
    }

    /** 拗音的行标题：11 个行（ka/sa/ta/na/ha/ma/ra/ga/za/ba/pa） */
    private String[] aoRowTitles() {
        String[] r = new String[11];
        for (int i = 0; i < 6; i++) {
            r[i] = qing.get((i + 1) * 5).hiragana();
        }
        r[6] = qing.get(40).hiragana();
        r[7] = zhuo.get(0).hiragana();
        r[8] = zhuo.get(5).hiragana();
        r[9] = zhuo.get(15).hiragana();
        r[10] = zhuo.get(20).hiragana();
        return r;
    }


    /** 清音表：6 列 GridView */
    public List<GridItem> buildQingTable() {
        List<GridItem> list = new ArrayList<>();
        int size = qing.size();

        List<Kana> filteredAll = new ArrayList<>(qing);
        filteredAll.remove(48); // we
        filteredAll.remove(47); // wu
        filteredAll.remove(46); // wi
        filteredAll.remove(38); // ye
        filteredAll.remove(36); // yi
        String[] allRomaji = new String[filteredAll.size()];
        for (int i = 0; i < filteredAll.size(); i++) allRomaji[i] = filteredAll.get(i).romaji();
        list.add(new GridItem(0, "", allRomaji, null));

        String[] col0 = new String[11];
        for (int i = 0; i < 11; i++) col0[i] = qing.get(i * 5).romaji();
        list.add(new GridItem(1, qing.get(0).hiragana() + "段", col0, null));

        for (int col = 1; col < 5; col++) {
            String[] arr = new String[10];
            for (int row = 0; row < 10; row++) arr[row] = qing.get(row * 5 + col).romaji();

            if (col == 1 || col == 2 || col == 3) {
                List<String> tmp = new ArrayList<>(Arrays.asList(arr));
                tmp.remove(9);            // wi/wu/we
                if (col != 2) tmp.remove(7); // yi/ye (u段不去 yu)
                arr = tmp.toArray(new String[0]);
            }
            list.add(new GridItem(1, qing.get(col).hiragana() + "段", arr, null));
        }

        for (int i = 0; i < size; i++) {
            list.add(new GridItem(2, "", null, qing.get(i)));
        }

        for (int row = 0; row < 10; row++) {
            String[] arr = new String[5];
            for (int col = 0; col < 5; col++) arr[col] = qing.get(row * 5 + col).romaji();

            if (row == 7 || row == 9) {
                List<String> tmp = new ArrayList<>(Arrays.asList(arr));
                tmp.remove(3); // ye/we
                if (row == 9) tmp.remove(2); // wu
                tmp.remove(1); // yi/wi
                arr = tmp.toArray(new String[0]);
            }
            list.add((row + 1) * 6, new GridItem(1, qing.get(row * 5).hiragana() + "行", arr, null));
        }

        list.add(66, new GridItem(1,
                qing.get(50).hiragana() + "行",
                new String[]{qing.get(50).romaji()},
                null));

        list.get(50).enabled = false; // yi
        list.get(52).enabled = false; // ye
        list.get(62).enabled = false; // wi
        list.get(63).enabled = false; // wu
        list.get(64).enabled = false; // we

        return list;
    }

    /** 浊音表：6 列 GridView */
    public List<GridItem> buildZhuoTable() {
        List<GridItem> list = new ArrayList<>();
        int size = zhuo.size();

        String[] all = new String[size];
        for (int i = 0; i < size; i++) all[i] = zhuo.get(i).romaji();
        list.add(new GridItem(0, "", all, null));

        for (int col = 0; col < 5; col++) {
            String[] arr = new String[5];
            for (int row = 0; row < 5; row++) arr[row] = zhuo.get(row * 5 + col).romaji();
            list.add(new GridItem(1, qing.get(col).hiragana() + "段", arr, null));
        }

        for (int i = 0; i < size; i++) {
            list.add(new GridItem(2, "", null, zhuo.get(i)));
        }

        for (int row = 0; row < 5; row++) {
            String[] arr = new String[5];
            for (int col = 0; col < 5; col++) arr[col] = zhuo.get(row * 5 + col).romaji();
            list.add((row + 1) * 6, new GridItem(1, zhuo.get(row * 5).hiragana() + "行", arr, null));
        }

        return list;
    }

    /** 拗音表：4 列 GridView */
    public List<GridItem> buildAoTable() {
        List<GridItem> list = new ArrayList<>();
        int size = ao.size();

        String[] all = new String[size];
        for (int i = 0; i < size; i++) all[i] = ao.get(i).romaji();
        list.add(new GridItem(0, "", all, null));

        String[] colTitles = aoColumnTitles();
        for (int col = 0; col < 3; col++) {
            String[] arr = new String[11];
            for (int row = 0; row < 11; row++) arr[row] = ao.get(row * 3 + col).romaji();
            list.add(new GridItem(1, colTitles[col] + "段", arr, null));
        }

        for (int i = 0; i < size; i++) {
            list.add(new GridItem(2, "", null, ao.get(i)));
        }

        String[] rowTitles = aoRowTitles();
        for (int row = 0; row < 11; row++) {
            String[] arr = new String[3];
            for (int col = 0; col < 3; col++) arr[col] = ao.get(row * 3 + col).romaji();
            list.add((row + 1) * 4, new GridItem(1, rowTitles[row] + "行", arr, null));
        }

        return list;
    }
}
