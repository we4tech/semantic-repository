/* $Id: UserCredential.java 260 2008-03-19 05:38:06Z hasan $ */
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
 * $LastChangedDate: 2008-03-19 11:38:06 +0600 (Wed, 19 Mar 2008) $
 * $LastChangedRevision: 260 $
 ******************************************************************************
*/
package com.ideabase.repository.common.object;

import com.ideabase.repository.common.ObjectToString;

/**
 * User credential information holder.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class UserCredential {
  private String user;
  private String password;
  private String stateId;


  public UserCredential(final String pUser, final String pPassword) {
    user = pUser;
    password = pPassword;
  }

  public String getUser() {
    return user;
  }

  public String getPassword() {
    return password;
  }

  public String getStateId() {
    return stateId;
  }

  public void setStateId(final String pStateId) {
    stateId = pStateId;
  }

  @Override
  public String toString() {
    return ObjectToString.toString(this);
  }
}
