package com.baosight.bssim.models;

import com.baosight.bssim.controllers.ApplicationController;
import com.baosight.bssim.exceptions.ModelException;
import com.baosight.bssim.helpers.CodeHelper;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.StringWriter;
import java.util.*;

public class JavaModel {
    private TableModel table;
    private Set imports = new TreeSet<String>();

    public JavaModel(TableModel table) {
        this.table = table;
    }

    // instanceMethods -- 关联 belongs_to
    private String[] belongsToMethods() {
        String[] result = null;
        StringBuilder content;
        JSONObject configJson = this.table.getTableConfig().getJson();

        JSONArray belongsTo = configJson.optJSONArray("belongs_to");
        if (belongsTo != null) {
            result = new String[belongsTo.length()];

            for(int i=0; i<belongsTo.length(); i++) {
                JSONObject json = convertRelationConfig(belongsTo.get(i));

                String fullName = json.getString("table"); // 关系表全名，例如：XSSD.TSDSD01
                String suffix = json.optString("suffix", "");

                TableModel anotherOne = new TableModel(fullName);
                String table_name = anotherOne.getClassName().toLowerCase();

                content = new StringBuilder()
                        .append("/**\n")
                        .append(" * 关联 ").append(anotherOne.getComment()).append("\n")
                        .append(" */\n")
                        .append("public " + anotherOne.getClassName() + " " + table_name + ("".equals(suffix) ? suffix : ("By" + suffix)) +"() {\n")
                        .append("    return " + anotherOne.getClassName() + ".find(this." + table_name + "Id" + suffix + ");\n")
                        .append("}");

                result[i] = CodeHelper.indent(content.toString());

                imports.add(anotherOne.getPackage()+"."+anotherOne.getClassName());
            }
        }

        return result;
    }

    // instanceMethods -- 关联 has_many
    private String[] hasManyMethods() {
        String[] result = null;
        StringBuilder content;
        JSONObject configJson = this.table.getTableConfig().getJson();

        JSONArray hasMany = configJson.optJSONArray("has_many");
        if (hasMany != null) {
            result = new String[hasMany.length()];

            for(int i=0; i<hasMany.length(); i++) {
                JSONObject json = convertRelationConfig(hasMany.get(i));

                String fullName = json.getString("table"); // 关系表全名，例如：XSSD.TSDSD02
                String suffix = json.optString("suffix", "");
                String columnName = table.getName() + "_ID" + ("".equals(suffix) ? suffix : ("_" + suffix.toUpperCase()));

                TableModel anotherOne = new TableModel(fullName);

                content = new StringBuilder()
                        .append("/**\n")
                        .append(" * 关联 ").append(anotherOne.getComment()).append("\n")
                        .append(" */\n")
                        .append("public ModelQuerier " + anotherOne.getClassName().toLowerCase() + ("".equals(suffix) ? suffix : ("By" + suffix)) + "s() {\n")
                        .append("    Map arg = new HashMap();\n")
                        .append("    arg.put(\"" + CodeHelper.toCamel(columnName) +"\", this.id);\n")
                        .append("    return new ModelQuerier(" + anotherOne.getQuoteFullTableName() + ", \"" + columnName + " = #" + CodeHelper.toCamel(columnName) + "#\", arg);\n")
                        .append("}");

                result[i] = CodeHelper.indent(content.toString());

                imports.add(anotherOne.getPackage()+"."+anotherOne.getClassName());
            }
        }

        return result;
    }

    // instanceMethods -- 关联 has_one
    private  String[] hasOneMethods() {
        String[] result = null;
        StringBuilder content;
        JSONObject configJson = this.table.getTableConfig().getJson();

        JSONArray hasOne = configJson.optJSONArray("has_one");
        if (hasOne != null) {
            result = new String[hasOne.length()];

            for(int i=0; i<hasOne.length(); i++) {
                JSONObject json = convertRelationConfig(hasOne.getString(i));

                TableModel anotherOne = new TableModel(json.getString("table"));

                content = new StringBuilder()
                        .append("/**\n")
                        .append(" * 关联 ").append(anotherOne.getComment()).append("\n")
                        .append(" */\n")
                        .append("public " + anotherOne.getClassName() + " " + anotherOne.getName().toLowerCase() + "() {\n")
                        .append("    Map arg = new HashMap();\n")
                        .append("    arg.put(\"" + table.getName().toLowerCase() + "Id\", this.id);\n")
                        .append("    return " + anotherOne.getClassName() + ".where(arg).first();\n")
                        .append("}");

                result[i] = CodeHelper.indent(content.toString());

                imports.add(anotherOne.getPackage()+"."+anotherOne.getClassName());
            }
        }

        return result;
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
            root.put("belongsToArray", belongsToMethods());
            root.put("hasManyArray", hasManyMethods());
            root.put("hasOneArray", hasOneMethods());
            root.put("importArray", imports);

            Template tmpl = cfg.getTemplate("java.ftl");

            StringWriter out = new StringWriter();
            tmpl.process(root, out);
            out.flush();

            return out.toString();
        } catch (Exception e) {
            throw new ModelException(e.getMessage());
        }
    }

    private JSONObject convertRelationConfig(Object configFragment) {
        JSONObject json;

        if(configFragment instanceof String){
            json = new JSONObject();
            json.put("table", configFragment);
        }else if(configFragment instanceof JSONObject){
            json = (JSONObject)configFragment;
        }else{
            throw new ModelException("不合法的配置！");
        }

        return json;
    }
}
