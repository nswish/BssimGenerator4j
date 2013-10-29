<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>

<%
    String messageStatus = "no-message";
    if(request.getAttribute("message") != null)
        messageStatus = request.getAttribute("message") instanceof String ? "success" : "error";
%>
<!DOCTYPE html>
<html>
<head>
    <link href="/bootstrap/css/bootstrap.min.css" rel="stylesheet" media="screen" />
    <link href="/assets/css/main.css" rel="stylesheet" media="screen" />
    <script src="/jQuery/jquery-1.7.2.min.js"></script>
    <title></title>
</head>
<body>
<div id="header" class="navbar navbar-fixed-top">
    <div class="navbar-inner">
        <div class="center width1000">
            <a class="brand" href="#">BSSIM 辅助开发工具</a>
            <ul class="nav">
                <li><a href="/"><i class="icon-home"></i>首页</a></li>
            </ul>
            <jsp:include page="/views/gen/_tables_drop_menu.jsp" />
            <ul class="nav">
                <li><a href="/config">
                    <i class="icon-cog"></i>
                    配置
                </a></li>
            </ul>
            <ul class="nav">
                <li><a href="/doc/main"><i class="icon-book"></i>文档</a></li>
            </ul>
        </div>
    </div>
</div>
<div id="message" class="<%=messageStatus%>">
    <% if (request.getAttribute("message") != null) { %>
    <div class="width1000 center">
        <a id="message_close_tag" style="text-decoration: none;color: white;float: right;margin-right:3px;" href="#">X</a>
        <pre id="message_text"><%= request.getAttribute("message") %></pre>
    </div>
    <script>
        $('#message_close_tag').bind('click', function() {
            $('#message').removeClass('success').removeClass('error').addClass('no-message');
            $('#message_text').parent().empty();
        });
    </script>
    <% } %>
</div>
<div id="content" class="center width1000">