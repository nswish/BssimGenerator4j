package com.baosight.bssim.routes;

/**
 * Created by ns on 14-1-16.
 */
public class HandleResult {
    private boolean status;
    private String message;
    private Object data;

    public HandleResult(boolean status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
