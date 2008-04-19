/*
 * $Id: ExecutionResponse.java 250 2008-01-07 10:18:29Z hasan $
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

import java.util.List;

/**
 * Execution response is retruned after each successful web service execution.<br>
 * successful web service execution doesn't mean successful web service end
 * business logic execution.<br>
 * this response also holds the failure reason and state.<Br>
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class ExecutionResponse {
  
  private boolean state;
  private String message;
  private List<ItemRef> itemRef;

  /**
   * Optional property, it is used only for login request.
   */
  private String mAuthKey;

  public boolean getState() {
    return state;
  }

  public void setState(final boolean pState) {
    state = pState;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(final String pMessage) {
    message = pMessage;
  }

  public List<ItemRef> getItemRef() {
    return itemRef;
  }

  public void setItemRef(final List<ItemRef> pItemRef) {
    itemRef = pItemRef;
  }

  @Override
  public String toString() {
    return ObjectToString.toString(this);
  }

  public String getAuthKey() {
    return mAuthKey;
  }

  public void setAuthKey(final String pAuthKey) {
    mAuthKey = pAuthKey;
  }
}
