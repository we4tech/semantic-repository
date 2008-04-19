/* $Id: BaseRestfullControllerTestcase.java 250 2008-01-07 10:18:29Z hasan $ */
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
package com.ideabase.repository.test.webservice;

import com.ideabase.repository.test.BaseTestCase;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.mock.web.MockServletContext;
import org.jmock.Mock;
import org.jmock.core.Stub;
import org.jmock.core.Invocation;

import javax.servlet.ServletContext;

/**
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class BaseRestfullControllerTestcase extends BaseTestCase {

  protected WebApplicationContext mWebApplicationContext;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mWebApplicationContext = mockWebapplicationContext();
  }

  private WebApplicationContext mockWebapplicationContext() {
    final ServletContext servletContext = new MockServletContext();
    return WebApplicationContextUtils.getWebApplicationContext(servletContext);
  }

}
