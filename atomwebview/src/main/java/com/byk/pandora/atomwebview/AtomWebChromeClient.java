package com.byk.pandora.atomwebview;

import android.net.Uri;
import android.support.annotation.CallSuper;
import android.text.TextUtils;

import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

/**
 * Created by Byk on 2018/1/24.
 *
 * @author Byk
 */
public class AtomWebChromeClient extends WebChromeClient {

    private static final int MAX_PROGRESS = 100;

    private AtomWebView mWebView;

    public AtomWebChromeClient(AtomWebView webView) {
        mWebView = webView;
    }

    public IProgress progress() {
        return null;
    }

    @CallSuper
    @Override
    public void onProgressChanged(WebView webView, int progress) {
        if (progress >= MAX_PROGRESS) {
            setProgressVisible(false);
        } else {
            setProgressValue(progress);
            setProgressVisible(true);
        }
        super.onProgressChanged(webView, progress);
    }

    @CallSuper
    @Override
    public boolean onJsPrompt(WebView view, String url, String msg, String defaultValue, JsPromptResult jsResult) {
        Uri uri = Uri.parse(msg);
        String scheme = uri.getScheme();
        if (!TextUtils.isEmpty(scheme)) {
            String confirm = "";
            if (!AtomUtil.apply(mWebView.getContext(), uri, msg, scheme)) {
                AtomResult result = new AtomResult();
                result.setScheme(scheme);
                result.setContent(msg);
                result.setAction(uri.getAuthority());
                result.setData(uri.getQuery());
                result.setUri(uri);
                confirm = mWebView.applyJsBridge(result);
            }

            jsResult.confirm(confirm);
            return true;
        }
        return super.onJsPrompt(view, url, msg, defaultValue, jsResult);
    }

    private void setProgressValue(int value) {
        if (progress() != null) {
            progress().onProgress(value);
        }
    }

    private void setProgressVisible(boolean visible) {
        if (progress() != null) {
            progress().onVisible(visible);
        }
    }

    public interface IProgress {

        void onProgress(int progress);

        void onVisible(boolean visible);
    }
}
