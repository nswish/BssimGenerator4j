package models;

import com.baosight.bssim.controllers.ApplicationController;
import com.baosight.bssim.models.TableModel;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TableModelTestCase {
    @Test
    public void testCreateFromJson() throws IOException {
        ApplicationController.BASE_PATH = "src/test/java/data";

        File file = new File(ApplicationController.BASE_PATH+"/db/XSSA.TSASA01.meta.json");
        String json = FileUtils.readFileToString(file, "utf-8");

        file = new File(ApplicationController.BASE_PATH+"/XSSA.TSASA01.java.test");
        String expectJavaCode = FileUtils.readFileToString(file, "utf-8");

        TableModel table = TableModel.newInstance(json);
        String actualJavaCode = table.genJavaCode();
        System.out.println(actualJavaCode);

        assertEquals(removeGenenrateDateStr(expectJavaCode), removeGenenrateDateStr(actualJavaCode));
    }

    @Test
    public void testMeta() {
        String table = "XSSA.TSASA01";
        TableModel model = new TableModel(table);
        System.out.println(new JSONObject(model.getMeta()).toString(4));
    }

    @Test
    public void testJavaFtl() throws IOException {
        ApplicationController.BASE_PATH = "src/test/java/data";

        File file = new File(ApplicationController.BASE_PATH+"/db/XSSA.TSASA01.meta.json");
        String json = FileUtils.readFileToString(file, "utf-8");

        file = new File(ApplicationController.BASE_PATH+"/XSSA.TSASA01.java.test");
        String expectJavaCode = FileUtils.readFileToString(file, "utf-8");

        TableModel table = TableModel.newInstance(json);
        String actualJavaCode = table.genJavaCode();
        System.out.println(actualJavaCode);

        assertEquals(removeGenenrateDateStr(expectJavaCode), removeGenenrateDateStr(actualJavaCode));
    }

    @Test
    public void testXmlFtl() throws IOException {
        ApplicationController.BASE_PATH = "src/test/java/data";

        File file = new File(ApplicationController.BASE_PATH+"/db/XSSA.TSASA01.meta.json");
        String json = FileUtils.readFileToString(file, "utf-8");

        file = new File(ApplicationController.BASE_PATH+"/XSSA.TSASA01.xml.test");
        String expectXmlCode = FileUtils.readFileToString(file, "utf-8");

        TableModel table = TableModel.newInstance(json);
        String actualXmlCode = table.genXmlCode();
        System.out.println(actualXmlCode);

        assertEquals(removeGenenrateDateStr(expectXmlCode), removeGenenrateDateStr(actualXmlCode));
    }

    private String removeGenenrateDateStr(String str) {
        return str.replaceAll("Generate Date : .*\\n", "").replaceAll("Generate Time.*\\n", "");
    }
}
