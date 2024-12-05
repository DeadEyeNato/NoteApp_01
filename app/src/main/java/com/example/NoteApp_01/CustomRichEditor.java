package com.example.NoteApp_01;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import java.lang.reflect.Field;

import jp.wasabeef.richeditor.RichEditor;

public class CustomRichEditor extends RichEditor {

    private boolean isEditorLoaded = false;
    private String pendingHtmlContent = null;

    public CustomRichEditor(Context context) {
        super(context);
        init();
    }

    public CustomRichEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        super.setOnInitialLoadListener(isReady -> {
            isEditorLoaded = isReady;
            if (isEditorLoaded && pendingHtmlContent != null) {
                super.setHtml(pendingHtmlContent);
                pendingHtmlContent = null;
            }
        });
    }

    @Override
    public void setHtml(String contents) {
        if (isEditorLoaded) {
            super.setHtml(contents);
        } else {
            pendingHtmlContent = contents;
        }
    }

    public void getHtml(ValueCallback<String> callback) {
        executeJavaScript("RE.getHtml();", callback);
    }

    private void executeJavaScript(String script, ValueCallback<String> callback) {
        try {
            Field field = RichEditor.class.getDeclaredField("mWebView");
            field.setAccessible(true);
            WebView webView = (WebView) field.get(this);
            webView.evaluateJavascript(script, value -> {
                if (callback != null) {
                    // Remove any surrounding quotes from the result
                    if (value != null && value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
                        value = value.substring(1, value.length() - 1).replace("\\n", "\n").replace("\\\"", "\"");
                    }
                    callback.onReceiveValue(value);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onReceiveValue("");
            }
        }
    }
}
