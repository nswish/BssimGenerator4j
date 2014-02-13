<!DOCTYPE html>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="EF" uri="/WEB-INF/framework/tlds/EF-2.0.tld" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <script type="text/javascript" src="./EF/iplat-ui-2.0.js"></script>
    <script type="text/javascript" src="./${firstModule}<#if secondModule?has_content>/${secondModule}</#if>/${jspName}.js"></script>
    <link href="bssim/css/bssim.css" rel="stylesheet" />
</head>
<body>
    <form id="mainForm" method="POST" action='<%=request.getContextPath()+"/DispatchAction.do"%>'>
        <jsp:include flush="false" page="../../EF/Form/iplat.ef.head.jsp"></jsp:include>

        <div id="ef_region_inqu" title="查询条件"><div>
            <table class="bssim-table">
    <#if queryList?has_content>
        <#list queryList as query>
            <#if query_index % 3 == 0>
                <tr>
            </#if>
            <#list table.columns as column>
                <#if column.name == query>
                    <td class="col-1-r">${column.comment}：</td>
                    <td class="col-1"><EF:EFInput ename="${column.camelName}" blockId="inqu_status" row="0"></EF:EFInput></td>
                </#if>
            </#list>
            <#if query_has_next>
                <#if query_index % 3 == 2>
                </tr>
                </#if>
            <#else>
                <#list 1..(2-query_index%3) as n>
                    <td class="col-1-r"></td>
                    <td class="col-1"></td>
                </#list>
                </tr>
            </#if>
        </#list>
    <#else>
                <tr>
                    <td class="col-1-r"></td>
                    <td class="col-1"></td>
                    <td class="col-1-r"></td>
                    <td class="col-1"></td>
                    <td class="col-1-r"></td>
                    <td class="col-1"></td>
                </tr>
    </#if>
            </table>
        </div></div>

        <div id="ef_region_result" title="查询结果">
            <div id="ef_grid_result" style="overflow: hidden;">
            </div>
        </div>

        <EF:EFRegion />

        <EF:EFGrid blockId="result" style="operationBar:false;" readonly="true">
<#list table.columns as column>
    <#if ["REC_CREATOR", "REC_CREATE_TIME", "REC_REVISOR", "REC_REVISE_TIME", "ARCHIVE_FLAG"]?seq_contains(column.name)>
    <#else>
        <#if column.name == "ID">
            <EF:EFColumn ename="${column.camelName}" visible="false"></EF:EFColumn>
        <#else>
            <EF:EFColumn ename="${column.camelName}" cname="${column.comment}"></EF:EFColumn>
        </#if>
    </#if>
</#list>
        </EF:EFGrid>

        <div style='display: none'><div id='dialog' style="background-color: white;">
            <EF:EFInput type="hidden" blockId="save" row="0" ename="id"></EF:EFInput>
            <table class="bssim-table">
        <#list table.columns as column>
            <#if ["REC_CREATOR", "REC_CREATE_TIME", "REC_REVISOR", "REC_REVISE_TIME", "ARCHIVE_FLAG", "ID"]?seq_contains(column.name)>
            <#else>
                <tr>
                    <td class="col-3-r">${column.comment}：</td><td class="col-4"><EF:EFInput blockId="save" row="0" ename="${column.camelName}"></EF:EFInput></td>
                </tr>
            </#if>
        </#list>
                <tr>
                    <td></td><td><EF:EFButton ename="save" cname="保存"></EF:EFButton></td>
                </tr>
            </table>
        </div></div>

        <jsp:include flush="false" page="../../EF/Form/iplat.ef.tail.jsp"></jsp:include>
    </form>
</body>
</html>