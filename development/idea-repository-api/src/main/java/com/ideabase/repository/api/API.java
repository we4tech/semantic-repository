/* $Id: API.java 260 2008-03-19 05:38:06Z hasan $ */
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
 * API itself a singeton implementation, it keep the instance of all services.
 * this class is introduced to help out {@see LoginModule}. because login module
 * is out of spring area, so better to give it a chance to have an access
 * to our internal services through this API.
 *
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class API {

  private static API INSTANCE = new API();

  private RepositoryService mRepositoryService;
  private UserService mUserService;
  private EventManager mEventManager;
  private StateManager mStateManager;

  /**
   * Default constructor, if already an instance is created
   */
  private API() {
  }

  /**
   * Return the instance of this singleton class.
   * @return the instance of this singleton class.
   */
  public static API giveMe() {
    return INSTANCE;
  }

  public RepositoryService repositoryService() {
    return mRepositoryService;
  }

  public void setRepositoryService(final RepositoryService pRepositoryService) {
    mRepositoryService = pRepositoryService;
  }

  public UserService userService() {
    return mUserService;
  }

  public void setUserService(final UserService pUserService) {
    mUserService = pUserService;
  }

  public EventManager eventManager() {
    return mEventManager;
  }

  public void setEventManager(final EventManager pEventManager) {
    mEventManager = pEventManager;
  }

  public void setStateManager(final StateManager pStateManager) {
    mStateManager = pStateManager;
  }

  public StateManager getStateManager() {
    return mStateManager;
  }
}
