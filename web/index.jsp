<%
  RequestDispatcher dispatcher = request.getRequestDispatcher("/gen");
  dispatcher.forward(request,response);
%>