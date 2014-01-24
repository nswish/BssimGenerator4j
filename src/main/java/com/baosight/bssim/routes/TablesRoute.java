package com.baosight.bssim.routes;

import static spark.Spark.*;

import com.baosight.bssim.exceptions.ControllerException;
import com.baosight.bssim.helpers.DatabaseHelperFactory;
import com.baosight.bssim.helpers.interfaces.DatabaseHelper;
import com.baosight.bssim.models.ConfigModel;
import com.baosight.bssim.models.TableModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import spark.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
                JSONObject config = new ConfigModel("GlobalConfig").getJson();

                if (config.optString("svn_repo_path") == null) {
                    throw new ControllerException("请在设置中指定svn版本库的本地路径");
                }

                boolean debug = config.optBoolean("svn_debug", false);

                try {
                    Process p;
                    String cmd;
                    StringBuilder result = new StringBuilder();
                    File dir = new File(config.getString("svn_repo_path"));

                    // svn update
                    cmd = "svn up";
                    p = Runtime.getRuntime().exec(cmd, null, dir);
                    p.waitFor();
                    if(debug)
                        result.append(getExecResult(p));

                    // generate code and save to filesystem
                    TableModel model = new TableModel(id);

                    String javaPath = model.getJavaPath();
                    FileUtils.writeStringToFile(new File(dir + File.separator + "src" + File.separator + javaPath), model.genJavaCode(), "UTF8");

                    String xmlPath = model.getXmlPath();
                    FileUtils.writeStringToFile(new File(dir + File.separator + "src" + File.separator + xmlPath), model.genXmlCode(), "UTF8");

                    // svn status
                    cmd = "svn status";
                    p = Runtime.getRuntime().exec(cmd, null, dir);
                    p.waitFor();
                    String statusMsg = getExecResult(p);
                    if (debug)
                        result.append(getExecResult(p));

                    // svn add
                    String[] addTokens = statusMsg.split("\n");
                    for (int i=0; i<addTokens.length; i++) {
                        int index = addTokens[i].indexOf("src/com/baosight");
                        if (index > -1) {
                            cmd = "svn add " + addTokens[i].substring(index);
                            p = Runtime.getRuntime().exec(cmd, null, dir);
                            p.waitFor();
                            if (debug)
                                result.append(getExecResult(p));
                        }
                    }

                    // svn commit
                    cmd = "svn commit -m BssimGenerator";
                    p = Runtime.getRuntime().exec(cmd, null, dir);
                    p.waitFor();
                    result.append(getExecResult(p));

                    return new HandleResult(true, result.toString(), null);
                } catch (InterruptedException e) {
                    return new HandleResult(false, e.getMessage(), null);
                } catch (Exception e) {
                    return new HandleResult(false, e.getMessage(), null);
                }
            }
        });
    }


    private static String getExecResult(Process p) throws IOException {
        byte[] buffer;
        StringBuilder result = new StringBuilder();
        InputStream in = p.getInputStream();
        InputStream err = p.getErrorStream();

        if (in.available() > 0) {
            buffer = new byte[in.available()];
            in.read(buffer);
            result.append(new String(buffer));
        }

        if (err.available() > 0) {
            buffer = new byte[err.available()];
            err.read(buffer);
            result.append(new String(buffer));
        }

        return result.toString();
    }
}
