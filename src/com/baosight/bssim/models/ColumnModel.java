package com.baosight.bssim.models;

import com.baosight.bssim.helpers.CodeHelper;
import org.apache.commons.lang3.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: ns
 * Date: 13-8-30
 * Time: 下午4:22
 * To change this template use File | Settings | File Templates.
 */
public class ColumnModel {
    private String  name;
    private String  comment;
    private String  type;
    private int     length;
    private int     scale;

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    private String  dbType;
    private boolean nullable;

    public String getName() {
        return name;
    }

    public String getQuoteName() {
        return "\"" + getName() + "\"";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCamelName() {
        StringBuilder result = new StringBuilder();

        String[] tokens = this.name.toLowerCase().split("_");
        // for conforming javabean specification:
        //     “FooBah” becomes “fooBah”
        //     “Z” becomes “z”
        //     “URL” becomes “URL”
        result.append(tokens.length > 1 && tokens[0].length() == 1 ? tokens[0].toUpperCase() : tokens[0].toLowerCase());
        for(int i=1; i<tokens.length; i++){
            result.append(StringUtils.capitalize(tokens[i]));
        }

        return result.toString();
    }

    public String getQuoteCamelName() {
        return new StringBuilder().append("\"").append(this.getCamelName()).append("\"").toString();
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
        return comment;
    }

    public String getQuoteComment() {
        return new StringBuilder().append("\"").append(this.getComment()).append("\"").toString();
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getJavaType(){
        if (this.type.equals("C")){
            return "String";
        } else if(this.type.equals("N")){
            if(this.scale == 0 && this.length <= 9){
                return "Integer";
            }

            if(this.scale == 0 && this.length <= 18){
                return "Long";
            }

            return "BigDecimal";
        } else {
            return "Object";
        }
    }

    public String getDefaultValue() {
        if (this.type.equals("C")) {
            return "\" \"";
        } else if(this.type.equals("N")) {
            if(this.scale == 0 && this.length <= 9){
                return "new Integer(0)";
            }

            if(this.scale == 0 && this.length <= 18){
                return "new Long(0)";
            }

            return "new BigDecimal(0)";
        } else {
            return "null";
        }
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
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
                                  .append("    if(this.isInDB)taintedAttrs.put(" + getQuoteCamelName() + ", " + getCamelName()).append(");\n")
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

    public String fragmentForSelectWithWhere() {
        return new StringBuilder().append("<isNotEmpty prepend=\" AND \" property=" + getQuoteCamelName() + ">\n")
                                  .append("    " + getName() + " = #" + getCamelName() + "#\n")
                                  .append("</isNotEmpty>").toString();
    }
}
