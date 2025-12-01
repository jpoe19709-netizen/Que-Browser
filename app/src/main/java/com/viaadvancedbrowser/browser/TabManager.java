package com.viaadvancedbrowser.browser;

import android.content.Context;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.webkit.WebView;

public class TabManager {
    
    private Context context;
    private ViewGroup tabContainer;
    private SparseArray<AdvancedWebView> tabs;
    private int currentTabId;
    private int nextTabId;
    
    public TabManager(Context context, int containerId) {
        this.context = context;
        this.tabContainer = ((android.app.Activity) context).findViewById(containerId);
        this.tabs = new SparseArray<>();
        this.nextTabId = 1;
    }
    
    public int createNewTab(String url) {
        AdvancedWebView webView = new AdvancedWebView(context);
        setupWebView(webView);
        
        int tabId = nextTabId++;
        tabs.put(tabId, webView);
        
        tabContainer.removeAllViews();
        tabContainer.addView(webView);
        
        currentTabId = tabId;
        
        if (url != null && !url.isEmpty()) {
            webView.loadUrl(url);
        } else {
            webView.loadUrl("https://www.google.com");
        }
        
        return tabId;
    }
    
    public void closeTab(int tabId) {
        AdvancedWebView webView = tabs.get(tabId);
        if (webView != null) {
            webView.destroy();
            tabs.remove(tabId);
        }
        
        if (tabs.size() == 0) {
            createNewTab(null);
        } else {
            switchToTab(tabs.keyAt(0));
        }
    }
    
    public void switchToTab(int tabId) {
        AdvancedWebView webView = tabs.get(tabId);
        if (webView != null) {
            tabContainer.removeAllViews();
            tabContainer.addView(webView);
            currentTabId = tabId;
        }
    }
    
    public AdvancedWebView getCurrentWebView() {
        return tabs.get(currentTabId);
    }
    
    public int getCurrentTabId() {
        return currentTabId;
    }
    
    public SparseArray<AdvancedWebView> getAllTabs() {
        return tabs.clone();
    }
    
    private void setupWebView(AdvancedWebView webView) {
        WebView.setWebContentsDebuggingEnabled(true);
        
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
    }
}