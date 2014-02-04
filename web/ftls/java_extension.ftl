<#macro fields>
    /**
     * 按驼峰命名规则的字段名称
     */
    public static class fields {
    <#list table.columns as column>
        /**
         * ${column.comment}
         */
        public static final String ${column.camelName} = "${column.camelName}";
    </#list>
    }
</#macro>

<#macro columns>
    /**
     * 按下划线命名规则的字段名称
     */
    public static class columns {
    <#list table.columns as column>
        /**
         * ${column.comment}
         */
        public static final String ${column.name} = "${column.name}";
    </#list>
    }
</#macro>

<#macro setter column>
    /**
     * set the ${column.camelName} - ${column.comment}
     */
    public void set${column.camelName?cap_first}(${column.javaType} ${column.camelName}) {
        if(this.isInDB && !taintedAttrs.containsKey("${column.camelName}"))taintedAttrs.put("${column.camelName}", this.${column.camelName});
        this.${column.camelName} = ${column.camelName};
    }
</#macro>

<#macro fromAttrs>
    /**
     * 类似fromMap方法，但参数包含的数据类型必须是严格的
     */
    public void fromAttrs(Map attrs) {
    <#list table.columns as column>
        if(attrs.get("${column.camelName}") != null)set${column.camelName?cap_first}((${column.javaType})attrs.get("${column.camelName}"));
    </#list>
    }
</#macro>

<#macro toAttrs>
    /**
     * 类似toMap方法，但返回结果的数据类型是严格的
     */
    public Map toAttrs() {
        Map result = new HashMap();
    <#list table.columns as column>
        result.put("${column.camelName}", this.${column.camelName});
    </#list>
        return result;
    }
</#macro>

<#-- TODO:noid should be withoutId -->
<#macro noIdDuplicate>
    /**
     * 复制当前对象(id不被复制)
     */
    public ${table.className} noIdDuplicate() {
        ${table.className} dup = new ${table.className}();

        Map attrs = this.toAttrs();
        attrs.put("id", new Long(0));
        dup.fromAttrs(attrs);

        dup.isInDB = false;
        dup.isExtended = true;

        return dup;
    }
</#macro>

<#macro json>
    /**
     * 转换为Json字符串
     */
    public String toJson() {
        return JSONObject.fromObject(toAttrs()).toString(4);
    }

    /**
     * toJson的反向操作
     */
    public void fromJson(String jsonstr) {
        fromAttrs(JSONObject.fromObject(jsonstr));
    }
</#macro>

<#macro newInstance>
    /**
     * 创建新的实例
     */
    public static ${table.className} newInstance() {
        ${table.className} result = new ${table.className}();
        result.isExtended = true;
        return result;
    }

    /**
     * 创建新的实例(属性初始化为传入的参数)
     */
    public static ${table.className} newInstance(Map attr) {
        ${table.className} result = newInstance();
        result.fromMap(attr);
        return result;
    }
</#macro>

<#macro id>
    /**
     * 按id查找
     */
    public static ${table.className} find(Long id) {
        return find(id, false);
    }

    /**
     * 按id查找(上锁)
     */
    public static ${table.className} findWithLock(Long id) {
        return find(id, true);
    }

    /**
     * 按id查找(可选择是否上锁)
     */
    private static ${table.className} find(Long id, boolean isLocked) {
        Map arg = new HashMap();
        arg.put("id", id);
        if(isLocked)arg.put("forUpdate", "FOR UPDATE");
        List<${table.className}> result = getDao().query("${table.name?substring(1)}E.select_by_id", arg);

        if(result.size() > 0){
            ${table.className} a = result.get(0);
            a.isExtended = true;
            a.isInDB = true;
            return a;
        } else {
            return null;
        }
    }

    /**
     * 按id查找，参数为String类型
     */
    public static ${table.className} find(String id) {
        return find(Long.parseLong(id), false);
    }

    /**
     * 按id查找，参数为String类型(上锁)
     */
    public static ${table.className} findWithLock(String id) {
        return find(Long.parseLong(id), true);
    }

    /**
     * 按id数组查找
     * 如果结果集包含的项数少于id数组的项数，则抛出异常
     */
    public static ${table.className}[] find(Long[] ids) {
        return find(ids, false);
    }

    /**
     * 按id数组查找(上锁)
     * 如果结果集包含的项数少于id数组的项数，则抛出异常
     */
    public static ${table.className}[] findWithLock(Long[] ids) {
        return find(ids, true);
    }

    /**
     * 按id数组查找(可选择是否上锁)
     * 如果结果集包含的项数少于id数组的项数，则抛出异常
     */
    private static ${table.className}[] find(Long[] ids, boolean isLocked) {
        if(ids == null || ids.length == 0) throw new ModelException("id数组不能为null或者空");

        Map arg = new HashMap();
        arg.put("ids", ids);
        if(isLocked)arg.put("forUpdate", "FOR UPDATE");
        List<${table.className}> result = getDao().query("${table.name?substring(1)}E.select_by_ids", arg);

        if(result.size() != ids.length) {
            throw new ModelException("结果集包含的项数少于id数组的项数");
        } else {
            return result.toArray(new ${table.className}[0]);
        }
    }
</#macro>

<#macro q>
    /**
     * 自定义查询
     */
    public static CustomQuerier q(String sqlmap, Map arg) {
        return new CustomQuerier(sqlmap, arg);
    }
</#macro>

<#macro where>
    /**
     * 简单Where查询
     * 传入的参数最终会转换为 'key1 = value1 and key2 = value2 and ...' 这样形式的字符串
     */
    public static ModelQuerier where(Map arg) {
        return new ModelQuerier("${table.fullName}").where(arg);
    }

    /**
     * Where查询
     * Map中的key和value做替换
     *    { type: "a", name: "b" }
     *    "type = #type# or name like #name# || '%'"
     * => "type = 'a' or name like 'b' || ‘%’"
     */
    public static ModelQuerier where(String where, Map arg) {
        return new ModelQuerier("${table.fullName}").where(where, arg);
    }
</#macro>

<#macro getFullTableName>
    /**
     * 得到对应数据表的全称
     */
    public String getFullTableName() {
        return "${table.fullName}";
    }
</#macro>

<#macro delete>
    /**
     * 删除
     */
    public void delete() {
        super.delete("${table.fullName}");
    }
</#macro>

<#macro belongs_to>
    <#list belongsToArray as belong>
${belong}
    <#if belong_has_next>

    </#if>
    </#list>
</#macro>

<#macro has_many>
    <#list hasManyArray as hasmany>
${hasmany}
    <#if hasmany_has_next>

    </#if>
    </#list>
</#macro>

<#macro has_one>
    <#list hasOneArray as hasone>
${hasone}
    <#if hasone_has_next>

    </#if>
    </#list>
</#macro>
