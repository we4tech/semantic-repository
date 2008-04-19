/* $Id: CacheInvalidator.java 250 2008-01-07 10:18:29Z hasan $ */
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
package com.ideabase.repository.common.cache;

/**
 * Cache invalidator is used to verify whether the cache should be flushed or
 * should return the content.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan
 *         (hasan)</a>
 */
public interface CacheInvalidator {

  /**
   * Invoke this method to verify the cache, wehther we need to repopulate
   * the cache object.
   * @return {@code true} is returned if cache is not yet expired or not
   *          required to invalidate.
   */
  boolean isCacheExpired();
}
