/* $Id: UserService.java 250 2008-01-07 10:18:29Z hasan $ */
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
package com.ideabase.repository.api;

import com.ideabase.repository.common.object.User;
import com.ideabase.repository.common.object.UserCredential;

import javax.security.auth.Subject;
import java.security.Permission;

/**
 * Authentication and Authorization service.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public interface UserService {

  /**
   * Set login module name.
   * @param pLoginModuleName login module name.
   */
  public void setLoginModuleName(final String pLoginModuleName);

  /**
   * Create new user on storage.
   * @param pUser user credential and role.
   * @return newly created user id.
   */
  public Integer registerUser(final User pUser);

  /**
   * Remove {@code User} profile from repository service.
   * @param pUser user profile object.
   */
  public void unregisterUser(final User pUser);

  /**
   * Authenticate user credentials and set user role and {@code authToken}.
   * which is used for later service invocation.<br>
   *
   * @param pUserCredential user credentials.
   * @return {@see Subject} authenticated subject is returned.
   */
  public Subject login(final UserCredential pUserCredential);

  /**
   * Verify whether user is allowed for certain action.
   * @return {@code true} if user is allowed.
   * @param pSubject logged in user subject.
   * @param pPermission check for permission of an action.
   */
  public boolean isAllowed(final Subject pSubject, 
                           final Permission pPermission);

  /**
   * Log off user session. and invalidate user authentication token.
   * @param pUser user from session.
   */
  public void logout(final User pUser);
}
