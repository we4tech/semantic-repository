<head>
  <script type="text/javascript"
          src="<%= request.getContextPath() %>/javascripts/prototype.js">
  </script>
  <script type="text/javascript">
      var Action = {
        notNull : function(pString) {
          return pString != null && pString.length > 0;
        },

        onSubmit : function(pForm) {
          try {
            $("submit-button").disabled = "true";
            var userName = pForm.userId.value;
            var password = pForm.password.value;
            var confirm = pForm.confirm.value;
            var admin = pForm.admin.checked;

            var state = this.notNull(userName)
                && this.notNull(password)
                && this.notNull(confirm)
                && password == confirm;

            // construct xml
            if (state) {
              var xml = "<request>";
              xml += "<item>";
              xml += "<target-class>user</target-class>";
              xml += "<title><![CDATA[" + userName;
              xml += "]]></title>";
              xml += "<fields>";
              xml += "<field name='title'><![CDATA[" + userName;
              xml += "]]></field>";
              xml += "<field name='user'><![CDATA[" + userName;
              xml += "]]></field>";
              xml += "<field name='password'><![CDATA[" + password;
              xml += "]]></field>";
              xml += "<field name='role_admin'>" + admin;
              xml += "</field>";
              xml += "</fields>";
              xml += "</item>";
              xml += "</request>";

              // send ajax request
              var url = "<%= request.getContextPath()%>/rest/service/register/user.xml";
              var params = "index=" + pForm.indexRepository.value + "&user=" + encodeURI(xml);
              var ajax = new Ajax.Request(
                  url,
                  {
                    method: "POST",
                    parameters: params,
                    onSuccess : function(r) {
                      alert("You account has been created.");
                      try {
                        var response = r.responseXML;
                        var el = response.getElementsByTagName("response")
                        if (el != null && el.length > 0) {
                          el = el[0]
                          var state = el.getAttribute("state");
                          if (state == "true") {
                            var itemEl = response.getElementsByTagName("item");
                            if (itemEl != null && itemEl.length > 0) {
                              itemEl = itemEl[0];
                              var uri = itemEl.firstChild.data;
                              // update div
                              var element = $("uri");
                              var template = element.innerHTML;
                              template = template.replace("{$uri}", uri);
                              element.innerHTML = template;
                              element.style.display = "block";
                            }
                          }
                        }
                      } catch (_E) {
                        alert(_E);
                      }
                      $("submit-button").removeAttribute("disabled");
                    },
                    onFailure : function(r) {
                      alert("Request failed - " + r);
                      $("submit-button").removeAttribute("disabled");
                    }
                  });
            } else {
              alert("Please fill up all the fields.");
            }

          } catch(_E) {
            alert(_E);
          }
          // prevent form submission
          return false;
        }
      }
    </script>
</head>