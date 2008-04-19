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
package com.ideabase.repository.webservice.helper;

import java.util.Map;
import java.util.HashMap;

import com.ideabase.repository.common.exception.ServiceException;
import com.ideabase.repository.webservice.controller.RESTfulAction;

import javax.servlet.http.HttpServletRequest;

/**
 * Parse user requested uri and extract parameter out of that.
 *
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan
 *         (hasan)</a>
 */
public class URIParameterHelper {

  private static final String URI_PREFIX = "service";
  private static final String URI_REGEX = "(.*/" + URI_PREFIX +
      "/(.+)/(.+)\\.(.+))";
  public static final String ACTION = "a";
  public static final String PARAMETER = "p";
  public static final String FORMAT = "f";
  private static final String URI_SPLITER = "/";
  private static final String URI_FORMAT_SPLITER = "\\.";

  /**
   * <pre>
   * determine url format - ^.*service/(.+)/(.+)\.(.+)$
   *                                  [action] [params] [format]
   * acceptable parameters -                   string|int request|json|text
   * except this format all url are treated as an invalid request.
   * </pre>
   */
  public static Map<String, String> findParams(final String pUri) {
    if (pUri.matches(URI_REGEX)) {
      final Map<String, String> paramMap = new HashMap<String, String>();
      final String[] splitUri = pUri.split(URI_SPLITER);
      final int uriSplitCount = splitUri.length;
      final String params = splitUri[uriSplitCount - 1];
      final String[] splitParams = params.split(URI_FORMAT_SPLITER);

      paramMap.put(PARAMETER, splitParams[0]);
      paramMap.put(FORMAT, splitParams[1]);
      paramMap.put(ACTION, splitUri[uriSplitCount - 2]);
      return paramMap;
    } else {
      throw new ServiceException(URI_REGEX, "Invalid request found.");
    }
  }

  /**
   * Build {@see RESTfulAction} object from {@code HttpServletRequest} object.
   * @param pRequest http servlet request.
   * @return newly built {@see RESTfulAction} object.<br>
   * @exception ServiceException is returned, if invalid action uri found.
   */
  public static RESTfulAction buildRESTfulAction(
      final HttpServletRequest pRequest) {

    // parse uri and find the parameters.
    final String uri = pRequest.getRequestURI();
    final Map<String, String> params = findParams(uri);

    // Build restful action object.
    final RESTfulAction action = new RESTfulAction();
    action.setUri(uri);
    action.setAuthToken(pRequest.getSession().getId());
    action.setAction(params.get(URIParameterHelper.ACTION));
    action.setParameter(params.get(URIParameterHelper.PARAMETER));
    action.setResponseFormat(params.get(URIParameterHelper.FORMAT));
    action.setRequestMethod(pRequest.getMethod().toUpperCase());
    //action.setParams(pRequest.getParameterMap());

    return action;
  }

}
