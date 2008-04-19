/* $Id:URLPermission.java 249 2007-12-02 08:32:47Z hasan $ */
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

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

import java.security.Permission;

import com.ideabase.repository.common.ObjectToString;

/**
 * Implementation of Action permission.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class URLPermission extends Permission {

  private static transient final Logger LOG =
      LogManager.getLogger(URLPermission.class);

  private String mActions;

  /**
   * Default constructor, it accepts url Regex and action token.
   * @param pName user request uri.
   * @param pActions user request method.
   */
  public URLPermission(final String pName, final String pActions) {
    super(pName);
    mActions = pActions;
    LOG.debug("Construct url permission object - " + pName + ", " + pActions);
  }

  /**
   * Return true if specified permission url match with this url regular
   * expression and that action token equals to this action token.
   *
   * @param pPermission permission attributes.
   * @return true if url match the regex and action token equals to defined
   *         action token.
   */
  public boolean implies(final Permission pPermission) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Implies permission for - " + pPermission);
    }
    if (!(pPermission instanceof URLPermission)) {
      throw new IllegalArgumentException("Invalid permission requested, " +
          "this class can understand only URLPermission.");
    }
    final URLPermission that = (URLPermission) pPermission;
    final String thatUrl = that.getName();
    final String thatAction = that.getActions();

    if (LOG.isDebugEnabled()) {
      LOG.debug("Permission - " + this);
    }
    return thatUrl.matches(this.getName())
        && thatAction.equalsIgnoreCase(this.getActions());
  }

  public boolean equals(final Object pObj) {
    if (pObj == null) {
      return false;
    }
    if (pObj == this) {
      return true;
    } else if (pObj instanceof URLPermission) {
      URLPermission that = (URLPermission) pObj;
      return that.getName().equals(this.getName());
    }
    return false;
  }

  public int hashCode() {
    return getName().hashCode();
  }

  public String getActions() {
    return mActions;
  }

  @Override
  public String toString() {
    return ObjectToString.toString(this);
  }
}
