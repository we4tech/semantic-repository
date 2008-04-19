/* $Id: MockHttpServletRequestFixed.java 250 2008-01-07 10:18:29Z hasan $ */
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
package com.ideabase.repository.test.helper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletInputStream;
import javax.servlet.RequestDispatcher;

import java.util.Enumeration;
import java.util.Map;
import java.util.Locale;
import java.util.HashMap;
import java.security.Principal;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.io.BufferedReader;

import org.springframework.mock.web.MockHttpSession;

/**
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class MockHttpServletRequestFixed implements HttpServletRequest {

  private String mRequestUri;
  private final HttpSession mSession = new MockHttpSession();
  private final Map<Object, Object> mAttributes = new HashMap<Object, Object>();
  private final Map<String, String> mParams = new HashMap<String, String>();
  private int mStatus;
  private String mMethod;

  public void setRequestURI(String pString) {
    mRequestUri = pString;
  }

  public String getRequestURI() {
    return mRequestUri;
  }

  public String getAuthType() {
    return null;
  }

  public Cookie[] getCookies() {
    return new Cookie[0];
  }

  public long getDateHeader(String pString) {
    return 0;
  }

  public String getHeader(String pString) {
    return null;
  }

  public Enumeration getHeaders(String pString) {
    return null;
  }

  public Enumeration getHeaderNames() {
    return null;
  }

  public int getIntHeader(String pString) {
    return 0;
  }

  public String getMethod() {
    return mMethod;
  }

  public void setMethod(final String pMethod) {
    mMethod = pMethod;
  }

  public String getPathInfo() {
    return null;
  }

  public String getPathTranslated() {
    return null;
  }

  public String getContextPath() {
    return null;
  }

  public String getQueryString() {
    return null;
  }

  public String getRemoteUser() {
    return null;
  }

  public boolean isUserInRole(String pString) {
    return false;
  }

  public Principal getUserPrincipal() {
    return null;
  }

  public String getRequestedSessionId() {
    return null;
  }

  public StringBuffer getRequestURL() {
    return null;
  }

  public String getServletPath() {
    return null;
  }

  public HttpSession getSession(boolean b) {
    return mSession;
  }

  public HttpSession getSession() {
    return mSession;
  }

  public boolean isRequestedSessionIdValid() {
    return false;
  }

  public boolean isRequestedSessionIdFromCookie() {
    return false;
  }

  public boolean isRequestedSessionIdFromURL() {
    return false;
  }

  @Deprecated
  public boolean isRequestedSessionIdFromUrl() {
    return false;
  }

  public Object getAttribute(String pString) {
    return null;
  }

  public Enumeration getAttributeNames() {
    return null;
  }

  public String getCharacterEncoding() {
    return null;
  }

  public void setCharacterEncoding(String pString) throws UnsupportedEncodingException {

  }

  public int getContentLength() {
    return 0;
  }

  public String getContentType() {
    return null;
  }

  public ServletInputStream getInputStream() throws IOException {
    return null;
  }

  public String getParameter(String pString) {
    return mParams.get(pString);
  }

  public void addParameter(final String pKey, String pValue) {
    mParams.put(pKey, pValue);
  }

  public Enumeration getParameterNames() {
    return null;
  }

  public String[] getParameterValues(String pString) {
    return new String[0];
  }

  public Map getParameterMap() {
    return null;
  }

  public String getProtocol() {
    return null;
  }

  public String getScheme() {
    return null;
  }

  public String getServerName() {
    return null;
  }

  public int getServerPort() {
    return 0;
  }

  public BufferedReader getReader() throws IOException {
    return null;
  }

  public String getRemoteAddr() {
    return null;
  }

  public String getRemoteHost() {
    return null;
  }

  public void setAttribute(String pString, Object pObject) {

  }

  public void removeAttribute(String pString) {

  }

  public Locale getLocale() {
    return null;
  }

  public Enumeration getLocales() {
    return null;
  }

  public boolean isSecure() {
    return false;
  }

  public RequestDispatcher getRequestDispatcher(String pString) {
    return null;
  }

  @Deprecated
  public String getRealPath(String pString) {
    return null;
  }

  public int getRemotePort() {
    return 0;
  }

  public String getLocalName() {
    return null;
  }

  public String getLocalAddr() {
    return null;
  }

  public int getLocalPort() {
    return 0;
  }
}
