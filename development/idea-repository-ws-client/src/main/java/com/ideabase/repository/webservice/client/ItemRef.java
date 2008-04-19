/*
 * $Id: ItemRef.java 250 2008-01-07 10:18:29Z hasan $
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
 * Web service result ItemRef. it holds pointer to fetch content through another
 * web service rquest. or the number of ItemRef.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class ItemRef {

  private Integer id;
  private WebServiceRequest itemContentRequest;

  public Integer getId() {
    return id;
  }

  public ItemRef setId(final Integer pId) {
    id = pId;
    return this;
  }

  public WebServiceRequest getItemContentRequest() {
    return itemContentRequest;
  }

  public ItemRef setItemContentRequest(final WebServiceRequest pRequest) {
    itemContentRequest = pRequest;
    return this;
  }

  @Override
  public String toString() {
    return ObjectToString.toString(this);
  }
}
