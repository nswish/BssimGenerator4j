package com.baosight.bssim.helpers.interfaces;

import com.baosight.bssim.models.ColumnModel;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface DatabaseHelper {
    /**
     * 查询数据表的注释
     */
    public String queryTableComment(String schemaName, String tableName);

    /**
     * 查询数据表的最后修改时间(精确到秒)
     */
    public Date queryTableLastModifiedTime(String schemaName, String tableName);

    /**
     *  查询数据表的所有字段定义
     *
     *  [{
     *      name:     [string, upcase],
     *      comment:  [string],
     *      dbType:   [string],
     *      length:   [number],
     *      scale:    [number]
     *  }]
     */
    public List queryTableColumns(String schemaName, String tableName);

    /**
     * 查询数据表清单
     */
    public List<Map<String, Object>> queryTableList(String schemaName, String tableNames);

}
