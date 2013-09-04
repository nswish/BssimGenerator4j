package com.baosight.bssim.controllers;

import com.baosight.bssim.exceptions.ControllerException;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 此类负责：
 *    1. 路由解析
 *    2. 路由转发
 *    3. 消息设置
 *
 * 路由规则：
 *    动词：GET
 *    /resource           -> index
 *    /resource/id        -> show
 *
 *    动词：POST
 *    /resource/action    -> action
 *    /resource/id/action -> action
 */
public abstract class ApplicationController  extends HttpServlet {
    public static String BASE_PATH = "/tmp";

    private String controller;
    private String methodName;
    private String redirect_path;
    private String render_path;
    private Object message;

    public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException{
        super.service(req, resp);

        Object message = getMessage();
        if(req.getSession().getAttribute("message") != null) {
            message = req.getSession().getAttribute("message");
            req.getSession().removeAttribute("message");
        }

        req.setAttribute("message", message);
        setMessage(null);

        if (this.redirect_path != null){
            resp.sendRedirect(this.redirect_path);
            this.redirect_path = null;

            req.getSession().setAttribute("message", message);
        } else {
            RequestDispatcher dispatcher;
            if(this.render_path != null){
                dispatcher = req.getRequestDispatcher(render_path);
            } else {
                this.controller = StringUtils.strip(req.getServletPath(), "/").split("/")[0];
                dispatcher = req.getRequestDispatcher("/views/"+this.controller+"/"+this.methodName+".jsp");
            }
            dispatcher.forward(req, resp);
        }
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException{
        String pathInfo = req.getPathInfo();
        this.methodName = null;

        if (pathInfo == null) {
            this.methodName = "index";
        } else {
            this.methodName = "show";
            req.setAttribute("id", StringUtils.strip(pathInfo, "/"));
        }

        try {
            Method method = this.getClass().getDeclaredMethod(this.methodName, HttpServletRequest.class, HttpServletResponse.class);
            method.setAccessible(true);
            method.invoke(this, req, resp);
        } catch (InvocationTargetException e){
            setMessage(e.getTargetException());
        } catch (Exception e) {
            setMessage(e);
        }

    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException{
        String pathInfo = req.getPathInfo();
        this.methodName = null;

        if (pathInfo == null) {
            throw new ControllerException("找不到处理函数...");
        }

        String[] tokens = StringUtils.strip(pathInfo, "/").split("/");
        if (tokens.length == 1) {
            this.methodName = tokens[0].trim();
        } else if(tokens.length == 2){
            this.methodName = tokens[1].trim();
            req.setAttribute("id", tokens[0].trim());
        }

        try {
            Method method = this.getClass().getDeclaredMethod(this.methodName, HttpServletRequest.class, HttpServletResponse.class);
            method.setAccessible(true);
            method.invoke(this, req, resp);
        } catch (InvocationTargetException e){
            setMessage(e.getTargetException());
        } catch (Exception e) {
            setMessage(e);
        }
    }

    protected void setMessage(Object message) {
        this.message = message;
    }

    protected Object getMessage() {
        return message;
    }

    protected void render(String path) {
        this.render_path = path;
    }

    protected void redirect_to(String path){
        this.redirect_path = path;
    }
}