package com.baosight.bssim.models;


import com.baosight.bssim.controllers.ApplicationController;
import com.baosight.bssim.exceptions.ModelException;
import com.baosight.bssim.helpers.CodeHelper;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.File;
import java.io.StringWriter;
import java.util.*;

public class XmlModel {
    private TableModel table;

    public XmlModel(TableModel table) {
        this.table = table;
    }

    public String toCode() {
        Configuration cfg = new Configuration();
        try {
            cfg.setDirectoryForTemplateLoading(new File(ApplicationController.BASE_PATH+"/ftls"));
            cfg.setDefaultEncoding("utf-8");
            cfg.setObjectWrapper(new DefaultObjectWrapper());

            Map root = new HashMap();
            root.put("table", this.table);
            root.put("generateDate", new Date());

            Template tmpl = cfg.getTemplate("xml.ftl");

            StringWriter out = new StringWriter();
            tmpl.process(root, out);
            out.flush();

            return out.toString();
        } catch (Exception e) {
            throw new ModelException(e.getMessage());
        }
    }
}
