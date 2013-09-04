<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="/views/layouts/main_header.jsp" />
<div>
    <% List<Map<String, Object>> result = (List<Map<String, Object>>)request.getAttribute("result"); %>
    <%
        for(int i=0; i<result.size(); i++){
            List<Map<String, Object>> rows = (List<Map<String, Object>>)result.get(i);

            if (rows.size() == 0)break;

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
                    <td style="text-align: center;"><%=j+1%></td>
                    <td><%=row.get("TABLE_NAME")%></td>
                    <td><%=row.get("COMMENTS")%></td>
                    <td style="text-align: center;"><%=row.get("COLUMN_COUNT")%></td>
                    <td style="text-align: center;"><%=row.get("INDEX_COUNT")%></td>
                    <td style="text-align: center;"><a href="/gen/<%=user+"."+row.get("TABLE_NAME")%>">生成代码</a></td>
                </tr>
                <% } %>
            </table>
        </div>
    </div>
    <% } %>
</div>
<jsp:include page="/views/layouts/main_footer.jsp" />