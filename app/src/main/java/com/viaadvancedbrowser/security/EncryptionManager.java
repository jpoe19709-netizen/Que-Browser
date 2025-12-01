package com.viaadvancedbrowser.security;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class EncryptionManager {
    private static final String PREFS_FILE = "via_secure_prefs";
    private SharedPreferences securePrefs;

    public EncryptionManager(Context context) throws GeneralSecurityException, IOException {
        MasterKey masterKey = new MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build();

        securePrefs = EncryptedSharedPreferences.create(
            context,
            PREFS_FILE,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }

    public void putString(String key, String value) {
        securePrefs.edit().putString(key, value).apply();
    }

    public String getString(String key, String defaultValue) {
        return securePrefs.getString(key, defaultValue);
    }

    public void remove(String key) {
        securePrefs.edit().remove(key).apply();
    }
}
