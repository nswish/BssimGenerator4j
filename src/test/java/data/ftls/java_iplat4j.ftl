<#macro attrs>
    <#list table.columns as column>
    private ${column.javaType} ${column.camelName} = ${column.defaultValue};    /*${column.comment}*/
    </#list>
</#macro>

<#macro constructor>
    /**
     * the constructor
     */
    public ${table.className}() {
        initMetaData();
    }
</#macro>

<#macro getter column>
    /**
     * get the ${column.camelName} - ${column.comment}
     * @return the ${column.camelName}
     */
    public ${column.javaType} get${column.camelName?cap_first}() {
        return this.${column.camelName};
    }
</#macro>

<#macro initMetaData>
    /**
     * initialize the metadata
     */
    public void initMetaData() {
        EiColumn eiColumn;

    <#list table.columns as column>
        eiColumn = new EiColumn("${column.camelName}");
        eiColumn.setDescName("${column.comment}");
        eiColumn.setFieldLength(${column.length});
        <#if column.type == "N">
        eiColumn.setType("${column.type}");
        eiColumn.setScaleLength(${column.scale});
        </#if>
        eiMetadata.addMeta(eiColumn);
        <#if column_has_next>

        </#if>
    </#list>
    }
</#macro>

<#macro fromMap>
    /**
     * get the value from Map
     */
    public void fromMap(Map map) {
    <#list table.columns as column>
        <#if column.type == "C">
        set${column.camelName?cap_first}(StringUtils.defaultIfEmpty(StringUtils.toString(map.get("${column.camelName}")), ${column.camelName}));
        </#if>
        <#if column.type == "N">
        set${column.camelName?cap_first}(NumberUtils.to${column.javaType}(StringUtils.toString(map.get("${column.camelName}")), ${column.camelName}));
        </#if>
    </#list>
    }
</#macro>

<#macro toMap>
    /**
     * set the value to Map
     */
    public Map toMap() {
        Map map = new HashMap();
        <#list table.columns as column>
        map.put("${column.camelName}", StringUtils.toString(${column.camelName}, eiMetadata.getMeta("${column.camelName}")));
        </#list>
        return map;
    }
</#macro>