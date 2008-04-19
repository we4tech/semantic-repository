/*
 * $Id: ResponseProcessor.java 250 2008-01-07 10:18:29Z hasan $
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
package com.ideabase.repository.webservice.client.processor;

import com.ideabase.repository.webservice.client.WebServiceResponse;

/**
 * Response processor is used to process web service response in a specific
 * format. for example - xml, text, json and other.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public interface ResponseProcessor {

  /**
   * Process web service response and generate an object.
   * @param pResponse returned web service response.
   * @return the generated object after processing web service response.
   * @param pExpectedClass expected class is used to understand the type of
   *        response. for example: SearchResult, reminds the the service output
   *        must be output which formatted for search result.
   */
  <T> T processResponse(final WebServiceResponse pResponse,
                        final Class<T> pExpectedClass);
}
