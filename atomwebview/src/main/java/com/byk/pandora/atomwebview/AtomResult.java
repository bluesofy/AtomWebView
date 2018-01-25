package com.byk.pandora.atomwebview;

import android.net.Uri;

/**
 * Created by Byk on 2018/1/24.
 *
 * @author Byk
 */
public class AtomResult {

    private String scheme;

    private String action;

    private String data;

    private Uri uri;

    private String content;

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
