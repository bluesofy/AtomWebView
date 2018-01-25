package com.byk.pandora.atomwebview;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

/**
 * Created by Byk on 2018/1/24.
 *
 * @author Byk
 */
public class AtomWebView extends WebView {

    public static final int HIT_TYPE_UNKNOWN = HitTestResult.UNKNOWN_TYPE;
    public static final int HIT_TYPE_IMAGE = HitTestResult.IMAGE_TYPE;
    public static final int HIT_TYPE_SRC_IMAGE_ANCHOR = HitTestResult.SRC_IMAGE_ANCHOR_TYPE;
    public static final int HIT_TYPE_SRC_ANCHOR = HitTestResult.SRC_ANCHOR_TYPE;
    public static final int HIT_TYPE_PHONE = HitTestResult.PHONE_TYPE;
    public static final int HIT_TYPE_EMAIL = HitTestResult.EMAIL_TYPE;
    public static final int HIT_TYPE_GOE = HitTestResult.GEO_TYPE;

    private IJsBridge mJsBridge;
    private ILongClickWatcher mLongClickWatcher;
    private IScrollWatcher mScrollWatcher;

    private boolean mHasInit;

    private float mCurContentHeight;
    private float mScale;

    public AtomWebView(Context context) {
        this(context, null);
    }

    public AtomWebView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AtomWebView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    public void invokeJs(final String jsMethod, final ValueCallback<String> callback) {
        post(new Runnable() {
            @Override
            public void run() {
                evaluateJavascript(jsMethod, callback);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            onScrollChanged(getScrollX(), getScrollY(), getScrollX(), getScrollY());
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onScrollChanged(int newX, int newY, int oldX, int oldY) {
        super.onScrollChanged(newX, newY, oldX, oldY);
        if (newY != oldY) {
            if (mScrollWatcher != null) {
                mScrollWatcher.onChanged(newX, newY, oldX, oldY);
            }

            float contentHeight = getContentHeight() * getCurScale();
            if (mCurContentHeight != contentHeight && newY > 0 && contentHeight <= newY + getHeight()) {
                if (mScrollWatcher != null) {
                    mScrollWatcher.onBottom(newX, newY, oldX, oldY);
                }
                mCurContentHeight = contentHeight;
            }
        }
    }

    public AtomWebView setJsBridge(IJsBridge bridge) {
        mJsBridge = bridge;
        return this;
    }

    public AtomWebView setLongClickWatcher(ILongClickWatcher watcher) {
        mLongClickWatcher = watcher;
        return this;
    }

    public AtomWebView setScrollWatcher(IScrollWatcher watcher) {
        mScrollWatcher = watcher;
        return this;
    }

    public boolean hasVerticalScrollbar() {
        return computeVerticalScrollRange() > computeVerticalScrollExtent();
    }

    public boolean hasHorizontalScrollbar() {
        return computeHorizontalScrollRange() > computeHorizontalScrollExtent();
    }

    public void close() {
        loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
        clearHistory();
        ((ViewGroup) getParent()).removeView(this);
        destroy();
    }

    protected String applyJsBridge(AtomResult result) {
        if (mJsBridge != null) {
            return mJsBridge.onReceive(result);
        }
        return "";
    }

    protected void setCurScale(float value) {
        mScale = value;
    }

    protected boolean enableDelayLoadImages() {
        return Build.VERSION.SDK_INT >= 19;
    }

    protected boolean enableJs() {
        return true;
    }

    protected boolean loadWithOverviewMode() {
        return false;
    }

    protected int cacheMode() {
        return WebSettings.LOAD_DEFAULT;
    }

    protected void init() {
        if (mHasInit) {
            return;
        }

        mHasInit = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        WebSettings webSetting = this.getSettings();

        // Set Http And Https Mixed
        webSetting.setMixedContentMode(webSetting.getMixedContentMode());

        webSetting.setLoadsImagesAutomatically(enableDelayLoadImages());

        webSetting.setJavaScriptEnabled(enableJs());
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);

        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(true);
        webSetting.setLoadWithOverviewMode(loadWithOverviewMode());

        webSetting.setGeolocationEnabled(true);

        webSetting.setAppCacheEnabled(true);
        webSetting.setDomStorageEnabled(true);
        // webSetting.setDatabaseEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setCacheMode(cacheMode());

        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);

        // webSetting.setDefaultTextEncodingName("utf-8");
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);

        initSpec(webSetting);

        setListener();
    }

    protected void initSpec(WebSettings webSetting) {}

    private void setListener() {
        removeJavascriptInterface("searchBoxJavaBridge_");

        setWebChromeClient(new AtomWebChromeClient(this));
        setWebViewClient(new AtomWebViewClient(this));
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                HitTestResult result = getHitTestResult();
                if (result != null && mLongClickWatcher != null) {
                    return mLongClickWatcher.onLongClick(result.getType(), result);
                }
                return false;
            }
        });
    }

    private float getCurScale() {
        if (mScale == 0) {
            return getScale();
        } else {
            return mScale;
        }
    }

    public interface IJsBridge {

        String onReceive(AtomResult result);
    }

    public interface ILongClickWatcher {

        boolean onLongClick(int type, HitTestResult result);
    }

    public interface IScrollWatcher {

        void onChanged(int newX, int newY, int oldX, int oldY);

        void onBottom(int newX, int newY, int oldX, int oldY);
    }
}
