package com.lch.netkit.v2.util;



import android.support.annotation.NonNull;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

public class HttpRequestUtil {

    public static String addParamToUrl(@NonNull String url, @NonNull Map<String, String> params) {

        if (url.contains("?")) {
            if (!url.endsWith("?")) {
                if (!url.endsWith("&")) {
                    url += "&";
                }
            }
        } else {
            url += "?";
        }

        StringBuilder sb = new StringBuilder(url);

        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey())
                    .append("=")
                    .append(urlEncode(entry.getValue()))
                    .append("&");
        }

        return sb.toString();
    }

    public static String addParamToUrl(@NonNull String url, @NonNull JSONObject params) {

        if (url.contains("?")) {
            if (!url.endsWith("?")) {
                if (!url.endsWith("&")) {
                    url += "&";
                }
            }
        } else {
            url += "?";
        }

        StringBuilder sb = new StringBuilder(url);

        Iterator<String> keys = params.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            sb.append(key)
                    .append("=")
                    .append(urlEncode(params.optString(key)))
                    .append("&");
        }

        return sb.toString();
    }

    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "utf-8");
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return s;
    }
}