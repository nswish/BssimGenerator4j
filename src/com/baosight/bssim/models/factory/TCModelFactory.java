package com.baosight.bssim.models.factory;

import com.baosight.bssim.models.ColumnModel;
import com.baosight.bssim.models.TableModel;

/**
 * Created with IntelliJ IDEA.
 * User: ns
 * Date: 13-11-16
 * Time: 下午7:45
 * To change this template use File | Settings | File Templates.
 */
public interface TCModelFactory {
    public TableModel createTableModel();
    public ColumnModel[] createColumnModels();
}
