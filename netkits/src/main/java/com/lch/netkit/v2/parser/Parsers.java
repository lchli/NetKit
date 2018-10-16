package com.lch.netkit.v2.parser;

import org.json.JSONObject;

/**
 * 常见的Parser.
 */
public final class Parsers {

    public static final Parser<JSONObject> JSON = new JsonParser();
    public static final Parser<String> STRING = new StringParser();
}
