package com.viaadvancedbrowser.network;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class HTTPSEverywhere {
    
    private Set<String> httpsDomains;
    
    public HTTPSEverywhere() {
        loadHTTPSDomains();
    }
    
    private void loadHTTPSDomains() {
        httpsDomains = new HashSet<>(Arrays.asList(
            "google.com",
            "facebook.com",
            "youtube.com",
            "wikipedia.org",
            "reddit.com",
            "amazon.com",
            "twitter.com",
            "instagram.com",
            "linkedin.com",
            "github.com"
        ));
    }
    
    public String upgradeToHTTPS(String url) {
        if (!shouldUpgrade(url)) {
            return url;
        }
        
        if (url.startsWith("http://")) {
            return url.replace("http://", "https://");
        }
        
        return url;
    }
    
    private boolean shouldUpgrade(String url) {
        try {
            String domain = new URL(url).getHost();
            for (String httpsDomain : httpsDomains) {
                if (domain.endsWith(httpsDomain)) {
                    return true;
                }
            }
        } catch (MalformedURLException e) {
            return false;
        }
        
        return false;
    }
    
    public void addDomain(String domain) {
        httpsDomains.add(domain);
    }
}