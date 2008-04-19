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
package com.ideabase.repository.webservice.request;

import com.ideabase.repository.common.object.ObjectBase;
import com.ideabase.repository.webservice.controller.RESTfulAction;

import java.io.Reader;
import java.util.List;

/**
 * Web service based request handler. it handles request based on the format
 * desire by the web service client. for example: xml, html, json etc...
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public interface WebServiceRequestHandler {

  /**
   * Handle REST request.
   * @param pAction restful action request.
   * @return the processed object must be the child of {@see AbstractObjectBase}
   * @param pContentReader content input stream.
   */
  <T extends ObjectBase> List<T> handleRequest(
      final RESTfulAction pAction, final Reader pContentReader);
}
