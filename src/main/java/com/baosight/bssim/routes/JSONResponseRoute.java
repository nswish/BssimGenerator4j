package com.baosight.bssim.routes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONObject;
import spark.ResponseTransformerRoute;

public abstract class JSONResponseRoute extends ResponseTransformerRoute {
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    protected JSONResponseRoute(String path) {
        super(path, "application/json");
    }

    public String render(Object model) {
        return gson.toJson(model);
    }
}
