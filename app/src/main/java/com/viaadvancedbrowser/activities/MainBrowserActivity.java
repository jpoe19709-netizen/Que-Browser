package com.viaadvancedbrowser.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import androidx.appcompat.app.AppCompatActivity;

import com.viaadvancedbrowser.R;
import com.viaadvancedbrowser.browser.AdvancedWebView;
import com.viaadvancedbrowser.browser.EnhancedWebChromeClient;
import com.viaadvancedbrowser.browser.EnhancedWebViewClient;
import com.viaadvancedbrowser.browser.TabManager;
import com.viaadvancedbrowser.network.CustomDNSManager;
import com.viaadvancedbrowser.password.PasswordManager;
import com.viaadvancedbrowser.script.ScriptManager;
import com.viaadvancedbrowser.settings.PreferencesManager;
import com.viaadvancedbrowser.vpn.VPNManager;

public class MainBrowserActivity extends AppCompatActivity {
    
    private AdvancedWebView webView;
    private TabManager tabManager;
    private CustomDNSManager dnsManager;
    private VPNManager vpnManager;
    private ScriptManager scriptManager;
    private PasswordManager passwordManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initializeManagers();
        setupWebView();
        handleIntent(getIntent());
    }
    
    private void initializeManagers() {
        // Initialize DNS manager
        dnsManager = new CustomDNSManager(this);
        if (PreferencesManager.isCustomDNSEnabled(this)) {
            dnsManager.applyDNSSettings();
        }
        
        // Initialize VPN manager
        vpnManager = new VPNManager(this);
        
        // Initialize script manager
        scriptManager = new ScriptManager(this);
        
        // Initialize password manager
        passwordManager = new PasswordManager(this);
        
        // Initialize tab manager
        tabManager = new TabManager(this, R.id.tab_container);
    }
    
    private void setupWebView() {
        webView = findViewById(R.id.webview);
        
        EnhancedWebViewClient webViewClient = new EnhancedWebViewClient(
            scriptManager, 
            passwordManager
        );
        webView.setWebViewClient(webViewClient);
        
        webView.setWebChromeClient(new EnhancedWebChromeClient(this));
        
        // Apply privacy settings
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
    }
    
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            String url = intent.getDataString();
            if (url != null) {
                webView.loadUrl(url);
            }
        } else {
            // Load home page
            webView.loadUrl(PreferencesManager.getHomePage(this));
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.menu_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.menu_dns) {
            startActivity(new Intent(this, DNSSettingsActivity.class));
            return true;
        } else if (id == R.id.menu_vpn) {
            startActivity(new Intent(this, VPNConfigActivity.class));
            return true;
        } else if (id == R.id.menu_reading_mode) {
            startActivity(new Intent(this, ReadingModeActivity.class));
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) {
            webView.onResume();
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) {
            webView.onPause();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.destroy();
        }
    }
    
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}