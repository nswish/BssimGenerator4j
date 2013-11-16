package com.baosight.bssim.models;

import com.baosight.bssim.models.factory.JsonTCModelFactory;
import com.baosight.bssim.models.factory.TCModelFactory;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: ns
 * Date: 13-11-16
 * Time: 下午8:34
 * To change this template use File | Settings | File Templates.
 */
public class JsonTCModelFactoryTestCase {
    @Test
    public void testCreate() throws IOException {
        String jsonstr = FileUtils.readFileToString(new File("./test/data/XSSA.TSASA02.json"));
        TCModelFactory factory = new JsonTCModelFactory(jsonstr);
        TableModel tableModel = factory.createTableModel();

        assertEquals("XSSA.TSASA02", tableModel.getFullTableName());
        assertEquals("货款信息附加属性表", tableModel.getComment());
        assertEquals(factory.createColumnModels().length, tableModel.getColumns().length);

        System.out.println(tableModel.genJavaCode());
        assertEquals(FileUtils.readFileToString(new File("./test/data/XSSA.TSASA02.java.test")), tableModel.genJavaCode());
    }
}
