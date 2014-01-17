package com.baosight.bssim.routes;

import com.baosight.bssim.models.ConfigModel;
import spark.Request;
import spark.Response;

import static spark.Spark.get;

/**
 * Created by ns on 14-1-17.
 */
public class ConfigRoute {
    public static void all() {
        get(new JSONResponseRoute("/config") {
            public Object handle(Request request, Response response) {
                ConfigModel config = new ConfigModel("GlobalConfig");

                return new HandleResult(true, null, config.getGson());
            }
        });
    }

}
