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
<div id="header" class="navbar">
    <div class="navbar-inner">
        <div class="center width1000">
            <a class="brand" href="#">BSSIM 辅助开发工具</a>
            <ul class="nav">
                <li><a href="/">首页</a></li>
            </ul>
            <ul class="nav">
                <li><a href="/config">配置</a></li>
            </ul>
        </div>
    </div>
    <div id="message" class="<%=messageStatus%>">
        <% if (request.getAttribute("message") != null) { %>
        <div class="width1000 center">
            <pre id="message_text"><%= request.getAttribute("message") %></pre>
        </div>
        <% } %>
    </div>
</div>

<div id="content" class="center width1000">