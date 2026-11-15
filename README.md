# 🦕 恐龍探險島 | Dinosaur Adventure Island

適合 **4 歲** 小朋友的 **原生 Android、全離線** 恐龍遊戲，橫向畫面，大圖、大按鈕、中英文語音導覽，不使用 WebView、不需登入、沒有廣告或課金。

## 第一版內容

| 功能 | 小朋友的玩法 |
|---|---|
| 📖 恐龍圖鑑 | 18 隻恐龍，每隻有不同原創彩色插圖，點開可聽名稱、食性、身高、體重及有趣特徵 |
| 🌴 森林探險 | 點擊樹叢找藏起來的恐龍，得到貼紙 |
| 🥬 餵食恐龍 | 選擇植物或肉肉，答錯沒有懲罰，答對收集貼紙 |
| 🦴 挖掘化石 | 點五下慢慢挖出恐龍，完成獲得貼紙 |
| ❓ 猜猜恐龍 | 聽題目，從三隻恐龍中找出正確答案 |
| ⭐ 收藏冊 | 永久記錄已收集的恐龍貼紙 |
| ⚙️ 設定 | 中文／英文、朗讀開關、朗讀速度 |

所有內容均可離線遊玩，學習進度由 Android `SharedPreferences` 自動保存。朗讀依賴裝置已安裝的 Android Text-to-Speech 語音。

### 參考來源與美術

- 視覺方向參考 [Paleo.gg 的恐龍圖鑑](https://www.paleo.gg/zh/games/jurassic-world-alive/dinodex) ，但**不複製**該站圖片、角色混種設定或遊戲資產。
- 所有 18 張恐龍卡片圖與叢林場景皆由本專案的 `tools/generate_dinosaur_art.py` 生成，是獨立製作的 SVG 向量插畫經轉為 PNG。
- 恐龍身高與體重以方便幼兒理解的**約略值**呈現；古生物估算會因研究與個體不同而變動。

## 專案規格

- Kotlin 2.0 / Jetpack Compose / Material 3
- Android Gradle Plugin 8.7.3 / Gradle 8.9 / Java 17
- 最低 Android 8.0 (`minSdk=26`)，目標 Android 15 (`targetSdk=35`)
- Package ID: `com.jimmy.dinosaurkids`
- 螢幕：橫向 (`sensorLandscape`)
- 不需 `INTERNET` 權限

## 重要：簽章金鑰先由專案擁有者提供

**目前只準備專案原始碼與 Actions 流程；金鑰未準備前不應執行 release 打包。**

請先在你的新 repo 設定頁 `Settings → Secrets and variables → Actions → New repository secret` 加入：

| Actions Secret | 用途 |
|---|---|
| `KEYSTORE_BASE64` | 將 Android `.jks` 簽章檔轉為 Base64 後的**完整內容** |
| `KEYSTORE_PASSWORD` | Keystore 密碼 |
| `KEY_ALIAS` | 金鑰別名 |
| `KEY_PASSWORD` | 該金鑰的密碼 |

在 Windows PowerShell 將 `YOUR-KEY.jks` 轉成 Base64：

```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("C:\path\to\YOUR-KEY.jks")) | Set-Clipboard
```

請**不要**將 `.jks`、密碼或 Base64 金鑰提交到 GitHub 或貼在聊天視窗。

## CI / 最新版 Release

工作流程：`.github/workflows/release.yml`

- 對 `main` push 或手動執行 Actions 時，**先驗證 4 個 secrets**。
- 使用 Actions 中的 Key 簽章 APK，並用 `apksigner verify` 驗證。
- Release 固定為 tag `latest`，每次移除舊 Release，只保留最新 APK 與 SHA-256。
- 產出 `dinosaur-kids-latest.apk`。

## 如何建置

新 repo 推送本專案後，設定好簽章金鑰，從 Actions 執行 `Android Signed Release`，或 push 到 `main` 讓 workflow 自動執行。

本專案目前不含私有金鑰。遊戲原始碼與資源可重複產生；圖片生成腳本需 Python 的 `cairosvg`。