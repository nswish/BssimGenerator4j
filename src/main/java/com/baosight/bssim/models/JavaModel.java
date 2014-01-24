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

import java.io.*;
import java.util.*;

public class JavaModel {
    private String javaPackage;
    private Set imports = new LinkedHashSet(5);
    private String classComment;
    private String className;
    private String[] attrs;
    private List fieldsAndColumns = new ArrayList(2);
    private List constructorMethods = new ArrayList(2);
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

    public void generateIPlat4j() {
        StringBuilder content;

        this.attrs = new String[table.getColumns().length];

        for(int i=0; i<table.getColumns().length; i++) {
            ColumnModel column = table.getColumns()[i];

            // attrs
            this.attrs[i] = column.fragmentForAttr();
            if ("BigDecimal".equals(column.getJavaType()))
                this.addImport("java.math.BigDecimal");

            // instanceMethods -- accessors -- getter
            this.addMethod(this.instanceMethods, column.fragmentForGetter(), "get the " + column.getCamelName() + " - " + column.getComment() + "\n"
                    + "@return the " + column.getCamelName());

            // instanceMethods -- accessors -- setter
            this.addMethod(this.instanceMethods, column.fragmentForSetter(), "set the " + column.getCamelName() + " - " + column.getComment());
        }

        // constructor
        content = new StringBuilder()
                .append("public " + table.getClassName() + "() {\n")
                .append("    initMetaData();\n")
                .append("}");

        this.addMethod(this.constructorMethods, content.toString(), "the constructor");

        // instanceMethods -- metadata
        content = new StringBuilder()
                .append("public void initMetaData() {\n")
                .append("    EiColumn eiColumn;\n\n")
                .append(CodeHelper.indent(CodeHelper.concatFragments(table.getColumns(), "Meta", "\n\n"))).append("\n")
                .append("}");

        this.addMethod(this.instanceMethods, content.toString(), "initialize the metadata");
        this.addImport("com.baosight.iplat4j.core.ei.EiColumn");

        // instanceMethods -- fromMap
        content = new StringBuilder()
                .append("public void fromMap(Map map) {\n")
                .append(CodeHelper.indent(CodeHelper.concatFragments(table.getColumns(), "FromMap", "\n"))).append("\n")
                .append("}");

        this.addMethod(this.instanceMethods, content.toString(), "get the value from Map");
        this.addImport("java.util.Map");
        this.addImport("com.baosight.iplat4j.util.StringUtils");
        this.addImport("com.baosight.iplat4j.util.NumberUtils");

        // instanceMethods -- toMap
        content = new StringBuilder()
                .append("public Map toMap() {\n")
                .append("    Map map = new HashMap();\n")
                .append(CodeHelper.indent(CodeHelper.concatFragments(table.getColumns(), "ToMap", "\n"))).append("\n")
                .append("    return map;\n")
                .append("}");

        this.addMethod(this.instanceMethods, content.toString(), "set the value to Map");
    }

    public void generateExtension() {
        StringBuilder content;
        JSONObject configJson = this.table.getTableConfig().getJson();

        // instanceMethods -- getVersion
        content = new StringBuilder()
                .append("public int getVersion() {\n")
                .append("    return 1;\n")
                .append("}");
        this.addMethod(this.instanceMethods, content.toString(), "生成代码的版本号");

        // fieldsAndColumns -- fields
        content = new StringBuilder()
                .append("public static class fields {\n")
                .append(CodeHelper.indent(CodeHelper.concatFragments(table.getColumns(), "Field", "\n"))).append("\n")
                .append("}");

        this.addMethod(this.fieldsAndColumns, content.toString(), "按驼峰命名规则的字段名称");

        // fieldsAndColumns -- columns
        content = new StringBuilder()
                .append("public static class columns {\n")
                .append(CodeHelper.indent(CodeHelper.concatFragments(table.getColumns(), "Column", "\n"))).append("\n")
                .append("}");

        this.addMethod(this.fieldsAndColumns, content.toString(), "按下划线命名规则的字段名称");

        // static methods -- newInstance
        content = new StringBuilder()
                .append("public static " + table.getClassName() + " newInstance() {\n")
                .append("    " + table.getClassName() + " result = new " + table.getClassName() + "();\n")
                .append("    result.isExtended = true;\n")
                .append("    return result;\n")
                .append("}");

        this.addMethod(this.classMethods, content.toString(), "创建新的实例");

        // static methods -- newInstance(Map attr)
        content = new StringBuilder()
                .append("public static " + table.getClassName() + " newInstance(Map attr) {\n")
                .append("    " + table.getClassName() + " result = newInstance();\n")
                .append("    result.fromMap(attr);\n")
                .append("    return result;\n")
                .append("}");

        this.addMethod(this.classMethods, content.toString(), "创建新的实例(属性初始化为传入的参数)");

        // static methods -- find(Long id)
        content = new StringBuilder()
                .append("public static " + table.getClassName() + " find(Long id) {\n")
                .append("    return find(id, false);\n")
                .append("}");

        this.addMethod(this.classMethods, content.toString(), "按id查找");

        // static methods -- findWithLock(Long id)
        content = new StringBuilder()
                .append("public static " + table.getClassName() + " findWithLock(Long id) {\n")
                .append("    return find(id, true);\n")
                .append("}");

        this.addMethod(this.classMethods, content.toString(), "按id查找(上锁)");

        // static methods -- find(Long id, boolean isLocked)
        content = new StringBuilder()
                .append("private static " + table.getClassName() + " find(Long id, boolean isLocked) {\n")
                .append("    Map arg = new HashMap();\n")
                .append("    arg.put(\"id\", id);\n")
                .append("    if(isLocked)arg.put(\"forUpdate\", \"FOR UPDATE\");\n")
                .append("    List<" + table.getClassName() + "> result = getDao().query(\"" + table.getName().substring(1) + "E.select_by_id\", arg);\n")
                .append("\n")
                .append("    if(result.size() > 0){\n")
                .append("        " + table.getClassName() + " a = result.get(0);\n")
                .append("        a.isExtended = true;\n")
                .append("        a.isInDB = true;\n")
                .append("        return a;\n")
                .append("    } else {\n")
                .append("        return null;\n")
                .append("    }\n")
                .append("}");

        this.addMethod(this.classMethods, content.toString(), "按id查找(可选择是否上锁)");
        this.addImport("java.util.HashMap");
        this.addImport("java.util.List");

        // static methods -- find(String id)
        content = new StringBuilder()
                .append("public static " + table.getClassName() + " find(String id) {\n")
                .append("    return find(Long.parseLong(id), false);\n")
                .append("}");

        this.addMethod(this.classMethods, content.toString(), "按id查找，参数为String类型");

        // static methods -- findWithLock(String id)
        content = new StringBuilder()
                .append("public static " + table.getClassName() + " findWithLock(String id) {\n")
                .append("    return find(Long.parseLong(id), true);\n")
                .append("}");

        this.addMethod(this.classMethods, content.toString(), "按id查找，参数为String类型(上锁)");

        // static methods -- find(Long[] ids)
        content = new StringBuilder()
                .append("public static " + table.getClassName() + "[] find(Long[] ids) {\n")
                .append("    return find(ids, false);\n")
                .append("}");

        this.addMethod(this.classMethods, content.toString(), "按id数组查找\n如果结果集包含的项数少于id数组的项数，则抛出异常");

        // static methods -- findWithLock(Long[] ids)
        content = new StringBuilder()
                .append("public static " + table.getClassName() + "[] findWithLock(Long[] ids) {\n")
                .append("    return find(ids, true);\n")
                .append("}");

        this.addMethod(this.classMethods, content.toString(), "按id数组查找(上锁)\n如果结果集包含的项数少于id数组的项数，则抛出异常");

        // static methods -- find(Long[] ids, boolean isLocked)
        content = new StringBuilder()
                .append("private static " + table.getClassName() + "[] find(Long[] ids, boolean isLocked) {\n")
                .append("    if(ids == null || ids.length == 0) throw new ModelException(\"id数组不能为null或者空\");\n")
                .append("\n")
                .append("    Map arg = new HashMap();\n")
                .append("    arg.put(\"ids\", ids);\n")
                .append("    if(isLocked)arg.put(\"forUpdate\", \"FOR UPDATE\");\n")
                .append("    List<" + table.getClassName() + "> result = getDao().query(\"" + table.getName().substring(1) + "E.select_by_ids\", arg);\n")
                .append("\n")
                .append("    if(result.size() != ids.length) {\n")
                .append("        throw new ModelException(\"结果集包含的项数少于id数组的项数\");\n")
                .append("    } else {\n")
                .append("        return result.toArray(new " + table.getClassName() + "[0]);\n")
                .append("    }\n")
                .append("}");

        this.addMethod(this.classMethods, content.toString(), "按id数组查找(可选择是否上锁)\n如果结果集包含的项数少于id数组的项数，则抛出异常");
        this.addImport("com.baosight.bssim.common.exception.ModelException");

        // static methods -- q(String sqlmap, Map arg)
        content = new StringBuilder()
                .append("public static CustomQuerier q(String sqlmap, Map arg) {\n")
                .append("    return new CustomQuerier(sqlmap, arg);\n")
                .append("}");

        this.addMethod(this.classMethods, content.toString(), "自定义查询");

        // static methods -- where(Map arg)
        content = new StringBuilder()
                .append("public static ModelQuerier where(Map arg) {\n")
                .append("    return new ModelQuerier(" + table.getQuoteFullTableName() + ").where(arg);\n")
                .append("}");

        this.addMethod(this.classMethods, content.toString(), "简单Where查询\n传入的参数最终会转换为 'key1 = value1 and key2 = value2 and ...' 这样形式的字符串");

        // static methods -- where(String where, Map arg)
        content = new StringBuilder()
                .append("public static ModelQuerier where(String where, Map arg) {\n")
                .append("    return new ModelQuerier(" + table.getQuoteFullTableName() + ").where(where, arg);\n")
                .append("}");

        this.addMethod(this.classMethods, content.toString(), "Where查询\nMap中的key和value做替换\n   { type: \"a\", name: \"b\" }\n   \"type = #type# or name like #name# || '%'\"\n=> \"type = 'a' or name like 'b' || ‘%’\"");

        // static methods -- getFullTableName
        content = new StringBuilder()
                .append("public String getFullTableName() {\n")
                .append("    return " + table.getQuoteFullTableName() + ";\n")
                .append("}");

        this.addMethod(this.classMethods, content.toString(), "得到对应数据表的全称");

        // static methods -- delete
        content = new StringBuilder()
                .append("public void delete() {\n")
                .append("    super.delete(" + table.getQuoteFullTableName() + ");\n")
                .append("}");

        this.addMethod(this.classMethods, content.toString(), "删除");

        // instanceMethods -- fromAttrs(Map attrs)
        content = new StringBuilder()
                .append("public void fromAttrs(Map attrs) {\n")
                .append(CodeHelper.indent(CodeHelper.concatFragments(table.getColumns(), "FromAttrs", "\n"))).append("\n")
                .append("}");

        this.addMethod(this.instanceMethods, content.toString(), "类似fromMap方法，但参数包含的数据类型必须是严格的");

        // instanceMethods -- toAttrs()
        content = new StringBuilder()
                .append("public Map toAttrs() {\n")
                .append("    Map result = new HashMap();\n")
                .append(CodeHelper.indent(CodeHelper.concatFragments(table.getColumns(), "ToAttrs", "\n"))).append("\n")
                .append("    return result;\n")
                .append("}");

        this.addMethod(this.instanceMethods, content.toString(), "类似toMap方法，但返回结果的数据类型是严格的");

        // instanceMethods -- noIdDuplicate
        content = new StringBuilder()
                .append("public " + table.getClassName() + " noIdDuplicate() {\n")
                .append("    " + table.getClassName() + " dup = new " + table.getClassName() + "();\n")
                .append("\n")
                .append("    Map attrs = this.toAttrs();\n")
                .append("    attrs.put(\"id\", new Long(0));\n")
                .append("    dup.fromAttrs(attrs);\n")
                .append("\n")
                .append("    dup.isInDB = false;\n")
                .append("    dup.isExtended = true;\n")
                .append("\n")
                .append("    return dup;\n")
                .append("}");

        this.addMethod(this.instanceMethods, content.toString(), "复制当前对象(id不被复制)");

        // instanceMethods -- toJson
        content = new StringBuilder()
                .append("public String toJson() {\n")
                .append("    return JSONObject.fromObject(toAttrs()).toString(4);\n")
                .append("}");

        this.addMethod(this.instanceMethods, content.toString(), "转换为Json字符串");
        this.addImport("net.sf.json.JSONObject");

        // instanceMethods -- fromJson
        content = new StringBuilder()
                .append("public void fromJson(String jsonstr) {\n")
                .append("    fromAttrs(JSONObject.fromObject(jsonstr));\n")
                .append("}");

        this.addMethod(this.instanceMethods, content.toString(), "toJson的反向操作");
        this.addImport("net.sf.json.JSONObject");

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

    public String toCode() {

        // generate code fragments
        generateBasic();
        generateIPlat4j();
        generateExtension();

        // combine fragments to full code
        StringBuilder result = new StringBuilder()
                .append(this.javaPackage).append("\n\n")                                   // package
                .append(StringUtils.join(this.imports.toArray(), "\n")).append("\n\n")     // import
                .append(this.classComment).append("\n")                                    // class comment
                .append("@SuppressWarnings({\"serial\", \"rawtypes\", \"unchecked\"})\n")  // @SuppressWarnings
                .append(this.className).append(" {\n")                                     // class name and modifier
                .append(CodeHelper.indent(StringUtils.join(new String[]{                   // class body
                        // attrs
                        StringUtils.join(this.attrs, "\n"),

                        // fieldsAndColumns
                        StringUtils.join(this.fieldsAndColumns, "\n\n"),

                        // constructors
                        StringUtils.join(this.constructorMethods, "\n\n"),

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
