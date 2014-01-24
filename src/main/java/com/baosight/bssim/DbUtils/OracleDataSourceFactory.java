package com.baosight.bssim.DbUtils;

import com.baosight.bssim.models.ConfigModel;
import oracle.jdbc.pool.OracleDataSource;
import org.json.JSONObject;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

public class OracleDataSourceFactory {
    public static DataSource createDataSource() throws SQLException {
        JSONObject configJson = new ConfigModel("GlobalConfig").getJson();
        String URL = "jdbc:oracle:thin:@//"+configJson.get("database_ip")+":"+configJson.getInt("database_port")
                +"/"+configJson.get("database_service");

        OracleDataSource ods = new OracleDataSource();
        ods.setURL(URL);
        ods.setUser(configJson.getString("database_username"));
        ods.setPassword(configJson.getString("database_password"));

        // 解决oracle 10g 的jdbc 在处理Date类型时丢失分秒部分的问题
        Properties properties = new Properties();
        properties.setProperty("oracle.jdbc.V8Compatible", "true");
        ods.setConnectionProperties(properties);

        return ods;
    }
}
