package com.baosight.bssim.controllers;

import org.apache.commons.io.FileUtils;
import org.pegdown.PegDownProcessor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: ns
 * Date: 13-10-25
 * Time: 下午10:58
 * To change this template use File | Settings | File Templates.
 */
public class DocController extends ApplicationController {
    private void show(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String id = req.getAttribute("id")+"";

        String content = FileUtils.readFileToString(new File(ApplicationController.BASE_PATH + "/docs/" +id + ".md"), "UTF-8");
        req.setAttribute("content", new PegDownProcessor().markdownToHtml(content));
    }
}
