package com.baosight.bssim.models;

import com.baosight.bssim.helpers.CodeHelper;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ColumnModel {
    private Map meta = new HashMap();

    public ColumnModel(Map meta) {
        this.meta = meta;
    }

    public String getDbType() {
        return meta.get("dbType")+"";
    }

    public boolean isNullable() {
        return Boolean.parseBoolean(meta.get("nullable")+"");
    }

    public String getName() {
        return meta.get("name")+"";
    }

    public String getQuoteName() {
        return "\"" + getName() + "\"";
    }

    public String getCamelName() {
        return CodeHelper.toCamel(getName());
    }

    public String getUnderscoreCamelName() {
        return new StringBuilder().append("_").append(this.getCamelName()).toString();
    }

    public String getQuoteCamelName() {
        return new StringBuilder().append("\"").append(this.getCamelName()).append("\"").toString();
    }

    public String getQuoteUnderscoreCamelName() {
        return new StringBuilder().append("\"").append(this.getUnderscoreCamelName()).append("\"").toString();
    }

    public String getComment() {
        return meta.get("comment")+"";
    }

    public String getType() {
        return meta.get("type")+"";
    }

    public String getJavaType(){
        if (getType().equals("C")){
            return "String";
        } else if(getType().equals("N")){
            if(getScale() == 0 && getLength() <= 9){
                return "Integer";
            }

            if(getScale() == 0 && getLength() <= 18){
                return "Long";
            }

            return "BigDecimal";
        } else {
            return "Object";
        }
    }

    public String getDefaultValue() {
        if (getType().equals("C")) {
            return "\" \"";
        } else if(getType().equals("N")) {
            if(getScale() == 0 && getLength() <= 9){
                return "new Integer(0)";
            }

            if(getScale() == 0 && getLength() <= 18){
                return "new Long(0)";
            }

            return "new BigDecimal(0)";
        } else {
            return "null";
        }
    }

    public int getLength() {
        return new BigDecimal(meta.get("length") + "").intValue();
    }

    public int getScale() {
        return new BigDecimal(meta.get("scale")+"").intValue();
    }

/////////////////////////////////////////////// For Xml Code Generation ////////////////////////////////////////////////

    public String fragmentForSqlMapComment() {
        return getComment() + " " + getQuoteName() + " " + getDbType()
               + "(" + getLength() + (getScale() > 0 ? ","+getScale() : "") + ") "
               + (isNullable() ? "" : "NOT NULL");
    }

    public String fragmentForSelectWithColumns() {
        return getName() + " as " + getQuoteCamelName() + "   <!-- " + getComment() + " -->";
    }

    public String fragmentForInsertWithColumns() {
        return getName() + "   <!-- " + getComment() + " -->";
    }

    public String fragmentForInsertWithValues() {
        return "#" + getCamelName() + "#";
    }

    public String fragmentForUpdateWithSet() {
        return new StringBuilder().append("<isNotEmpty property=" + getQuoteCamelName() + ">\n")
                                  .append("    ," + getName() + " = #" + getCamelName() + "#\n")
                                  .append("</isNotEmpty>").toString();
    }

    public String fragmentForUpdateWithWhere() {
        return new StringBuilder().append("<isNotEmpty prepend=\" AND \" property=" + getQuoteUnderscoreCamelName() + ">\n")
                                  .append("    " + getName() + " = #" + getUnderscoreCamelName() + "#\n")
                                  .append("</isNotEmpty>").toString();
    }

    public String fragmentForSelectWithWhere() {
        return new StringBuilder().append("<isNotEmpty prepend=\" AND \" property=" + getQuoteCamelName() + ">\n")
                                  .append("    " + getName() + " = #" + getCamelName() + "#\n")
                                  .append("</isNotEmpty>").toString();
    }
}
