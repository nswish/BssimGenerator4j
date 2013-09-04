package com.baosight.bssim.helpers.interfaces;

import com.baosight.bssim.models.ColumnModel;

public interface DatabaseHelper {
    /**
     * 查询数据表的注释
     */
    public String queryTableComment(String schemaName, String tableName);

    /**
     * 创建数据表的字段
     */
    public ColumnModel[] createColumns(String schemaName, String tableName);
}
