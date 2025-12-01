# Development Guide & Roadmap

## Quick Start

### Prerequisites
```bash
# Check Java
java -version          # Should be 21+

# Check Gradle  
gradle --version       # Should be 9.2.0+

# Need Android SDK (not installed yet)
# Download Android Studio: https://developer.android.com/studio
```

### First Build
```bash
# 1. Install Android SDK 34
#    (via Android Studio SDK Manager)

# 2. Clone/Open project in Android Studio
cd /workspaces/Que-Browser

# 3. Build debug APK
./gradlew assembleDebug

# 4. Install on device
adb install app/build/outputs/apk/debug/app-debug.apk

# 5. Run
adb shell am start -n com.viaadvancedbrowser.debug/.activities.MainBrowserActivity
```

---

## Project Structure Quick Reference

```
📁 Que-Browser/
  📄 build.gradle              ← Root build config (FIXED ✓)
  📄 gradle.properties         ← Build props (FIXED ✓)
  📄 settings.gradle           ← Module config
  📄 README.md                 ← Project overview
  📄 PROJECT_ANALYSIS.md       ← This file (NEW ✓)
  📄 ARCHITECTURE.md           ← Technical design (NEW ✓)
  📄 BUILD_INSTRUCTIONS.md     ← Build guide (GENERATED ✓)
  
  📁 app/
    📄 build.gradle            ← Dependencies (FIXED ✓)
    📄 proguard-rules.pro      ← Obfuscation
    
    📁 src/main/
      📄 AndroidManifest.xml   ← App permissions & components
      
      📁 java/com/viaadvancedbrowser/
        📁 activities/         ← 10 activity files
        📁 browser/            ← WebView + TabManager
        📁 database/           ← Room entities/DAOs
        📁 download/           ← Video download
        📁 network/            ← Security (ad-block, DNS, etc)
        📁 password/           ← Password manager
        📁 qr/                 ← QR scanner
        📁 reading/            ← Reading mode
        📁 reflow/             ← Text reflow
        📁 script/             ← User scripts
        📁 security/           ← Encryption
        📁 settings/           ← Preferences
        📁 utils/              ← Utilities
        📁 vpn/                ← VPN service
      
      📁 res/
        📁 layout/             ← 11 XML layouts
        📁 drawable/           ← 4 vector drawables
        📁 values/             ← strings, colors, styles
        📁 xml/                ← network_security_config
      
      📁 assets/
        📁 scripts/
          📄 readability.js    ← Content extraction
        📁 webui/
          📄 reading_mode.html ← Reading UI
```

---

## Implementation Status by Feature

### ✅ Fully Implemented
- **Multi-tab browser** - TabManager handles 4+ concurrent tabs
- **Ad blocking** - Filters 10+ known ad domains
- **Password manager** - Encrypted storage + biometric unlock + auto-fill
- **Custom DNS** - Resolver + DoH support (RFC 8484)
- **HTTPS enforcement** - Auto-upgrades http:// → https://
- **Certificate pinning** - MITM protection
- **Reading mode** - Uses Mozilla's readability.js
- **QR scanner** - ZXing integration
- **VPN service** - Android VpnService with 4 servers
- **User scripts** - JS injection framework
- **Bookmarks** - Room database with CRUD
- **Settings** - SharedPreferences wrapper

### ⚠️ Partial Implementation
- **Video download** - Activity UI only, no extraction logic
- **Text reflow** - Activity placeholder, needs CSS engine

### 🔧 Under Consideration
- **Form confirmation** - Currently auto-submits passwords
- **Ad domain updates** - Hardcoded, should be remote JSON
- **Real VPN provider** - Currently uses placeholder servers

---

## Common Development Tasks

### Add a New Security Feature
```java
// 1. Create in network/ module
public class MySecurityFilter {
    public boolean shouldAllow(String url) {
        // implementation
        return true;
    }
}

// 2. Integrate into EnhancedWebViewClient
EnhancedWebViewClient.java:
    MySecurityFilter filter = new MySecurityFilter();
    if (!filter.shouldAllow(url)) {
        return WebViewClient.LOAD_CANCEL;
    }

// 3. Test against sample URLs
AdBlocker.java reference for pattern
```

### Add a New Activity
```java
// 1. Create activity class
public class MyNewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_new);
    }
}

// 2. Create layout file
res/layout/activity_my_new.xml

// 3. Register in AndroidManifest.xml
<activity android:name=".activities.MyNewActivity" />

// 4. Add menu item in MainBrowserActivity
if (id == R.id.menu_my_new) {
    startActivity(new Intent(this, MyNewActivity.class));
}
```

### Update Ad Domain List
```java
// Current: AdBlocker.java lines 14-25
private void loadAdDomains() {
    adDomains = new HashSet<>(Arrays.asList(
        "doubleclick.net",          // ← Add new domains here
        "googleadservices.com",
        // ... etc
    ));
}

// Recommended: Load from JSON endpoint
public void loadAdDomainsFromRemote(String jsonUrl) {
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder().url(jsonUrl).build();
    // Parse response as JSON array
}
```

### Enable Password Manager for New Domain
```java
// Already works automatically!
// When user navigates to new domain:
// 1. PasswordManager.getPasswordsForDomain("newdomain.com")
// 2. If found: auto-fill
// 3. If not: user can save after login via form submission handler
```

### Implement Video Download
```java
// VideoDownloadActivity.java currently empty
// Recommended approach:

public class VideoDownloadManager {
    public void downloadVideo(String videoUrl, String filename) {
        // 1. Use OkHttpClient to fetch video
        // 2. Write to DCIM/ or Downloads/
        // 3. Notify MediaStore
        // 4. Show progress notification
    }
}

// Detection: Intercept in EnhancedWebViewClient
// when resource MIME type is video/*
```

---

## Build Variants & Signing

### Debug Build
```gradle
// Automatically configured
debug {
    applicationIdSuffix '.debug'
    versionNameSuffix '-DEBUG'
}

// Result: com.viaadvancedbrowser.debug
// No signature required
```

### Release Build
```gradle
release {
    minifyEnabled true          // ProGuard compression
    shrinkResources true        // Remove unused resources
    proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 
                 'proguard-rules.pro'
}

// Requires keystore for signing
// Create: keytool -genkey -v -keystore release.keystore ...
// Configure: signingConfigs in build.gradle
```

---

## Testing Checklist

Before release:

### Manual Testing
- [ ] Launch app, tap address bar
- [ ] Type URL (e.g., example.com)
- [ ] Verify HTTPS auto-upgrade
- [ ] Open Settings → check menu appears
- [ ] Open DNS Settings → verify layout
- [ ] Open Password Manager → add test credential
- [ ] Navigate to site with form → verify auto-fill
- [ ] Enable ad blocking → verify ads don't load
- [ ] Scan QR code → verify camera works
- [ ] Open reading mode → verify content extraction
- [ ] Close tab → verify TabManager cleanup
- [ ] Kill app → verify no crashes

### Automated Testing
```bash
# Run unit tests
./gradlew test

# Run instrumented tests (requires emulator)
./gradlew connectedAndroidTest

# Run linting
./gradlew lint
```

### Code Quality
```bash
# Check for issues
./gradlew build --scan

# View ProGuard mapping
app/build/outputs/mapping/release/mapping.txt
```

---

## Troubleshooting

### Build Fails: "Cannot resolve symbol"
```
→ Run: gradlew clean build
→ Check: Settings → Gradle → Gradle home (set to ANDROID_HOME)
```

### Build Fails: "SDK location not found"
```
→ Create: local.properties with:
   sdk.dir=/path/to/android-sdk
```

### App Crashes on Start
```
→ Check: AndroidManifest.xml has BrowserApplication class
→ Verify: All activities are registered
→ Review: logcat output: adb logcat | grep viaadvancedbrowser
```

### WebView Blank
```
→ Enable JS: WebSettings.setJavaScriptEnabled(true)
→ Allow camera: Permissions in manifest
→ Check: URL is valid http/https
```

### Password Manager Not Auto-filling
```
→ Check: Domain matches exactly (case-insensitive)
→ Verify: Password saved in this domain
→ Debug: Check logcat for PasswordManager logs
```

---

## Performance Optimization Tips

### Memory
```java
// WebView uses significant memory
// Best practices:
1. Reuse WebView instances (TabManager does this)
2. Destroy unused tabs (TabManager.closeTab)
3. Limit concurrent tabs to 4-5
4. Clear cache periodically: webView.clearCache(true)
```

### Network
```java
// OkHttp is already configured with:
1. Connection pooling (reuses TCP)
2. Response caching
3. Gzip compression

// To improve further:
1. Implement service worker cache
2. Use WebP images where possible
3. Minify JS/CSS assets
```

### Battery
```java
// VPN service uses background processing
// Optimize by:
1. Pause VPN when screen off
2. Batch DNS lookups
3. Disable unnecessary logging
```

---

## Deployment Checklist

### Pre-Release
- [ ] Update VERSION_CODE in build.gradle
- [ ] Update VERSION_NAME (semantic versioning)
- [ ] Update strings.xml for new features
- [ ] Test on API 21 device (min SDK)
- [ ] Test on API 34 device (target SDK)
- [ ] Review all permissions needed
- [ ] Update CHANGELOG.md

### Release
- [ ] Create signed APK: `./gradlew assembleRelease`
- [ ] Verify APK signature: `jarsigner -verify -verbose release.apk`
- [ ] Upload to Play Store / GitHub Releases
- [ ] Tag commit: `git tag v1.0.0`
- [ ] Announce on social media

### Post-Release
- [ ] Monitor crash reports
- [ ] Respond to user feedback
- [ ] Plan next version features

---

## Learning Resources

### Android Official
- [Android Developers](https://developer.android.com)
- [WebView Best Practices](https://developer.android.com/develop/ui/views/layout/webapps/webview)
- [Security Guidelines](https://developer.android.com/privacy-and-security)

### Related Libraries
- [OkHttp Docs](https://square.github.io/okhttp)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [Encrypt SharedPreferences](https://developer.android.com/reference/androidx/security/crypto/EncryptedSharedPreferences)

### Architecture Patterns
- [MVVM Pattern](https://developer.android.com/topic/architecture/viewmodel)
- [Dependency Injection](https://developer.android.com/training/dependency-injection)
- [App Architecture Guide](https://developer.android.com/topic/architecture)

---

## FAQ

**Q: Can I use Que-Browser as a library?**
A: Currently it's an app, not a library. To make it reusable, extract the core browser engine (AdvancedWebView + managers) into a separate Android Library module.

**Q: How do I add custom DNS servers?**
A: Edit CustomDNSManager.java or via Settings → DNS Settings activity. Servers are stored in PreferencesManager.

**Q: Can I block more ads?**
A: Currently hardcoded in AdBlocker.loadAdDomains(). Recommendation: Load from remote JSON for daily updates.

**Q: Is the VPN actually private?**
A: Currently uses placeholder servers. For production: integrate with real VPN provider (ExpressVPN SDK, Windscribe, etc.)

**Q: How are passwords encrypted?**
A: Using AndroidX EncryptedSharedPreferences (AES-256-GCM with Android Keystore master keys).

**Q: Can I use this on Android < 5.0?**
A: No, minSdk is 21. To support lower versions would require backporting security features.

**Q: How do I debug JavaScript issues?**
A: Use Chrome DevTools: chrome://inspect → select WebView

---

## Future Roadmap (Suggestions)

### Version 1.1.0
- [ ] Implement video download extraction
- [ ] Complete text reflow engine
- [ ] Add session restore on app restart
- [ ] User feedback notification system

### Version 1.2.0
- [ ] Sync bookmarks to cloud (optional)
- [ ] Themes: Dark mode, custom colors
- [ ] Download manager with pause/resume
- [ ] JavaScript console for debugging

### Version 2.0.0
- [ ] Replace placeholder VPN with real provider
- [ ] Remote ad domain list updates (JSON feed)
- [ ] Tracker report dashboard
- [ ] Plugin system for user scripts
- [ ] Chromium engine option (fallback from WebView)

---

## Contact & Support

For issues or questions:
1. Check this guide's troubleshooting section
2. Review logcat: `adb logcat -v ThreadTime | grep viaadvancedbrowser`
3. Check GitHub Issues
4. Review ARCHITECTURE.md for design decisions

---

## License & Attribution

- **Readability.js**: Mozilla License (included in assets/scripts)
- **ZXing**: Apache 2.0 (QR scanning)
- **OkHttp**: Apache 2.0 (HTTP client)
- **Room Database**: Apache 2.0 (data persistence)

See LICENSE file for full details.

---

**Generated:** 2025-12-01  
**Project Version:** 1.0.0  
**Gradle Version:** 9.2.0  
**Target API:** 34 (Android 14)

