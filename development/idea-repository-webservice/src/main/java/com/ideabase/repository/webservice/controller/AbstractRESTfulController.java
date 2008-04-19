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
import org.springframework.web.servlet.ModelAndView;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Implementation of RESTFul controller.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public abstract class AbstractRESTfulController implements Controller {

  protected static final Logger LOG =
      LogManager.getLogger(AbstractRESTfulController.class);
  protected static boolean DEBUG = LOG.isDebugEnabled();

  /**
   * Handle all request. restful request are suppose to format like the
   * following uri.<br>
   * {@code /service/item/32.request}<br>
   * {@code /service/item/find/post_id&post_email.json}<br>
   * <Br>
   * The authentication and authorization request will be handled from advice.<br>
   * therefore this class will just dispatch all kind of request. whether it
   * is authroized or not.<br>
   */
  public ModelAndView handleRequest(
      final HttpServletRequest pRequest,
      final HttpServletResponse pResponse) throws Exception {
    return null;
  }

}
