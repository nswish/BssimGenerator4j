package com.baosight.bssim.controllers;

import com.baosight.bssim.exceptions.ControllerException;
import com.baosight.bssim.exceptions.ModelException;
import com.baosight.bssim.helpers.interfaces.DatabaseHelper;
import com.baosight.bssim.models.ConfigModel;
import com.baosight.bssim.models.TableModel;
import org.apache.commons.io.FileUtils;
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
        JSONObject config = new ConfigModel("GlobalConfig").getJson();

        if (config.optString("svn_repo_path") == null) {
            throw new ControllerException("请在配置中指定svn版本库的路径(svn_repo_path)！");
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
            FileUtils.writeStringToFile(new File(dir+File.separator+"src"+File.separator+javaPath), model.genJavaCode(), "UTF8");

            String xmlPath = model.getXmlPath();
            FileUtils.writeStringToFile(new File(dir+File.separator+"src"+File.separator+xmlPath), model.genXmlCode(), "UTF8");

            // svn add
            cmd = "svn add " + "src" + File.separator + javaPath;
            p = Runtime.getRuntime().exec(cmd, null, dir);
            p.waitFor();
            if (debug)
                result.append(getExecResult(p));

            cmd = "svn add " + "src" + File.separator + xmlPath;
            p = Runtime.getRuntime().exec(cmd, null, dir);
            p.waitFor();
            if (debug)
                result.append(getExecResult(p));

            // svn commit
            cmd = "svn commit -m BssimGenerator";
            p = Runtime.getRuntime().exec(cmd, null, dir);
            p.waitFor();
            result.append(getExecResult(p));

            setMessage(result.toString());

            redirect_to("/gen/" + id);
        } catch (InterruptedException e) {
            throw new ControllerException("版本库操作异常！"+e.getMessage());
        }

    }

    private String getExecResult(Process p) throws IOException{
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
