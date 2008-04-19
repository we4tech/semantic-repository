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
package com.ideabase.repository.webservice.request.impl;

import com.ideabase.repository.webservice.request.WebServiceRequestHandler;
import com.ideabase.repository.webservice.request.RequestProcessor;
import com.ideabase.repository.webservice.controller.RESTfulAction;
import com.ideabase.repository.common.object.ObjectBase;

import java.io.Reader;
import java.util.List;

/**
 * Process web service request based on the user defined format.<br>
 * for example: handle xml based request, html based request or others...
 *
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class WebServiceRequestHandlerImpl implements WebServiceRequestHandler {

  private static final String FORMAT_XML = "xml";
  private static final String FORMAT_TXT = "txt";
  private static final String FORMAT_JSON = "json";
  private static final String FORMAT_HTML = "html";

  private RequestProcessor xmlRequestProcessor;
  private RequestProcessor jsonRequestProcessor;
  private RequestProcessor txtRequestProcessor;
  private RequestProcessor htmlRequestProcessor;

  public RequestProcessor getXmlRequestProcessor() {
    return xmlRequestProcessor;
  }

  public void setXmlRequestProcessor(
      final RequestProcessor pXmlRequestProcessor) {
    xmlRequestProcessor = pXmlRequestProcessor;
  }

  public RequestProcessor getJsonRequestProcessor() {
    return jsonRequestProcessor;
  }

  public void setJsonRequestProcessor(
      final RequestProcessor pJsonRequestProcessor) {
    jsonRequestProcessor = pJsonRequestProcessor;
  }

  public RequestProcessor getTxtRequestProcessor() {
    return txtRequestProcessor;
  }

  public void setTxtRequestProcessor(
      final RequestProcessor pTxtRequestProcessor) {
    txtRequestProcessor = pTxtRequestProcessor;
  }

  public RequestProcessor getHtmlRequestProcessor() {
    return htmlRequestProcessor;
  }

  public void setHtmlRequestProcessor(
      final RequestProcessor pHtmlRequestProcessor) {
    htmlRequestProcessor = pHtmlRequestProcessor;
  }

  /**
   * {@inheritDoc}
   */
  public <T extends ObjectBase> List<T> handleRequest(
      final RESTfulAction pAction, final Reader pContentReader) {

    final String format = pAction.getResponseFormat();
    if (FORMAT_TXT.equalsIgnoreCase(format)) {
      if (txtRequestProcessor != null) {
        return txtRequestProcessor.processRequest(pAction, pContentReader);
      }
    } else if (FORMAT_XML.equalsIgnoreCase(format)) {
      if (xmlRequestProcessor != null) {
        return xmlRequestProcessor.processRequest(pAction, pContentReader);
      }
    } else if (FORMAT_JSON.equalsIgnoreCase(format)) {
      if (jsonRequestProcessor != null) {
        return jsonRequestProcessor.processRequest(pAction, pContentReader);
      }
    } else if (FORMAT_HTML.equalsIgnoreCase(format)) {
      if (htmlRequestProcessor != null) {
        return htmlRequestProcessor.processRequest(pAction, pContentReader);
      }
    } else {
      throw new RuntimeException("No request processor found " +
                                 "for request format - " + format);
    }
    return null;
  }
}
