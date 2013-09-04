<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:include page="/views/layouts/main_header.jsp" />

<script src="/codemirror/lib/codemirror.js" ></script>
<link rel="stylesheet" href="/codemirror/lib/codemirror.css" />
<script src="/codemirror/mode/javascript/javascript.js" ></script>

<script>
    $(document).ready(function(){
        var myCodeMirror = CodeMirror.fromTextArea($('#configuration').get(0), {
            mode : 'text/javascript',
            smartIndent : false,
            lineNumbers : true
        });

        myCodeMirror.setSize('100%', 'auto');
    });
</script>

<style>
    #config_div {
        border: 1px lightgrey solid;
    }
</style>

<form id="config" name="config" method="POST" action="/config/update">
    <div id="config_div">
        <textarea id="configuration" name="configuration"><%=request.getAttribute("config")%></textarea>
    </div>
    <a href="javascript: document.config.submit();" class="btn pull-right">保存</a>
</form>

<jsp:include page="/views/layouts/main_footer.jsp" />