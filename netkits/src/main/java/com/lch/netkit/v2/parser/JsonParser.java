package com.lch.netkit.v2.parser;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonParser implements Parser<JSONObject> {

    @Override
    public JSONObject parse(String responseString) throws JSONException {
        return new JSONObject(responseString);
    }
}
