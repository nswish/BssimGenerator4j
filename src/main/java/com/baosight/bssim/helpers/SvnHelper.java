package com.baosight.bssim.helpers;

import com.baosight.bssim.exceptions.HelperException;
import com.baosight.bssim.models.ConfigModel;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class SvnHelper {
    public static String commit(Map<String, String> files) {
        StringBuilder result = new StringBuilder();
        JSONObject config = new ConfigModel("GlobalConfig").getJson();

        if (config.optString("svn_repo_path") == null) {
            throw new HelperException("请在设置中指定svn版本库的本地路径");
        }

        boolean debug = config.optBoolean("svn_debug", false);

        try {
            Process p;
            String cmd;

            File dir = new File(config.getString("svn_repo_path"));

            // svn update
            cmd = "svn up";
            p = Runtime.getRuntime().exec(cmd, null, dir);
            p.waitFor();
            if(debug)
                result.append(getExecResult(p));

            // write to file
            for (String path : files.keySet()) {
                FileUtils.writeStringToFile(new File(dir + File.separator + path), files.get(path), "UTF8");
            }

            // svn status
            cmd = "svn status";
            p = Runtime.getRuntime().exec(cmd, null, dir);
            p.waitFor();
            String statusMsg = getExecResult(p);
            if (debug)
                result.append(getExecResult(p));

            // svn add
            String[] addTokens = statusMsg.split("\n");
            for (int i=0; i<addTokens.length; i++) {
                cmd = "svn add " + addTokens[i];
                p = Runtime.getRuntime().exec(cmd, null, dir);
                p.waitFor();
                if (debug)
                    result.append(getExecResult(p));
            }

            // svn commit
            cmd = "svn commit -m BssimGenerator_v2.0.0";
            p = Runtime.getRuntime().exec(cmd, null, dir);
            p.waitFor();
            result.append(getExecResult(p));

            return result.toString();
        } catch (Exception ex) {
            throw new HelperException(result.append(ex.getMessage()).toString());
        }
    }

    private static String getExecResult(Process p) throws IOException {
        byte[] buffer;
        StringBuilder result = new StringBuilder();
        InputStream in = p.getInputStream();
        InputStream err = p.getErrorStream();

        if (in.available() > 0) {
            buffer = new byte[in.available()];
            in.read(buffer);
            result.append(new String(buffer));
        }

        if (err.available() > 0) {
            buffer = new byte[err.available()];
            err.read(buffer);
            result.append(new String(buffer));

            throw new HelperException(result.toString());
        }

        return result.toString();
    }
}
