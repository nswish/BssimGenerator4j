package models;

import com.baosight.bssim.models.TableModel;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TableModelTestCase {
    @Test
    public void testConstructor1() {
        TableModel table = new TableModel("XssA.TSaSa01");

        assertEquals("XSSA.TSASA01", table.getFullTableName());
        assertEquals("XSSA", table.getSchemaName());
        assertEquals("TSASA01", table.getTableName());
        assertEquals("SA", table.getFirstModuleName());
        assertEquals("SA", table.getSecondModuleName());
        assertEquals("com.baosight.bssim.sa.sa.domain.model", table.getPackage());
        assertEquals("Tsasa01", table.getClassName());

        table = new TableModel("XssA.TSa01");

        assertEquals("XSSA.TSA01", table.getFullTableName());
        assertEquals("XSSA", table.getSchemaName());
        assertEquals("TSA01", table.getTableName());
        assertEquals("SA", table.getFirstModuleName());
        assertEquals("", table.getSecondModuleName());
        assertEquals("com.baosight.bssim.sa.domain.model", table.getPackage());
        assertEquals("Tsa01", table.getClassName());
    }

    public void testConstructor2() {
        TableModel table = new TableModel("XssA", "TSaSa01");

        assertEquals("XSSA.TSASA01", table.getFullTableName());
        assertEquals("XSSA", table.getSchemaName());
        assertEquals("TSASA01", table.getTableName());
        assertEquals("SA", table.getFirstModuleName());
        assertEquals("SA", table.getSecondModuleName());
        assertEquals("com.baosight.bssim.sa.sa.domain.model", table.getPackage());
        assertEquals("Tsasa01", table.getClassName());

        table = new TableModel("XssA", "TSa01");

        assertEquals("XSSA.TSA01", table.getFullTableName());
        assertEquals("XSSA", table.getSchemaName());
        assertEquals("TSA01", table.getTableName());
        assertEquals("SA", table.getFirstModuleName());
        assertEquals("", table.getSecondModuleName());
        assertEquals("com.baosight.bssim.sa.domain.model", table.getPackage());
        assertEquals("Tsa01", table.getClassName());
    }

    @Test
    public void testGenJava() {
        TableModel table = new TableModel("XssA.TSaSa01");
        System.out.print(table.genJavaCode());
    }
}
