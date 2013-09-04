package com.baosight.bssim.helpers;

import com.baosight.bssim.DbUtils.OracleDataSourceFactory;
import com.baosight.bssim.exceptions.HelperException;
import com.baosight.bssim.helpers.interfaces.DatabaseHelper;
import com.baosight.bssim.models.ColumnModel;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ns
 * Date: 13-9-4
 * Time: 下午3:14
 * To change this template use File | Settings | File Templates.
 */
public class OracleHelper implements DatabaseHelper {
    @Override
    public String queryTableComment(String schemaName, String tableName) {
        String sql = "SELECT COMMENTS FROM ALL_TAB_COMMENTS WHERE OWNER = ? AND TABLE_NAME = ?";

        try {
            QueryRunner run = new QueryRunner(OracleDataSourceFactory.createDataSource());
            List<Map<String, Object>> result = run.query(sql, new MapListHandler(), schemaName, tableName);

            if (result.size() == 0) {
                throw new HelperException("数据表的注释信息不存在！");
            }

            return result.get(0).get("COMMENTS").toString();
        } catch (Exception e) {
            throw new HelperException(e.getMessage());
        }
    }

    @Override
    public ColumnModel[] createColumns(String schemaName, String tableName) {
        return new ColumnModel[0];  //To change body of implemented methods use File | Settings | File Templates.
    }
}
