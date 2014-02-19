package models;

import com.baosight.bssim.controllers.ApplicationController;
import com.baosight.bssim.models.TableModel;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by ns on 14-2-13.
 */
public class JspModelTestCase {
    @Test
    public void testJspFtl() throws IOException {
        ApplicationController.BASE_PATH = "src/test/java/data";

        File file = new File(ApplicationController.BASE_PATH+"/SLCO01.jsp.test");
        String expectJavaCode = FileUtils.readFileToString(file, "utf-8");

        List queryList = new ArrayList();
        queryList.add("STOCK_NAME");

        Map config = new HashMap();
        config.put("firstModule", "SL");
        config.put("secondModule", "CO");
        config.put("jspName", "SLCO01");
        config.put("queryList", queryList);

        TableModel table = new TableModel("BSSIM.TSLCO01");
        String actualJavaCode = table.genJspCode(config);
        System.out.println(actualJavaCode);

        assertEquals(expectJavaCode, removeGenenrateDateStr(actualJavaCode));
    }

    private String removeGenenrateDateStr(String str) {
        return str.replaceAll("<!-- Created at .*\\n", "");
    }
}
