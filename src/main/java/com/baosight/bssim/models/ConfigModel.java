package com.baosight.bssim.models;

import com.baosight.bssim.controllers.ApplicationController;
import com.baosight.bssim.exceptions.ModelException;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

/**
 * 配置信息以JSON格式存放
 *
 * 全局配置样例：
 {
     "database"          : 'oracle',                   # 数据库类型,缺省为oracle
     "database_ip"       : "10.25.76.190",             # 数据库地址
     "database_port"     : 1521,                       # 数据库端口
     "database_service"  : "erpdvlp",                  # 数据库服务名
     "database_username" : "simdvlp",                  # 数据库用户名
     "database_password" : "simdvlp",                  # 数据库用户密码

     "svn_repo_path"     : "/home/ns/dev/bssim",       # svn版本库的本地路径
     "svn_debug"         : false,                      # 是否打开svn的调试信息

    # 在此注册数据表，按schema分组
     "user_tables"       : [{
             "user"          : "XSSO",                      # schema名称
             "tables"        : ["TSOSOA1", "TSOSOA4"]       # 数据表
         },{
             "user"          : "XSSD",
             "tables"        : ["TSDSCC1","TSDSCC2","TSDWF01","TSDSD01","TSDSD02"]
         },{
             "user"          : "XSSM",
             "tables"        : ["TSMSR01", "TSMSR02"]
         },{
             "user"          : "XSTB",
             "tables"        : ["TTBWF01"]
         },{
             "user"          : "XSSA",
             "tables"        : ["TSA01","TSASA01", "TSASA02", "TSASA03", "TSASS01", "TSASS02", "TSASS03"]
         }]
     }
 */
public class ConfigModel {
    private final File configFile;
    private String fileContent;
    private JSONObject json;

    public ConfigModel(String name) {
        try {
            String dirPath = ApplicationController.BASE_PATH+File.separator+"db";
            FileUtils.forceMkdir(new File(dirPath));

            this.configFile = new File(dirPath + File.separator + name + ".json");

            if(this.configFile.exists()) {
                this.fileContent = FileUtils.readFileToString(this.configFile, "UTF-8");
            } else {
                this.fileContent = "{'database': 'oracle', 'database_ip':'10.25.76.190', 'database_port': 1521, 'database_username':'simdvlp', 'database_password':'simdvlp', 'database_service':'erpdvlp'}";
                FileUtils.writeStringToFile(this.configFile, this.fileContent, "UTF-8");
            }
        } catch (Exception ex) {
            throw new ModelException(ex.getMessage());
        }
    }

    public JSONObject getJson() {
        if (this.json == null) {
            this.json = new JSONObject(this.fileContent);
        }

        return this.json;
    }

    public HashMap getGson() {
        return new Gson().fromJson(this.fileContent, HashMap.class);
    }

    public String getFileContent() {
        return this.fileContent;
    }

    public void save(String content) {
        try {
            this.json = new JSONObject(content);
            this.fileContent = content;

            FileUtils.writeStringToFile(this.configFile, this.fileContent, "UTF-8");
        } catch (Exception ex){
            throw new ModelException("json格式转换出错！"+ex.getMessage());
        }
    }
}
