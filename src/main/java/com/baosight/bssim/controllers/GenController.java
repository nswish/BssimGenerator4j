package com.baosight.bssim.controllers;

import com.baosight.bssim.models.TableModel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class GenController extends ApplicationController {
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
        resp.addHeader("Content-Disposition","attachment;filename=" + model.getName().substring(1) + "E.XML");

        OutputStream out = resp.getOutputStream();
        out.write(model.genXmlCode().getBytes());
        out.flush();
        out.close();
    }
}
