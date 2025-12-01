package com.viaadvancedbrowser.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import com.viaadvancedbrowser.R;
import com.viaadvancedbrowser.password.PasswordManager;
import java.util.concurrent.Executor;

public class PasswordManagerActivity extends AppCompatActivity {

    private PasswordManager passwordManager;
    private Executor executor;
    private BiometricPrompt biometricPrompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_manager);

        passwordManager = new PasswordManager(this);
        executor = ContextCompat.getMainExecutor(this);

        BiometricManager biometricManager = BiometricManager.from(this);
        int can = biometricManager.canAuthenticate();
        if (can == BiometricManager.BIOMETRIC_SUCCESS) {
            biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    // Show passwords after success (for demo we just toast)
                    Toast.makeText(PasswordManagerActivity.this, "Biometric unlock successful", Toast.LENGTH_SHORT).show();
                    // TODO: populate UI with decrypted passwords
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    Toast.makeText(PasswordManagerActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                }
            });
            BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Unlock Password Vault")
                    .setSubtitle("Authenticate to view saved passwords")
                    .setNegativeButtonText("Use device PIN")
                    .build();
            biometricPrompt.authenticate(promptInfo);
        } else {
            Toast.makeText(this, "Biometric unavailable, showing passwords is disabled", Toast.LENGTH_LONG).show();
        }
    }
}
