package com.viaadvancedbrowser.network;

import android.content.Context;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AdBlocker {
    
    private Set<String> adDomains;
    
    public AdBlocker() {
        loadAdDomains();
    }
    
    public AdBlocker(Context context) {
        loadAdDomains();
    }
    
    private void loadAdDomains() {
        adDomains = new HashSet<>(Arrays.asList(
            "doubleclick.net",
            "googleadservices.com",
            "googlesyndication.com",
            "google-analytics.com",
            "facebook.com/tr",
            "adsystem.com",
            "adnxs.com",
            "amazon-adsystem.com",
            "scorecardresearch.com",
            "2mdn.net"
        ));
    }
    
    public boolean isAd(String url) {
        if (url == null) return false;
        
        for (String domain : adDomains) {
            if (url.contains(domain)) {
                return true;
            }
        }
        return false;
    }
    
    public void addAdDomain(String domain) {
        adDomains.add(domain);
    }
    
    public void removeAdDomain(String domain) {
        adDomains.remove(domain);
    }
}