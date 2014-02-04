<#import "java_iplat4j.ftl" as iplat4j>
<#import "java_extension.ftl" as extension>
package ${table.package};

import com.baosight.bssim.common.model.ModelEPBase;
import java.math.BigDecimal;
import com.baosight.iplat4j.core.ei.EiColumn;
import java.util.Map;
import com.baosight.iplat4j.util.StringUtils;
import com.baosight.iplat4j.util.NumberUtils;
import java.util.HashMap;
import java.util.List;
import com.baosight.bssim.common.exception.ModelException;
import net.sf.json.JSONObject;
import com.baosight.bssim.sa.sc.domain.model.Tsascc1;
import com.baosight.bssim.sa.sa.domain.model.Tsasa02;

/**
 * ${table.className}
 * Table Comment : ${table.comment}
 * Generate Date : ${generateDate?string("yyyy-MM-dd HH:mm:ss")}
 */
@SuppressWarnings({"serial", "rawtypes", "unchecked"})
public class ${table.className} extends ModelEPBase {
<@iplat4j.attrs />

<@extension.fields />

<@extension.columns />

<@iplat4j.constructor />

<#list table.columns as column>
    <@iplat4j.getter column=column />

    <@extension.setter column=column />
    <#if column_has_next>

    </#if>
</#list>

<@iplat4j.initMetaData />

<@iplat4j.fromMap />

<@iplat4j.toMap />

    /**
     * 生成代码的版本号
     */
    public int getVersion() {
        return 1;
    }

<@extension.fromAttrs />

<@extension.toAttrs />

<@extension.noIdDuplicate />

<@extension.json />

<#if belongsToArray??>
<@extension.belongs_to />

</#if>
<#if hasManyArray??>
<@extension.has_many/>

</#if>
<#if hasOneArray??>
<@extension.has_one />

</#if>
<@extension.newInstance />

<@extension.id />

<@extension.q />

<@extension.where />

<@extension.getFullTableName />

<@extension.delete />
}