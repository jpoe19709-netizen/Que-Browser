package com.viaadvancedbrowser.network;

import okhttp3.Dns;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.dnsoverhttps.DnsOverHttps;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class DNSOverHTTPS implements Dns {
    
    private DnsOverHttps dnsOverHttps;
    
    public DNSOverHTTPS() {
        OkHttpClient bootstrapClient = new OkHttpClient.Builder().build();
        
        dnsOverHttps = new DnsOverHttps.Builder()
            .client(bootstrapClient)
            .url(HttpUrl.get("https://cloudflare-dns.com/dns-query"))
            .build();
    }
    
    @Override
    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
        return dnsOverHttps.lookup(hostname);
    }
}