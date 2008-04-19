/**
 * $Id$
 * *****************************************************************************
 *    Copyright (C) 2005 - 2007 somewhere in .Net ltd.
 *    All Rights Reserved.  No use, copying or distribution of this
 *    work may be made except in accordance with a valid license
 *    agreement from somewhere in .Net LTD.  This notice must be included on
 *    all copies, modifications and derivatives of this work.
 * *****************************************************************************
 * $LastChangedBy$
 * $LastChangedDate$
 * $LastChangedRevision$
 * *****************************************************************************
 */

package com.ideabase.repository.common;

import javax.security.auth.Subject;

/**
 * This object is used to keep state related information.
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class RequestState {
  private String stateId;
  private String userName;
  private Subject subject;

  public String getStateId() {
    return stateId;
  }

  public void setStateId(final String pStateId) {
    stateId = pStateId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(final String pUserName) {
    userName = pUserName;
  }

  public Subject getSubject() {
    return subject;
  }

  public void setSubject(final Subject pSubject) {
    subject = pSubject;
  }
}
