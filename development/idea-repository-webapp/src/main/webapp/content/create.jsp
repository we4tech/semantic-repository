<%@ page import="com.ideabase.repository.common.object.GenericItem" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="com.ideabase.repository.api.API" %>
<%@ page import="java.util.Collections" %>
<%!
  private static final String ATTR_ITEM = "_item";
%>
<%
  GenericItem item;
  if (session.getAttribute(ATTR_ITEM) != null) {
    item = (GenericItem) session.getAttribute(ATTR_ITEM);
  } else {
    item = new GenericItem();
    session.setAttribute(ATTR_ITEM, item);
  }

  // populate object
  if (request.getMethod().equalsIgnoreCase("post")) {
    item.setTitle(request.getParameter("title"));
    final Enumeration parameterNames = request.getParameterNames();
    while (parameterNames.hasMoreElements()) {
      final String parameterName = (String) parameterNames.nextElement();
      if (parameterName.startsWith("field_")) {
        item.addField(parameterName.replaceAll("field_", ""), request.getParameter(parameterName));
      }
    }
    session.setAttribute(ATTR_ITEM, item);
  }

  String message = null;

  // handle action
  final String action = request.getParameter("action");
  if (action != null) {
    if (action.endsWith("add")) {
      item.addField(request.getParameter("new_field"), "");
      session.setAttribute(ATTR_ITEM, item);
    } else if (action.startsWith("delete:")) {
      item.removeField(action.split(":")[1].trim());
      session.setAttribute(ATTR_ITEM, item);
    } else if (action.endsWith("save")) {
      if (request.getParameter("indexRepository") != null) {
        item.setIndexRepository(request.getParameter("indexRepository"));
      } else {
        item.setIndexRepository("default");
      }
      final Integer itemId = API.giveMe().repositoryService().save(item);
      message = "successfully created resource on /service/get/" + itemId;
      item.setTitle(null);
      item.setId(null);
      session.setAttribute(ATTR_ITEM, item);
      %>
      <script type="text/javascript">
        window.location = "manage_items.jsp";
      </script>
      <%
    } else if (action.endsWith("delete")) {
      final Integer itemId = Integer.valueOf(request.getParameter("itemId"));
      API.giveMe().repositoryService().delete(itemId);
      %>
      <script type="text/javascript">
        window.location = "manage_items.jsp";
      </script>
      <%
    } else if (action.endsWith("edit")) {
      final Integer itemId = Integer.valueOf(request.getParameter("itemId"));
      item = API.giveMe().repositoryService().getItem(itemId, GenericItem.class);
      session.setAttribute(ATTR_ITEM, item);
      %>
      <script type="text/javascript">
        window.location = "create.jsp";
      </script>
      <%
    }
  }
%>
<div id="container">
  <h2>create new item</h2>
  <% if (message != null) { %>
    <div class="message">
      <%= message %>
    </div>
  <% } %>
  <form method="post">
    <label>select index repository</label>
    <%@ include file="../parts/indexes.jsp" %><br/>
    <label>title</label><br/>
    <input tabindex="0" style="width: 100%" type="text" name="title" value="<%= item.getTitle() %>"/><br/>
    <label>description</label><br/>
    <textarea tabindex="1" style="width: 100%; height: 200px" name="field_description"><%= item.getField("description") %></textarea><br/>
    <div class="fields">
      <% final Map<String, String> fields = item.getFields(); %>
      <% for (Map.Entry<String, String> entry : fields.entrySet()) {
          if (!entry.getKey().equals("description") && !entry.getKey().equals("title")) {
          %>
            <label>
              <%= entry.getKey() %>
              (<input type="submit" name="action" value="delete: <%= entry.getKey() %>"/>)
            </label><br/>
            <input style="width: 100%" type="text"
                   name="field_<%= entry.getKey() %>"
                   value="<%= entry.getValue() %>"/><br/>
          <%
        }
      }%>
    </div>
    <div class="add-form">
      <fieldset>
        <legend>add field</legend>
        <input style="width: 80%" type="text" name="new_field"/>
        <input align="right" type="submit" name="action" value="add"/>
      </fieldset>
    </div>
    <br/>
    <div style="float: right">
      <a href="manage_items.jsp">items</a> | <input type="submit" name="action" value="save"/>
    </div>
    <div class="clean"></div>
  </form>
</div>