# 日语五十音

一个离线的日语五十音学习工具：清音 / 浊音 / 拗音三张表，点击任意假名播放发音，
点击行、列标题或左上角的播放按钮可批量连播，右上角一键切换平假名 / 片假名显示。

## 特点

- **完全离线**：不申请任何权限，无需联网
- **依赖极简**：运行时只有 `androidx.core` 和 `androidx.viewpager`
- 支持 Android 8.0（API 26）及以上

## 构建

```bash
./gradlew assembleDebug      # 调试包
./gradlew lintDebug          # 静态检查
```

需要 JDK 21 与 Android SDK（compileSdk 37）。

## 数据结构

假名数据在 `app/src/main/assets/kana.json`，每条包含：

| 字段      | 含义                                 |
|---------|------------------------------------|
| `ping`  | 平假名                                |
| `pian`  | 片假名                                |
| `luoma` | 罗马音，同时用作 `assets/<luoma>.mp3` 的文件名 |

## 许可证

[GNU GPL v3](LICENSE)。
