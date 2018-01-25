package com.byk.pandora.atomwebview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by Byk on 2018/1/24.
 *
 * @author Byk
 */
public class AtomUtil {

    public static final String ATOM_SCHEME = "atoms";
    public static final String ATOM_SCHEME_CAMERA = "camera";

    public static final String SYSTEM_SCHEME_HTTP = "http";
    public static final String SYSTEM_SCHEME_TEL = "tel";
    public static final String SYSTEM_SCHEME_SMS = "smsto";

    public static final String SPLIT_CHAR = "$";

    public static boolean apply(Context context, Uri uri, String content, String scheme) {
        switch (scheme) {
            case SYSTEM_SCHEME_HTTP:
                browser(context, uri);
                return true;
            case SYSTEM_SCHEME_TEL:
                if (content.contains(SPLIT_CHAR)) {
                    uri = Uri.parse(content.split(SPLIT_CHAR)[0]);
                    call(context, uri);
                } else {
                    dial(context, uri);
                }
                return true;
            case SYSTEM_SCHEME_SMS:
                if (content.contains(SPLIT_CHAR)) {
                    String[] cmd = content.split(SPLIT_CHAR);
                    uri = Uri.parse(cmd[0]);
                    smsToWith(context, uri, cmd[1]);
                } else {
                    smsTo(context, uri);
                }
                return true;
            case ATOM_SCHEME:
                if (applyAtom(context, uri)) {
                    return true;
                }
                break;
            default:
                if (view(context, uri)) {
                    return true;
                }
                break;
        }
        return false;
    }

    public static boolean applyAtom(Context context, Uri uri) {
        String action = uri.getAuthority();
        switch (action) {
            case ATOM_SCHEME_CAMERA:
                camera(context, Integer.parseInt(uri.getQueryParameter("request")));
                return true;
            default:
                return false;
        }
    }

    public static boolean view(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(context.getPackageManager()) == null) {
            return false;
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            context.startActivity(intent);
            return true;
        }
    }

    public static void browser(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    public static void dial(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        context.startActivity(intent);
    }

    @SuppressLint("MissingPermission")
    public static void call(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_CALL, uri);
        context.startActivity(intent);
    }

    public static void smsTo(Context context, Uri uri) {
        smsToWith(context, uri, "");
    }

    public static void smsToWith(Context context, Uri uri, String smsBody) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", smsBody);
        context.startActivity(intent);
    }

    public static void camera(Context context, int requestId) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent, requestId);
        }
    }
}
