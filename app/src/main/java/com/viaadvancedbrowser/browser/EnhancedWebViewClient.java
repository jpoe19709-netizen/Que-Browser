package com.viaadvancedbrowser.browser;

import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.viaadvancedbrowser.network.AdBlocker;
import com.viaadvancedbrowser.network.HTTPSEverywhere;
import com.viaadvancedbrowser.password.PasswordManager;
import com.viaadvancedbrowser.script.ScriptManager;

public class EnhancedWebViewClient extends WebViewClient {
    
    private ScriptManager scriptManager;
    private PasswordManager passwordManager;
    private HTTPSEverywhere httpsEverywhere;
    private AdBlocker adBlocker;
    
    public EnhancedWebViewClient(ScriptManager scriptManager, PasswordManager passwordManager) {
        this.scriptManager = scriptManager;
        this.passwordManager = passwordManager;
        this.httpsEverywhere = new HTTPSEverywhere();
        this.adBlocker = new AdBlocker();
    }
    
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        String url = request.getUrl().toString();
        
        // Ad blocking
        if (adBlocker.isAd(url)) {
            return new WebResourceResponse("text/plain", "UTF-8", null);
        }
        
        // HTTPS upgrades
        String upgradedUrl = httpsEverywhere.upgradeToHTTPS(url);
        if (!upgradedUrl.equals(url)) {
            // Handle HTTPS upgrade
        }
        
        return super.shouldInterceptRequest(view, request);
    }
    
    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        
        // Inject user scripts
        scriptManager.injectScripts(view, url);
        
        // Auto-fill passwords if available
        passwordManager.autoFill(view, url);
    }
    
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        String url = request.getUrl().toString();
        
        // Apply HTTPS everywhere
        String secureUrl = httpsEverywhere.upgradeToHTTPS(url);
        if (!secureUrl.equals(url)) {
            view.loadUrl(secureUrl);
            return true;
        }
        
        return super.shouldOverrideUrlLoading(view, request);
    }
}