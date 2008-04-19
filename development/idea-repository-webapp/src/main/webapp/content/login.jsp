<%@ page import="com.ideabase.repository.api.API" %>
<%@ page import="com.ideabase.repository.common.object.UserCredential" %>
<%@ page import="javax.security.auth.Subject" %>
<%@ page import="com.ideabase.repository.common.WebConstants" %><%
  if (request.getMethod().equalsIgnoreCase("post")) {
    final String userName = request.getParameter("userName");
    final String password = request.getParameter("password");

    final UserCredential credential = new UserCredential(userName, password);
    final Subject subject = API.giveMe().userService().login(credential);
    if (subject != null) {
      session.setAttribute(WebConstants.SESSION_ATTR_USER_SUBJECT, subject);
      %>
      <script type="text/javascript">
        window.location = "manage_items.jsp";
      </script>
      <%
    }
  }
%>
<div id="container">
  <form action="login.jsp" method="post">
    <fieldset>
      <legend>authentication</legend>
      <label>user name</label><br/>
      <input type="text" name="userName" style="width: 100%"/><br/>
      <label>password</label><br/>
      <input type="password" name="password" style="width: 100%"/><br/>
      <input type="submit" name="action" value="login" style="float: right"/>
      <div class="clean"></div>
    </fieldset>
  </form>
</div>