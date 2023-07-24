package com.example.emos.wx.common.util;

import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class R extends HashMap<String, Object> {

    public R() {
        put("code", HttpStatus.SC_OK);
        put("message", "success");
    }

    public R put(String key, Object value) {
        put(key, value);
        return this;
    }

    public static R ok() {
        R r = new R();
        return r;
    }

    public static R ok(String message) {
        R r = new R();
        r.put("message", message);
        return r;
    }

    public static R ok(Map<String, Object> map) {
        R r = new R();
        r.putAll(map);
        return r;
    }

    public static R error(int code, String message) {
        R r = new R();
        r.put("code", code);
        r.put("message", message);
        return r;
    }

    public static R error(String message) {
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, message);
    }

    public static R error() {
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "未知异常, 请联系管理员");
    }
}
