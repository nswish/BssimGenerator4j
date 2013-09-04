package com.baosight.bssim.helpers;

import com.baosight.bssim.exceptions.ModelException;
import com.baosight.bssim.models.ColumnModel;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * User: ns
 * Date: 13-9-3
 * Time: 上午9:13
 * To change this template use File | Settings | File Templates.
 */
public class CodeHelper {
    public static String formatComment(String comment) {
        StringBuilder result = new StringBuilder();

        result.append("/**\n");
        result.append(("\n"+comment).replaceAll("\n", "\n * ").substring(1)).append("\n");
        result.append(" */");

        return result.toString();
    }

    public static String indent(String code, int distance) {
        String[] lines = code.split("\n");
        for (int i=0; i<lines.length; i++) {
            lines[i] = StringUtils.repeat(" ", distance) + lines[i];
        }

        return StringUtils.join(lines, "\n");
    }

    public static String indent(String code) {
        return indent(code, 4);
    }

    public static String concatFragments(ColumnModel[] columns, String fragmentName, String separator) {
        try{
            Method method = ColumnModel.class.getMethod("fragmentFor"+fragmentName);

            String[] lines = new String[columns.length];

            for(int i=0; i<columns.length; i++) {
                lines[i] = method.invoke(columns[i]) + "";
            }

            return StringUtils.join(lines, separator);
        } catch (Exception ex) {
            throw new ModelException(ex.getMessage());
        }
    }
}
