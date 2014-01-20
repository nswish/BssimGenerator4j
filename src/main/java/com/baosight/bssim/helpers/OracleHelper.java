package com.baosight.bssim.helpers;

import com.baosight.bssim.DbUtils.OracleDataSourceFactory;
import com.baosight.bssim.exceptions.HelperException;
import com.baosight.bssim.exceptions.ModelException;
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
    public List<Map<String, Object>> queryTableList(String schemaName, String tableNames) {
        String sql = "SELECT v1.OWNER, v1.TABLE_NAME,\n" +
                "           (SELECT v2.COMMENTS FROM ALL_TAB_COMMENTS v2 WHERE v2.OWNER=v1.OWNER AND v2.TABLE_NAME=v1.TABLE_NAME) AS COMMENTS\n" +
                "          FROM ALL_TABLES v1\n" +
                "         WHERE v1.OWNER = UPPER(?)\n" +
                "           AND V1.TABLE_NAME IN (" + tableNames +")\n" +
                "         ORDER BY\n" +
                "               v1.TABLE_NAME\n";

        try {
            QueryRunner run = new QueryRunner(OracleDataSourceFactory.createDataSource());
            return run.query(sql, new MapListHandler(), schemaName);
        } catch (Exception e) {
            throw new HelperException(e.getMessage());
        }
    }

    @Override
    public ColumnModel[] createColumns(String schemaName, String tableName) {
        String sql = "SELECT T1.COLUMN_NAME, T1.DATA_TYPE, T1.DATA_LENGTH, T1.DATA_PRECISION, T1.DATA_SCALE, T1.NULLABLE, T2.COMMENTS\n" +
                     "FROM ALL_TAB_COLS T1, ALL_COL_COMMENTS T2 \n" +
                     "WHERE T1.OWNER = ? AND T1.TABLE_NAME = ? AND T1.OWNER = T2.OWNER AND T1.TABLE_NAME = T2.TABLE_NAME AND T1.COLUMN_NAME = T2.COLUMN_NAME\n" +
                     "ORDER BY T1.COLUMN_ID";

        try {
            QueryRunner run = new QueryRunner(OracleDataSourceFactory.createDataSource());
            List<Map<String, Object>> result = run.query(sql, new MapListHandler(), schemaName, tableName);

            ColumnModel[] columns = new ColumnModel[result.size()];

            for(int i=0; i<columns.length; i++){
                Map row = result.get(i);

                columns[i] = new ColumnModel();
                columns[i].setName(row.get("COLUMN_NAME")+"");
                columns[i].setDbType(row.get("DATA_TYPE") + "");
                columns[i].setComment(row.get("COMMENTS") + "");

                if("N".equals(row.get("NULLABLE"))){
                    columns[i].setNullable(false);
                } else {
                    columns[i].setNullable(true);
                }

                if ((row.get("DATA_TYPE")+"").startsWith("VARCHAR")){
                    columns[i].setType("C");
                    columns[i].setLength(Integer.parseInt(row.get("DATA_LENGTH")+""));
                } else if("NUMBER".equals(row.get("DATA_TYPE"))) {
                    columns[i].setType("N");
                    columns[i].setLength(Integer.parseInt(row.get("DATA_PRECISION")+""));
                    columns[i].setScale(Integer.parseInt(row.get("DATA_SCALE")+""));
                }
            }

            return columns;
        } catch (Exception e) {
            throw new ModelException(e.getMessage());
        }
    }
}
