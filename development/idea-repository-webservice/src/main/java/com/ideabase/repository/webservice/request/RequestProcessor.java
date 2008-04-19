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

import com.ideabase.repository.webservice.controller.RESTfulAction;
import com.ideabase.repository.common.object.AbstractObjectBase;

import java.io.Reader;
import java.util.List;

/**
 * Web service request processor.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public interface RequestProcessor<T extends AbstractObjectBase> {

  /**
   * When WebService based client request is "xml", "html" or other format is
   * defined. Web service controller invokes the specific type handler to
   * understand the medium of communication.
   *
   * @param pAction Restful action properties.
   * @param pContentReader request xml, html, txt or other type of contents.
   * @return a list of {@see AbstractObjectBase} extended class.
   */
  List<T> processRequest(final RESTfulAction pAction,
                         final Reader pContentReader);
}
