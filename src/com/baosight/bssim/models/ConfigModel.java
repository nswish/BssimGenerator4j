package com.baosight.bssim.models;

import com.baosight.bssim.controllers.ApplicationController;
import com.baosight.bssim.exceptions.ModelException;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;

/**
 * 配置信息以JSON格式存放
 *
 * 全局配置样例：
 * {
 *      database          : 'oracle'
 *      database_ip       : '10.25.76.190'
 *      database_port     : 1521
 *      database_service  : 'erpdvlp'
 *      database_username : 'simdvlp'
 *      database_password : 'simdvlp'
 *
 *      svn_repo_path     : 'path_to_svn_repo'
 *
 *      user_tables : [{
 *          user   ：'XSSA',
 *          tables : ['TSA01', 'TSASA01', 'TSASA02', 'TSASS02', 'TSASS03']
 *      }]
 * }
 *
 * 数据表配置样例：
 * {
 *     "belongs_to" : ["XSSD.TSDSCC1"],
 *     "has_many"   : ["XSSD.TSDSCC1", "XSSD.TSDSCC2"]
 * }
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
                this.fileContent = "{\n}";
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
