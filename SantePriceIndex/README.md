# 🌾 Sante Price Index — Android App
### Vendor Intelligence for Weekly Market Sellers

---

## 📱 App Overview

A Kotlin Android app that helps small vendors in village weekly markets (Santes) price their goods
fairly and profitably, based on real city Mandi prices.

---

## 🏗️ Project Structure

```
SantePriceIndex/
├── app/src/main/
│   ├── java/com/sante/priceindex/
│   │   ├── MainActivity.kt                  ← Entry point, BottomNav + NavGraph
│   │   ├── ui/
│   │   │   ├── SharedViewModel.kt           ← Shared state (prices, selection)
│   │   │   ├── home/
│   │   │   │   ├── HomeFragment.kt          ← Price Watch screen
│   │   │   │   └── MandiPriceAdapter.kt     ← RecyclerView adapter
│   │   │   ├── profitcalc/
│   │   │   │   └── ProfitCalcFragment.kt    ← Profit Calculator
│   │   │   ├── priceboard/
│   │   │   │   ├── PriceBoardActivity.kt    ← Digital Slate (Yellow-on-Black)
│   │   │   │   └── PriceBoardAdapter.kt     ← Grid adapter for Price Board
│   │   │   └── trends/
│   │   │       └── TrendsFragment.kt        ← 7-day trend charts
│   │   ├── data/
│   │   │   ├── model/MandiPrice.kt          ← Data models + enums
│   │   │   └── repository/MandiRepository.kt ← Mock Firebase data
│   │   └── util/PricingEngine.kt            ← Cost-plus pricing algorithm
│   └── res/
│       ├── layout/                          ← All XML layouts
│       ├── navigation/nav_graph.xml         ← Navigation graph
│       ├── menu/bottom_nav_menu.xml         ← Bottom navigation
│       ├── values/colors.xml, themes.xml    ← Dark theme + yellow accents
│       └── drawable/                        ← Vector icons
```

---

## 🚀 Setup in Android Studio

### Step 1: Open the Project
1. Launch Android Studio
2. `File → Open` → Select the `SantePriceIndex` folder
3. Wait for Gradle sync to complete

### Step 2: Sync Dependencies
The app uses:
- **MPAndroidChart** (via JitPack) — for trend line charts
- **Material Components** — for sliders, buttons, cards
- **Navigation Component** — for fragment routing

If Gradle sync fails, go to `File → Invalidate Caches → Restart`.

### Step 3: Run the App
- Connect an Android device (API 24+) or start an emulator
- Click `▶ Run` or press `Shift + F10`

---

## 💡 The Pricing Algorithm (PricingEngine.kt)

```
Total Cost/kg  = Mandi Price + Transport Cost/kg + (Mandi Price × Wastage%)
     RRP/kg    = Total Cost/kg × (1 + Profit Margin%)
```

**Example:**
- Onion Mandi Price = ₹18/kg
- Transport = ₹2/kg
- Wastage = 10%
- Profit = 25%

```
Total Cost = 18 + 2 + (18 × 0.10) = 18 + 2 + 1.8 = ₹21.8/kg
RRP        = 21.8 × 1.25           = ₹27.25/kg
Market Price (rounded) = ₹28/kg
```

---

## 🔥 Adding Real Firebase Data

1. Create a Firebase project at https://console.firebase.google.com
2. Add an Android app with package: `com.sante.priceindex`
3. Download `google-services.json` → place in `app/` folder
4. Uncomment Firebase lines in `app/build.gradle`:
   ```groovy
   id 'com.google.gms.google-services'
   // ...
   implementation platform('com.google.firebase:firebase-bom:32.7.0')
   implementation 'com.google.firebase:firebase-database-ktx'
   ```
5. In `MandiRepository.kt`, replace `fetchTodayPrices()` with the Firebase snippet shown in the comments

**Firebase Realtime DB Structure:**
```json
{
  "mandi_prices": {
    "onion": {
      "id": "onion",
      "vegetableName": "Onion",
      "vegetableNameHindi": "प्याज",
      "mandiPrice": 18.0,
      "unit": "kg",
      "trend": "RISING",
      "emoji": "🧅"
    }
  }
}
```

---

## 📱 Screens

| Screen | Description |
|--------|-------------|
| **Price Watch** | Live Mandi prices with trend indicators (↑↓→) |
| **Profit Calc** | Enter transport/wastage costs → Get RRP + Net Profit |
| **Trends** | 7-day line chart with week-over-week analysis |
| **Price Board** | Full-screen **Yellow on Black** digital slate for customers |

---

## 🎓 Financial Literacy Terms (Taught in App)

| Term | Meaning |
|------|---------|
| **Gross Sales** | Total Revenue collected |
| **Net Profit** | Revenue − All Costs (Mandi + Transport + Wastage) |
| **Profit %** | (Net Profit ÷ Total Investment) × 100 |
| **RRP** | Recommended Retail Price (fair price for customers) |

---

## 🛠️ Troubleshooting

**Build error: "Unresolved reference: MPAndroidChart"**
→ Make sure JitPack is in `settings.gradle` repositories (already included)

**Navigation not working**
→ Ensure fragment IDs in `nav_graph.xml` match menu item IDs in `bottom_nav_menu.xml`

**Price Board not going fullscreen**
→ Test on a physical device; emulator may not hide navigation bar fully

---

## 📦 Dependencies Summary

```groovy
// UI
implementation 'com.google.android.material:material:1.11.0'
implementation 'androidx.recyclerview:recyclerview:1.3.2'
implementation 'androidx.cardview:cardview:1.0.0'
implementation 'androidx.constraintlayout:constraintlayout:2.1.4'

// Navigation
implementation 'androidx.navigation:navigation-fragment-ktx:2.7.6'
implementation 'androidx.navigation:navigation-ui-ktx:2.7.6'

// ViewModel + LiveData
implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0'
implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.7.0'

// Charts (JitPack)
implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'
```
