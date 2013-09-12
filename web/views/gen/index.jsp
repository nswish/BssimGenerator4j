<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="/views/layouts/main_header.jsp" />
<style>
    tr td:first-child {
        width: 5%;
        text-align: center;
    }

    tr td:first-child + td {
        width: 20%;
    }

    tr td:first-child + td + td {
        width: 40%;
    }

    tr td:first-child + td + td + td {
        width: 10%;
        text-align: center;
    }

    tr td:first-child + td + td + td + td {
        width: 10%;
        text-align: center;
    }

    tr td:first-child + td + td + td + td + td {
        width: 15%;
        text-align: center;
    }
</style>
<div>
    <% List<Map<String, Object>> result = (List<Map<String, Object>>)request.getAttribute("result"); %>
    <%
        for(int i=0; i<result.size(); i++){
            List<Map<String, Object>> rows = (List<Map<String, Object>>)result.get(i);
            String user = rows.get(0).get("OWNER")+"";
    %>
    <div class="region">
        <div class="region-head">
            <span style="font-size: 15px;">用户：<%=user%></span>
        </div>
        <div class="region-body">
            <table class="table table-bordered">
                <tr>
                    <th>#</th>
                    <th>表名</th>
                    <th>注释</th>
                    <th>字段数</th>
                    <th>索引数</th>
                    <th>操作</th>
                </tr>
                <% for(int j=0; j<rows.size(); j++) {%>
                <tr>
                    <% Map<String, Object> row = (Map<String, Object>)rows.get(j); %>
                    <td><%=j+1%></td>
                    <td><%=row.get("TABLE_NAME")%></td>
                    <td><%=row.get("COMMENTS")%></td>
                    <td><%=row.get("COLUMN_COUNT")%></td>
                    <td><%=row.get("INDEX_COUNT")%></td>
                    <td><a href="/gen/<%=user+"."+row.get("TABLE_NAME")%>">生成代码</a></td>
                </tr>
                <% } %>
            </table>
        </div>
    </div>
    <br />
    <% } %>
</div>
<jsp:include page="/views/layouts/main_footer.jsp" />