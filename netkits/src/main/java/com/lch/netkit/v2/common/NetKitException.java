package com.lch.netkit.v2.common;

/**
 * Created by Administrator on 2019/1/17.
 */

public class NetKitException extends Throwable {
    private String httpCode;

    public NetKitException() {
    }

    public NetKitException(String message) {
        super(message);
    }

    public NetKitException(String message, Throwable cause) {
        super(message, cause);
    }

    public NetKitException(Throwable cause) {
        super(cause);
    }

    public String getHttpCode() {
        return httpCode;
    }

    public NetKitException setHttpCode(String httpCode) {
        this.httpCode = httpCode;
        return this;
    }

    public static NetKitException newInstance(String message, String httpCode) {
        return new NetKitException(message).setHttpCode(httpCode);
    }
}
