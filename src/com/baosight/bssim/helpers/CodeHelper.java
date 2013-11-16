package com.baosight.bssim.helpers;

import com.baosight.bssim.exceptions.ModelException;
import com.baosight.bssim.models.ColumnModel;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;

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
            if(lines[i].length() != 0)
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
    
    public static String toCamel(String name) {
        StringBuilder result = new StringBuilder();

        String[] tokens = name.toLowerCase().split("_");
        // for conforming javabean specification:
        //     “FooBah” becomes “fooBah”
        //     “Z” becomes “z”
        //     “URL” becomes “URL”
        //     "f_invoice_no" becomes "f_invoiceNo"
        if (tokens.length > 1 && tokens[0].length() == 1) {
            String[] tempTokens = tokens;
            tokens = new String[tempTokens.length - 1];

            tokens[0] = tempTokens[0] + "_" + tempTokens[1];
            for (int i=1; i<tokens.length; i++) {
                tokens[i] = tempTokens[i+1];
            }
        }

        result.append(tokens[0].toLowerCase());
        for(int i=1; i<tokens.length; i++){
            result.append(StringUtils.capitalize(tokens[i]));
        }

        return result.toString();
    }
}
