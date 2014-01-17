package com.baosight.bssim.helpers;

import com.baosight.bssim.exceptions.HelperException;
import com.baosight.bssim.helpers.interfaces.DatabaseHelper;
import com.baosight.bssim.models.ConfigModel;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

public class DatabaseHelperFactory {
    public static DatabaseHelper newInstance() {
        JSONObject config = new ConfigModel("GlobalConfig").getJson();
        String database = config.getString("database") == null ? "oracle" : config.getString("database");
        String clazz = "com.baosight.bssim.helpers." + StringUtils.capitalize(database) + "Helper";

        DatabaseHelper helper;
        try{
            helper = (DatabaseHelper)Class.forName(clazz).newInstance();
        } catch (Exception ex) {
            throw new HelperException("未找到类: "+clazz);
        }

        return helper;
    }
}
