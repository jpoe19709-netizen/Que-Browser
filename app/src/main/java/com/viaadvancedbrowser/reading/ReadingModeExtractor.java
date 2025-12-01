package com.viaadvancedbrowser.reading;

import android.content.Context;
import android.content.Intent;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.util.Log;

/**
 * Improved ReadingModeExtractor:
 * - Dynamically loads Mozilla Readability from CDN at runtime (when the page loads)
 * - Creates a clone of the document and runs Readability on the clone to avoid modifying original page
 * - Returns the parsed article JSON and starts ReadingModeActivity
 */
public class ReadingModeExtractor {

    private static final String TAG = "ReadingModeExtractor";
    private Context context;

    public ReadingModeExtractor(Context context) {
        this.context = context;
    }

    public void extractReadableContent(final WebView webView) {
        // Inject a small helper script that loads Readability from CDN and runs it
        String loader = "(function(){\n" +
                "  if(window.__via_readability_loaded) {\n" +
                "    try { var clone = document.cloneNode(true); var article = new Readability(clone).parse(); return JSON.stringify(article); } catch(e){ return JSON.stringify({error:e.message}); }\n" +
                "  }\n" +
                "  var s = document.createElement('script');\n" +
                "  s.src = 'https://cdn.jsdelivr.net/npm/@mozilla/readability@0.6.0/Readability.js';\n" +
                "  s.onload = function(){ window.__via_readability_loaded = true; try{ var clone = document.cloneNode(true); var article = new Readability(clone).parse(); window.__via_via_article = article; }catch(e){ window.__via_via_article = {error:e.message}; } };\n" +
                "  s.onerror = function(e){ window.__via_via_article = {error:'failed to load readability'}; };\n" +
                "  document.documentElement.appendChild(s);\n" +
                "  // wait briefly for script to load and then return the article if available\n" +
                "  return (function waitForArticle(){\n" +
                "     if(window.__via_via_article) return JSON.stringify(window.__via_via_article);\n" +
                "     return null;\n" +
                "  })();\n" +
                "})();";

        // First try to run loader; result may be null (loading), so we schedule a delayed second run via JS setTimeout
        webView.evaluateJavascript(loader, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                if (value != null && !value.equals("null")) {
                    dispatchResult(value);
                } else {
                    // schedule second pass after 700ms to allow CDN load
                    String check = "(function(){ return window.__via_via_article ? JSON.stringify(window.__via_via_article) : null; })();";
                    webView.evaluateJavascript("setTimeout(function(){" + check + "},700);", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value2) {
                            if (value2 != null && !value2.equals("null")) {
                                dispatchResult(value2);
                            } else {
                                // final attempt: return any available or error
                                webView.evaluateJavascript(check, new ValueCallback<String>() {
                                    @Override
                                    public void onReceiveValue(String v3) {
                                        dispatchResult(v3);
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    private void dispatchResult(String json) {
        if (json == null || json.equals("null")) {
            Log.w(TAG, "No readability result available"); return;
        }
        Intent intent = new Intent(context, com.viaadvancedbrowser.activities.ReadingModeActivity.class);
        intent.putExtra("reading_json", json);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
