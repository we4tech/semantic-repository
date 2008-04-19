/*
 * $Id: WebServiceResponse.java 250 2008-01-07 10:18:29Z hasan $
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

import com.ideabase.repository.common.ObjectToString;

/**
 * Service response is encapsulated with in this class. this class has used
 * fluent interface.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan
 *        (hasan)</a>
 */
public class WebServiceResponse {

  private String responseContent;
  private String contentType;
  private String serviceUri;
  private Integer responseStatus;
  private String cookies;

  public WebServiceResponse() {}

  public String getResponseContent() {
    return responseContent;
  }

  public void setResponseContent(final String pResponseContent) {
    responseContent = pResponseContent;
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType(final String pContentType) {
    contentType = pContentType;
  }

  public String getServiceUri() {
    return serviceUri;
  }

  public void setServiceUri(final String pServiceUri) {
    serviceUri = pServiceUri;
  }

  public Integer getResponseStatus() {
    return responseStatus;
  }

  public void setResponseStatus(final Integer pResponseStatus) {
    responseStatus = pResponseStatus;
  }

  public String getCookies() {
    return cookies;
  }

  public void setCookies(final String pCookies) {
    cookies = pCookies;
  }

  @Override
  public String toString() {
    return ObjectToString.toString(this);
  }
}