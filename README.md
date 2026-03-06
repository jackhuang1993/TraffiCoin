# TraffiCoin & Flight Tracker ✈️ 💰

這是一個基於 **Clean Architecture** 與 **MVVM** 架構開發的 Android 應用程式，整合了即時航班資訊查詢與匯率換算功能。本專案旨在展示現代化 Android 開發的最佳實踐，包括 Jetpack Compose、Hilt 依賴注入以及嚴謹的錯誤處理機制。

---

## 🛠 技術棧 (Tech Stack)

* **UI**: Jetpack Compose - 全宣告式 UI 框架。
* **架構**: MVVM (Model-View-ViewModel) + Repository Pattern。
* **依賴注入**: Hilt  - 簡化 Android 中的相依項注入。
* **異步處理**: Kotlin Coroutines & Flow。
* **網路層**: Retrofit + OkHttp。
* **本地存儲**: DataStore。
* **單元測試**: JUnit 4 + Compose Test Rule。

---

## ✨ 亮點功能 (Feature Highlights)

### 1. 客製化計算機鍵盤
* **專業輸入邏輯**：捨棄系統鍵盤，實作自定義計算機介面。
* **即時換算**：輸入金額時即時連動匯率計算，提供流暢的 UX 體驗。

### 2. 嚴謹的錯誤處理機制
* **分級異常捕獲**：區分 `IOException` (網路斷線) 與 `HttpException` (伺服器錯誤)，並提供使用者友善的錯誤提示。
* **狀態回饋**：在數據更新失敗時，透過 UI 滾動回頂部與錯誤字串提示，給予使用者明確的操作反饋。

---

## 🏗 架構設計 (Architectural Design)

專案遵循 **SOLID 原則**，確保程式碼具備高可測試性與可維護性：

* **Domain Layer**: 包含資料實體 (Data Models) 與業務規則。
* **Data Layer**: 透過 Repository 模式抽象化資料來源（API/Local），並處理 API 轉跳與 Header 注入。
* **UI Layer**: 使用 Compose 進行狀態驅動開發，確保 UI 與邏輯完全解耦。

---

## 🧪 測試策略 (Testing Strategy)

* **單元測試 (Unit Test)**：針對 ViewModel 的狀態轉換與計算機邏輯進行全面驗證。
* **UI 測試**：利用 `Espresso` 驗證 Dialog 的關閉行為（Back Press）與 Navigation 的正確性。

---

## 🚀 快速上手 (Quick Start)

1. 複製專案：`git clone [URL]`
2. 在 `local.properties` 中加入你的 API Key：
```properties
COIN_API_KEY=your_api_key_here
```
3. 直接運行 `app` 模組。
以幫你寫出更詳細的 **「Design Decisions (設計決策)」**，向面試官解釋為什麼你選擇 **Hilt 而不是 Koin**，或是為什麼選擇 **Compose 而不是 XML**。這通常是資深職位面試最愛問的部分。

---

## 操作影片:

* 基本操作: https://youtu.be/vdPDCIi9FVg
* 樣式變更(翻轉、Dark)：https://youtu.be/I59s0NiKRGY

