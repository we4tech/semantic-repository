/* $Id$ */
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
 * $LastChangedBy$
 * $LastChangedDate$
 * $LastChangedRevision$
 ******************************************************************************
*/
package com.ideabase.repository.webservice.advice;

import com.ideabase.repository.api.StateManager;
import com.ideabase.repository.api.UserService;
import com.ideabase.repository.common.RequestState;
import com.ideabase.repository.common.WebConstants;
import com.ideabase.repository.core.auth.URLPermission;
import com.ideabase.repository.webservice.controller.RESTfulAction;
import com.ideabase.repository.webservice.controller.RESTfulController;
import com.ideabase.repository.webservice.exception.ActionExecutionException;
import com.ideabase.repository.webservice.exception.AuthenticationException;
import com.ideabase.repository.webservice.exception.AuthorizationException;
import com.ideabase.repository.webservice.helper.URIParameterHelper;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Control AA for web contxt related request.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
@Aspect
public class WebAuthenticationAndAuthorizationAdvice {

  private static final Logger LOG =
      LogManager.getLogger(WebAuthenticationAndAuthorizationAdvice.class);
  private static final boolean DEBUG = LOG.isDebugEnabled();

  private final UserService mUserService;
  private StateManager mStateManager;

  /**
   * Default constructor, it accepts default dependency on {@see UserService}
   * @param pUserService default dependency.
   */
  public WebAuthenticationAndAuthorizationAdvice(
      final UserService pUserService) {
    mUserService = pUserService;
  }

  public void setStateManager(final StateManager pStateManager) {
    mStateManager = pStateManager;
  }

  public StateManager getStateManager() {
    return mStateManager;
  }

  /**
   * Retrieve {@see RESTfulController} and http request response object.<Br>
   * verify whether user session contains the valid {@see Subject} object.<br>
   * if user is already authenticated, verify user authorization.<br>
   * if user is authorized, invoke {@code processAuthorizedAction} method from
   * {@see RESTfulController}.<br>
   * otherwise invoke {@code processUnauthorizedAction} method.<br>
   *
   * @param pProceedingJoinPoint intermediate join point state.
   */
  @Around("com.ideabase.repository.core.aspect.ArchitecturePointcuts." +
          "webServiceOperation()")
  public void verifyAuthentication(
      final ProceedingJoinPoint pProceedingJoinPoint) {
    LOG.debug("Verify authentication.");

    // find target object instance.
    final RESTfulController controller =
        (RESTfulController) pProceedingJoinPoint.getTarget();

    // find http servlet request and response
    final Object[] arguments = pProceedingJoinPoint.getArgs();
    final HttpServletRequest request = (HttpServletRequest) arguments[0];
    final HttpServletResponse response = (HttpServletResponse) arguments[1];
    final HttpSession session = request.getSession();

    if (DEBUG) {
      LOG.debug("Request object - " + request);
    }
    // logged on user must contain subject object on session context.
    try {
      if (DEBUG) {
        LOG.debug("Request uri - " + request.getRequestURI());
      }
      // Build restful action object from request context.
      final RESTfulAction action =
          URIParameterHelper.buildRESTfulAction(request);
      try {
        verifyAuthenticationAndAuthorization(
            action, session, request, response, controller);
      } catch (Throwable e) {
        if (DEBUG) {
          LOG.debug("Failed to process an action.", e);
        }
        if (e instanceof AuthorizationException) {
          controller.processErrorAction(action,
            RESTfulController.ErrorCode.UNAUTHORIZED_ACTION, request, response);
        } else if (e instanceof ActionExecutionException) {
          controller.processErrorAction(
              action, RESTfulController.ErrorCode.FAILURE_EXECUTION,
              request, response);
        } else if ((e instanceof AuthenticationException)
                || (e instanceof LoginException)) {
          controller.processErrorAction(
              action, RESTfulController.ErrorCode.LOGIN_FAILED,
              request, response);
        } else {
          controller.processErrorAction(
              action, RESTfulController.ErrorCode.INVALID_ACTION,
              request, response);
        }
      }
    } catch (Throwable t) {
      if (DEBUG) {
        LOG.debug("Exception stacktrace - ", t);
      }
      controller.processErrorAction(null,
          RESTfulController.ErrorCode.INVALID_ACTION, request, response);
    }
  }

  private void verifyAuthenticationAndAuthorization(
      final RESTfulAction pAction, final HttpSession pSession,
      final HttpServletRequest pRequest, final HttpServletResponse pResponse,
      final RESTfulController pController)
      throws Exception {
    // if user already logged on, this method will return Subject object
    // from session context.
    final Subject subject = userAlreadyAuthenticated(pRequest, pSession);
    if (subject == null) {
      try {
        pController.processUnauthenticatedAction(pAction, pRequest, pResponse);
      } catch (Throwable t) {
        throw AuthenticationException.aNew(t);
      }
    } else {
      // verify user authorization
      verifyUserAuthorization(pAction, subject, pRequest,
                              pResponse, pController);
    }
  }

  /**
   * <h3>Process authorization</h3>
   * if user requested uri has permission invoke
   * {@see RESTfulController#processAuthorizedAction}.<br>
   * otherwise invoke {@see RESTfulController#processUnauthenticatedAction).<br>
   */
  private void verifyUserAuthorization(final RESTfulAction pAction,
                                       final Subject pSubject,
                                       final HttpServletRequest pRequest,
                                       final HttpServletResponse pResponse,
                                       final RESTfulController pController)
      throws Exception {
    final String requestUri = pRequest.getRequestURI();
    final String method = pRequest.getMethod().toUpperCase();
    URLPermission urlPermission = new URLPermission(requestUri, method);

    // if user is authorized to access the requested uri.
    if (mUserService.isAllowed(pSubject, urlPermission)) {
      if (DEBUG) {
        LOG.debug("Processing authorized pAction for permission - " +
                  urlPermission);
      }
      try {
        pController.processAuthorizedAction(pAction, pRequest, pResponse);
      } catch (Throwable e) {
        throw ActionExecutionException.aNew(e);
      }
    }
    // user is not authorized to access the requested uri.
    else {
      throw AuthorizationException.IDENTICAL;
    }
  }

  /**
   * Verify whether request session context contains
   * {@see WebConstants#SESSION_ATTR_USER_SUBJECT} attribute.<br>
   * if this attribute is exists, it refers to an authenticated request.<br>
   * otherwise it is refered to an unauthenticated request.<br>
   *
   * @param pRequest http request
   * @param pSession http session context.
   * @return the {@see Subject} from user session context.
   */
  private Subject userAlreadyAuthenticated(final HttpServletRequest pRequest,
                                           final HttpSession pSession) {
    if (DEBUG) {
      LOG.debug("Verify whether user already authenticated - " + pSession);
    }
    // find subject from http request parameter
    final String authToken =
        pRequest.getParameter(WebConstants.PARAM_AUTH_TOKEN);
    if (DEBUG) {
      LOG.debug("Authentication token - " + authToken);
    }
    if (mStateManager != null && authToken != null) {
      if (DEBUG) {
        LOG.debug("Verify state through StateManager instead of " +
                  "typical http session.");
      }
      final RequestState requestState =
          mStateManager.getRequestStateForToken(authToken);
      LOG.debug("RequestState - " + requestState);
      LOG.debug("StateManager - " + mStateManager);
      if (requestState != null) {
        if (DEBUG) {
          LOG.debug("Authentication token is accepting through " +
              "http request parameter.");
        }
        return requestState.getSubject();
      }
    } else {
      if (DEBUG) {
        LOG.debug("Authentication token is accepting through http " +
                  "request session.");
      }
      return (Subject) pSession.
          getAttribute(WebConstants.SESSION_ATTR_USER_SUBJECT);
    }
    return null;
  }
}
