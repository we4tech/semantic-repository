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
package com.ideabase.repository.webservice.controller;

import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public interface RESTfulController extends Controller {

  /**
   * Invoke this method when unauthenticated service is requested.
   * @param pAction restful action parameters
   * @param pRequest servlet request
   * @param pResponse servlet response
   * @throws Exception if exception raised during unauthenticated process.
   */
  void processUnauthenticatedAction(final RESTfulAction pAction,
                                    final HttpServletRequest pRequest,
                                    final HttpServletResponse pResponse)
      throws Exception;

  /**
   * Invoke this method when service raise any error.
   * @param pAction restful action
   * @param pErrorCode error type.
   * @param pRequest servlet request.
   * @param pResponse servlet response.
   * TODO: also add original exception
   */
  void processErrorAction(final RESTfulAction pAction,
                          final ErrorCode pErrorCode,
                          final HttpServletRequest pRequest,
                          final HttpServletResponse pResponse);

  /**
   * Process authorized action. if user requested uri and method is accepted.
   * @param pAction restful action
   * @param pRequest servlet request.
   * @param pResponse servlet response.
   * @throws Exception when exception raised during processing authorized
   *         action.
   */
  void processAuthorizedAction(final RESTfulAction pAction,
                               final HttpServletRequest pRequest,
                               final HttpServletResponse pResponse)
      throws Exception;

  /**
   * All available error codes for restful service.
   */
  public static enum ErrorCode {
    /**
     * When user request for an action, which requires authentication. or
     * when user requested action is not available.
     */
    INVALID_ACTION,

    /**
     * When login failure occured, this error code is invoked with error action.
     */
    LOGIN_FAILED,

    /**
     * When any resource is not available. 
     */
    RESOURCE_NOT_AVAILABLE,

    /**
     * When authentication is required for a specific action.
     */
    AUTHENTICATION_REQUIRED,

    /**
     * Failed to execute an action.
     */
    FAILURE_EXECUTION,

    /**
     * When user is not allowed to access an action.
     */
    UNAUTHORIZED_ACTION
  }
}
