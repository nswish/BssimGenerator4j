<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE sqlMap PUBLIC "-//iBATIS.com//DTD SQL Map 2.0//EN" "http://www.ibatis.com/dtd/sql-map-2.dtd">
<!-- Table Information
    Generate Time: ${generateDate?string("yyyy-MM-dd HH:mm:ss")}
    Table Name: ${table.name} ${table.comment}

    <#list table.columns as column>
    ${column.comment} "${column.name}" ${column.dbType}(${column.length}<#if column.scale==0><#else>,${column.scale}</#if>) <#if !column.nullable>NOT NULL<#else>NULL</#if>
    </#list>
-->

<sqlMap namespace="${table.name?substring(1)}E">
    <select id="select_by_id" parameterClass="java.util.HashMap" resultClass="${table.package}.${table.className}">
        SELECT
        <#list table.columns as column>
            ${column.name} as "${column.camelName}"   <!-- ${column.comment} --><#if column_has_next>,</#if>
        </#list>
        FROM ${table.fullName}
        WHERE ID = #id#

        <isNotEmpty property="forUpdate">
            $forUpdate$
        </isNotEmpty>
    </select>

    <select id="select_by_ids" parameterClass="java.util.HashMap" resultClass="${table.package}.${table.className}">
        SELECT
        <#list table.columns as column>
            ${column.name} as "${column.camelName}"   <!-- ${column.comment} --><#if column_has_next>,</#if>
        </#list>
        FROM ${table.fullName}
        WHERE ID IN
        <iterate property="ids" conjunction="," open="(" close=")">
            #ids[]#
        </iterate>
        ORDER BY ID

        <isNotEmpty property="forUpdate">
            $forUpdate$
        </isNotEmpty>
    </select>

    <select id="select_ex" parameterClass="java.util.HashMap" resultClass="${table.package}.${table.className}">
        SELECT
        <#list table.columns as column>
            ${column.name} as "${column.camelName}"   <!-- ${column.comment} --><#if column_has_next>,</#if>
        </#list>
        FROM ${table.fullName}
        WHERE 1=1
        <isNotEmpty prepend=" AND " property="fixWhere">
            $fixWhere$
        </isNotEmpty>

        <isNotEmpty prepend=" AND " property="where">
            $where$
        </isNotEmpty>

        <#list table.columns as column>
        <isNotEmpty prepend=" AND " property="${column.camelName}">
            ${column.name} = #${column.camelName}#
        </isNotEmpty>
        </#list>

        <isNotEmpty prepend=" ORDER BY " property="orderBy">
            $orderBy$
        </isNotEmpty>

        <isNotEmpty property="forUpdate">
            $forUpdate$
        </isNotEmpty>
    </select>

    <insert id="insert">
        INSERT INTO ${table.fullName} (
        <#list table.columns as column>
            ${column.name}   <!-- ${column.comment} --><#if column_has_next>,</#if>
        </#list>
        ) VALUES (<#list table.columns as column>#${column.camelName}#<#if column_has_next>, </#if></#list>)
    </insert>

    <update id="update_by_id">
        UPDATE ${table.fullName}
        SET ID = ID
    <#list table.columns as column>
        <#if column.name != "ID">
        <isNotEmpty property="${column.camelName}">
            ,${column.name} = #${column.camelName}#
        </isNotEmpty>
        </#if>
    </#list>

        WHERE ID = #id#

    <#list table.columns as column>
        <#if column.name != "ID">
        <isNotEmpty prepend=" AND " property="_${column.camelName}">
            ${column.name} = #_${column.camelName}#
        </isNotEmpty>
        </#if>
    </#list>
    </update>

    <delete id="delete_by_id">
        DELETE FROM ${table.fullName} WHERE ID = #id#
    </delete>

    <delete id="delete_by_ids">
        DELETE FROM ${table.fullName}
        WHERE ID IN
        <iterate property="ids" conjunction="," open="(" close=")">
            #ids[]#
        </iterate>
    </delete>
</sqlMap>