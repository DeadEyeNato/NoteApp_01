package com.example.NoteApp_01;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import java.lang.reflect.Field;

import jp.wasabeef.richeditor.RichEditor;

public class CustomRichEditor extends RichEditor {

    private boolean isEditorLoaded = false;

    public CustomRichEditor(Context context) {
        super(context);
        init();
    }

    public CustomRichEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOnInitialLoadListener(isReady -> {
            isEditorLoaded = isReady;
            // Perform actions after the editor is ready
        });
    }

    @Override
    public void setHtml(String contents) {
        if (isEditorLoaded) {
            super.setHtml(contents);
        } else {
            setOnInitialLoadListener(isReady -> {
                if (isReady) {
                    super.setHtml(contents);
                }
            });
        }
    }

    public void syncCheckedStateAndGetHtml(ValueCallback<String> callback) {
        String js = "(function() {" +
                "var inputs = document.querySelectorAll('input[type=checkbox]');" +
                "inputs.forEach(function(input) {" +
                "if (input.checked) {" +
                "input.setAttribute('checked', 'checked');" +
                "} else {" +
                "input.removeAttribute('checked');" +
                "}" +
                "});" +
                "return RE.getHtml();" +
                "})();";
        executeJavaScript(js, callback);
    }

    private void executeJavaScript(String script, ValueCallback<String> callback) {
        try {
            Field field = RichEditor.class.getDeclaredField("mWebView");
            field.setAccessible(true);
            WebView webView = (WebView) field.get(this);
            webView.evaluateJavascript(script, callback);
        } catch (Exception e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onReceiveValue(null);
            }
        }
    }
}
