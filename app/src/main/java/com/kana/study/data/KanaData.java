package com.kana.study.data;

import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 假名表数据。
 * <p>
 * 原始数据来自 assets/kana.json：清音 51（10 行 5 列 + ん）、浊音 25（5×5）、拗音 33（11×3）。
 * 三张表的排版规则完全一样，都由 build() 生成：
 * <pre>
 *   [全部播放] [列标题 ×N]
 *   [行标题]   [假名 ×N]
 *   ...
 * </pre>
 * 「や行的い/え」「わ行的い/う/え」这些现代日语里不存在的位置，特征是平假名在表中重复出现过，
 * 由 existing() 自动识别标灰，不再硬编码下标 —— 调整 kana.json 顺序不会错位。
 */
public class KanaData {

    private static KanaData instance;

    private final List<Kana> qing;
    private final List<Kana> zhuo;
    private final List<Kana> ao;

    /** 只在主线程调用；kana.json 是随包资源，读不出来就是打包坏了，直接崩掉比静默降级好排查 */
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

    /** InputStream.readAllBytes() 要 API 33，这里自己来（available() + 单次 read 不保证读满） */
    private static byte[] readAll(InputStream is) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(8192);
        byte[] buf = new byte[8192];
        int n;
        while ((n = is.read(buf)) != -1) out.write(buf, 0, n);
        return out.toByteArray();
    }

    private static List<Kana> parse(JSONArray arr) throws JSONException {
        List<Kana> list = new ArrayList<>(arr.length());
        for (int i = 0; i < arr.length(); i++) {
            JSONObject o = arr.getJSONObject(i);
            list.add(new Kana(o.getString("ping"), o.getString("pian"), o.getString("luoma")));
        }
        return list;
    }

    /** 清音：6 列（行标题 + 5 个假名） */
    public List<GridItem> buildQingTable() {
        List<List<Kana>> rows = rows(qing, 5);
        return build(rows, rows.get(0), firstOfEachRow(rows));
    }

    /** 浊音：6 列；列标题借用清音的あ/い/う/え/お */
    public List<GridItem> buildZhuoTable() {
        List<List<Kana>> rows = rows(zhuo, 5);
        return build(rows, rows(qing, 5).get(0), firstOfEachRow(rows));
    }

    /** 拗音：4 列（行标题 + 3 个假名）；行列标题都借用清音 / 浊音表里的假名 */
    public List<GridItem> buildAoTable() {
        List<List<Kana>> q = rows(qing, 5);
        List<List<Kana>> z = rows(zhuo, 5);
        // 列标题取や行的 や / ゆ / よ
        List<Kana> colLabels = List.of(q.get(7).get(0), q.get(7).get(2), q.get(7).get(4));
        // 行标题：か さ た な は ま ら（清音）+ が ざ ば ぱ（浊音）
        List<Kana> rowLabels = List.of(
                q.get(1).get(0), q.get(2).get(0), q.get(3).get(0), q.get(4).get(0),
                q.get(5).get(0), q.get(6).get(0), q.get(8).get(0),
                z.get(0).get(0), z.get(1).get(0), z.get(3).get(0), z.get(4).get(0));
        return build(rows(ao, 3), colLabels, rowLabels);
    }

    /** 把一维假名表按 width 切成若干行，最后一行可以不满（清音的 ん 就是单独一行） */
    private static List<List<Kana>> rows(List<Kana> data, int width) {
        List<List<Kana>> rows = new ArrayList<>();
        for (int i = 0; i < data.size(); i += width) {
            rows.add(data.subList(i, Math.min(i + width, data.size())));
        }
        return rows;
    }

    private static List<Kana> firstOfEachRow(List<List<Kana>> rows) {
        List<Kana> labels = new ArrayList<>(rows.size());
        for (List<Kana> row : rows) labels.add(row.get(0));
        return labels;
    }

    /**
     * 标记每个格子是否是真实存在的假名：平假名第一次出现算存在，
     * 重复出现的（や行的い/え、わ行的い/う/え）算占位，显示为灰色且不可点。
     */
    private static boolean[][] existing(List<List<Kana>> rows) {
        Set<String> seen = new HashSet<>();
        boolean[][] exists = new boolean[rows.size()][];
        for (int r = 0; r < rows.size(); r++) {
            exists[r] = new boolean[rows.get(r).size()];
            for (int c = 0; c < exists[r].length; c++) {
                exists[r][c] = seen.add(rows.get(r).get(c).hiragana());
            }
        }
        return exists;
    }

    private static List<GridItem> build(List<List<Kana>> rows,
                                        List<Kana> colLabels,
                                        List<Kana> rowLabels) {
        boolean[][] exists = existing(rows);
        List<GridItem> list = new ArrayList<>();

        // [0] 全部播放：整张表按行序连播，跳过占位
        List<String> all = new ArrayList<>();
        for (int r = 0; r < rows.size(); r++) {
            for (int c = 0; c < rows.get(r).size(); c++) {
                if (exists[r][c]) all.add(rows.get(r).get(c).romaji());
            }
        }
        list.add(GridItem.all(all.toArray(new String[0])));

        // [1..N] 列标题（あ段、い段…），点击连播整列
        for (int c = 0; c < colLabels.size(); c++) {
            List<String> col = new ArrayList<>();
            for (int r = 0; r < rows.size(); r++) {
                if (c < rows.get(r).size() && exists[r][c]) col.add(rows.get(r).get(c).romaji());
            }
            list.add(GridItem.header(colLabels.get(c), "段", col.toArray(new String[0])));
        }

        // 逐行：行标题（あ行、か行…）+ 该行的假名
        for (int r = 0; r < rows.size(); r++) {
            List<String> line = new ArrayList<>();
            for (int c = 0; c < rows.get(r).size(); c++) {
                if (exists[r][c]) line.add(rows.get(r).get(c).romaji());
            }
            list.add(GridItem.header(rowLabels.get(r), "行", line.toArray(new String[0])));
            for (int c = 0; c < rows.get(r).size(); c++) {
                list.add(GridItem.cell(rows.get(r).get(c), exists[r][c]));
            }
        }

        return list;
    }
}
