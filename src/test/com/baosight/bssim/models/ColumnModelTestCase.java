package com.baosight.bssim.models;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: ns
 * Date: 13-8-30
 * Time: 下午4:30
 * To change this template use File | Settings | File Templates.
 */
public class ColumnModelTestCase {
    @Test
    public void testAccessor(){
        ColumnModel column = new ColumnModel();
        column.setName("ORDER_NO");

        assertEquals("orderNo", column.getCamelName());
        assertEquals("OrderNo", column.getCapName());
        assertEquals("getOrderNo", column.getGetterName());
        assertEquals("setOrderNo", column.getSetterName());
    }
}
