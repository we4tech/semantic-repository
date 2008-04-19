/*
 * $Id: WebServiceController.java 250 2008-01-07 10:18:29Z hasan $
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

/**
 * WebService controller is responsible for interacting with HTTP or
 * other media of communication.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public interface WebServiceController {

  /**
   * Request to web service end point, and retrieve the content with in the
   * web service response class. <br>
   * all services are requested through the {@see WebServiceRequest} object.
   * @param pRequest the web service request object.
   * @return the response of web service.
   */
  WebServiceResponse sendServiceRequest(final WebServiceRequest pRequest);
}
