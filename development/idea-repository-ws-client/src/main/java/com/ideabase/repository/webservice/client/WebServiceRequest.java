/*
 * $Id: WebServiceRequest.java 250 2008-01-07 10:18:29Z hasan $
 ******************************************************************************
 * Copyright (C) 2007 somewhere in .Net ltd.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from somewhere in .Net LTD.  This notice must be included on
 * all copies, modifications and derivatives of this work.
 ******************************************************************************
 * $LastChangedBy: hasan $
 * $LastChangedDate: 2008-01-07 16:18:29 +0600 (Mon, 07 Jan 2008) $
 * $LastChangedRevision: 250 $
 ******************************************************************************
*/
package com.ideabase.repository.webservice.client;

import java.util.Map;
import java.util.HashMap;

import com.ideabase.repository.common.ObjectToString;

/**
 * Web service request parameters are generated from this class.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan
 *        (hasan)</a>
 */
public class WebServiceRequest {

  private String serviceUri;
  private Map<String, String> parameters = new HashMap<String, String>();
  private String cookies;
  private String requestMethod;
  private String authKey;

  public String getServiceUri() {
    return serviceUri;
  }

  public void setServiceUri(final String pServiceUri) {
    serviceUri = pServiceUri;
  }

  public String getCookies() {
    return cookies;
  }

  public void setCookies(final String pCookies) {
    cookies = pCookies;
  }

  public String getRequestMethod() {
    return requestMethod;
  }

  public void setRequestMethod(final String pRequestMethod) {
    requestMethod = pRequestMethod;
  }

  public void addParameter(final String pName, final String pValue) {
    parameters.put(pName, pValue);
  }

  public String getParameter(final String pName) {
    return parameters.get(pName);
  }

  public Map<String, String> getParameters() {
    return parameters;
  }

  public void setParameters(final Map<String, String> pParameters) {
    parameters = pParameters;
  }

  public String getAuthKey() {
    return authKey;
  }

  public void setAuthKey(final String pAuthKey) {
    authKey = pAuthKey;
  }

  @Override
  public String toString() {
    return ObjectToString.toString(this);
  }
}
