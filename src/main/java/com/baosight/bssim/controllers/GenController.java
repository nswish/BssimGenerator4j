package com.baosight.bssim.controllers;

import com.baosight.bssim.exceptions.ControllerException;
import com.baosight.bssim.helpers.DatabaseHelperFactory;
import com.baosight.bssim.helpers.interfaces.DatabaseHelper;
import com.baosight.bssim.models.ConfigModel;
import com.baosight.bssim.models.TableModel;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class GenController extends ApplicationController {
    private void index(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        try {
            List result = new ArrayList();
            req.setAttribute("result", result);

            JSONObject config = new ConfigModel("GlobalConfig").getJson();
            DatabaseHelper helper = DatabaseHelperFactory.newInstance();

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

        try{
            ConfigModel config = new ConfigModel(id);
            config.save(req.getParameter("script_content"));
            setMessage("已保存...");
            redirect_to("/gen/"+id);
        } catch (Exception ex) {
            show(req, resp);
            req.setAttribute("script_content", req.getParameter("script_content"));
            setMessage(ex);
            render("/views/gen/show.jsp");
        }
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
/*
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
*/

            // svn commit
            cmd = "svn commit -m BssimGenerator";
            p = Runtime.getRuntime().exec(cmd, null, dir);
            p.waitFor();
            result.append(getExecResult(p));

            setMessage(result.toString());

        } catch (InterruptedException e) {
            throw new ControllerException("版本库操作异常！"+e.getMessage());
        } finally {
            redirect_to("/gen/" + id);
        }

    }

    private void downloadJava(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String id = req.getAttribute("id")+"";
        TableModel model = new TableModel(id);

        resp.setContentType("application/x-download");
        resp.addHeader("Content-Disposition","attachment;filename=" + model.getClassName() + ".java");

        OutputStream out = resp.getOutputStream();
        out.write(model.genJavaCode().getBytes());
        out.flush();
        out.close();
    }

    private void downloadXml(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String id = req.getAttribute("id")+"";
        TableModel model = new TableModel(id);

        resp.setContentType("application/x-download");
        resp.addHeader("Content-Disposition","attachment;filename=" + model.getTableName().substring(1) + "E.XML");

        OutputStream out = resp.getOutputStream();
        out.write(model.genXmlCode().getBytes());
        out.flush();
        out.close();
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
