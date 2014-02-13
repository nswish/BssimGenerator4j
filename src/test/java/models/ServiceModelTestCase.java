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
public class ServiceModelTestCase {
    @Test
    public void testServiceFtl() throws IOException {
        ApplicationController.BASE_PATH = "src/test/java/data";

        File file = new File(ApplicationController.BASE_PATH+"/ServiceSLCO01.java.test");
        String expectJavaCode = FileUtils.readFileToString(file, "utf-8");

        Map config = new HashMap();
        config.put("firstModule", "SL");
        config.put("secondModule", "CO");
        config.put("serviceName", "ServiceSLCO01");

        TableModel table = new TableModel("BSSIM.TSLCO01");
        String actualJavaCode = table.genServiceCode(config);
        System.out.println(actualJavaCode);

        assertEquals(expectJavaCode, actualJavaCode);
    }
}
