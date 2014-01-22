package com.baosight.bssim.routes;

import com.baosight.bssim.controllers.ApplicationController;
import org.apache.commons.io.FileUtils;
import org.pegdown.PegDownProcessor;
import spark.Request;
import spark.Response;

import java.io.File;

import static spark.Spark.get;

public class DocRoute {
    public static void all(){
        get(new JSONResponseRoute("/docs/:docName") {
            public Object handle(Request request, Response response) {
                String schemaTable = request.params(":docName");

                try {
                    String content = FileUtils.readFileToString(new File(ApplicationController.BASE_PATH + "/docs/" + schemaTable + ".md"), "UTF-8");
                    return new HandleResult(true, "读取成功", new PegDownProcessor().markdownToHtml(content));
                } catch (Exception e) {
                    return new HandleResult(false, e.getMessage(), null);
                }

            }
        });

    }
}
