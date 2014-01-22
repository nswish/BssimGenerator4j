package com.baosight.bssim.routes;

import com.baosight.bssim.models.ConfigModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;

public class ConfigRoute {
    public static void all() {
        get(new JSONResponseRoute("/config") {
            public Object handle(Request request, Response response) {
                ConfigModel config = new ConfigModel("GlobalConfig");

                return new HandleResult(true, null, config.getGson());
            }
        });

        post(new JSONResponseRoute("/config") {
            public Object handle(Request request, Response response) {
                Gson gson = new GsonBuilder().create();
                Map input = gson.fromJson(request.body(), HashMap.class);

                ConfigModel config = new ConfigModel("GlobalConfig");
                Map configGson = config.getGson();
                configGson.putAll(input);
                config.save(gson.toJson(configGson));

                return new HandleResult(true, "保存完成", config.getGson());
            }
        });

        delete(new JSONResponseRoute("/config/tables/:fullTable") {
            @Override
            public Object handle(Request request, Response response) {
                String[] tokens = request.params(":fullTable").split("\\.");
                String user = tokens[0];
                String table = tokens[1];

                ConfigModel config = new ConfigModel("GlobalConfig");
                Map configGson = config.getGson();

                List userTables = (List)configGson.get("user_tables");
                for(int i=0;i<userTables.size();i++){

                    Map userTable = (Map)userTables.get(i);
                    if (userTable.get("user").equals(user)){

                        List tables = (List)userTable.get("tables");
                        if(tables.contains(table)){
                            tables.remove(table);

                            if(tables.isEmpty()){
                                userTables.remove(userTable);
                            }

                            break;
                        }
                    }
                }

                config.save(new Gson().toJson(configGson));
                return new HandleResult(true, null, configGson);
            }
        });

        post(new JSONResponseRoute("/config/tables") {
            @Override
            public Object handle(Request request, Response response) {
                Map input = new Gson().fromJson(request.body(), HashMap.class);
                String schema = (input.get("user")+"").toUpperCase();
                String table = (input.get("table")+"").toUpperCase();

                ConfigModel config = new ConfigModel("GlobalConfig");
                Map configGson = config.getGson();

                List userTables = (List)configGson.get("user_tables");
                boolean found = false;
                for(int i=0;i<userTables.size();i++){
                    Map userTable = (Map)userTables.get(i);
                    if (userTable.get("user").equals(schema)){
                        found = true;

                        List tables = (List)userTable.get("tables");
                        if(tables.contains(table)){
                            return new HandleResult(false, "已存在", configGson);
                        } else {
                            tables.add(table);
                        }

                        break;
                    }
                }

                if(!found) {
                    Map userTable = new HashMap();
                    userTable.put("user", schema);

                    List tables = new ArrayList(1);
                    tables.add(table);
                    userTable.put("tables", tables);

                    userTables.add(userTable);
                }

                config.save(new Gson().toJson(configGson));

                return new HandleResult(true, null, configGson);
            }
        });
    }

}
