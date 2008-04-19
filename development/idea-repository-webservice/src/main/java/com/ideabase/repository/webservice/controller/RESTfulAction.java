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

import java.util.Map;
import java.util.HashMap;

import com.ideabase.repository.common.ObjectToString;

/**
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class RESTfulAction {

  private String mUri;
  private Map<String, String> mParams = new HashMap<String, String>();
  private String mAction;
  private String mParameter;
  private String mAuthToken;
  private String mFormat;
  private String mRequestMethod;

  public RESTfulAction() {
  }

  public String getUri() {
    return mUri;
  }

  public void setUri(final String pUri) {
    mUri = pUri;
  }

  public String getRequestMethod() {
    return mRequestMethod;
  }

  public void setRequestMethod(final String pRequestMethod) {
    mRequestMethod = pRequestMethod;
  }

  public String getAction() {
    return mAction;
  }

  public void setAction(final String pAction) {
    mAction = pAction;
  }

  public String getParameter() {
    return mParameter;
  }

  public void setParameter(final String pParameter) {
    mParameter = pParameter;
  }

  public Map<String, String> getParams() {
    return mParams;
  }

  public void setParams(final Map<String, String> pParams) {
    mParams = pParams;
  }

  public String getAuthToken() {
    return mAuthToken;
  }

  public void setAuthToken(final String pToken) {
    mAuthToken = pToken;
  }

  public String getResponseFormat() {
    return mFormat;
  }

  public void setResponseFormat(final String pFormat) {
    mFormat = pFormat;
  }

  public String toString() {
    return ObjectToString.toString(this);
  }
}
