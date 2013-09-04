package com.baosight.bssim.models;

import com.baosight.bssim.helpers.CodeHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class JavaModel {
    private String javaPackage;
    private Set imports = new LinkedHashSet(5);
    private String className;
    private String classComment;
    private List fields = new ArrayList(15);
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

        this.classComment = "/**\n" +
                " * " + table.getClassName() + "\n" +
                " * table comment : \n" +
                " * generate date : " + DateFormatUtils.format(new Date(), "y-MM-dd") + "\n" +
                " */";
    }

    public void generateIPlat4j() {
        // fields
        for(int i=0; i<table.getColumns().length; i++) {
            ColumnModel column = table.getColumns()[i];

            this.addField("private " + column.getJavaType() + " " + column.getCamelName() + " = " + column.getDefaultValue(), column.getComment());
            if ("BigDecimal".equals(column.getJavaType()))
                this.addImport("java.math.BigDecimal");
        }

        // constructor
        this.addConstructorMethod("    public Tsasa01() {\n" +
                "        initMetaData();\n" +
                "    }", "the constructor");

        // instanceMethods -- accessors
        for(int i=0; i<table.getColumns().length; i++) {
            ColumnModel column = table.getColumns()[i];

            String comment = "get the " + column.getCamelName() + " - " + column.getComment() + "\n"
                    + "@return the " + column.getCamelName();
            String method = "    public String " + column.getGetterName() + "() {\n" +
                    "        return this." + column.getCamelName() + ";\n" +
                    "    }";
            this.addInstanceMethod(method, comment);

            comment = "set the " + column.getCamelName() + " - " + column.getComment();
            method = "    public void " + column.getSetterName() + "(" + column.getJavaType() + " " + column.getCamelName() + ") {\n" +
                    "        if(this.isInDB)taintedAttrs.put(" + column.getQuoteCamelName() + ", " + column.getCamelName() + ");\n" +
                    "        this." + column.getCamelName() + " = " + column.getCamelName() + ";\n" +
                    "    }";
            this.addInstanceMethod(method, comment);
        }

        // instanceMethods -- metadata
        StringBuilder method = new StringBuilder();
        String[] items = new String[table.getColumns().length];

        for(int i=0; i<table.getColumns().length; i++) {
            StringBuilder result = new StringBuilder();
            ColumnModel column = table.getColumns()[i];

            result.append("        eiColumn = new EiColumn(" + column.getCamelName() + ");\n")
                  .append("        eiColumn.setDescName(" + column.getQuoteComment() + ");\n")
                  .append("        eiColumn.setFieldLength(" + column.getLength() + ");\n");

            if ("N".equals(column.getType())){
                result.append("        eiColumn.setType(\"N\");\n")
                      .append("        eiColumn.setScaleLength(" + column.getScale() + ");\n");
            }

            result.append("        eiMetadata.addMeta(eiColumn);");

            items[i] = result.toString();
        }

        method.append("    public void initMetaData() {\n")
                .append("        EiColumn eiColumn;\n\n")
                .append(StringUtils.join(items, "\n\n")).append("\n")
                .append("    }");

        this.addInstanceMethod(method.toString(), "initialize the metadata");
        this.addImport("com.baosight.iplat4j.core.ei.EiColumn");

        // instanceMethods -- fromMap
        method = new StringBuilder();
        items = new String[table.getColumns().length];

        for(int i=0; i<table.getColumns().length; i++) {
            ColumnModel column = table.getColumns()[i];

            if ("C".equals(column.getType())) {
                items[i] = "        " + column.getSetterName() + "(StringUtils.defaultIfEmpty(StringUtils.toString(map.get(" + column.getQuoteCamelName() + ")), " + column.getCamelName() + "));";
            } else if("N".equals(column.getType())) {
                items[i] = "        " + column.getSetterName() + "(NumberUtils.to" + column.getJavaType() + "(StringUtils.toString(map.get(" + column.getQuoteCamelName() + ")), " + column.getCamelName() + "));";
            } else {
                items[i] = "";
            }
        }

        method.append("    public void fromMap(Map map) {\n")
                .append(StringUtils.join(items, "\n")).append("\n")
                .append("    }");

        this.addInstanceMethod(method.toString(), "get the value from Map");
        this.addImport("java.util.Map");
        this.addImport("com.baosight.iplat4j.util.StringUtils");
        this.addImport("com.baosight.iplat4j.util.NumberUtils");

        // instanceMethods -- toMap
        method = new StringBuilder();
        items = new String[table.getColumns().length];

        for(int i=0; i<table.getColumns().length; i++) {
            ColumnModel column = table.getColumns()[i];
            items[i] = "        map.put(" + column.getQuoteCamelName() + ", StringUtils.toString(recCreator, eiMetadata.getMeta(" + column.getQuoteCamelName() + ")));";
        }

        method.append("    public Map toMap() {\n")
              .append("        Map map = new HashMap();\n")
              .append(StringUtils.join(items, "\n")).append("\n")
              .append("    }");

        this.addInstanceMethod(method.toString(), "set the value to Map");
    }

    public void generateExtension() {
        JSONObject configJson = this.table.getTableConfig().getJson();

        // fieldsAndColumns -- fields
        StringBuilder content = new StringBuilder();

        String[] fragments = new String[this.table.getColumns().length];
        for (int i=0; i<table.getColumns().length; i++){
            ColumnModel column = table.getColumns()[i];
            fragments[i] = column.fragmentForField();
        }

        content.append("public static class fields {\n").append(CodeHelper.indent(StringUtils.join(fragments, "\n"))).append("\n}");

        this.addfieldsAndColumns(CodeHelper.indent(content.toString()), "按驼峰命名规则的字段名称");

        // fieldsAndColumns -- columns
        content = new StringBuilder();

        fragments = new String[this.table.getColumns().length];
        for (int i=0; i<table.getColumns().length; i++){
            ColumnModel column = table.getColumns()[i];
            fragments[i] = column.fragmentForColumn();
        }

        content.append("public static class columns {\n").append(CodeHelper.indent(StringUtils.join(fragments, "\n"))).append("\n}");

        this.addfieldsAndColumns(CodeHelper.indent(content.toString()), "按下划线命名规则的字段名称");

        // static methods -- newInstance
        content = new StringBuilder();
        content.append("public static " + table.getClassName() + " newInstance() {\n")
               .append("    " + table.getClassName() + " result = new " + table.getClassName() + "();\n")
               .append("    result.isExtended = true;\n")
               .append("    return result;\n")
               .append("}");

        this.addClassMethod(CodeHelper.indent(content.toString()), "创建新的实例");

        // static methods -- newInstance(Map attr)
        content = new StringBuilder();
        content.append("public static " + table.getClassName() + " newInstance(Map attr) {\n")
                .append("    " + table.getClassName() + " result = newInstance();\n")
                .append("    result.fromMap(attr);\n")
                .append("    return result;\n")
                .append("}");

        this.addClassMethod(CodeHelper.indent(content.toString()), "创建新的实例(属性初始化为传入的参数)");

        // static methods -- find(Long id)
        content = new StringBuilder();
        content.append("public static " + table.getClassName() + " find(Long id) {\n")
               .append("    Map arg = new HashMap();\n")
               .append("    arg.put(\"id\", id);\n")
               .append("    List<" + table.getClassName() + "> result = getDao().query(\"" + table.getTableName().substring(1) + "E.select_by_id\", arg);\n")
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

        this.addClassMethod(CodeHelper.indent(content.toString()), "按id查找");
        this.addImport("java.util.HashMap");
        this.addImport("java.util.List");

        // static methods -- find(String id)
        content = new StringBuilder();
        content.append("public static " + table.getClassName() + " find(String id) {\n" +
                "    return find(Long.parseLong(id));\n" +
                "}");

        this.addClassMethod(CodeHelper.indent(content.toString()), "按id查找，参数为String类型");

        // static methods -- find(Long[] ids)
        content = new StringBuilder();
        content.append("public static " + table.getClassName() + "[] find(Long[] ids) {\n")
               .append("    if(ids == null || ids.length == 0) throw new ModelException(\"id数组不能为null或者空\");\n")
               .append("\n")
               .append("    Map arg = new HashMap();\n")
               .append("    arg.put(\"ids\", ids);\n")
               .append("    List<" + table.getClassName() + "> result = getDao().query(\"" + table.getTableName().substring(1) + "E.select_by_ids\", arg);\n")
               .append("\n")
               .append("    if(result.size() != ids.length) {\n")
               .append("        throw new ModelException(\"结果集包含的项数少于id数组的项数\");\n")
               .append("    } else {\n")
               .append("        return result.toArray(new " + table.getClassName() + "[0]);\n")
               .append("    }\n")
               .append("}");

        this.addClassMethod(CodeHelper.indent(content.toString()), "按id数组查找\n如果结果集包含的项数少于id数组的项数，则抛出异常");
        this.addImport("com.baosight.bssim.common.exception.ModelException");

        // static methods -- q(String sqlmap, Map arg)
        content = new StringBuilder();
        content.append("public static CustomQuerier q(String sqlmap, Map arg) {\n")
               .append("    return new CustomQuerier(sqlmap, arg);\n")
               .append("}");

        this.addClassMethod(CodeHelper.indent(content.toString()), "自定义查询");

        // static methods -- where(Map arg)
        content = new StringBuilder();
        content.append("public static ModelQuerier where(Map arg) {\n")
               .append("    return new ModelQuerier(" + table.getQuoteFullName() + ").where(arg);\n")
               .append("}");

        this.addClassMethod(CodeHelper.indent(content.toString()), "简单Where查询\n传入的参数最终会转换为 'key1 = value1 and key2 = value2 and ...' 这样形式的字符串");

        // static methods -- where(String where, Map arg)
        content = new StringBuilder();
        content.append("public static ModelQuerier where(String where, Map arg) {\n")
               .append("    return new ModelQuerier(" + table.getQuoteFullName() + ").where(where, arg);\n")
               .append("}");

        this.addClassMethod(CodeHelper.indent(content.toString()), "Where查询\nMap中的key和value做替换\n   { type: \"a\", name: \"b\" }\n   \"type = #type# or name like #name# || '%'\"\n=> \"type = 'a' or name like 'b' || ‘%’\"");

        // static methods -- getFullTableName
        content = new StringBuilder();
        content.append("public String getFullTableName() {\n")
               .append("    return " + table.getQuoteFullName() + ";\n")
               .append("}");

        this.addClassMethod(CodeHelper.indent(content.toString()), "得到对应数据表的全称");

        // static methods -- delete
        content = new StringBuilder();
        content.append("public void delete() {\n")
               .append("    super.delete(" + table.getQuoteFullName() + ");\n")
               .append("}");

        this.addClassMethod(CodeHelper.indent(content.toString()), "删除");

        // instanceMethods -- fromAttrs(Map attrs)
        content = new StringBuilder();

        fragments = new String[this.table.getColumns().length];
        for (int i=0; i<table.getColumns().length; i++){
            ColumnModel column = table.getColumns()[i];
            fragments[i] = column.fragmentForFromAttrs();
        }

        content.append("public void fromAttrs(Map attrs) {\n")
               .append(CodeHelper.indent(StringUtils.join(fragments, "\n"))).append("\n")
               .append("}");

        this.addInstanceMethod(CodeHelper.indent(content.toString()), "类似fromMap方法，但参数包含的数据类型必须是严格的");

        // instanceMethods -- toAttrs()
        content = new StringBuilder();

        fragments = new String[this.table.getColumns().length];
        for (int i=0; i<table.getColumns().length; i++){
            ColumnModel column = table.getColumns()[i];
            fragments[i] = column.fragmentForToAttrs();
        }

        content.append("public Map toAttrs() {\n")
               .append("    Map result = new HashMap();\n")
               .append(CodeHelper.indent(StringUtils.join(fragments, "\n"))).append("\n")
               .append("    return result;\n")
               .append("}");

        this.addInstanceMethod(CodeHelper.indent(content.toString()), "类似toMap方法，但返回结果的数据类型是严格的");

        // instanceMethods -- noIdDuplicate
        content = new StringBuilder();
        content.append("public " + table.getClassName() + " noIdDuplicate() {\n")
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

        this.addInstanceMethod(CodeHelper.indent(content.toString()), "复制当前对象(id不被复制)");

        // instanceMethods -- toJson
        content = new StringBuilder();
        content.append("public String toJson() {\n")
               .append("    return JSONObject.fromObject(toAttrs()).toString(4);\n")
               .append("}");

        this.addInstanceMethod(CodeHelper.indent(content.toString()), "转换为Json字符串");
        this.addImport("net.sf.json.JSONObject");

        // instanceMethods -- fromJson
        content = new StringBuilder();
        content.append("public void fromJson(String jsonstr) {\n")
                .append("    fromAttrs(JSONObject.fromObject(jsonstr));\n")
                .append("}");

        this.addInstanceMethod(CodeHelper.indent(content.toString()), "toJson的反向操作");
        this.addImport("net.sf.json.JSONObject");

        // instanceMethods -- 关联 belongs_to
        JSONArray belongsTo = configJson.optJSONArray("belongs_to");
        if (belongsTo != null) {
            for(int i=0; i<belongsTo.length(); i++) {
                TableModel anotherOne = new TableModel(belongsTo.getString(i));

                content = new StringBuilder();
                content.append("public " + anotherOne.getClassName() + " " + anotherOne.getClassName().toLowerCase() +"() {\n")
                       .append("    return " + anotherOne.getClassName() + ".find(this." + anotherOne.getClassName().toLowerCase() + "Id);\n")
                       .append("}");

                this.addInstanceMethod(CodeHelper.indent(content.toString()), "关联 ");
            }
        }

        // instanceMethods -- 关联 has_many
        JSONArray hasMany = configJson.optJSONArray("has_many");
        if (hasMany != null) {
            for(int i=0; i<hasMany.length(); i++) {
                TableModel anotherOne = new TableModel(hasMany.getString(i));

                content = new StringBuilder();
                content.append("public ModelQuerier " + anotherOne.getClassName().toLowerCase() + "s() {\n")
                       .append("    Map arg = new HashMap();\n")
                       .append("    arg.put(\"" + table.getClassName().toLowerCase() + "Id\", this.id);\n")
                       .append("    return new ModelQuerier(\"" + anotherOne.getFullName() + "\", \"" + table.getTableName() + "_ID = #" + table.getTableName().toLowerCase() + "Id#\", arg);\n")
                       .append("}");

                this.addInstanceMethod(CodeHelper.indent(content.toString()), "关联 ");
            }
        }

        // instanceMethods -- 关联 has_one
        JSONArray hasOne = configJson.optJSONArray("has_one");
        if (hasOne != null) {
            for(int i=0; i<hasOne.length(); i++) {
                TableModel anotherOne = new TableModel(hasOne.getString(i));

                content = new StringBuilder();
                content.append("public " + anotherOne.getClassName() + " " + anotherOne.getTableName().toLowerCase() + "() {\n")
                       .append("    Map arg = new HashMap();\n")
                       .append("    arg.put(\"" + table.getTableName().toLowerCase() + "Id\", this.id);\n")
                       .append("    return " + anotherOne.getClassName() + ".where(arg).first();\n")
                       .append("}");

                this.addInstanceMethod(CodeHelper.indent(content.toString()), "关联 ");
            }
        }
    }

    public String toCode() {
        StringBuilder result = new StringBuilder();

        generateBasic();
        generateIPlat4j();
        generateExtension();

        // package
        result.append(this.javaPackage).append("\n\n");

        // import
        Iterator iter = this.imports.iterator();
        while (iter.hasNext()){
            String imp = iter.next()+"";
            result.append(imp).append("\n");
        }
        if (this.imports.size() > 0)result.append("\n");

        // class begin
        result.append(this.classComment).append("\n");                                   // class comment
        result.append("@SuppressWarnings({\"serial\", \"rawtypes\", \"unchecked\"})\n"); // fixed annotation
        result.append(this.className).append(" {\n");                                    // class name and modifier

        result.append(StringUtils.join(new String[]{

            // fields
            StringUtils.join(this.fields, "\n"),

            // fieldsAndColumns
            StringUtils.join(this.fieldsAndColumns, "\n\n"),

            // constructors
            StringUtils.join(this.constructorMethods, "\n\n"),

            // instanceMethods
            StringUtils.join(this.instanceMethods, "\n\n"),

            // classMethods
            StringUtils.join(this.classMethods, "\n\n")

        }, "\n\n")).append("\n");

        // class end
        result.append("}");

        return result.toString();
    }

    public void addImport(String imp) {
        this.imports.add("import " + imp + ";");
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void addClassComment(String comment) {
        this.classComment = comment;
    }

    public void addField(String field, String comment) {
        this.fields.add("    " + field + ";" + "    /* " + comment +" */");
    }

    public void addInstanceMethod(String method, String comment) {
        this.instanceMethods.add(mergeContentAndComment(method, comment));
    }

    public void addConstructorMethod(String method, String comment) {
        this.constructorMethods.add(mergeContentAndComment(method, comment));
    }

    public void addClassMethod(String method, String comment) {
        this.classMethods.add(mergeContentAndComment(method, comment));
    }

    private String mergeContentAndComment(String method, String comment) {
        StringBuilder result = new StringBuilder();

        result.append("    /**\n");
        result.append(("\n"+comment).replaceAll("\n", "\n     * ").substring(1)).append("\n");
        result.append("     */\n");

        return result.append(method).toString();
    }

    public void addfieldsAndColumns(String content, String comment) {
        this.fieldsAndColumns.add(mergeContentAndComment(content, comment));
    }
}
