# 計算機功能實作計畫

本計畫旨在為專案新增一個完整的計算機功能，包含四則運算、清除功能，並根據需求調整 UI 配色（深灰色背景、橘色等於鍵）。

## 使用者審閱請求

> [!IMPORTANT]
> - 計算機將採用現代化的 Compose 佈局（Grid 網格形式）。
> - 預設背景將設為 `Color.DarkGray` (或接近的深灰色)。
> - 「等於」按鈕將設為 `Color(0xFFFF9800)` (橘色)。

## 提議的變更

### [應用程式核心]

將實作計算機的邏輯與介面。

#### [MODIFY] [MainActivity.kt](file:///C:/Users/User/AndroidStudioProjects/Mycalu/app/src/main/java/com/example/mycalu/MainActivity.kt)
- 移除範例 `Greeting` 程式碼。
- 新增 `CalculatorApp` 主進入點。
- 實作 `CalculatorScreen` 負責 UI 佈局。
- 實作計算邏輯（狀態處理、運算符號判斷）。

#### [MODIFY] [Color.kt](file:///C:/Users/User/AndroidStudioProjects/Mycalu/app/src/main/java/com/example/mycalu/ui/theme/Color.kt)
- 定義計算機專用的顏色（橘色、深灰色）。

## 驗證計畫

### 手動驗證
- 啟動 App 後，確認背景為深灰色。
- 測試按鈕 0-9 是否能正確輸入。
- 測試加減乘除功能：
    - 例如：12 + 34 = 46
    - 例如：50 * 2 = 100
- 測試清除按鈕 (AC) 是否能歸零。
- 確認等於鍵顏色是否為橘色。
