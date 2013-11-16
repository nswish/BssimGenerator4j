package com.baosight.bssim.models.factory;

import com.baosight.bssim.models.ColumnModel;
import com.baosight.bssim.models.TableModel;
import org.json.JSONArray;
import org.json.JSONObject;

public class JsonTCModelFactory implements TCModelFactory{
    private JSONObject json;
    public JsonTCModelFactory(String jsonstr) {
        this.json = new JSONObject(jsonstr);
    }

    @Override
    public TableModel createTableModel() {
        TableModel model = new TableModel(json.get("schema")+"."+json.get("table"));
        model.setComment(json.getString("comment"));
        model.setFactory(this);
        return model;
    }

    @Override
    public ColumnModel[] createColumnModels() {
        JSONArray columnArray = json.getJSONArray("columns");
        ColumnModel[] result = new ColumnModel[columnArray.length()];

        for (int i=0; i<columnArray.length(); i++) {
            JSONObject columnjson = columnArray.getJSONObject(i);
            result[i] = new ColumnModel();
            result[i].setName(columnjson.getString("name"));
            result[i].setComment(columnjson.getString("comment"));
            result[i].setType(columnjson.optString("type", "C"));
            result[i].setDbType(columnjson.optString("dbtype", result[i].getType()));
            result[i].setLength(columnjson.optInt("length", 0));
            result[i].setScale(columnjson.optInt("scale", 0));
            result[i].setNullable(columnjson.optBoolean("nullable", true));
        }

        return result;
    }
}
