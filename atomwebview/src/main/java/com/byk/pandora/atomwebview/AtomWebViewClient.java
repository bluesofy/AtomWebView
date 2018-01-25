package com.byk.pandora.atomwebview;

import android.support.annotation.CallSuper;

import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * Created by Byk on 2018/1/24.
 *
 * @author Byk
 */
public class AtomWebViewClient extends WebViewClient {

    private AtomWebView mWebView;

    public AtomWebViewClient(AtomWebView webView) {
        mWebView = webView;
    }

    public IErrorView errorView() {
        return null;
    }

    @CallSuper
    @Override
    public void onPageFinished(WebView webView, String url) {
        WebSettings webSetting = webView.getSettings();
        if (!webSetting.getLoadsImagesAutomatically()) {
            webSetting.setLoadsImagesAutomatically(true);
        }
        super.onPageFinished(webView, url);
    }

    @CallSuper
    @Override
    public void onReceivedError(WebView webView, int errorCode, String desc, String errorUrl) {
        super.onReceivedError(webView, errorCode, desc, errorUrl);
        if (errorView() != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            errorView().onError(webView, errorCode, desc, errorUrl);
        }
    }

    @CallSuper
    @Override
    public void onScaleChanged(WebView webView, float oldScale, float newScale) {
        mWebView.setCurScale(newScale);
        super.onScaleChanged(webView, oldScale, newScale);
    }

    public interface IErrorView {

        void onError(WebView webView, int errorCode, String desc, String errorUrl);
    }
}
