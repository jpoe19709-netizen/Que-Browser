package com.viaadvancedbrowser.browser;

import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import androidx.appcompat.app.AppCompatActivity;

public class EnhancedWebChromeClient extends WebChromeClient {
    
    private AppCompatActivity activity;
    
    public EnhancedWebChromeClient(AppCompatActivity activity) {
        this.activity = activity;
    }
    
    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        // Handle JavaScript alerts
        new android.app.AlertDialog.Builder(activity)
            .setTitle("Alert")
            .setMessage(message)
            .setPositiveButton("OK", (dialog, which) -> result.confirm())
            .setCancelable(false)
            .create()
            .show();
        return true;
    }
    
    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        new android.app.AlertDialog.Builder(activity)
            .setTitle("Confirm")
            .setMessage(message)
            .setPositiveButton("OK", (dialog, which) -> result.confirm())
            .setNegativeButton("Cancel", (dialog, which) -> result.cancel())
            .setCancelable(false)
            .create()
            .show();
        return true;
    }
    
    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        // Handle JavaScript prompts
        final android.widget.EditInput input = new android.widget.EditText(activity);
        input.setText(defaultValue);
        
        new android.app.AlertDialog.Builder(activity)
            .setTitle("Prompt")
            .setMessage(message)
            .setView(input)
            .setPositiveButton("OK", (dialog, which) -> result.confirm(input.getText().toString()))
            .setNegativeButton("Cancel", (dialog, which) -> result.cancel())
            .setCancelable(false)
            .create()
            .show();
        return true;
    }
    
    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        // Update progress bar if needed
        activity.setProgress(newProgress * 100);
    }
    
    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        if (activity != null && title != null) {
            activity.setTitle(title);
        }
    }
}