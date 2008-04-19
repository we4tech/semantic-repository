/* $Id: UserServiceTest.java 250 2008-01-07 10:18:29Z hasan $ */
/*
 ******************************************************************************
 *   Copyright (C) 2007 IDEASense, (hasin & hasan) 
 *
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 ******************************************************************************
 * $LastChangedBy: hasan $
 * $LastChangedDate: 2008-01-07 16:18:29 +0600 (Mon, 07 Jan 2008) $
 * $LastChangedRevision: 250 $
 ******************************************************************************
*/
package com.ideabase.repository.test.api;

import com.ideabase.repository.test.BaseTestCase;
import com.ideabase.repository.test.TestCaseRepositoryHelper;
import com.ideabase.repository.api.UserService;
import com.ideabase.repository.api.APIServiceKey;
import com.ideabase.repository.api.RepositoryService;
import com.ideabase.repository.common.object.UserCredential;
import com.ideabase.repository.common.object.User;
import com.ideabase.repository.core.auth.URLPermission;
import com.ideabase.repository.core.auth.RepositoryUserPrincipal;

import javax.security.auth.Subject;
import java.security.Permission;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Authentication and authorization service test.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class UserServiceTest extends BaseTestCase {

  private UserService mUserService;
  private RepositoryService mRepositoryService;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mUserService =
        (UserService) mContext.getBean(APIServiceKey.USER_SERVICE);
    mRepositoryService =
        (RepositoryService) mContext.getBean(APIServiceKey.REPOSITORY_SERVICE);

    assertNotNull("Failed to load user service", mUserService);
    TestCaseRepositoryHelper.fixRemoveAllItems(mRepositoryService);
  }

  /**
   * Ensure user service can authenticate user credentials.
   * Successful authentication ended with a {@code User} profile object.
   */
  public void testLogin() {
    final String userName = "veryrandom-user" + Math.random();
    final String password = DigestUtils.shaHex("password is md5");

    // create user
    final Integer userId = TestCaseRepositoryHelper.
        fixCreateUser(mUserService, userName, password);

    // authenticate user
    final UserCredential userCredential =
        new UserCredential(userName, password);

    // authenticate user.
    final Subject subject = mUserService.login(userCredential);
    // retrieve user principal.
    final RepositoryUserPrincipal principal =
        subject.getPrincipals(RepositoryUserPrincipal.class).iterator().next();

    // verify 
    assertNotNull("Authentication failed.", subject);
    assertNotNull("Authenticated user principal is null.", principal);
    assertNotNull("Authenticated user id is not attached with principal.",
                  principal.getId());
    LOG.debug("Subject - " + subject);
  }

  public void testAuthorization() {
    final String userName = "veryrandom-user" + Math.random();
    final String password = DigestUtils.shaHex("password is md5");

    // create user
    final Integer userId = TestCaseRepositoryHelper.
        fixCreateUser(mUserService, userName, password);

    // authenticate
    final UserCredential userCredential =
        new UserCredential(userName, password);
    final Subject subject = mUserService.login(userCredential);

    // verify authentication
    assertNotNull("Authentication failed.", subject);

    // verify authorization
    final Permission permission =
        new URLPermission("service/get/23.xml", "GET");
    assertTrue("Authorization failed, user not allowed to access an URL",
               mUserService.isAllowed(subject, permission));
  }

  public void testRegister() {
    final User user = new User();
    user.setUser("gmap");
    user.setPassword("gmap");
    user.setRole(User.FIELD_ROLE_READ, true);
    user.setRole(User.FIELD_ROLE_WRITE, true);
    user.setRole(User.FIELD_ROLE_ADMIN, true);

    mUserService.registerUser(user);
    assertNotNull("user has not been created.", user.getId());
    LOG.debug("Newly created user id - " + user.getId());

    mUserService.login(new UserCredential(
        user.getUser(), user.getPassword()));
  }

  @Override
  protected void tearDown() throws Exception {
//    TestCaseRepositoryHelper.fixRemoveAllItems(mRepositoryService);
  }
}
