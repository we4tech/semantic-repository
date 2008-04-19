/* $Id: CacheAdvice.java 249 2007-12-02 08:32:47Z hasan $ */
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
 * $LastChangedDate: 2007-12-02 14:32:47 +0600 (Sun, 02 Dec 2007) $
 * $LastChangedRevision: 249 $
 ******************************************************************************
*/
package com.ideabase.repository.core.aspect;

/**
 * Deletgate content from cache. all @Cache annotated methods are cached for
 * specified time.<br> 
 * <br> this class follows the following rules - <br>
 * <ul>
 *  <li>If object exists from cache and not yet expired, return it.</li>
 *  <li>If object doesn't exist from cache, invoke the original method.
 *  and populate the cache.</li>
 *  <li>If orginal method returns null or an empty list, no cache is created.</li>
 * </ul>
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan
 *         (hasan)</a>
 */
public class CacheAdvice {
  
}
