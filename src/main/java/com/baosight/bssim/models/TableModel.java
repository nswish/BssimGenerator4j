package com.baosight.bssim.models;

import com.baosight.bssim.exceptions.ModelException;
import com.baosight.bssim.helpers.DatabaseHelperFactory;
import com.baosight.bssim.helpers.interfaces.DatabaseHelper;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * 映射数据表
 */
public class TableModel {
    private String schemaName;
    private String tableName;
    private String comment;
    private ColumnModel[] columns;

    private DatabaseHelper helper;
    private JavaModel javaModel;
    private XmlModel xmlModel;
    private ConfigModel tableConfig;

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

        this.schemaName = schemaName.trim().toUpperCase();
        this.tableName = tableName.trim().toUpperCase();

        this.helper = DatabaseHelperFactory.newInstance();

        this.queryTableComment();

        this.tableConfig = new ConfigModel(getFullTableName());
        this.javaModel = new JavaModel(this);
        this.xmlModel = new XmlModel(this);
    }

    /**
     * 获取表的注释名称
     */
    private void queryTableComment() {
        this.comment = this.helper.queryTableComment(this.schemaName, this.tableName);
    }

    /**
     * 构造字段对象
     */
    private void createColumns() {
        this.columns = this.helper.createColumns(this.schemaName, this.tableName);
    }

    /**
     * 表的全名，大写
     */
    public String getFullTableName() {
        return this.schemaName + "." + this.tableName;
    }

    /**
     * 带引号的表的全名，大写
     */
    public String getQuoteFullTableName() {
        return "\"" + getFullTableName() + "\"";
    }

    /**
     * 表的模式名，大写
     */
    public String getSchemaName() {
        return this.schemaName;
    }

    /**
     * 表的名称，大写
     */
    public String getTableName() {
        return this.tableName;
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
        return this.tableName.substring(1, 3);
    }

    /**
     * 二级模块名
     *
     * 取表名的第四、第五位
     * 如果第四位不是字母开头，则视为没有二级模块，返回空字符串
     */
    public String getSecondModuleName() {
        if(StringUtils.isAlpha(this.tableName.substring(3, 4))){
            return this.tableName.substring(3, 5);
        } else {
            return "";
        }
    }

    /**
     * 类名
     */
    public String getClassName() {
        return StringUtils.capitalize(this.tableName.toLowerCase());
    }

    /**
     * 表的注释名称
     */
    public String getComment() {
        return this.comment;
    }

    /**
     * 字段
     */
    public ColumnModel[] getColumns() {
        if (this.columns == null)
            this.createColumns();
        return this.columns;
    }

    /**
     * 字段(不包含ID)
     */
    public ColumnModel[] getColumnsWithoutId() {
        if (this.columns == null)
            this.createColumns();

        ColumnModel[] result = new ColumnModel[this.columns.length - 1];
        for (int i=0, j=0; i<this.columns.length; i++) {
            if (!"ID".equals(this.columns[i].getName())){
                result[j++] = this.columns[i];
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
        return this.javaModel.toCode();
    }

    /**
     * 生成 Xml 代码
     */
    public String genXmlCode() {
        checkRequirement();
        return this.xmlModel.toCode();
    }

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
            throw new ModelException("因为数据表[" + this.getFullTableName() + "]没有ID字段，所以无法正确生成代码！");
        }
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
                + File.separator + "sql" + File.separator + getTableName().substring(1) + "E.xml";
    }
}
