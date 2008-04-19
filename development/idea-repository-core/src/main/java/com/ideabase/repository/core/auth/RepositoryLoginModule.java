/* $Id:RepositoryLoginModule.java 249 2007-12-02 08:32:47Z hasan $ */
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
 * $LastChangedBy:hasan $
 * $LastChangedDate:2007-12-02 14:32:47 +0600 (Sun, 02 Dec 2007) $
 * $LastChangedRevision:249 $
 ******************************************************************************
*/
package com.ideabase.repository.core.auth;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import org.apache.commons.codec.digest.DigestUtils;

import javax.security.auth.spi.LoginModule;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;
import javax.security.auth.callback.*;
import java.util.Map;
import java.util.List;
import java.io.IOException;

import com.ideabase.repository.api.API;
import com.ideabase.repository.common.object.User;
import com.ideabase.repository.common.object.Hit;
import com.ideabase.repository.common.Query;

/**
 * Implementation of Repository login module.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class RepositoryLoginModule implements LoginModule {

  /**
   * Logger instance
   */
  private static final Logger LOG =
          LogManager.getLogger(RepositoryLoginModule.class);
  /**
   * Debug enable state
   */
  private static final boolean DEBUG = LOG.isDebugEnabled();

  /**
   * Prompt message for user name
   */
  public static final String PROMPT_USER_NAME = "userName";

  /**
   * Prompt message for user password
   */
  public static final String PROMPT_USER_PASSWORD = "userPassword";

  /**
   * Echo on state.
   */
  private static final boolean ECHO_ON = false;

  /**
   * User principal type "user"
   */
  private static final String PRINCIPAL_USER = "user";

  /**
   * User principal type "admin"
   */
  private static final String PRINCIPAL_ADMIN = "admin";

  /**
   * Authentication subject
   */
  private Subject mSubject;

  /**
   * Authentication callback handler.
   */
  private CallbackHandler mCallbackHandler;

  /**
   * Map of shared state.
   */
  private Map mSharedState;

  /**
   * Map of authentication module options
   */
  private Map mOptions;

  /**
   * Authentication success state.
   */
  private boolean mSucceeded = false;

  /**
   * Repository user principal.
   */
  private RepositoryUserPrincipal mUser;

  /**
   * State of commit success state.
   */
  private boolean mCommitSucceeded;

  /**
   * Initiate authentication module process. instantiate {@see Subject},
   * {@see CallbackHandler}, shared state and options.
   * @exception throw {@see IllegalArgumentException} if callback handler
   *            is null.
   */
  public void initialize(final Subject pSubject,
                         final CallbackHandler pCallbackHandler,
                         final Map pSharedState,
                         final Map pOptions) {
    if (pCallbackHandler == null) {
      throw new IllegalArgumentException("CallbackHandler must be defined.");
    }
    LOG.debug("Initialize LoginModule.");
    mSubject = pSubject;
    mCallbackHandler = pCallbackHandler;
    mSharedState = pSharedState;
    mOptions = pOptions;
  }

  /**
   * clean out the states, request for retrieving user password and user name<br>
   * verify user name and password. if both match return successful
   * authentication.<br>
   * otherwise throw {@see LoginException} or return false.
   * @return true if login is successful,
   * @throws LoginException if authentication failed.
   */
  public boolean login() throws LoginException {
    LOG.debug("Login action in process.");
    mSucceeded = false;
    mCommitSucceeded = false;
    try {
      // find user name;
      final String[] userAndPassword = getUserAndPassword();
      final String userName = userAndPassword[0];
      final String userPassword = userAndPassword[1];

      // Try to authenticate
      LOG.debug("Trying to perform loging action.");
      authenticateUser(userName, userPassword);
      mSucceeded = true;
      LOG.debug("Login successful.");
    } catch (Exception e) {
      LOG.warn("Exception raised during authentication.", e);
      throw new LoginException("Login action failed.");
    }
    return mSucceeded;
  }

  /**
   * Perform Lucene index search by user name and user password. <br>
   * if indexer returns some value. it means the requset is from valid user.<br>
   * send another callback request, this callback request pack the user id.<br>
   * the callback handler can retrieve user object from storage.<br>
   * create new {@see RepositoryUserPrincipal}. <br>
   */
  private void authenticateUser(final String pUserName,
                                final String pUserPassword)
      throws LoginException, IOException, UnsupportedCallbackException {

    final String userHashKey = DigestUtils.shaHex(pUserName + pUserPassword);
    final User user = API.giveMe().repositoryService().
                      getItemByTitle(userHashKey, User.class);
    if (user != null) {
      final Integer userId = user.getId();
      // send other callback request with user id.
      Callback[] callbacks = new Callback[1];
      callbacks[0] = new TextOutputCallback(
          TextOutputCallback.INFORMATION, String.valueOf(userId));
      mCallbackHandler.handle(callbacks);

      if (DEBUG) {
        LOG.debug("Search result id - " + userId);
      }

      // create repository user principal
      if (!user.isAdmin()) {
        mUser = new RepositoryUserPrincipal(PRINCIPAL_USER, userId);
      } else {
        mUser = new RepositoryUserPrincipal(PRINCIPAL_ADMIN, userId);
      }
    } else {
      throw new LoginException("Login failed. Invalid user or password.");
    }
  }

  /**
   * Send callback request for user name and user password.<br>
   * @return return a string array with index 0 of user name and index 1
   *         of password.
   */
  private String[] getUserAndPassword()
      throws IOException, UnsupportedCallbackException {
    final String[] userInputs = new String[2];
    Callback[] callbacks = new Callback[2];
    callbacks[0] = new NameCallback(PROMPT_USER_NAME);
    callbacks[1] = new PasswordCallback(PROMPT_USER_PASSWORD, ECHO_ON);
    // send callback request to the authentication request sender
    mCallbackHandler.handle(callbacks);
    userInputs[0] = ((NameCallback) callbacks[0]).getName();
    userInputs[1] =
        String.valueOf(((PasswordCallback) callbacks[1]).getPassword());
    // clear password callback
    ((PasswordCallback) callbacks[1]).clearPassword();
    // return user name and password.
    return userInputs;
  }

  /**
   * if login not succeeded return false.<br>
   * else if subject is set to readonly throw exception.<br>
   * if subject pricipal doesn't contain user. add new
   * {@see RepositoryUserPricipal}. <br>
   * set {@code mCommitSucceeded} state = true
   * <br>
   * {@inheritDoc}
   */
  public boolean commit() throws LoginException {
    LOG.debug("Commit action is triggered.");
    if (!mSucceeded) {
      return false;
    } else {
      if (mSubject.isReadOnly()) {
        throw new LoginException("Subject is read-only");
      }
      // add Principals to the Subject
      if (!mSubject.getPrincipals().contains(mUser)) {
        mSubject.getPrincipals().add(mUser);
      }

      LOG.debug("commit - Authentication has completed successfully");
    }
    mCommitSucceeded = true;
    return true;
  }

  /**
   * If login wasn't succeeded return false.<br>
   * else if login succeeded but commit wasn't completed or failed<br>
   * clean out states and return true.<br>
   * <br>
   * {@inheritDoc}
   */
  public boolean abort() throws LoginException {
    LOG.debug("abort - Authentication has not completed successfully");
    if (!mSucceeded) {
      return false;
    } else if (mSucceeded && !mCommitSucceeded) {
      mSucceeded = false;
      mUser = null;
    } else {
      logout();
    }
    return true;
  }

  /**
   * remove user principal from subject and clean out the states.
   * <br>
   * {@inheritDoc}
   */
  public boolean logout() throws LoginException {
    if (mSubject.isReadOnly()) {
      throw new LoginException("Subject is read-only");
    }
    mSubject.getPrincipals().remove(mUser);

    // clean out state
    mSucceeded = false;
    mCommitSucceeded = false;
    mUser = null;

    LOG.debug("logout - Subject is being logged out");

    return true;
  }
}
