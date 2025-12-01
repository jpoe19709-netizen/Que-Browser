package com.viaadvancedbrowser.activities;

import android.os.Bundle;
import android.webkit.WebView;
import androidx.appcompat.app.AppCompatActivity;

public class ReadingModeActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle s){
        super.onCreate(s);
        WebView w = new WebView(this);
        setContentView(w);
        String json = getIntent().getStringExtra("reading_json");
        // Expecting JSON with content field containing HTML
        if (json != null) {
            // Simple display: load the content as HTML (in production sanitize/clean before use)
            // We assume json contains {"content":"<article>...</article>"}
            // To keep things simple, load the content as a blob:
            // Note: value may be quoted JSON string from evaluateJavascript, so strip outer quotes if present.
            if (json.length()>1 && json.charAt(0)=='\"' && json.charAt(json.length()-1)=='\"') {
                json = json.substring(1, json.length()-1).replace("\\\"","\"");
            }
            // crude parse to find content field
            int idx = json.indexOf("\"content\":");
            String html = "";
            if (idx!=-1) {
                int start = json.indexOf('"', idx+10);
                int end = json.lastIndexOf('"');
                html = json.substring(idx+11, json.length()-2);
                html = html.replaceAll("\\\\n","\n").replaceAll("\\\\\"","\""); // unescape
            } else {
                html = "<html><body><h3>No readable content</h3></body></html>";
            }
            w.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
        }
    }
}
