<div id="container">
  <h2>Congratulations!!</h2>

  <p>
    you have successfully <b>configured</b> your repository service.<br/>
    please <a href="#"
              onclick="document.getElementById('register').style.display = 'block'">register</a>
    an <b>account</b> or
    <a href="login.jsp">login</a> using your existing account.
    <br/>
  </p>

  <div id="uri" style="display: none">
    <b>Created uri</b> - {$uri}
  </div>
  <div id='register' style="display: none">
    <form action="/" onsubmit="return Action.onSubmit(this)">
      <label>index repository</label><br/>
      <%@ include file="../parts/indexes.jsp" %>
      <hr/>
      <label>user id</label><br/>
      <input class="text" type="text" name="userId"/><br/>
      <label>password</label><br/>
      <input class="text" type="password" name="password"/><br/>
      <label>confirm</label><br/>
      <input class="text" type="password" name="confirm"/><br/>
      <span><input type="checkbox" name="admin" value="1" checked="checked"/></span>
      <span><label>admin user</label></span><br/>

      <div class="center">
        <input type="submit" value="Register" id="submit-button"/>
      </div>
    </form>

    <div class="tools">
      <a href="manage_items.jsp">manage list of items</a>
    </div>
  </div>
</div>