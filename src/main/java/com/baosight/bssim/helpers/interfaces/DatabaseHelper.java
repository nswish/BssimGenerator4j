package com.baosight.bssim.helpers.interfaces;

import com.baosight.bssim.models.ColumnModel;

import java.util.List;
import java.util.Map;

public interface DatabaseHelper {
    /**
     * 查询数据表的注释
     */
    public String queryTableComment(String schemaName, String tableName);

    /**
     * 查询数据表清单
     */
    public List<Map<String, Object>> queryTableList(String schemaName, String tableNames);

    /**
     * 创建数据表的字段
     */
    public ColumnModel[] createColumns(String schemaName, String tableName);
}
