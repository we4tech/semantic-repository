<%@ page import="com.ideabase.repository.api.RepositoryService" %>
<%@ page import="com.ideabase.repository.api.API" %>
<%@ page import="java.util.List" %>
<%@ page import="com.ideabase.repository.common.object.GenericItem" %>
<%
  final RepositoryService repositoryService = API.giveMe().repositoryService();

  // find list of repository items with pagination
  final int max = 10;
  String stringOffset = request.getParameter("offset");
  final int offset;
  if (stringOffset == null) {
    offset = 0;
  } else {
    offset = Integer.valueOf(stringOffset);
  }
  final List<Integer> items = repositoryService.getAllItems(offset, max);
%>
<div id="container">
  <h2>manage items</h2>      
  <div class="navigation">
    total number of items <%= repositoryService.getAllItemsCount() %> |
    <a href="create.jsp">create item</a> |
    <a href="search.jsp">search</a>
  </div>

  <div class="items">
    <% if (items == null || items.isEmpty()) { %>
      <b>no item found.</b>
    <% } else { %>
      <% for (final Integer itemId: items) { %>
        <% final GenericItem item = repositoryService.getItem(itemId, GenericItem.class); %>
        <div class="item">
          <h4 valign="middle">
            #<%= item.getId() %> - <%= item.getTitle() %>
          </h4>
          <div style="float: right;">
            <a href="create.jsp?action=delete&itemId=<%= item.getId() %>"
               onclick="return confirm('do you really want to remove this item?')" title="delete">delete</a>,
            <a href="create.jsp?action=edit&itemId=<%= item.getId() %>" title="edit">edit</a>
          </div>
          <div class="clean"></div>
        </div>
      <% } %>
    <% } %>
    <div class="pagination">
      <a href="manage_items.jsp?offset=<%= ((offset - max) >= 0) ? (offset - max) : offset %>">&lt;older</a>
      <a href="manage_items.jsp?offset=<%= offset + max %>">newer&gt;</a>
    </div>
  </div>
</div>