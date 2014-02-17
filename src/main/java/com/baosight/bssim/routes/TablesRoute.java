package com.baosight.bssim.routes;

import com.baosight.bssim.exceptions.ControllerException;
import com.baosight.bssim.helpers.DatabaseHelperFactory;
import com.baosight.bssim.helpers.SvnHelper;
import com.baosight.bssim.helpers.interfaces.DatabaseHelper;
import com.baosight.bssim.models.ConfigModel;
import com.baosight.bssim.models.TableModel;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.Request;
import spark.Response;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;

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

                    return new HandleResult(true, "读取完成", result);
                } catch (Exception e) {
                    return new HandleResult(false, e.getMessage(), null);
                }
            }
        });

        get(new JSONResponseRoute("/tables/:fullTable") {
            @Override
            public Object handle(Request request, Response response) {
                Map result = new HashMap();

                try {
                    String schemaTable = request.params(":fullTable");

                    ConfigModel config = new ConfigModel(schemaTable);
                    result.put("script_content", config.getFileContent());

                    TableModel table = new TableModel(schemaTable);
                    result.put("javaCode", table.genJavaCode());
                    result.put("xmlCode", table.genXmlCode());

                    return new HandleResult(true, "代码已生成", result);
                } catch (Exception e) {
                    return new HandleResult(false, e.getMessage(), result);
                }
            }
        });

        get(new JSONResponseRoute("/tables/:fullTable/columns") {
            @Override
            public Object handle(Request request, Response response) {
                Map result = new HashMap();

                try{
                    String schemaTable = request.params(":fullTable");

                    TableModel table = new TableModel(schemaTable);
                    result.put("columns", table.getColumns());

                    return new HandleResult(true, "", result);
                } catch (Exception e) {
                    return new HandleResult(false, e.getMessage(), result);
                }
            }
        });

        post(new JSONResponseRoute("/tables/:schemaTable/config") {
            @Override
            public Object handle(Request request, Response response) {
                String schemaTable = request.params(":schemaTable");
                try {
                    Map configGson = new GsonBuilder().setPrettyPrinting().create().fromJson(request.body(), HashMap.class);

                    ConfigModel config = new ConfigModel(schemaTable);
                    config.save(configGson.get("script_content")+"");

                    return new HandleResult(true, "保存完成", null);
                } catch (Exception e) {
                    return new HandleResult(false, e.getMessage(), null);
                }
            }
        });

        post(new JSONResponseRoute("/tables/:schemaTable/commit") {
            @Override
            public Object handle(Request request, Response response) {
                String id = request.params(":schemaTable");

                try {
                    TableModel model = new TableModel(id);
                    String javaPath = model.getJavaPath();
                    String xmlPath = model.getXmlPath();

                    Map commit = new HashMap();
                    commit.put("src" + File.separator + javaPath, model.genJavaCode());
                    commit.put("src" + File.separator + xmlPath,  model.genXmlCode());

                    String result = SvnHelper.commit(commit);

                    return new HandleResult(true, result, null);
                } catch (Exception e) {
                    return new HandleResult(false, e.getMessage(), null);
                }
            }
        });
    }
}
