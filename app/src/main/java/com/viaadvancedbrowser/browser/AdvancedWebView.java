package com.viaadvancedbrowser.browser;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

import com.viaadvancedbrowser.network.AdBlocker;
import com.viaadvancedbrowser.settings.PreferencesManager;

public class AdvancedWebView extends WebView {
    
    private AdBlocker adBlocker;
    private boolean isAdBlockEnabled;
    
    public AdvancedWebView(Context context) {
        super(context);
        init();
    }
    
    public AdvancedWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public AdvancedWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    
    private void init() {
        isAdBlockEnabled = PreferencesManager.isAdBlockEnabled(getContext());
        if (isAdBlockEnabled) {
            adBlocker = new AdBlocker(getContext());
        }
    }
    
    @Override
    public void loadUrl(String url) {
        if (isAdBlockEnabled && adBlocker.isAd(url)) {
            // Block ad URLs
            return;
        }
        super.loadUrl(url);
    }
    
    public void enableAdBlock(boolean enable) {
        isAdBlockEnabled = enable;
        if (enable && adBlocker == null) {
            adBlocker = new AdBlocker(getContext());
        }
    }
}