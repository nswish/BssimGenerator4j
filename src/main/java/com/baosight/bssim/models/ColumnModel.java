package com.baosight.bssim.models;

import com.baosight.bssim.helpers.CodeHelper;
import org.apache.commons.lang3.StringUtils;

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

    public String getCapName(){
        return StringUtils.capitalize(this.getCamelName());
    }

    public String getSetterName(){
        return "set"+this.getCapName();
    }

    public String getGetterName() {
        return "get"+this.getCapName();
    }

    public String getComment() {
        return meta.get("comment")+"";
    }

    public String getQuoteComment() {
        return new StringBuilder().append("\"").append(this.getComment()).append("\"").toString();
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
        return Integer.parseInt(meta.get("length")+"");
    }

    public int getScale() {
        return Integer.parseInt(meta.get("scale")+"");
    }


/////////////////////////////////////////////// For Java Code Generation ///////////////////////////////////////////////
    public String fragmentForAttr() {
        return new StringBuilder().append("private " + getJavaType() + " " + getCamelName() + " = " + getDefaultValue() + ";").append("    /*" + getComment() + "*/").toString();
    }

    public String fragmentForGetter() {
        return new StringBuilder().append("public " + getJavaType() + " " + getGetterName()).append("() {\n")
                                  .append("    return this." + getCamelName()).append(";\n")
                                  .append("}")
                                  .toString();
    }

    public String fragmentForSetter() {
        return new StringBuilder().append("public void " + getSetterName() + "(" + getJavaType() + " " + getCamelName()).append(") {\n")
                                  .append("    if(this.isInDB && !taintedAttrs.containsKey(" + getQuoteCamelName() + "))taintedAttrs.put(" + getQuoteCamelName() + ", this." + getCamelName()).append(");\n")
                                  .append("    this." + getCamelName() + " = " + getCamelName()).append(";\n")
                                  .append("}")
                                  .toString();
    }

    public String fragmentForMeta() {
        StringBuilder result = new StringBuilder();

        result.append("eiColumn = new EiColumn(" + getQuoteCamelName() + ");\n")
              .append("eiColumn.setDescName(" + getQuoteComment() + ");\n")
              .append("eiColumn.setFieldLength(" + getLength() + ");\n");

        if ("N".equals(getType())){
            result.append("eiColumn.setType(\"N\");\n")
                  .append("eiColumn.setScaleLength(" + getScale() + ");\n");
        }

        return result.append("eiMetadata.addMeta(eiColumn);").toString();
    }

    public String fragmentForFromMap() {
        if ("C".equals(getType())) {
            return getSetterName() + "(StringUtils.defaultIfEmpty(StringUtils.toString(map.get(" + getQuoteCamelName() + ")), " + getCamelName() + "));";
        } else if("N".equals(getType())) {
            return getSetterName() + "(NumberUtils.to" + getJavaType() + "(StringUtils.toString(map.get(" + getQuoteCamelName() + ")), " + getCamelName() + "));";
        } else {
            return "";
        }
    }

    public String fragmentForToMap() {
        return "map.put(" + getQuoteCamelName() + ", StringUtils.toString(" + getCamelName() + ", eiMetadata.getMeta(" + getQuoteCamelName() + ")));";
    }

    public String fragmentForField() {
        String comment = CodeHelper.formatComment(this.getComment());
        String content = "public static final String " + this.getCamelName() + " = " + this.getQuoteCamelName() + ";";
        return comment + "\n" + content;
    }

    public String fragmentForColumn() {
        String comment = CodeHelper.formatComment(this.getComment());
        String content = "public static final String " + getName() + " = " + getQuoteName() + ";";
        return comment + "\n" + content;
    }

    public String fragmentForFromAttrs() {
        return "if(attrs.get(" + getQuoteCamelName() + ") != null)" + getSetterName() + "((" + getJavaType() + ")attrs.get(" + getQuoteCamelName() + "));";
    }

    public String fragmentForToAttrs() {
        return "result.put(" + getQuoteCamelName() + ", this." + getCamelName() + ");";
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
