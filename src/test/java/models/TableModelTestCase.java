package models;

import com.baosight.bssim.models.TableModel;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void testGetJson() {
        TableModel table = new TableModel("XSSA.TSASA01");
        JSONObject expect = new JSONObject("{\"lastModifiedTime\":\"2014-01-24 09:58:06\",\"schema\":\"XSSA\",\"name\":\"TSASA01\",\"columns\":[{\"scale\":0,\"name\":\"REC_CREATOR\",\"length\":8,\"dbType\":\"VARCHAR2\",\"comment\":\"记录创建者\"},{\"scale\":0,\"name\":\"REC_CREATE_TIME\",\"length\":14,\"dbType\":\"VARCHAR2\",\"comment\":\"记录创建时间\"},{\"scale\":0,\"name\":\"REC_REVISOR\",\"length\":8,\"dbType\":\"VARCHAR2\",\"comment\":\"记录修改人员\"},{\"scale\":0,\"name\":\"REC_REVISE_TIME\",\"length\":14,\"dbType\":\"VARCHAR2\",\"comment\":\"记录修改时间\"},{\"scale\":0,\"name\":\"ARCHIVE_FLAG\",\"length\":1,\"dbType\":\"VARCHAR2\",\"comment\":\"归档标记\"},{\"scale\":0,\"name\":\"ID\",\"length\":18,\"dbType\":\"NUMBER\",\"comment\":\"ID\"},{\"scale\":0,\"name\":\"RECEIPT_CODE\",\"length\":15,\"dbType\":\"VARCHAR2\",\"comment\":\"收款收条号\"},{\"scale\":0,\"name\":\"COMPANY_CODE\",\"length\":16,\"dbType\":\"VARCHAR2\",\"comment\":\"公司别\"},{\"scale\":0,\"name\":\"SETTLE_USER_NUM\",\"length\":20,\"dbType\":\"VARCHAR2\",\"comment\":\"结算用户代码\"},{\"scale\":0,\"name\":\"AGREEMT_NUM_CSY\",\"length\":30,\"dbType\":\"VARCHAR2\",\"comment\":\"协议号(厂商银)\"},{\"scale\":0,\"name\":\"RECEIPT_USAGE\",\"length\":4,\"dbType\":\"VARCHAR2\",\"comment\":\"收款用途\"},{\"scale\":0,\"name\":\"RECEIPT_MODE\",\"length\":4,\"dbType\":\"VARCHAR2\",\"comment\":\"收款方式\"},{\"scale\":0,\"name\":\"RECEIPT_STATUS\",\"length\":2,\"dbType\":\"VARCHAR2\",\"comment\":\"收款状态\"},{\"scale\":0,\"name\":\"PAPER_NO\",\"length\":20,\"dbType\":\"VARCHAR2\",\"comment\":\"票据号\"},{\"scale\":2,\"name\":\"PAPER_AMT\",\"length\":20,\"dbType\":\"NUMBER\",\"comment\":\"票面金额\"},{\"scale\":2,\"name\":\"RMB_AMT\",\"length\":20,\"dbType\":\"NUMBER\",\"comment\":\"人民币金额\"},{\"scale\":0,\"name\":\"CURRENCY\",\"length\":3,\"dbType\":\"VARCHAR2\",\"comment\":\"币种\"},{\"scale\":6,\"name\":\"EXCHANGE_RATE\",\"length\":12,\"dbType\":\"NUMBER\",\"comment\":\"汇率\"},{\"scale\":0,\"name\":\"BUILT_DATE\",\"length\":8,\"dbType\":\"VARCHAR2\",\"comment\":\"出票日期\"},{\"scale\":0,\"name\":\"END_DATE\",\"length\":8,\"dbType\":\"VARCHAR2\",\"comment\":\"到期日期\"},{\"scale\":0,\"name\":\"RECEIVE_DATE\",\"length\":8,\"dbType\":\"VARCHAR2\",\"comment\":\"收票日期\"},{\"scale\":0,\"name\":\"AUDIT_PERSON\",\"length\":8,\"dbType\":\"VARCHAR2\",\"comment\":\"审核人\"},{\"scale\":0,\"name\":\"AUDIT_TIME\",\"length\":14,\"dbType\":\"VARCHAR2\",\"comment\":\"审核时间\"},{\"scale\":0,\"name\":\"REMARK\",\"length\":200,\"dbType\":\"VARCHAR2\",\"comment\":\"备注\"},{\"scale\":0,\"name\":\"BACK_REMARK\",\"length\":200,\"dbType\":\"VARCHAR2\",\"comment\":\"退回原因\"},{\"scale\":0,\"name\":\"TSASCC1_ID\",\"length\":18,\"dbType\":\"NUMBER\",\"comment\":\"货款信息配置主表ID\"}],\"fullName\":\"XSSA.TSASA01\",\"comment\":\"货款信息表\"}");
        String actual = new Gson().toJson(table.getMeta());
        assertEquals(new Gson().fromJson(expect.toString(), HashMap.class), new Gson().fromJson(actual, HashMap.class));
    }
}
