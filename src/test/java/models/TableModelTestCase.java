package models;

import com.baosight.bssim.models.TableModel;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class TableModelTestCase {
    @Test
    public void testCreateFromJson() throws IOException {
        File file = new File("src/test/java/data/XSSA.TSASA01.meta.json");
        String json = FileUtils.readFileToString(file, "utf-8");
        TableModel table = TableModel.newInstance(json);
        System.out.println(table.genJavaCode());
    }

    @Test
    public void testMeta() {
        String table = "XSSA.TSASA01";
        TableModel model = new TableModel(table);
        System.out.println(new JSONObject(model.getMeta()).toString(4));
    }
}
