package models;

import com.baosight.bssim.controllers.ApplicationController;
import com.baosight.bssim.models.TableModel;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by ns on 14-2-13.
 */
public class JsModelTestCase {
    @Test
    public void testJsFtl() throws IOException {
        ApplicationController.BASE_PATH = "src/test/java/data";

        File file = new File(ApplicationController.BASE_PATH+"/SLCO01.js.test");
        String expectJavaCode = FileUtils.readFileToString(file, "utf-8");

        Map config = new HashMap();
        config.put("firstModule", "SL");
        config.put("secondModule", "CO");
        config.put("jsName", "SLCO01");

        TableModel table = new TableModel("BSSIM.TSLCO01");
        String actualJavaCode = table.genJsCode(config);
        System.out.println(actualJavaCode);

        assertEquals(expectJavaCode, actualJavaCode);
    }
}
