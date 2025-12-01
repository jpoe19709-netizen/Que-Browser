package com.viaadvancedbrowser.reflow;

import android.webkit.WebView;

public class TextReflowEngine {
    
    private WebView webView;
    private float currentZoomLevel = 1.0f;
    private boolean isReflowEnabled = true;
    
    public TextReflowEngine(WebView webView) {
        this.webView = webView;
        setupReflow();
    }
    
    private void setupReflow() {
        injectReflowScript();
    }
    
    private void injectReflowScript() {
        String reflowScript = 
            "javascript:(function() {" +
            "var meta = document.createElement('meta');" +
            "meta.name = 'viewport';" +
            "meta.content = 'width=device-width, initial-scale=1.0, maximum-scale=5.0, user-scalable=yes';" +
            "document.getElementsByTagName('head')[0].appendChild(meta);" +
            "" +
            "function reflowText() {" +
            "  var elements = document.querySelectorAll('p, span, div, article, section');" +
            "  for (var i = 0; i < elements.length; i++) {" +
            "    var el = elements[i];" +
            "    el.style.wordWrap = 'break-word';" +
            "    el.style.overflowWrap = 'break-word';" +
            "    el.style.maxWidth = '100%';" +
            "  }" +
            "}" +
            "reflowText();" +
            "window.addEventListener('resize', reflowText);" +
            "window.addEventListener('orientationchange', reflowText);" +
            "})();";
        
        webView.evaluateJavascript(reflowScript, null);
    }
    
    public void setZoomLevel(float zoomLevel) {
        this.currentZoomLevel = zoomLevel;
        webView.getSettings().setTextZoom((int)(zoomLevel * 100));
        
        if (isReflowEnabled) {
            adjustLayoutForZoom(zoomLevel);
        }
    }
    
    private void adjustLayoutForZoom(float zoomLevel) {
        String adjustScript = String.format(
            "javascript:(function() {" +
            "document.body.style.width = '%f%%';" +
            "var images = document.getElementsByTagName('img');" +
            "for (var i = 0; i < images.length; i++) {" +
            "  images[i].style.maxWidth = '100%%';" +
            "  images[i].style.height = 'auto';" +
            "}" +
            "})();",
            100 / zoomLevel
        );
        
        webView.evaluateJavascript(adjustScript, null);
    }
    
    public void enableReflow(boolean enable) {
        this.isReflowEnabled = enable;
    }
}