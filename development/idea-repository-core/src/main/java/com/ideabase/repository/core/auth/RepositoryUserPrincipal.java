/* $Id:RepositoryUserPrincipal.java 249 2007-12-02 08:32:47Z hasan $ */
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
 * $LastChangedDate:2007-12-02 14:32:47 +0600 (Sun, 02 Dec 2007) $
 * $LastChangedRevision:249 $
 ******************************************************************************
*/
package com.ideabase.repository.core.auth;

import com.ideabase.repository.common.ObjectToString;

import java.security.Principal;
import java.io.Serializable;

/**
 * Repository user principal implementation of {@see Principal} and added
 * {@see Serializable} mixin.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class RepositoryUserPrincipal implements Principal, Serializable {

  private String mName;
  private Integer mId;

  public RepositoryUserPrincipal(final String pName, final Integer pId) {
    if (pName == null || pId == null) {
      throw new IllegalArgumentException("Name or ID is not defined.");
    }
    mName = pName;
    mId = pId;
  }

  public String getName() {
    return mName;
  }

  public Integer getId() {
    return mId;
  }

  @Override
  public boolean equals(final Object pObj) {
    if (pObj == null) {
      return false;
    } else if (pObj == this) {
      return true;
    } else if (pObj instanceof RepositoryUserPrincipal) {
      final RepositoryUserPrincipal that = ((RepositoryUserPrincipal) pObj);
      return that.getName().equals(this.getName())
             && that.getId().equals(this.getId());
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return mName.hashCode();
  }

  @Override
  public String toString() {
    return ObjectToString.toString(this);
  }
}
