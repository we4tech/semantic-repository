/* $Id: APIServiceKey.java 260 2008-03-19 05:38:06Z hasan $ */
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
package com.ideabase.repository.api;

/**
 * All api service key is defined here. these keys are used on spring 
 * bean definition.
 * 
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan 
 *         (hasan)</a>
 */
public class APIServiceKey {

  // ---------- SERVICE RELATED OBJECTS -------------------
  public static final String REPOSITORY_SERVICE = "repositoryService";
  public static final String USER_SERVICE = "userService";
  public static final String STATE_MANAGER = "stateManager";

  // ---------- CACHE RELATED OBJECTS ---------------------

  private APIServiceKey() {
    // No instance accepted.
  }
  
}
