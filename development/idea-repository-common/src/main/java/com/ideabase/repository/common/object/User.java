/* $Id:User.java 224 2007-07-14 10:39:36Z hasan $ */
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
 * $LastChangedBy:hasan $
 * $LastChangedDate:2007-07-14 03:39:36 -0700 (Sat, 14 Jul 2007) $
 * $LastChangedRevision:224 $
 ******************************************************************************
*/
package com.ideabase.repository.common.object;

import java.util.Map;

import com.ideabase.repository.common.Query;

/**
 * User object.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class User extends AbstractObjectBase {

  public static final String FIELD_PASSWORD = "password";
  public static final String FIELD_USER = "user";
  public static final String FIELD_ROLE_READ = "role_read";
  public static final String FIELD_ROLE_WRITE = "role_write";
  public static final String FIELD_ROLE_ADMIN = "role_admin";

  public String getUser() {
    return getField(FIELD_USER);
  }

  public void setUser(final String pUserName) {
    addField(FIELD_USER, pUserName);
    addField(INDEX_FIELD_TITLE, pUserName);
  }

  public String getPassword() {
    return getField(FIELD_PASSWORD);
  }

  public void setPassword(final String pPassword) {
    addField(FIELD_PASSWORD, pPassword);
  }

  public boolean canRead() {
    return isTrue(getField(FIELD_ROLE_READ));
  }

  public boolean canWrite() {
    return isTrue(getField(FIELD_ROLE_WRITE));
  }

  public boolean isAdmin() {
    return isTrue(getField(FIELD_ROLE_ADMIN));
  }

  private boolean isTrue(final String pValue) {
    return pValue != null && Boolean.valueOf(pValue);
  }

  public void setRoles(final Map<String, Boolean> pRoles) {
    for (final Map.Entry<String, Boolean> entry : pRoles.entrySet()) {
      addField(entry.getKey(), String.valueOf(entry.getValue()));
    }
  }

  public void removeRole(final String pRole) {
    removeField(pRole);
  }

  public void setRole(final String pRole, final boolean pState) {
    addField(pRole, String.valueOf(pState));
  }

  /**
   * User name (internally the title field) must be an unique field.
   * @return the integrity test query.
   */
  @Override
  public Query getIntegrityVerificationQuery() {
    return new Query().and(FIELD_USER, getUser());
  }
}
