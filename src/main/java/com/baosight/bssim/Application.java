package com.baosight.bssim;

import static spark.Spark.*;

import com.baosight.bssim.routes.TablesRoute;
import spark.*;

/**
 * Created by ns on 14-1-17.
 */
public class Application implements spark.servlet.SparkApplication {

    @Override
    public void init() {
        get(new Route("/hello"){
            @Override
            public Object handle(Request request, Response response) {
                return "Hello";
            }
        });

        TablesRoute.all();
    }
}
