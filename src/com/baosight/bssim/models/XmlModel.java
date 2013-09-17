package com.baosight.bssim.models;


import com.baosight.bssim.helpers.CodeHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class XmlModel {
    private TableModel table;

    private List selectStatements = new ArrayList(5);
    private List insertStatements = new ArrayList(5);
    private List updateStatements = new ArrayList(5);
    private List deleteStatements = new ArrayList(5);

    public XmlModel(TableModel table) {
        this.table = table;
    }

    public void generateSelectStatements() {
        StringBuilder content;

        // select_by_id
        content = new StringBuilder()
                .append("SELECT\n")
                .append(CodeHelper.indent(CodeHelper.concatFragments(table.getColumns(), "SelectWithColumns", ",\n"))).append("\n")
                .append("FROM " + table.getFullTableName() + "\n")
                .append("WHERE ID = #id#\n\n")
                .append("<isNotEmpty property=\"forUpdate\">\n")
                .append("    $forUpdate$\n")
                .append("</isNotEmpty>");

        this.addSelect("select_by_id", "java.util.HashMap", table.getFullClassName(), content.toString());

        // select_by_ids
        content = new StringBuilder()
                .append("SELECT\n")
                .append(CodeHelper.indent(CodeHelper.concatFragments(table.getColumns(), "SelectWithColumns", ",\n"))).append("\n")
                .append("FROM " + table.getFullTableName() + "\n")
                .append("WHERE ID IN\n")
                .append("<iterate property=\"ids\" conjunction=\",\" open=\"(\" close=\")\">\n")
                .append("    #ids[]#\n")
                .append("</iterate>\n")
                .append("ORDER BY ID\n\n")
                .append("<isNotEmpty property=\"forUpdate\">\n")
                .append("    $forUpdate$\n")
                .append("</isNotEmpty>");

        this.addSelect("select_by_ids", "java.util.HashMap", table.getFullClassName(), content.toString());

        // select_ex
        content = new StringBuilder()
                .append("SELECT\n")
                .append(CodeHelper.indent(CodeHelper.concatFragments(table.getColumns(), "SelectWithColumns", ",\n"))).append("\n")
                .append("FROM " + table.getFullTableName() + "\n")
                .append("WHERE 1=1\n")
                .append("<isNotEmpty prepend=\" AND \" property=\"fixWhere\">\n")
                .append("    $fixWhere$\n")
                .append("</isNotEmpty>\n")
                .append("\n")
                .append("<isNotEmpty prepend=\" AND \" property=\"where\">\n")
                .append("    $where$\n")
                .append("</isNotEmpty>\n")
                .append("\n")
                .append(CodeHelper.concatFragments(table.getColumns(), "SelectWithWhere", "\n")).append("\n")
                .append("\n")
                .append("<isNotEmpty prepend=\" ORDER BY \" property=\"orderBy\">\n")
                .append("    $orderBy$\n")
                .append("</isNotEmpty>\n\n")
                .append("<isNotEmpty property=\"forUpdate\">\n")
                .append("    $forUpdate$\n")
                .append("</isNotEmpty>");

        this.addSelect("select_ex", "java.util.HashMap", table.getFullClassName(), content.toString());
    }

    public void generateInsertStatements() {
        StringBuilder content;

        content = new StringBuilder()
                .append("INSERT INTO " + table.getFullTableName() + " (\n")
                .append(CodeHelper.indent(CodeHelper.concatFragments(table.getColumns(), "InsertWithColumns", ",\n"))).append("\n")
                .append(") VALUES (").append(CodeHelper.concatFragments(table.getColumns(), "InsertWithValues", ", ")).append(")");

        this.addInsert("insert", content.toString());
    }

    public void generateUpdateStatements() {
        StringBuilder content;

        content = new StringBuilder()
                .append("UPDATE " + table.getFullTableName() + "\n")
                .append("SET ID = ID\n")
                .append(CodeHelper.concatFragments(table.getColumnsWithoutId(), "UpdateWithSet", "\n")).append("\n")
                .append("WHERE ID = #id#");

        this.addUpdate("update_by_id", content.toString());
    }

    public void generateDeleteStatements() {
        StringBuilder content;

        // delete_by_id
        content = new StringBuilder()
                .append("DELETE FROM " + table.getFullTableName() + " WHERE ID = #id#");

        this.addDelete("delete_by_id", content.toString());

        // delete_by_ids
        content = new StringBuilder()
                .append("DELETE FROM " + table.getFullTableName()).append("\n")
                .append("WHERE ID IN\n")
                .append("<iterate property=\"ids\" conjunction=\",\" open=\"(\" close=\")\">\n")
                .append("    #ids[]#\n")
                .append("</iterate>");

        this.addDelete("delete_by_ids", content.toString());
    }

    public String toCode() {

        // generate code fragments
        generateSelectStatements();
        generateInsertStatements();
        generateUpdateStatements();
        generateDeleteStatements();

        // combine fragments to full code
        StringBuilder result = new StringBuilder()

                // doc header & doc type
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
                .append("<!DOCTYPE sqlMap PUBLIC \"-//iBATIS.com//DTD SQL Map 2.0//EN\" \"http://www.ibatis.com/dtd/sql-map-2.dtd\">\n")
                .append("<!-- Table Information\n")

                // doc comment
                .append("    Generate Time: " + DateFormatUtils.format(new Date(), "y-MM-dd HH:mm:ss")).append("\n")
                .append("    Table Name: " + table.getTableName() + " " + table.getComment()).append("\n\n")
                .append(CodeHelper.indent(CodeHelper.concatFragments(table.getColumns(), "SqlMapComment", "\n"))).append("\n")
                .append("-->\n\n")

                // sqlmap
                .append("<sqlMap namespace=\"" + table.getTableName().substring(1)  + "E\">\n")

                // sqlmap statements
                .append(CodeHelper.indent(StringUtils.join(new String[]{
                        StringUtils.join(this.selectStatements, "\n\n"),

                        StringUtils.join(this.insertStatements, "\n\n"),

                        StringUtils.join(this.updateStatements, "\n\n"),

                        StringUtils.join(this.deleteStatements, "\n\n")
                }, "\n\n")))
                .append("\n")
                .append("</sqlMap>");

        return result.toString();
    }

    public void addSelect(String id, String paramType, String resultType, String content) {
        StringBuilder result = new StringBuilder()
                .append("<select id=\"" + id + "\" parameterClass=\"" + paramType + "\" resultClass=\"" + resultType + "\">\n")
                .append(CodeHelper.indent(content)).append("\n")
                .append("</select>");

        this.selectStatements.add(result.toString());
    }

    public void addInsert(String id, String content) {
        StringBuilder result = new StringBuilder()
                .append("<insert id=\"" + id + "\">\n")
                .append(CodeHelper.indent(content)).append("\n")
                .append("</insert>");

        this.insertStatements.add(result.toString());
    }

    public void addUpdate(String id, String content) {
        StringBuilder result = new StringBuilder()
                .append("<update id=\"" + id + "\">\n")
                .append(CodeHelper.indent(content)).append("\n")
                .append("</update>");

        this.updateStatements.add(result.toString());
    }

    public void addDelete(String id, String content) {
        StringBuilder result = new StringBuilder()
                .append("<delete id=\"" + id + "\">\n")
                .append(CodeHelper.indent(content)).append("\n")
                .append("</delete>");

        this.deleteStatements.add(result.toString());
    }
}
