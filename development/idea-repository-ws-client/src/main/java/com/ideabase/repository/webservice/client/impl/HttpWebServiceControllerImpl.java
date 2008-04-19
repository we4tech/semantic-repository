/*
 * $Id: HttpWebServiceControllerImpl.java 250 2008-01-07 10:18:29Z hasan $
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
package com.ideabase.repository.webservice.client.impl;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.Header;

import com.ideabase.repository.webservice.client.WebServiceController;
import com.ideabase.repository.webservice.client.WebServiceResponse;
import com.ideabase.repository.webservice.client.WebServiceRequest;
import com.ideabase.repository.common.exception.ServiceException;

/**
 * Implementation of {@see WebServiceController}.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class HttpWebServiceControllerImpl implements WebServiceController {

  private static final String GET = "GET";
  private static final String POST = "POST";
  private static final String DELETE = "DELETE";
  private static final String HEADER_COOKIE = "Cookie";
  private static final String HEADER_SET_COOKIE = "Set-Cookie";
  private static final String HEADER_CONTENT_TYPE = "Content-Type";

  private final Logger LOG = LogManager.getLogger(getClass());
  private final String mBaseUrl;
  private String mCookies;

  /**
   * Default constructor, it accepts base url and previous cookies.
   * @param pBaseUrl base service url
   * @param pCookies previous cookie reference, if no cookies exists set
   *        it null.
   */
  public HttpWebServiceControllerImpl(final String pBaseUrl,
                                      final String pCookies) {
    mBaseUrl = pBaseUrl;
    mCookies = pCookies;
  }

  /**
   * {@inheritDoc}
   */
  public WebServiceResponse sendServiceRequest(final WebServiceRequest pRequest)
  {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Sending request - " + pRequest);
    }
    // build query string
    final NameValuePair[] parameters = buildParameters(pRequest);

    // Send request to server
    final HttpMethod httpMethod = prepareMethod(
        pRequest.getRequestMethod(), pRequest.getServiceUri(), parameters);

    // set cookie policy
    httpMethod.getParams().setCookiePolicy(CookiePolicy.RFC_2109);

    // set cookies
    if (mCookies != null) {
      httpMethod.setRequestHeader(HEADER_COOKIE, mCookies);
    }
    if (pRequest.getCookies() != null) {
      httpMethod.setRequestHeader(HEADER_COOKIE, pRequest.getCookies());
      mCookies = pRequest.getCookies();
    }

    // execute method
    final HttpClient httpClient = new HttpClient();
    final WebServiceResponse response;
    try {
      final int status = httpClient.executeMethod(httpMethod);

      // build web sercie response
      response = new WebServiceResponse();
      response.setResponseStatus(status);
      response.setResponseContent(new String(httpMethod.getResponseBody()));
      response.setContentType(httpMethod.
          getResponseHeader(HEADER_CONTENT_TYPE).getValue());
      response.setServiceUri(httpMethod.getURI().toString());

      // set cookies
      final Header cookieHeader =
          httpMethod.getResponseHeader(HEADER_SET_COOKIE);
      if (cookieHeader != null) {
        mCookies = cookieHeader.getValue();
      }

      // set cookies to the returning response object.
      response.setCookies(mCookies);

      if (LOG.isDebugEnabled()) {
        LOG.debug("Cookies - " + mCookies);
        LOG.debug("Response - " + response);
      }
    } catch (Exception e) {
      throw ServiceException.aNew(pRequest,
            "Failed to send web service request.", e);
    } finally {
      httpMethod.releaseConnection();
    }

    return response;
  }

  private NameValuePair[] buildParameters(final WebServiceRequest pRequest) {
    final Map<String, String> params = pRequest.getParameters();
    if (params != null && !params.isEmpty()) {
      final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
      for (final Map.Entry<String, String> param : params.entrySet()) {
        final NameValuePair nameValuePair =
            new NameValuePair(param.getKey(), param.getValue());
        nameValuePairs.add(nameValuePair);
      }
      return nameValuePairs.toArray(new NameValuePair[] {});
    }
    return null;
  }

  private HttpMethod prepareMethod(final String pMethod,
                                   final String pServiceUri,
                                   final NameValuePair[] pParameters) {
    LOG.debug("Prepare appropriate http method.");

    final String baseUrl;
    if (mBaseUrl.endsWith("/")) {
      baseUrl = mBaseUrl.substring(0, mBaseUrl.length() - 1);
    } else {
      baseUrl = mBaseUrl;
    }
    final String fullQualifiedUri = baseUrl + pServiceUri;
    final HttpMethod httpMethod;
    // GET method
    if (GET.equalsIgnoreCase(pMethod)) {
      httpMethod = new GetMethod(fullQualifiedUri);
      if (pParameters != null) {
        httpMethod.setQueryString(pParameters);
      }
    }
    // POST method
    else if (POST.equalsIgnoreCase(pMethod)) {
      httpMethod = new PostMethod(fullQualifiedUri);
      if (pParameters != null) {
        ((PostMethod) httpMethod).addParameters(pParameters);
      }
    }
    // DELETE method
    else if (DELETE.equalsIgnoreCase(pMethod)) {
      httpMethod = new DeleteMethod(fullQualifiedUri);
      if (pParameters != null) {
        httpMethod.setQueryString(pParameters);
      }
    }
    // If no such known method.
    else {
      throw new UnsupportedOperationException(
            "Unknown method type - " + pMethod);
    }
    return httpMethod;
  }
}
