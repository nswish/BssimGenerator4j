<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String id = request.getAttribute("id")+"";
    String script_content = request.getAttribute("script_content")+"";
    String javaCode = request.getAttribute("javaCode")+"";
    String xmlCode = request.getAttribute("xmlCode")+"";
%>

<jsp:include page="/views/layouts/main_header.jsp" />
<script src="/codemirror/lib/codemirror.js"></script>
<link rel="stylesheet" href="/codemirror/lib/codemirror.css">
<link rel="stylesheet" href="/codemirror/theme/eclipse.css">
<script src="/codemirror/mode/javascript/javascript.js"></script>
<script src="/codemirror/mode/clike/clike.js"></script>
<script src="/codemirror/mode/xml/xml.js"></script>

<script>
    $(document).ready(function(){
        CodeMirror.fromTextArea($('#script_content').get(0), {
            mode : 'javascript',
            theme : 'eclipse',
            lineNumbers : true
        }).setSize('100%', 'auto');

        CodeMirror.fromTextArea($('#javaCode').get(0), {
            mode : 'clike',
            theme : 'eclipse'
        }).setSize('100%', 'auto');

        CodeMirror.fromTextArea($('#xmlCode').get(0), {
            mode : 'xml',
            theme : 'eclipse'
        }).setSize('100%', 'auto');

        $('#xml_div, #script_div').hide();
    });

    var showjava = function() {
        $('#xml_div').hide();
        $('#java_div').show();
    }

    var showxml = function() {
        $('#java_div').hide();
        $('#xml_div').show();
    }

    var showscript = function() {
        $('#script_div').show();
    }

    var hidescript = function() {
        $('#script_div').hide();
    }
</script>

<div style="border-bottom: grey dashed 1px;">
    <a href="javascript: showscript();"><%= id %></a>
    <a class="pull-right" href='javascript: $("#gen_code_form").submit();'>提交</a>
</div>

<div id="script_div" style="padding-top: 4px;">
    <form id="script_form" action="/gen/<%=id%>/update" method="POST">
        <textarea id="script_content" name="script_content"><%=script_content%></textarea>
        <a href='javascript: $("#script_form").submit();' class="pull-right">保存</a>
        <a href='javascript: hidescript();' class="pull-right" style="padding-right: 8px;">取消</a>
    </form>
</div>

<div id="toggle_div" style="padding: 5px 0px 5px 0px;margin-top: 10px; text-align: center;">
    <a href="javascript: showjava();">Java</a> |
    <a href="javascript: showxml();">Xml</a>
</div>

<form id="gen_code_form" method="POST" action="/gen/<%=id%>/commit">
    <div id="java_div" style="border: 1px grey dashed;">
        <textarea id="javaCode" name="javaCode"><%=javaCode%></textarea>
    </div>

    <div id="xml_div" style="border: 1px grey dashed;">
        <textarea id="xmlCode" name="xmlCode"><%=xmlCode%></textarea>
    </div>
</form>

<br />

<jsp:include page="/views/layouts/main_footer.jsp" />