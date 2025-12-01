package com.viaadvancedbrowser.settings;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import java.util.*;

public class PreferencesManager {
    
    // Theme constants
    public static final int THEME_LIGHT = 0;
    public static final int THEME_DARK = 1;
    public static final int THEME_SYSTEM = 2;
    
    // Preference keys
    private static final String KEY_THEME = "theme";
    private static final String KEY_HOME_PAGE = "home_page";
    private static final String KEY_CUSTOM_DNS_ENABLED = "custom_dns_enabled";
    private static final String KEY_SELECTED_DNS = "selected_dns";
    private static final String KEY_DNS_OVER_HTTPS = "dns_over_https";
    private static final String KEY_AD_BLOCK_ENABLED = "ad_block_enabled";
    
    private static SharedPreferences prefs;
    
    public static void initialize(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }
    
    // Theme preferences
    public static int getTheme(Context context) {
        return prefs.getInt(KEY_THEME, THEME_SYSTEM);
    }
    
    public static void setTheme(Context context, int theme) {
        prefs.edit().putInt(KEY_THEME, theme).apply();
    }
    
    // Home page
    public static String getHomePage(Context context) {
        return prefs.getString(KEY_HOME_PAGE, "https://www.google.com");
    }
    
    public static void setHomePage(Context context, String homePage) {
        prefs.edit().putString(KEY_HOME_PAGE, homePage).apply();
    }
    
    // DNS preferences
    public static boolean isCustomDNSEnabled(Context context) {
        return prefs.getBoolean(KEY_CUSTOM_DNS_ENABLED, false);
    }
    
    public static String getSelectedDNS(Context context) {
        return prefs.getString(KEY_SELECTED_DNS, "system");
    }
    
    public static void setSelectedDNS(Context context, String dns) {
        prefs.edit().putString(KEY_SELECTED_DNS, dns).apply();
    }
    
    public static boolean isDNSOverHTTPSEnabled(Context context) {
        return prefs.getBoolean(KEY_DNS_OVER_HTTPS, false);
    }
    
    public static void setDNSOverHTTPS(Context context, boolean enabled) {
        prefs.edit().putBoolean(KEY_DNS_OVER_HTTPS, enabled).apply();
    }
    
    // Ad blocking
    public static boolean isAdBlockEnabled(Context context) {
        return prefs.getBoolean(KEY_AD_BLOCK_ENABLED, true);
    }
    
    public static void setAdBlockEnabled(Context context, boolean enabled) {
        prefs.edit().putBoolean(KEY_AD_BLOCK_ENABLED, enabled).apply();
    }
    
    // Custom DNS servers
    public static List<String> getCustomDNSServers(Context context) {
        // Implementation for custom DNS servers
        return Arrays.asList("1.1.1.1", "1.0.0.1");
    }
}