package com.baosight.bssim.routes;

import static spark.Spark.*;

import com.baosight.bssim.helpers.DatabaseHelperFactory;
import com.baosight.bssim.helpers.interfaces.DatabaseHelper;
import com.baosight.bssim.models.ConfigModel;
import com.baosight.bssim.models.TableModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TablesRoute {
    public static void all() {
        get(new JSONResponseRoute("/tables") {
            public Object handle(Request request, Response response) {
                try {
                    Map result = new HashMap();

                    JSONObject config = new ConfigModel("GlobalConfig").getJson();
                    DatabaseHelper helper = DatabaseHelperFactory.newInstance();

                    JSONArray userTables = config.optJSONArray("user_tables");
                    if(userTables == null)
                        return new HandleResult(true, null, result);

                    for(int i=0; i<userTables.length(); i++){
                        String user = userTables.getJSONObject(i).optString("user");
                        JSONArray tables = userTables.getJSONObject(i).optJSONArray("tables");

                        if (user == null || tables == null)continue;

                        List row = helper.queryTableList(user, "'" + tables.join("', '").replaceAll("\"","") + "'");

                        if (row.size() == 0)continue;

                        result.put(user, row);
                    }

                    return new HandleResult(true, null, result);
                } catch (Exception e) {
                    return new HandleResult(false, e.getMessage(), null);
                }
            }
        });

        get(new JSONResponseRoute("/tables/:fullTable") {
            @Override
            public Object handle(Request request, Response response) {
                Map result = new HashMap();

                String schemaTable = request.params(":fullTable");

                ConfigModel config = new ConfigModel(schemaTable);
                result.put("script_content", config.getFileContent());

                TableModel table = new TableModel(schemaTable);
                result.put("javaCode", table.genJavaCode());
                result.put("xmlCode", table.genXmlCode());

                return new HandleResult(true, null, result);
            }
        });

        post(new JSONResponseRoute("/tables/:schemaTable/config") {
            @Override
            public Object handle(Request request, Response response) {
                String schemaTable = request.params(":schemaTable");
                Map configGson = new GsonBuilder().setPrettyPrinting().create().fromJson(request.body(), HashMap.class);

                ConfigModel config = new ConfigModel(schemaTable);
                config.save(configGson.get("script_content")+"");

                return new HandleResult(true, null, null);
            }
        });
    }
}
