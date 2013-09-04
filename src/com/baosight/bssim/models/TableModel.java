package com.baosight.bssim.models;

import com.baosight.bssim.DbUtils.OracleDataSourceFactory;
import com.baosight.bssim.exceptions.ModelException;
import com.baosight.bssim.helpers.interfaces.DatabaseHelper;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        JSONObject config = new ConfigModel("GlobalConfig").getJson();
        String database = config.getString("database") == null ? "oracle" : config.getString("database");
        String clazz = "com.baosight.bssim.helpers." + StringUtils.capitalize(database) + "Helper";

        try{
            this.helper = (DatabaseHelper)Class.forName(clazz).newInstance();
        } catch (Exception ex) {
            throw new ModelException("未找到类: "+clazz);
        }

        this.queryTableComment();
        this.createColumns();

        this.tableConfig = new ConfigModel(getFullName());
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
        String sql = "SELECT COLUMN_NAME, DATA_TYPE, DATA_LENGTH, DATA_PRECISION, DATA_SCALE, DATA_TYPE, NULLABLE FROM ALL_TAB_COLS " +
                     "WHERE OWNER = ? AND TABLE_NAME = ? ORDER BY COLUMN_ID";

        try {
            QueryRunner run = new QueryRunner(OracleDataSourceFactory.createDataSource());
            List<Map<String, Object>> result = run.query(sql, new MapListHandler(), this.schemaName, this.tableName);

            this.columns = new ColumnModel[result.size()];

            for(int i=0; i<this.columns.length; i++){
                Map row = result.get(i);

                this.columns[i] = new ColumnModel();
                this.columns[i].setName(row.get("COLUMN_NAME")+"");
                this.columns[i].setDbType(row.get("DATA_TYPE")+"");

                if("N".equals(row.get("NULLABLE"))){
                    this.columns[i].setNullable(false);
                } else {
                    this.columns[i].setNullable(true);
                }

                if ((row.get("DATA_TYPE")+"").startsWith("VARCHAR")){
                    this.columns[i].setType("C");
                    this.columns[i].setLength(Integer.parseInt(row.get("DATA_LENGTH")+""));
                } else if("NUMBER".equals(row.get("DATA_TYPE"))) {
                    this.columns[i].setType("N");
                    this.columns[i].setLength(Integer.parseInt(row.get("DATA_PRECISION")+""));
                    this.columns[i].setScale(Integer.parseInt(row.get("DATA_SCALE")+""));
                }
            }

            sql = "SELECT COLUMN_NAME, COMMENTS FROM ALL_COL_COMMENTS  WHERE OWNER = ? AND TABLE_NAME = ?";
            result = run.query(sql, new MapListHandler(), this.schemaName, this.tableName);
            Map nameCommentMap = new HashMap();

            for(int i=0; i<result.size(); i++){
                nameCommentMap.put(result.get(i).get("COLUMN_NAME")+"", result.get(i).get("COMMENTS")+"");
            }

            for(int i=0; i<this.columns.length; i++){
                this.columns[i].setComment(nameCommentMap.get(this.columns[i].getName())+"");
            }
        } catch (Exception e) {
            throw new ModelException(e.getMessage());
        }

    }

    /**
     * 表的全名，大写
     */
    public String getFullName() {
        return this.schemaName + "." + this.tableName;
    }

    /**
     * 带引号的表的全名，大写
     */
    public String getQuoteFullName() {
        return "\"" + getFullName() + "\"";
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
        return this.columns;
    }

    /**
     * 字段(不包含ID)
     */
    public ColumnModel[] getColumnsWithoutId() {
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
     * 生成 Java 代码
     */
    public String genJavaCode() {
        return this.javaModel.toCode();
    }

    /**
     * 生成 Xml 代码
     */
    public String genXmlCode() {
        return this.xmlModel.toCode();
    }
}
