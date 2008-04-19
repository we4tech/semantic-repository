/*
 * $Id:UserServiceImpl.java 249 2007-12-02 08:32:47Z hasan $
 ******************************************************************************
 * Copyright (C) 2007 somewhere in .Net ltd.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from somewhere in .Net LTD.  This notice must be included on
 * all copies, modifications and derivatives of this work.
 ******************************************************************************
 * $LastChangedBy:hasan $
 * $LastChangedDate:2007-12-02 14:32:47 +0600 (Sun, 02 Dec 2007) $
 * $LastChangedRevision:249 $
 ******************************************************************************
*/
package com.ideabase.repository.core.service;

import com.ideabase.repository.api.RepositoryService;
import com.ideabase.repository.api.StateManager;
import com.ideabase.repository.api.UserService;
import com.ideabase.repository.common.RequestState;
import com.ideabase.repository.common.exception.ServiceException;
import com.ideabase.repository.common.object.User;
import com.ideabase.repository.common.object.UserCredential;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.security.Permission;
import java.security.PrivilegedAction;

/**
 * Implementation of {@code UserService}. it is responsible to createItem, deleteItem,
 * authentication and authorization of Service user.
 *
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class UserServiceImpl implements UserService {

  private final Logger mLog = LogManager.getLogger(UserServiceImpl.class);
  private final RepositoryService mRepositoryService;
  private String mLoginModuleName;
  private StateManager mStateManager;

  /**
   * Default constructor, it accepts required dependency
   * {@see RepositoryService}
   * @param pRepositoryService required dependency repository service.
   */
  public UserServiceImpl(final RepositoryService pRepositoryService) {
    mRepositoryService = pRepositoryService;
  }

  public StateManager getStateManager() {
    return mStateManager;
  }

  public void setStateManager(final StateManager pStateManager) {
    mStateManager = pStateManager;
  }

  /**
   * {@inheritDoc}
   */
  public void setLoginModuleName(final String pLoginModuleName) {
    mLoginModuleName = pLoginModuleName;
  }

  /**
   * <h3>Password from {@see User} object will be hashed by SHA.</h3>
   * {@inheritDoc}
   * @param pUser {@inheritDoc}
   */
  public Integer registerUser(final User pUser) {
    // hash password using SHA algorithm.
    pUser.setTitle(buildUserHashKey(pUser));

    // create new user.
    return mRepositoryService.save(pUser);
  }

  private String buildUserHashKey(final User pUser) {
    return DigestUtils.shaHex(pUser.getUser() + pUser.getPassword());
  }

  /**
   * {@inheritDoc}
   * @param pUser {@inheritDoc}
   */
  public void unregisterUser(final User pUser) {
    if (pUser.getId() == null) {
      throw new ServiceException(pUser, "No id defined with parameter User.");
    }
    mRepositoryService.delete(pUser.getId());
  }

  /**
   * {@inheritDoc}
   */
  public Subject login(final UserCredential pUserCredential) {
    try {
      final CallbackHandlerImpl callbackHandler =
          new CallbackHandlerImpl(pUserCredential);
      final LoginContext loginContext =
          new LoginContext(mLoginModuleName, callbackHandler);
      loginContext.login();
      final Subject subject = loginContext.getSubject();
      if (mStateManager != null && subject != null) {
        final RequestState requestState = new RequestState();
        requestState.setStateId(pUserCredential.getStateId());
        requestState.setSubject(subject);
        requestState.setUserName(pUserCredential.getUser());
        mStateManager.addRequestStateForToken(
            pUserCredential.getStateId(), requestState);
      }
      return subject;
    } catch (LoginException e) {
      throw new ServiceException(pUserCredential,
          "Failed to authenticate an user with the credentials - "
              + pUserCredential, e);
    }
  }

  /**
   * {@inheritDoc}
   */
  public boolean isAllowed(final Subject pSubject, final Permission pPermission)
  {
    final SecurityManager securityManager;
    if (System.getSecurityManager() == null) {
      mLog.debug("No predefined security manager found.");
      securityManager = new SecurityManager();
    } else {
      securityManager = System.getSecurityManager();
    }

    try {
      mLog.debug("Do as privileged action.");
      Subject.doAsPrivileged(pSubject, new PrivilegedAction() {
        public Object run() {
          securityManager.checkPermission(pPermission);
          return null;
        }
      }, null);
      mLog.debug("user action is previleged.");
      return true;
    } catch (RuntimeException e) {
      // No logging here, because, if exception raised it refers to permission
      // failure.
      mLog.warn("Exception raised during verifying the authorization", e);
      return false;
    }
  }

  /**
   * {@inheritDoc}
   * @param pUser {@inheritDoc}
   */
  public void logout(final User pUser) {

  }

  public static class CallbackHandlerImpl implements CallbackHandler {
    private UserCredential mUserCredential;
    private Integer mId;

    public CallbackHandlerImpl(final UserCredential pUserCredential) {
      mUserCredential = pUserCredential;
    }

    public Integer getItemId() {
      return mId;
    }

    /**
     * Handle callback request, set user and password with in the Name and
     * Password callback request.
     */
    public void handle(Callback[] callbacks)
        throws IOException, UnsupportedCallbackException {
      if (callbacks != null && callbacks.length == 2) {
        NameCallback nameCallback = (NameCallback) callbacks[0];
        PasswordCallback passwordCallback = (PasswordCallback) callbacks[1];
        nameCallback.setName(mUserCredential.getUser());
        passwordCallback.
            setPassword(mUserCredential.getPassword().toCharArray());
      } else if (callbacks != null && callbacks.length == 1) {
        TextOutputCallback callback = (TextOutputCallback) callbacks[0];
        mId = Integer.parseInt(callback.getMessage());
      }
    }
  }
}
