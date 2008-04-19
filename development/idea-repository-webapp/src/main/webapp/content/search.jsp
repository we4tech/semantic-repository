<%@ page import="java.util.List" %>
<%@ page import="com.ideabase.repository.api.RepositoryService" %>
<%@ page import="com.ideabase.repository.api.API" %>
<%@ page import="com.ideabase.repository.common.Query" %>
<%@ page import="org.apache.lucene.queryParser.QueryParser" %>
<%@ page import="org.apache.lucene.analysis.standard.StandardAnalyzer" %>
<%@ page import="com.ideabase.repository.common.object.PaginatedList" %>
<%@ page import="com.ideabase.repository.common.object.Hit" %>
<%!
  final RepositoryService mRepositoryService = API.giveMe().repositoryService();
  final QueryParser mQueryParser = new QueryParser("title", new StandardAnalyzer());
  final int MAX = 10;
%>
<%
  PaginatedList<Hit> searchResults = null;
  String queryString = request.getParameter("query");
  if (queryString == null) {
    queryString = "";
  }
  int currentPage = 1;
  if (request.getMethod().equalsIgnoreCase("post")) {
    final String action = request.getParameter("action");
    if (action != null) {
      if (action.endsWith("search")) {
        currentPage = 1;
        int offset = 0;
        final Query query = new Query(mQueryParser.parse(queryString));
        query.skipRows(offset);
        query.maxRows(MAX);
        searchResults = mRepositoryService.getItemsByQuery(query);
        session.setAttribute("QUERY", queryString);
      }
    }
  }

  final String offsetNumber = request.getParameter("page");
  if (offsetNumber != null) {
    queryString = (String) session.getAttribute("QUERY");
    final Query query = new Query(mQueryParser.parse(queryString));
    currentPage = Integer.valueOf(offsetNumber);
    query.skipRows((currentPage - 1) * MAX);
    query.maxRows(MAX);
    searchResults = mRepositoryService.getItemsByQuery(query);
  }
%>
<div id="container">
  <h2>search</h2>
  <div class="navigation">
    <a href="create.jsp">create item</a> |
    <a href="manage_items.jsp">manage items</a>
  </div>
  <div id="search-form">
    <form action="search.jsp" method="post">
      <input type="text" name="query" value="<%= queryString %>"/> <input type="submit" name="action" value="search"/>
    </form>
  </div>

  <div id="search-results">
    <% if (searchResults == null) {
      %>no result found.<%
    } else {
      %><ul><%
      for (final Object object : searchResults) {
        final Hit hit = (Hit) object;
        %>
          <li>
            #<%= hit.getId() %> - <a href="#<%= hit.getTitle() %>"><%= hit.getTitle()%></a>
          </li>
        <%
      }

      int prevPage = currentPage - 1;
      if (prevPage < 1) {
        prevPage = 1;
      }

      int nextPage = currentPage + 1;
      if (nextPage > searchResults.getPageCount()) {
        nextPage = searchResults.getPageCount();
      }
      %>
      </ul>
      <div class="navigation">
        <a href="?page=<%= prevPage %>">&lt; older</a>&nbsp;<%
        for (int i = 0; i < searchResults.getPageCount(); i++) { %><%
          if (i != 0) { %>,&nbsp;<% } %><a href="?page=<%= (i+1) %>"><%= (i+1) %></a><%
        } %>&nbsp;<a href="?page=<%= nextPage %>">newer &gt;</a>
      </div>
      <%
    } %>
  </div>
</div>