package com.baosight.bssim.DbUtils;

import com.baosight.bssim.models.ConfigModel;
import oracle.jdbc.pool.OracleDataSource;
import org.json.JSONObject;

import javax.sql.DataSource;
import java.sql.SQLException;

public class OracleDataSourceFactory {
    public static DataSource createDataSource() throws SQLException {
        JSONObject configJson = new ConfigModel("GlobalConfig").getJson();
        String URL = "jdbc:oracle:thin:@//"+configJson.get("database_ip")+":"+configJson.get("database_port")+"/"+configJson.get("database_service");

        OracleDataSource ods = new OracleDataSource();
        ods.setURL(URL);
        ods.setUser(configJson.getString("database_username"));
        ods.setPassword(configJson.getString("database_password"));

        return ods;
    }
}
