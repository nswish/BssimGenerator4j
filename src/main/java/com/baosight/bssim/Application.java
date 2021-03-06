package com.baosight.bssim;

import com.baosight.bssim.routes.ConfigRoute;
import com.baosight.bssim.routes.DocRoute;
import com.baosight.bssim.routes.FormRoute;
import com.baosight.bssim.routes.TablesRoute;

/**
 * Created by ns on 14-1-17.
 */
public class Application implements spark.servlet.SparkApplication {

    @Override
    public void init() {
        TablesRoute.all();
        ConfigRoute.all();
        DocRoute.all();
        FormRoute.all();
    }
}
