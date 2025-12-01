package com.viaadvancedbrowser.vpn;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class BrowserVpnService extends VpnService {
    
    private ParcelFileDescriptor vpnInterface;
    private boolean isRunning = false;
    private VPNConfig currentConfig;
    
    private static final String TAG = "BrowserVpnService";
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && "connect".equals(intent.getAction())) {
            currentConfig = intent.getParcelableExtra("vpn_config");
            startVPN();
        } else if (intent != null && "disconnect".equals(intent.getAction())) {
            stopVPN();
        }
        return START_STICKY;
    }
    
    private void startVPN() {
        try {
            Builder builder = new Builder();
            builder.setSession("ViaAdvancedVPN")
                   .addAddress("10.0.0.2", 32)
                   .addDnsServer("8.8.8.8")
                   .addRoute("0.0.0.0", 0)
                   .setMtu(1500);
            
            // Configure based on current config
            if (currentConfig != null) {
                for (String dns : currentConfig.getDnsServers()) {
                    builder.addDnsServer(dns);
                }
            }
            
            vpnInterface = builder.establish();
            isRunning = true;
            
            // Send broadcast that VPN is connected
            Intent connectedIntent = new Intent("VPN_STATUS_CHANGED");
            connectedIntent.putExtra("status", "connected");
            LocalBroadcastManager.getInstance(this).sendBroadcast(connectedIntent);
            
        } catch (Exception e) {
            stopVPN();
        }
    }
    
    private void stopVPN() {
        isRunning = false;
        
        try {
            if (vpnInterface != null) {
                vpnInterface.close();
                vpnInterface = null;
            }
        } catch (Exception e) {
            // Ignore
        }
        
        // Send broadcast that VPN is disconnected
        Intent disconnectedIntent = new Intent("VPN_STATUS_CHANGED");
        disconnectedIntent.putExtra("status", "disconnected");
        LocalBroadcastManager.getInstance(this).sendBroadcast(disconnectedIntent);
        
        stopSelf();
    }
    
    @Override
    public void onDestroy() {
        stopVPN();
        super.onDestroy();
    }
    
    public boolean isRunning() {
        return isRunning;
    }
}