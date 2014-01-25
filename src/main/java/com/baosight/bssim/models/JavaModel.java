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
    private String javaPackage;
    private Set imports = new LinkedHashSet(5);
    private String classComment;
    private String className;
    private List instanceMethods = new ArrayList(15);
    private List classMethods = new ArrayList(15);

    private TableModel table;

    public JavaModel(TableModel table) {
        this.table = table;
    }

    public void generateBasic() {
        this.javaPackage = "package " + table.getPackage() + ";";

        this.className = "public class " + table.getClassName() + " extends ModelEPBase";
        this.addImport("com.baosight.bssim.common.model.ModelEPBase");

        this.classComment = CodeHelper.formatComment(
            table.getClassName() + "\n" +
            "Table Comment : " + table.getComment() + "\n" +
            "Generate Date : " + DateFormatUtils.format(new Date(), "y-MM-dd HH:mm:ss")
        );
    }

    public void generateExtension() {
        StringBuilder content;
        JSONObject configJson = this.table.getTableConfig().getJson();

        // instanceMethods -- 关联 belongs_to
        JSONArray belongsTo = configJson.optJSONArray("belongs_to");
        if (belongsTo != null) {
            for(int i=0; i<belongsTo.length(); i++) {
                JSONObject json = convertRelationConfig(belongsTo.get(i));
                
                String fullName = json.getString("table"); // 关系表全名，例如：XSSD.TSDSD01
                String suffix = json.optString("suffix", "");
                
                TableModel anotherOne = new TableModel(fullName);
                String table_name = anotherOne.getClassName().toLowerCase();

                content = new StringBuilder()
                        .append("public " + anotherOne.getClassName() + " " + table_name + ("".equals(suffix) ? suffix : ("By" + suffix)) +"() {\n")
                        .append("    return " + anotherOne.getClassName() + ".find(this." + table_name + "Id" + suffix + ");\n")
                        .append("}");

                this.addMethod(this.instanceMethods, content.toString(), "关联 "+anotherOne.getComment());
                this.addImport(anotherOne.getFullClassName());
            }
        }

        // instanceMethods -- 关联 has_many
        JSONArray hasMany = configJson.optJSONArray("has_many");
        if (hasMany != null) {
            for(int i=0; i<hasMany.length(); i++) {
                JSONObject json = convertRelationConfig(hasMany.get(i));

                String fullName = json.getString("table"); // 关系表全名，例如：XSSD.TSDSD02
                String suffix = json.optString("suffix", "");
                String columnName = table.getName() + "_ID" + ("".equals(suffix) ? suffix : ("_" + suffix.toUpperCase()));
                
                TableModel anotherOne = new TableModel(fullName);
                
                content = new StringBuilder()
                        .append("public ModelQuerier " + anotherOne.getClassName().toLowerCase() + ("".equals(suffix) ? suffix : ("By" + suffix)) + "s() {\n")
                        .append("    Map arg = new HashMap();\n")
                        .append("    arg.put(\"" + CodeHelper.toCamel(columnName) +"\", this.id);\n")
                        .append("    return new ModelQuerier(" + anotherOne.getQuoteFullTableName() + ", \"" + columnName + " = #" + CodeHelper.toCamel(columnName) + "#\", arg);\n")
                        .append("}");

                this.addMethod(this.instanceMethods, content.toString(), "关联 " + anotherOne.getComment());
            }
        }

        // instanceMethods -- 关联 has_one
        JSONArray hasOne = configJson.optJSONArray("has_one");
        if (hasOne != null) {
            for(int i=0; i<hasOne.length(); i++) {
                JSONObject json = convertRelationConfig(hasOne.getString(i));

                TableModel anotherOne = new TableModel(json.getString("table"));

                content = new StringBuilder()
                        .append("public " + anotherOne.getClassName() + " " + anotherOne.getName().toLowerCase() + "() {\n")
                        .append("    Map arg = new HashMap();\n")
                        .append("    arg.put(\"" + table.getName().toLowerCase() + "Id\", this.id);\n")
                        .append("    return " + anotherOne.getClassName() + ".where(arg).first();\n")
                        .append("}");

                this.addMethod(this.instanceMethods, content.toString(), "关联 " + anotherOne.getComment());
                this.addImport(anotherOne.getFullClassName());
            }
        }
    }

    // instanceMethods -- 关联 belongs_to
    public String[] belongsToMethods() {
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

                // todo: 关联查询的问题
                content = new StringBuilder()
                        .append("/**\n")
                        .append(" * 关联 货款信息配置主表\n")   // <--???
                        .append(" */\n")
                        .append("public " + anotherOne.getClassName() + " " + table_name + ("".equals(suffix) ? suffix : ("By" + suffix)) +"() {\n")
                        .append("    return " + anotherOne.getClassName() + ".find(this." + table_name + "Id" + suffix + ");\n")
                        .append("}");

                result[i] = CodeHelper.indent(content.toString());
            }
        }

        return result;
    }

    public String toCode() {

        // generate code fragments
        generateBasic();
        generateExtension();

        // combine fragments to full code
        StringBuilder result = new StringBuilder()
                .append(this.javaPackage).append("\n\n")                                   // package
                .append(StringUtils.join(this.imports.toArray(), "\n")).append("\n\n")     // import
                .append(this.classComment).append("\n")                                    // class comment
                .append("@SuppressWarnings({\"serial\", \"rawtypes\", \"unchecked\"})\n")  // @SuppressWarnings
                .append(this.className).append(" {\n")                                     // class name and modifier
                .append(CodeHelper.indent(StringUtils.join(new String[]{                   // class body

                        // instanceMethods
                        StringUtils.join(this.instanceMethods, "\n\n"),

                        // classMethods
                        StringUtils.join(this.classMethods, "\n\n")

                }, "\n\n")))
                .append("\n")
                .append("}");

        return result.toString();
    }

    public String fmToCode() {
        Configuration cfg = new Configuration();
        try {
            cfg.setDirectoryForTemplateLoading(new File(ApplicationController.BASE_PATH+"/ftls"));
            cfg.setDefaultEncoding("utf-8");
            cfg.setObjectWrapper(new DefaultObjectWrapper());

            Map root = new HashMap();
            root.put("table", this.table);
            root.put("generateDate", new Date());
            root.put("belongsToArray", belongsToMethods());

            Template tmpl = cfg.getTemplate("java.ftl");

            StringWriter out = new StringWriter();
            tmpl.process(root, out);
            out.flush();

            return out.toString();
        } catch (Exception e) {
            throw new ModelException(e.getMessage());
        }
    }

    public void addImport(String imp) {
        this.imports.add("import " + imp + ";");
    }

    public void addMethod(List list, String content, String comment) {
        list.add(new StringBuilder().append(CodeHelper.formatComment(comment)).append("\n").append(content));
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
