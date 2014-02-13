package com.baosight.bssim.models;

import com.baosight.bssim.controllers.ApplicationController;
import com.baosight.bssim.exceptions.ModelException;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

import java.io.File;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ns on 14-2-13.
 */
public class ServiceModel {
    private TableModel table;

    public ServiceModel(TableModel table) {
        this.table = table;
    }

    public String toCode(Map config) {
        Configuration cfg = new Configuration();
        try {
            String firstModule = config.get("firstModule")+"";
            String secondModule = config.get("secondModule")+"";
            String serviceName = config.get("serviceName")+"";

            cfg.setDirectoryForTemplateLoading(new File(ApplicationController.BASE_PATH+"/ftls"));
            cfg.setDefaultEncoding("utf-8");
            cfg.setObjectWrapper(new DefaultObjectWrapper());

            Map root = new HashMap();
            root.put("table", this.table);
            root.put("generateDate", new Date());
            root.put("firstModule", firstModule);
            root.put("secondModule", secondModule);
            root.put("serviceName", serviceName);

            Template tmpl = cfg.getTemplate("service.ftl");

            StringWriter out = new StringWriter();
            tmpl.process(root, out);
            out.flush();

            return out.toString();
        } catch (Exception e) {
            throw new ModelException(e.getMessage());
        }
    }
}
