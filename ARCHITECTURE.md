# Que-Browser Architecture & API Documentation

## System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                    MainBrowserActivity (UI)                 │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐       │
│  │ TabManager   │  │ Toolbar UI   │  │ Menu Items   │       │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘       │
│         │                 │                  │               │
│  ┌──────▼─────────────────▼──────────────────▼────────┐      │
│  │         AdvancedWebView (Custom WebView)           │      │
│  └──────┬─────────────────────────────────────────────┘      │
│         │                                                     │
└─────────┼─────────────────────────────────────────────────────┘
          │
    ┌─────▼──────────────────────────────────────────┐
    │        Request Interception Layer              │
    ├──────────────────────────────────────────────┬─┤
    │ EnhancedWebViewClient + EnhancedWebChromeClient
    └──────┬──────────────────────────────────────┬─┘
           │                                      │
    ┌──────▼────────────┐  ┌────────────────────▼──┐
    │   Security Layer  │  │  Content Injection   │
    ├──────────────────┤  ├──────────────────────┤
    │ • AdBlocker      │  │ • ScriptManager      │
    │ • HTTPSEverywhere│  │ • PasswordManager    │
    │ • CertificatePinner│  │ • ReadingMode       │
    │ • CustomDNSManager│  │                      │
    │ • DNSOverHTTPS   │  │                      │
    └────────┬─────────┘  └────────┬─────────────┘
             │                     │
    ┌────────▼─────────────────────▼──────────────┐
    │        Peripheral Services                  │
    ├─────────────────────────────────────────────┤
    │ • VPNManager/BrowserVpnService              │
    │ • BookmarkDao (Room Database)               │
    │ • PasswordManager (EncryptedSharedPrefs)    │
    │ • EncryptionManager (SecurityCrypto)        │
    │ • PreferencesManager (SharedPreferences)    │
    └─────────────────────────────────────────────┘
```

---

## Module Interaction Flow

### 1. Page Load Flow
```
User inputs URL
    ↓
MainBrowserActivity.loadUrl()
    ↓
AdvancedWebView.loadUrl()
    ↓
EnhancedWebViewClient.shouldOverrideUrlLoading()
    ├→ AdBlocker.isAd() [block ads]
    ├→ HTTPSEverywhere.enforceHttps()
    ├→ CertificatePinner.verify()
    └→ CustomDNSManager.resolve()
    ↓
Page Renders
    ↓
EnhancedWebChromeClient.onPageFinished()
    ├→ ScriptManager.injectUserScripts()
    ├→ PasswordManager.autoFill() [if applicable]
    └→ ReadingModeExtractor (prep if enabled)
    ↓
Ready for User Interaction
```

### 2. Password AutoFill Flow
```
Page Load Complete
    ↓
EnhancedWebViewClient checks domain
    ↓
PasswordManager.autoFill(webView, domain)
    ├→ getPasswordsForDomain() [query encrypted storage]
    └→ buildAutoFillScript() [generate JS]
    ↓
webView.evaluateJavascript() [inject & execute]
    ├→ Find email/username field
    ├→ Find password field
    ├→ Fill values
    └→ Optional: Auto-submit form
    ↓
Page interactivity restored
```

### 3. VPN Connection Flow
```
User enables VPN in VPNConfigActivity
    ↓
VPNManager.connectToServer(vpnServer)
    ├→ Create VPNConfig with DNS servers
    └→ Start BrowserVpnService
    ↓
BrowserVpnService.onStartCommand()
    ├→ Request VPN permission
    ├→ Create VPN interface (ParcelFileDescriptor)
    ├→ Route traffic through VPN
    └→ Broadcast connection status
    ↓
All WebView requests route through VPN tunnel
```

---

## API Reference

### MainBrowserActivity
```java
// Main browser entry point - launch URLs and manage features

public class MainBrowserActivity extends AppCompatActivity {
    // Lifecycle
    protected void onCreate(Bundle savedInstanceState)
    protected void onResume()
    protected void onPause()
    protected void onDestroy()
    
    // Navigation
    public void onBackPressed()
    
    // Menu handling
    public boolean onCreateOptionsMenu(Menu menu)
    public boolean onOptionsItemSelected(MenuItem item)
    
    // Intent handling
    private void handleIntent(Intent intent)  // Handles ACTION_VIEW
    
    // Component initialization
    private void initializeManagers()
    private void setupWebView()
}
```

### TabManager
```java
// Multi-tab browser management

public class TabManager {
    // Constructor
    public TabManager(Context context, int containerId)
    
    // Tab operations
    public int createNewTab(String url)      // Returns tab ID
    public void closeTab(int tabId)          // Removes tab
    public void switchToTab(int tabId)       // Makes active
    
    // Getters
    public AdvancedWebView getCurrentWebView()
    public int getCurrentTabId()
    public SparseArray<AdvancedWebView> getAllTabs()
}
```

### AdvancedWebView
```java
// Extended WebView with integrated ad-blocking

public class AdvancedWebView extends WebView {
    // Custom methods
    public void setAdBlocker(AdBlocker blocker)
    public void enableAdBlocking(boolean enable)
    
    // Inherits all WebView methods
    public void loadUrl(String url)
    public void goBack()
    public void goForward()
    // etc...
}
```

### AdBlocker
```java
// Ad domain filtering

public class AdBlocker {
    public AdBlocker()                           // Loads hardcoded domains
    public AdBlocker(Context context)            // Alternative constructor
    
    public boolean isAd(String url)             // Check if URL is ad
    public void addAdDomain(String domain)      // Add to blocklist
    public void removeAdDomain(String domain)   // Remove from blocklist
    
    // Internal
    private void loadAdDomains()                // Populate default list
}

// Hardcoded Domains (v1.0):
// doubleclick.net, googleadservices.com, googlesyndication.com,
// google-analytics.com, facebook.com/tr, adsystem.com, adnxs.com,
// amazon-adsystem.com, scorecardresearch.com, 2mdn.net
```

### PasswordManager
```java
// Encrypted password storage & auto-fill

public class PasswordManager {
    public PasswordManager(Context context)
        throws GeneralSecurityException, IOException
    
    // Save operations
    public void savePassword(String domain, String username, 
                           String password, String formData)
    
    // Retrieve operations
    public List<PasswordEntry> getPasswordsForDomain(String domain)
    
    // Auto-fill
    public void autoFill(WebView webView, String domain)
    
    // Internal
    private void persistAll(List<PasswordEntry> entries)
    private String buildAutoFillScript(String username, String password)
}

// Storage Format: JSON array of PasswordEntry objects
// Encryption: EncryptedSharedPreferences (AES-256)
// Key Storage: Android Keystore (hardware-backed when available)
```

### PasswordEntry
```java
// Password data model

public class PasswordEntry {
    private String domain;           // e.g., "github.com"
    private String username;         // Email or username
    private String password;         // Plaintext (encrypted at storage)
    private String formData;         // Optional form metadata
    private long timestamp;          // Creation time (milliseconds)
    
    // Getters/Setters
    public String getDomain()
    public void setDomain(String domain)
    // ... etc for all fields
}
```

### VPNManager
```java
// VPN connection orchestration

public class VPNManager {
    public VPNManager(Context context)
    
    // Connection management
    public void connectToServer(VPNServer server)  // Start VPN service
    public void disconnect()                       // Stop VPN service
    
    // Server management
    public List<VPNServer> getAvailableServers()  // 4 default servers
    public boolean isVPNActive()                   // Check status
    
    // Internal
    private boolean isServiceRunning(Class<?> serviceClass)
}

// Default Servers:
// - Netherlands: nl.vpn.example.com:1194/udp
// - USA East: us-east.vpn.example.com:1194/udp
// - Japan: jp.vpn.example.com:1194/udp
// - Singapore: sg.vpn.example.com:1194/udp
```

### BrowserVpnService
```java
// Android VPN service implementation

public class BrowserVpnService extends VpnService {
    // Intent actions
    // "connect" - establish VPN tunnel
    // "disconnect" - close VPN tunnel
    
    public int onStartCommand(Intent intent, int flags, int startId)
    
    // Called by VpnService when VPN closes
    public void onRevoke()
}
```

### CustomDNSManager
```java
// Custom DNS resolution

public class CustomDNSManager {
    public CustomDNSManager(Context context)
    
    public void applyDNSSettings()
    
    // Internal implementation details
}
```

### DNSOverHTTPS
```java
// RFC 8484 DNS-over-HTTPS implementation

public class DNSOverHTTPS {
    // DNS resolution via HTTPS endpoint
    // Typically uses Google DNS (8.8.8.8) or Cloudflare (1.1.1.1)
}
```

### HTTPSEverywhere
```java
// Automatic HTTPS upgrading

public class HTTPSEverywhere {
    public String enforceHttps(String url)  // Convert http:// → https://
}
```

### CertificatePinner
```java
// Certificate pinning for MITM protection

public class CertificatePinner {
    public boolean verify(String host, Certificate cert)
}
```

### ScriptManager
```java
// User script management

public class ScriptManager {
    public ScriptManager(Context context)
    
    public void executeScript(String script, WebView webView)
    public List<UserScript> getActiveScripts()
}

// UserScript model
public class UserScript {
    private String name;
    private String code;
    private boolean enabled;
    private List<String> matchPatterns;  // URL glob patterns
}
```

### ReadingModeExtractor
```java
// Content extraction for distraction-free reading

public class ReadingModeExtractor {
    // Uses readability.js (Mozilla algorithm)
    // Extracts article content, removes ads/sidebars
    // Returns JSON with {"content":"<article>...</article>"}
}
```

### TextReflowEngine
```java
// Text optimization for readability

public class TextReflowEngine {
    // Applies CSS transformations for better reading
    // Adjusts font sizes, line heights, margins
}
```

### EncryptionManager
```java
// Encryption wrapper for SharedPreferences

public class EncryptionManager {
    public EncryptionManager(Context context)
        throws GeneralSecurityException, IOException
    
    // Encrypted storage
    public void putString(String key, String value)
    public String getString(String key, String defaultValue)
    
    // Internally uses:
    // - EncryptedSharedPreferences (AndroidX Security)
    // - Android Keystore for key storage
    // - AES/GCM encryption
}
```

### PreferencesManager
```java
// Application settings management

public class PreferencesManager {
    // Static utility methods
    public static boolean isCustomDNSEnabled(Context context)
    public static String getHomePage(Context context)
    // ... etc for user preferences
}
```

### BookmarkDao
```java
// Room database access for bookmarks

public interface BookmarkDao {
    @Query("SELECT * FROM bookmarks")
    List<Bookmark> getAllBookmarks()
    
    @Insert
    void insertBookmark(Bookmark bookmark)
    
    @Update
    void updateBookmark(Bookmark bookmark)
    
    @Delete
    void deleteBookmark(Bookmark bookmark)
}
```

### Bookmark
```java
// Bookmark data model

@Entity(tableName = "bookmarks")
public class Bookmark {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String url;
    public String title;
    public long timestamp;
}
```

---

## Security Considerations

### Password Storage
- **Method:** EncryptedSharedPreferences (AES-256)
- **Key Storage:** Android Keystore (hardware-backed preferred)
- **Format:** JSON array with encrypted blobs
- **Threat Model:** Protects against app data extraction

### Network Security
- **Cleartext:** Disabled by default (network_security_config.xml)
- **Certificate Pinning:** Prevents MITM attacks
- **DNS Privacy:** DoH support for encrypted DNS
- **Ad Blocking:** Domain-based filtering

### Authentication
- **Biometric Support:** Fingerprint/face for password manager
- **App Permissions:** Minimal required permissions
- **Data Isolation:** Per-domain password storage

---

## Event Flow Examples

### Example 1: Loading google.com with AdBlocking
```
1. User types "google.com" in toolbar
2. MainBrowserActivity.setupWebView() → AdvancedWebView loads URL
3. EnhancedWebViewClient.shouldOverrideUrlLoading()
   - Check: AdBlocker.isAd("google.com") → false (allowed)
   - Check: HTTPSEverywhere("google.com") → "https://google.com"
   - Check: CertificatePinner.verify() → OK
   - Check: CustomDNSManager.resolve() → IP address
4. Page loads, resources requested:
   - HTML: ✅ google.com
   - JS: ✅ google.com
   - Ad request: ❌ doubleclick.net (blocked by AdBlocker)
5. EnhancedWebChromeClient.onPageFinished()
   - ScriptManager injects user scripts
   - PasswordManager auto-fills (if saved)
```

### Example 2: Reading Mode
```
1. User opens article, clicks "Reading Mode"
2. ReadingModeActivity.onCreate()
3. Request JavaScript execution: readability.js
   - Analyzes DOM structure
   - Extracts main article content
   - Removes ads, sidebars, footers
   - Returns JSON: {"content":"<article>..."}
4. ReadingModeActivity renders HTML in WebView
5. TextReflowEngine applies CSS optimizations
6. User reads distraction-free content
```

### Example 3: Password AutoFill
```
1. User navigates to github.com
2. Page loads with login form
3. EnhancedWebViewClient recognizes github.com domain
4. PasswordManager.autoFill(webView, "github.com")
5. buildAutoFillScript generates:
   - Find <input type="email">
   - Find <input type="password">
   - Populate with saved credentials
   - Optional auto-submit form
6. webView.evaluateJavascript(script)
7. User sees credentials filled, ready to submit
```

---

## Dependency Injection Pattern

The app uses manual dependency injection via manager classes:

```java
// MainBrowserActivity initializes all managers
private void initializeManagers() {
    dnsManager = new CustomDNSManager(this);
    vpnManager = new VPNManager(this);
    scriptManager = new ScriptManager(this);
    passwordManager = new PasswordManager(this);
    tabManager = new TabManager(this, R.id.tab_container);
}

// Pass managers to components
EnhancedWebViewClient client = new EnhancedWebViewClient(
    scriptManager,
    passwordManager
);
webView.setWebViewClient(client);
```

### Recommended Future Pattern (Dagger 2/Hilt)
```java
// Would enable constructor injection and better testability
@Inject PasswordManager passwordManager;
@Inject ScriptManager scriptManager;
```

---

## Testing Strategy

### Unit Tests (MockWebView not needed)
```java
// Test AdBlocker
@Test
public void testAdBlockerDetectsGoogleAnalytics() {
    AdBlocker blocker = new AdBlocker();
    assertTrue(blocker.isAd("google-analytics.com/ga.js"));
}

// Test PasswordEntry JSON parsing
@Test
public void testPasswordManagerParsing() {
    PasswordManager pm = new PasswordManager(context);
    pm.savePassword("github.com", "user", "pass123", "");
    List<PasswordEntry> entries = pm.getPasswordsForDomain("github.com");
    assertEquals(1, entries.size());
}
```

### Integration Tests
```java
// Test full activity flow
@RunWith(AndroidJUnit4.class)
public class MainBrowserActivityTest {
    @Rule
    public ActivityScenarioRule<MainBrowserActivity> activityRule =
        new ActivityScenarioRule<>(MainBrowserActivity.class);
}
```

---

## Configuration Management

### Keys in PreferencesManager
```
• custom_dns_enabled (boolean)
• custom_dns_server (string)
• home_page_url (string)
• ad_blocking_enabled (boolean)
• https_everywhere (boolean)
• script_execution_enabled (boolean)
```

### BuildConfig (Auto-generated)
```
• DEBUG = true/false
• APPLICATION_ID = "com.viaadvancedbrowser"
• VERSION_CODE = 1
• VERSION_NAME = "1.0.0"
```

---

## Summary

The Que-Browser codebase follows a modular architecture with clear separation of concerns:
- **UI Layer:** Activities for user interaction
- **Browser Engine:** Custom WebView with request interception
- **Security Layer:** Ad-blocking, HTTPS enforcement, certificate pinning
- **Content Injection:** Scripts, password auto-fill, reading mode
- **Peripheral Services:** VPN, bookmarks, encryption

Each module can be tested independently and components communicate via well-defined interfaces.

