package com.baosight.bssim.routes;

import com.baosight.bssim.models.ConfigModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;

/**
 * Created by ns on 14-1-17.
 */
public class ConfigRoute {
    public static void all() {
        get(new JSONResponseRoute("/config.json") {
            public Object handle(Request request, Response response) {
                ConfigModel config = new ConfigModel("GlobalConfig");

                return new HandleResult(true, null, config.getGson());
            }
        });

        post(new JSONResponseRoute("/config.json") {
            public Object handle(Request request, Response response) {
                Gson gson = new GsonBuilder().create();
                Map input = gson.fromJson(request.body(), HashMap.class);

                ConfigModel config = new ConfigModel("GlobalConfig");
                Map configGson = config.getGson();
                configGson.putAll(input);
                config.save(gson.toJson(configGson));

                return new HandleResult(true, null, config.getGson());
            }
        });
    }

}
