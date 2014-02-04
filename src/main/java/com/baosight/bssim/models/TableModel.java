package com.baosight.bssim.models;

import com.baosight.bssim.exceptions.ModelException;
import com.baosight.bssim.helpers.DatabaseHelperFactory;
import com.baosight.bssim.helpers.interfaces.DatabaseHelper;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 映射数据表
 */
public class TableModel {
    private ColumnModel[] columns;
    private Map<String, Object> meta = new HashMap<String, Object>();

    private DatabaseHelper helper = DatabaseHelperFactory.newInstance();
    private ConfigModel tableConfig;

    /**
     * 从JSON中创建Model
     */
    public static TableModel newInstance(String json) {
        TableModel model = new TableModel();
        model.meta = new Gson().fromJson(json, HashMap.class);
        model.tableConfig = new ConfigModel(model.getFullName());
        return model;
    }

    private TableModel() {

    }

    /**
     * 全名 = 模式名 + '.' + 表名
     *
     * 输入参数的大小写不敏感，但是程序内部统一按照大写处理
     */
    public TableModel(String fullName) {
        if (StringUtils.isBlank(fullName)) {
            throw new ModelException("全名不可以为空！");
        }

        String[] tokens = fullName.split("\\.");

        if (tokens.length < 2) {
            throw new ModelException("错误的全名: "+fullName);
        }

        init(tokens[0], tokens[1]);
    }

    /**
     * 分别给出模式名和表名
     *
     * 输入参数的大小写不敏感，但是程序内部统一按照大写处理
     */
    public TableModel(String schemaName, String tableName) {
        init(schemaName, tableName);
    }

    /**
     * 初始化
     */
    private void init(String schemaName, String tableName) {
        if (StringUtils.isBlank(schemaName)){
            throw new ModelException("模式名称不可以为空！");
        }

        if (StringUtils.isBlank(tableName)) {
            throw new ModelException("表名不可以为空！");
        }

        meta.put("schema", schemaName.trim().toUpperCase());
        meta.put("name", tableName.trim().toUpperCase());
        meta.put("fullName", meta.get("schema")+"."+meta.get("name"));

        this.tableConfig = new ConfigModel(getFullName());
    }

    /**
     * 表的注释名称
     */
    public String getComment() {
        if (meta.get("comment") == null){
            meta.put("comment", this.helper.queryTableComment(getSchema(), getName()));
        }

        return meta.get("comment")+"";
    }

    /**
     * 获取表的最后修改时间(yyyy-MM-dd HH:mm:ss)
     */
    public String getLastModifiedTime() {
        if (meta.get("lastModifiedTime") == null) {
            Date lastModifiedTime = this.helper.queryTableLastModifiedTime(getSchema(), getName());
            this.meta.put("lastModifiedTime", DateFormatUtils.format(lastModifiedTime, "yyyy-MM-dd HH:mm:ss"));
        }

        return meta.get("lastModifiedTime")+"";
    }

    /**
     * 字段
     */
    public ColumnModel[] getColumns() {
        if(meta.get("columns") == null){
            meta.put("columns", this.helper.queryTableColumns(getSchema(), getName()));
            this.columns = null;  // 重置
        }

        if (this.columns == null) {
            List<Map> columns = (List<Map>)meta.get("columns");

            this.columns = new ColumnModel[columns.size()];

            for (int i=0; i<columns.size(); i++) {
                this.columns[i] = new ColumnModel(columns.get(i));
            }
        }

        return this.columns;
    }

    /**
     * 表的全名，大写
     */
    public String getFullName() {
        return meta.get("fullName")+"";
    }

    /**
     * 带引号的表的全名，大写
     */
    public String getQuoteFullTableName() {
        return "\"" + getFullName() + "\"";
    }

    /**
     * 表的模式名，大写
     */
    public String getSchema() {
        return meta.get("schema")+"";
    }

    /**
     * 表的名称，大写
     */
    public String getName() {
        return meta.get("name")+"";
    }

    /**
     * 生成的Java类的package路径
     */
    public String getPackage() {
        return "com.baosight.bssim." + getFirstModuleName().toLowerCase()
                + (StringUtils.isBlank(getSecondModuleName()) ? "" : "." + getSecondModuleName().toLowerCase())
                + ".domain.model";
    }

    /**
     * 一级模块名
     *
     * 取表名的第二、第三位
     */
    public String getFirstModuleName() {
        return getName().substring(1, 3);
    }

    /**
     * 二级模块名
     *
     * 取表名的第四、第五位
     * 如果第四位不是字母开头，则视为没有二级模块，返回空字符串
     */
    public String getSecondModuleName() {
        if(StringUtils.isAlpha(getName().substring(3, 4))){
            return this.getName().substring(3, 5);
        } else {
            return "";
        }
    }

    /**
     * 类名
     */
    public String getClassName() {
        return StringUtils.capitalize(getName().toLowerCase());
    }

    /**
     * 字段(不包含ID)
     */
    public ColumnModel[] getColumnsWithoutId() {
        ColumnModel[] columns = getColumns();

        ColumnModel[] result = new ColumnModel[columns.length - 1];
        for (int i=0, j=0; i<columns.length; i++) {
            if (!"ID".equals(columns[i].getName())){
                result[j++] = columns[i];
            }
        }
        return result;
    }

    public ConfigModel getTableConfig() {
        return this.tableConfig;
    }

    /**
     * Java 类的全名
     */
    public String getFullClassName() {
        return getPackage() + "." + getClassName();
    }

    /**
     * 生成 Java 代码
     */
    public String genJavaCode() {
        checkRequirement();
        return new JavaModel(this).toCode();
    }

    /**
     * 生成 Xml 代码
     */
    public String genXmlCode() {
        checkRequirement();
        return new XmlModel(this).toCode();
    }

    /**
     * Java 文件的存放路径
     */
    public String getJavaPath() {
        return "com" + File.separator + "baosight" + File.separator + "bssim" + File.separator
                + getFirstModuleName().toLowerCase()
                + (StringUtils.isBlank(getSecondModuleName()) ? "" : File.separator + getSecondModuleName().toLowerCase())
                + File.separator + "domain" + File.separator + "model" + File.separator + getClassName()+ ".java";
    }

    /**
     * Xml 文件的存放路径
     */
    public String getXmlPath() {
        return "com" + File.separator + "baosight" + File.separator + "bssim" + File.separator
                + getFirstModuleName().toLowerCase()
                + (StringUtils.isBlank(getSecondModuleName()) ? "" : File.separator + getSecondModuleName().toLowerCase())
                + File.separator + "sql" + File.separator + getName().substring(1) + "E.xml";
    }

    /**
     * 得到表结构的Json对象
     *
     * 表结构的Json表示:
     *
     * {
     *      name: [string, upcase],
     *      schema: [string, upcase],
     *      fullName: [=shema.name, string, upcase],
     *      comment: [string],
     *      lastModifiedTime: [string],
     *      columns: [
     *          {
     *              name: [string, upcase],
     *              comment: [string],
     *              dbType: [string],
     *              type:   [string, upcase],
     *              length: [number],
     *              scale: [number],
     *              nullable: [boolean]
     *          }
     *      ]
     * }
     */
    public Map getMeta() {
        getComment();
        getLastModifiedTime();
        getColumns();

        return this.meta;
    }

    /**
     * 检查是否具备代码生成的条件
     */
    private void checkRequirement() {
        boolean idFlag = false;
        for (int i=0; i<getColumns().length; i++) {
            ColumnModel column = getColumns()[i];
            if ("ID".equals(column.getName())){
                idFlag = true;
                break;
            }
        }

        if (!idFlag) {
            throw new ModelException("因为数据表[" + this.getFullName() + "]没有ID字段，所以无法正确生成代码！");
        }
    }
}
