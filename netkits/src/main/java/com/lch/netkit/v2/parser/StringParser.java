package com.lch.netkit.v2.parser;

public class StringParser implements Parser<String> {

    @Override
    public String parse(String responseString) {
        return responseString;
    }
}
