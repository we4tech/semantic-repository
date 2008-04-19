/* $Id: CacheableObjectBaseValidator.java 250 2008-01-07 10:18:29Z hasan $ */
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
 * $LastChangedDate: 2008-01-07 16:18:29 +0600 (Mon, 07 Jan 2008) $
 * $LastChangedRevision: 250 $
 ******************************************************************************
*/
package com.ideabase.repository.api.cache;

import com.ideabase.repository.common.cache.CacheableObjectValidator;
import com.ideabase.repository.common.object.ObjectBase;

/**
 * Implementation of {@see CacheableObjectValidator} which validates the
 * {@see ObjectBase} type object.<br>
 * this implementation verify whether the {@see ObjectBase#getId} is
 * returning null. <br> if found null it returns false for {@see #isCachable}.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan
 *         (hasan)</a>
 */
public class CacheableObjectBaseValidator implements CacheableObjectValidator {

  private static CacheableObjectBaseValidator INSTANCE =
      new CacheableObjectBaseValidator();

  /**
   * Factory method, which returns the singletone instance of validator object.
   * @return singleton instance of {@see CacheableObjectValidator}.
   */
  public static CacheableObjectValidator getInstance() {
    return INSTANCE;
  }

  /**
   * Verify whether {@see ObjectBase#getId} is returning null or not.
   * if null found {@code false} is returned.
   * @param pObject the target cachable object.
   * @return is cachable returns true {@see ObjectBase#getId} is not null.
   */
  public boolean isCachable(final Object pObject) {
    return pObject instanceof ObjectBase
           && ((ObjectBase) pObject).getId() != null;
  }
}
