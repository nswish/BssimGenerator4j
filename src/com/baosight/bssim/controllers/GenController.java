package com.baosight.bssim.controllers;

import com.baosight.bssim.DbUtils.OracleDataSourceFactory;
import com.baosight.bssim.models.ConfigModel;
import com.baosight.bssim.models.TableModel;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GenController extends ApplicationController {
    private void index(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        try {
            List result = new ArrayList();
            req.setAttribute("result", result);

            ConfigModel config = new ConfigModel("GlobalConfig");
            JSONObject confingJson = config.getJson();

            QueryRunner run = new QueryRunner(OracleDataSourceFactory.createDataSource());

            JSONArray userTables = confingJson.optJSONArray("user_tables");
            if(userTables == null)
                return;

            for(int i=0; i<userTables.length(); i++){
                String user = userTables.getJSONObject(i).optString("user");
                JSONArray tables = userTables.getJSONObject(i).optJSONArray("tables");

                if (user == null || tables == null)continue;

                String sql = "SELECT v1.OWNER, v2.TABLE_NAME, v2.COMMENTS,\n" +
                        "               (SELECT count(1) FROM ALL_TAB_COLS WHERE TABLE_NAME = v1.TABLE_NAME AND OWNER = v1.OWNER) as COLUMN_COUNT,\n" +
                        "               (SELECT count(1) FROM ALL_INDEXES WHERE TABLE_NAME = v1.TABLE_NAME AND OWNER = v1.OWNER) as INDEX_COUNT\n" +
                        "          FROM ALL_TABLES v1, ALL_TAB_COMMENTS v2\n" +
                        "         WHERE v1.TABLE_NAME = v2.TABLE_NAME\n" +
                        "           AND v1.OWNER = v2.OWNER\n" +
                        "           AND v1.OWNER = UPPER(?)\n" +
                        "           AND V1.TABLE_NAME IN ('"+tables.join("', '").replaceAll("\"","")+"')\n" +
                        "         ORDER BY\n" +
                        "               v1.TABLE_NAME\n";

                result.add(run.query(sql, new MapListHandler(), user));
            }
        } catch (Exception e) {
            setMessage(e);
        }
    }

    private void show(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String id = req.getAttribute("id")+"";

        ConfigModel config = new ConfigModel(id);
        req.setAttribute("script_content", config.getFileContent());

        TableModel table = new TableModel(id);
        req.setAttribute("javaCode", table.genJavaCode());
        req.setAttribute("xmlCode", table.genXmlCode());
    }

    private void update(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String id = req.getAttribute("id")+"";

        ConfigModel config = new ConfigModel(id);
        config.save(req.getParameter("script_content"));

        setMessage("已保存...");
        redirect_to("/gen/"+id);
    }
}
