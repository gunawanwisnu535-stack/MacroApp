# 🎮 MacroApp - Android Macro Recorder & Player

Aplikasi Android untuk merekam dan memutar ulang aksi sentuhan layar secara otomatis, mirip seperti Nebula Touch.

## 📋 Fitur

✅ **Recording**: Merekam setiap tap, swipe, dan long press
✅ **Playback**: Memutar ulang macro dengan timing yang akurat
✅ **Floating Button**: Tombol mengambang untuk kontrol mudah
✅ **Accessibility Service**: Menggunakan API resmi Android tanpa root
✅ **Simple UI**: Interface yang mudah digunakan

## 🛠️ Setup & Instalasi

### Prerequisites
- Android Studio (Arctic Fox atau lebih baru)
- Java 11+
- Android SDK 24+ (API Level 24)

### Clone & Setup

```bash
git clone https://github.com/gunawanwisnu535-stack/MacroApp.git
cd MacroApp
```

### Build & Run

1. Buka project di Android Studio
2. Tunggu gradle sync selesai
3. Pilih perangkat atau emulator
4. Click **Run** (atau tekan Shift + F10)

## 📖 Penggunaan

### Step 1: Aktifkan Accessibility Service
1. Buka aplikasi MacroApp
2. Klik prompt untuk membuka Settings → Accessibility
3. Cari "MacroApp" dan aktifkan

### Step 2: Mulai Recording
1. Klik "▶️ Mulai Recording"
2. Lakukan aksi yang ingin direkam (tap, swipe, dll)
3. Klik "⏹️ Hentikan Recording" untuk selesai

### Step 3: Putar Macro
1. Klik "▶️ Putar Macro"
2. Macro akan dijalankan otomatis

## 🔧 Struktur Project

```
MacroApp/
├── app/
│   └── src/
│       └── main/
│           ├── java/com/example/macroapp/
│           │   ├── MainActivity.java           # Activity utama
│           │   └── service/
│           │       ├── MacroAccessibilityService.java  # Service untuk gesture
│           │       └── MacroRecordingService.java      # Service untuk recording
│           ├── res/
│           │   ├── layout/
│           │   │   └── activity_main.xml     # Layout UI
│           │   ├── xml/
│           │   │   └── accessibility_config.xml
│           │   └── values/
│           │       ├── strings.xml
│           │       ├── colors.xml
│           │       └── themes.xml
│           └── AndroidManifest.xml
├── build.gradle
└── settings.gradle
```

## 🚀 Implementasi Lanjutan

### TODO List (Fitur yang akan ditambah)
- [ ] Simpan macro ke file JSON
- [ ] Load macro dari file
- [ ] Editor visual untuk macro
- [ ] Delay/timing custom untuk setiap action
- [ ] Multi-touch support
- [ ] Replay dengan loop
- [ ] Export/Import macro

## 📱 API Reference

### MacroAccessibilityService Methods

```java
// Single tap
simulateClick(int x, int y, long duration)

// Swipe gesture
simulateSwipe(int x1, int y1, int x2, int y2, long duration)

// Long press
simulatePress(int x, int y, long duration)
```

## ⚠️ Important Notes

- Aplikasi memerlukan **Accessibility Service** untuk berfungsi
- Tidak memerlukan root access
- Kompatibel dengan Android 5.0+ (API 24+)
- Mematuhi kebijakan Google Play Store

## 📄 License

MIT License - bebas digunakan dan dimodifikasi

## 👨‍💻 Developer

Built by: gunawanwisnu535-stack

## 📞 Support

Jika ada pertanyaan atau bug report, silakan buka issue di repository ini.

---

**Happy Macro Recording! 🎯**
