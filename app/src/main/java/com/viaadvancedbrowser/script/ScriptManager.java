package com.viaadvancedbrowser.script;

import android.content.Context;
import android.webkit.WebView;
import java.util.*;

public class ScriptManager {
    
    private Context context;
    private Map<String, UserScript> userScripts;
    
    public ScriptManager(Context context) {
        this.context = context;
        this.userScripts = new HashMap<>();
        loadDefaultScripts();
    }
    
    private void loadDefaultScripts() {
        // Add default scripts
        UserScript darkMode = new UserScript(
            "Dark Mode",
            "document.body.style.filter='invert(1) hue-rotate(180deg)';",
            new String[]{"*"},
            InjectionTime.DOCUMENT_END
        );
        userScripts.put("dark_mode", darkMode);
    }
    
    public void injectScripts(WebView webView, String url) {
        for (UserScript script : userScripts.values()) {
            if (script.isEnabled() && script.matchesUrl(url)) {
                injectScript(webView, script);
            }
        }
    }
    
    private void injectScript(WebView webView, UserScript script) {
        String injectionCode = String.format(
            "(function() { %s })();",
            script.getCode()
        );
        
        webView.evaluateJavascript(injectionCode, null);
    }
    
    public void addUserScript(String name, String code, String[] matches, 
                            InjectionTime injectionTime) {
        UserScript script = new UserScript(name, code, matches, injectionTime);
        userScripts.put(name, script);
    }
}

enum InjectionTime {
    DOCUMENT_START,
    DOCUMENT_END,
    DOCUMENT_IDLE
}

class UserScript {
    private String name;
    private String code;
    private String[] urlMatches;
    private InjectionTime injectionTime;
    private boolean enabled;
    
    public UserScript(String name, String code, String[] urlMatches, InjectionTime injectionTime) {
        this.name = name;
        this.code = code;
        this.urlMatches = urlMatches;
        this.injectionTime = injectionTime;
        this.enabled = true;
    }
    
    public boolean matchesUrl(String url) {
        for (String pattern : urlMatches) {
            if (pattern.equals("*") || url.contains(pattern)) {
                return true;
            }
        }
        return false;
    }
    
    // Getters and setters
    public String getName() { return name; }
    public String getCode() { return code; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}