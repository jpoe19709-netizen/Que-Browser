package com.viaadvancedbrowser.password;

import android.content.Context;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import com.viaadvancedbrowser.security.EncryptionManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

/**
 * Production-ready PasswordManager (simplified):
 * - Stores password entries encrypted using EncryptedSharedPreferences (EncryptionManager)
 * - Allows saving, listing, and autofill via injected JS
 *
 * Note: For a production app you should use a proper Room DB with encrypted fields and stronger UX flows.
 */
public class PasswordManager {
    private static final String PREF_KEY_PASSWORDS = "saved_passwords_v1";
    private EncryptionManager encryption;
    private Context context;

    public PasswordManager(Context context) {
        this.context = context;
        try {
            this.encryption = new EncryptionManager(context);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Failed to init encryption", e);
        }
    }

    public void savePassword(String domain, String username, String password, String formData) {
        List<PasswordEntry> entries = getPasswordsForDomain(domain);
        PasswordEntry entry = new PasswordEntry();
        entry.setDomain(domain);
        entry.setUsername(username);
        entry.setPassword(password);
        entry.setFormData(formData);
        entry.setTimestamp(System.currentTimeMillis());
        entries.add(0, entry); // most recent first
        persistAll(entries);
    }

    private void persistAll(List<PasswordEntry> entries) {
        JSONArray arr = new JSONArray();
        try {
            for (PasswordEntry e : entries) {
                JSONObject o = new JSONObject();
                o.put("domain", e.getDomain());
                o.put("username", e.getUsername());
                o.put("password", e.getPassword());
                o.put("formData", e.getFormData());
                o.put("timestamp", e.getTimestamp());
                arr.put(o);
            }
            encryption.putString(PREF_KEY_PASSWORDS, arr.toString());
        } catch (JSONException ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<PasswordEntry> getPasswordsForDomain(String domain) {
        List<PasswordEntry> results = new ArrayList<>();
        String raw = encryption.getString(PREF_KEY_PASSWORDS, "[]");
        try {
            JSONArray arr = new JSONArray(raw);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                if (o.optString("domain").equalsIgnoreCase(domain)) {
                    PasswordEntry e = new PasswordEntry();
                    e.setDomain(o.optString("domain"));
                    e.setUsername(o.optString("username"));
                    e.setPassword(o.optString("password"));
                    e.setFormData(o.optString("formData"));
                    e.setTimestamp(o.optLong("timestamp"));
                    results.add(e);
                }
            }
        } catch (JSONException ex) {
            // ignore parse errors, return empty
        }
        return results;
    }

    /**
     * Auto-fill the first matching entry into the webview.
     * This implementation injects a safer JS snippet that attempts to find username/email and password fields.
     */
    public void autoFill(WebView webView, String domain) {
        List<PasswordEntry> entries = getPasswordsForDomain(domain);
        if (entries.isEmpty()) return;

        PasswordEntry entry = entries.get(0);
        String js = buildAutoFillScript(entry.getUsername(), entry.getPassword());
        webView.evaluateJavascript(js, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                // optional: handle callback value
            }
        });
    }

    private String buildAutoFillScript(String username, String password) {
        // Escape single quotes in username/password
        username = username.replace("'","\\'").replace("\"","\\\"");
        password = password.replace("'","\\'").replace("\"","\\\"\");

        String script = "(function(){\n" +
                "function findField(types){\n" +
                "  var inputs = document.getElementsByTagName('input');\n" +
                "  for(var i=0;i<inputs.length;i++){ var t = inputs[i].type||''; var name = inputs[i].name||''; var id = inputs[i].id||''; var placeholder = inputs[i].placeholder||''; var attr = (name+id+placeholder).toLowerCase(); for(var j=0;j<types.length;j++){ if(attr.indexOf(types[j])!==-1 || t===types[j]) return inputs[i]; } }\n" +
                "  return null;\n" +
                "}\n" +
                // Find username field
                "var user = findField(['email','username','user','login','account','text']);\n" +
                "var pass = findField(['password']);\n" +
                "if(user){ user.focus(); user.value='" + username + "'; }\n" +
                "if(pass){ pass.focus(); pass.value='" + password + "'; }\n" +
                "// Try to submit form if both fields present\n" +
                "if(user && pass){ var f = user.form || (pass.form? pass.form : null); if(f){ try{ f.submit(); }catch(e){} } }\n" +
                "return true;\n" +
                "})();";
        return script;
    }
}
