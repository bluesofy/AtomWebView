package com.byk.pandora.atomwebview.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.byk.pandora.atomwebview.AtomResult;
import com.byk.pandora.atomwebview.AtomWebView;
import com.tencent.smtt.sdk.ValueCallback;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final AtomWebView webView = findViewById(R.id.webview);
        webView.setJsBridge(new AtomWebView.IJsBridge() {
            @Override
            public String onReceive(AtomResult result) {
                return applyResult(result);
            }
        });

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.invokeJs("javascript:callJs()", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        tips(s);
                    }
                });
            }
        });

        webView.loadUrl("file:///android_asset/js.html");
    }

    private String applyResult(AtomResult result) {
        tips(result.getScheme() + "." + result.getAction());
        return null;
    }

    private void tips(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT)
             .show();
    }
}
