package com.baosight.bssim.controllers;

import com.baosight.bssim.models.ConfigModel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: ns
 * Date: 13-8-29
 * Time: 上午10:28
 * To change this template use File | Settings | File Templates.
 */
public class ConfigController extends ApplicationController {
    private void index(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException{
        ConfigModel config = new ConfigModel("GlobalConfig");
        req.setAttribute("config", config.getFileContent());

        try {
            config.getJson();
        } catch (Exception ex) {
            setMessage(ex);
        }

    }

    private void update(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException{
        ConfigModel config = new ConfigModel("GlobalConfig");

        try {
            config.save(req.getParameter("configuration"));
            setMessage("已保存...");
            redirect_to("/config");
        } catch (Exception ex) {
            setMessage(ex);
            req.setAttribute("config", req.getParameter("configuration"));
            render("/views/config/index.jsp");
        }


    }
}
