<%@ page import="org.json.JSONObject" %>
<%@ page import="com.baosight.bssim.models.ConfigModel" %>
<%@ page import="org.json.JSONArray" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    JSONObject cfg = new ConfigModel("GlobalConfig").getJson();
    JSONArray schema_tables = cfg.optJSONArray("user_tables");
%>

<ul class="nav">
    <li>
        <a class="dropdown-toggle" data-toggle="dropdown" href="#">
            <i class="icon-th"></i>
            数据表
        </a>
        <ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
            <li class="nav-header">SCHEMA</li>
        <%
            if (schema_tables == null) {
        %>
            <li class="dropdown-submenu">
                <a tabindex="-1" href="javascript:void(0);">
                    无数据表
                </a>
            </li>
        <%
            } else {
        %>
            <%
                for (int i=0; i<schema_tables.length(); i++) {
                    JSONObject schema_table = schema_tables.optJSONObject(i);
                    String user = schema_table.optString("user");
                    JSONArray tables = schema_table.optJSONArray("tables");
                    if (user != null) {
            %>
            <li class="dropdown-submenu">
                <a tabindex="-1" href="javascript:void(0);">
                    <%=user%>
                </a>
                <ul class="dropdown-menu">
                    <li class="nav-header">TABLE</li>
                    <%
                        if (tables == null) {
                    %>
                        <li>
                            <a href="#"> 无数据表 </a>
                        </li>
                    <%
                        } else {
                            for (int j=0; j<tables.length();j++) {
                                String table = tables.optString(j);
                    %>
                        <li><a href="/gen/<%=user%>.<%=table%>"><%=table%></a></li>
                    <%
                            }
                        }
                    %>
                </ul>
            </li>
        <%
                    }
                }
            }
        %>
        </ul>
    </li>

</ul>