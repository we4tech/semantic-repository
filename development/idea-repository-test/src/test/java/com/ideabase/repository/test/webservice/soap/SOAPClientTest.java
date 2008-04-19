/*
 * $Id: SOAPClientTest.java 250 2008-01-07 10:18:29Z hasan $
 * *****************************************************************************
 * Copyright (C) 2007 somewhere in .Net ltd.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from somewhere in .Net LTD.  This notice must be included on
 * all copies, modifications and derivatives of this work.
 * *****************************************************************************
 * $LastChangedBy: hasan $
 * $LastChangedDate: 2008-01-07 16:18:29 +0600 (Mon, 07 Jan 2008) $
 * $LastChangedRevision: 250 $
 * *****************************************************************************
*/
package com.ideabase.repository.test.webservice.soap;

import com.ideabase.repository.api.APIServiceKey;
import com.ideabase.repository.api.RepositoryService;
import com.ideabase.repository.api.UserService;
import com.ideabase.repository.common.object.User;
import com.ideabase.repository.test.BaseTestCase;
import com.ideabase.repository.test.TestCaseRepositoryHelper;

/**
 * SOAP based web service client test.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class SOAPClientTest extends BaseTestCase {

  private RepositoryService mRepositoryService;
  private UserService mUserService;
//  private SOAPClientManager mSOAPClientManager =
//      new SOAPClientManager("http://localhost:8080/repository/");

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mRepositoryService = (RepositoryService) mContext.
                         getBean(APIServiceKey.REPOSITORY_SERVICE);
    mUserService = (UserService) mContext.getBean(APIServiceKey.USER_SERVICE);
    TestCaseRepositoryHelper.fixRemoveAllItems(mRepositoryService);
  }

  public void testRegister() {
    final User user = new User() {{
      setUser("we4tech2");
      setPassword("hasankhan");
      setRole(User.FIELD_ROLE_ADMIN, true);
      setRole(User.FIELD_ROLE_READ, true);
      setRole(User.FIELD_ROLE_WRITE, true);
    }};

  }
}
