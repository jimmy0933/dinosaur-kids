# 🦕 恐龍探險島 | Dinosaur Adventure Island

為 4 歲兒童設計的 Android 原生恐龍遊戲，橫向畫面、可離線遊玩、大圖示、繁體中文與英文語音。

## 第一版

- 🦖 恐龍圖鑑：18 種恐龍、食性、身高體重和有趣特徵
- 🌴 森林探險：尋找藏在樹叢裡的恐龍
- 🌿 恐龍餵食：依照食性選食物
- 🦴 挖掘化石：觸控挖出恐龍
- ❓ 猜恐龍：聽語音找正確圖片
- ⭐ 貼紙收藏冊：本機儲存學習進度
- ⚙️ 語言、語音開關與速度設定

設計參考 https://www.paleo.gg/zh/games/jurassic-world-alive/dinodex ，不直接複製其角色插畫或其他資產。

## 簽章

透過 GitHub Actions Secrets 保管簽章資料，**切勿上傳 keystore 或明碼密碼**。

需要：`KEYSTORE_BASE64`、`KEYSTORE_PASSWORD`、`KEY_ALIAS`、`KEY_PASSWORD`。

CI 建置成功後固定發佈 `latest` Release，並清除舊的 Release。

## 開發規格

Kotlin、Jetpack Compose、Android 原生、橫向畫面，不含 WebView、廣告、帳戶或網路權限。

