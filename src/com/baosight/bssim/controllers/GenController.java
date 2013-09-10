package com.baosight.bssim.controllers;

import com.baosight.bssim.exceptions.ModelException;
import com.baosight.bssim.helpers.interfaces.DatabaseHelper;
import com.baosight.bssim.models.ConfigModel;
import com.baosight.bssim.models.TableModel;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GenController extends ApplicationController {
    private void index(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        try {
            List result = new ArrayList();
            req.setAttribute("result", result);

            JSONObject config = new ConfigModel("GlobalConfig").getJson();
            String database = config.getString("database") == null ? "oracle" : config.getString("database");
            String clazz = "com.baosight.bssim.helpers." + StringUtils.capitalize(database) + "Helper";

            DatabaseHelper helper;
            try{
                helper = (DatabaseHelper)Class.forName(clazz).newInstance();
            } catch (Exception ex) {
                throw new ModelException("未找到类: "+clazz);
            }

            JSONArray userTables = config.optJSONArray("user_tables");
            if(userTables == null)
                return;

            for(int i=0; i<userTables.length(); i++){
                String user = userTables.getJSONObject(i).optString("user");
                JSONArray tables = userTables.getJSONObject(i).optJSONArray("tables");

                if (user == null || tables == null)continue;
                List row = helper.queryTableList(user, "'" + tables.join("', '").replaceAll("\"","") + "'");

                if (row.size() == 0)continue;

                result.add(row);
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

    private void commit(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String id = req.getAttribute("id")+"";

        String[] cmd = new String[]{"/bin/sh", "-c", "svn up | tail -n1" };

        Process p = Runtime.getRuntime().exec(cmd, null, new File("/home/ns/dev/RubymineProjects/BssimGenerator/repo/bssim"));

        try {
            p.waitFor();
        } catch (InterruptedException e) {

        }

        String result = getExecResult(p.getInputStream());
        System.out.print(result);

        setMessage(result);
        redirect_to("/gen/" + id);
    }

    private String getExecResult(InputStream in) throws IOException{
        byte[] buffer = new byte[in.available()];
        in.read(buffer);
        return new String(buffer, "UTF-8");
    }
}
