<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.ideabase.repository.api.API" %>
<%@ page import="com.ideabase.repository.core.service.RepositoryServiceImpl" %>
<%@ page import="com.ideabase.repository.core.index.RepositoryItemIndex" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.lang.reflect.Proxy" %>
<%@ page import="com.ideabase.repository.common.WebConstants" %>
<html>
  <%@ include file="parts/header.jsp" %>
  <style type="text/css">
    body {
      font-size: 20px;
      font-family: verdana;
    }

    b {
      background: lightYellow;
    }

    #container {
      width: 300px;
      margin-left: auto;
      margin-right: auto;
      border: 1px solid #ccc;
      padding: 10px;
      margin-top: 5%;
    }

    h2 {
      margin: 0px;
      padding: 0px;
    }

    #register {
      width: 200px;
      margin-left: auto;
      margin-right: auto;
    }

    #register .text {
      width: 100%;
      height: 30px;
      padding: 2px;
      font-size: 18px;
    }

    #register .center {
      text-align: right;
    }
    #uri {
      padding: 5px;
      border: 2px solid #ccc;
      background: aquamarine;
      margin-bottom: 5px;
      margin-top: 5px;
    }
    .navigation {
      font-size: 10px;
      text-align: right;
    }
    .items {
      font-size: 10px;
      padding: 5px;
    }
    .items .item {
      padding: 2px;
      border-bottom: 1px solid #ccc;
    }
    .clean {
      clear:both;
    }
    .fields {
      padding: 5px;
      border: 2px solid gold;
      background: lightyellow;
    }
    #search-results ul {
      margin: 0px;  
    }

  </style>
  <body>
    সগতম
    <%
      final boolean loggedOn = session.getAttribute(WebConstants.SESSION_ATTR_USER_SUBJECT) != null;
      final String requestedURI = request.getRequestURI();
      if (loggedOn && requestedURI.endsWith("manage_items.jsp")) {
        %><jsp:include page="content/manage.jsp"/><%
      } else if (loggedOn && requestedURI.endsWith("create.jsp")) {
        %><jsp:include page="content/create.jsp"/><%
      } else if (requestedURI.endsWith("login.jsp")) {
        %><jsp:include page="content/login.jsp"/><%
      } else if (loggedOn && requestedURI.endsWith("search.jsp")) {
        %><jsp:include page="content/search.jsp"/><%
      } else {
        %><jsp:include page="content/index.jsp"/><%
      }
    %>

  </body>
</html>
