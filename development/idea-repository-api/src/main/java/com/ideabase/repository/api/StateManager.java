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

package com.ideabase.repository.api;

import com.ideabase.repository.common.RequestState;

import java.util.Map;

/**
 * State manager is used to manage state for each and every request.
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public interface StateManager extends ServiceAccessStatistics {

  /**
   * Retrieve an existing {@see RequestState} for the given authentication token.
   * @param pAuthToken authentication token.
   * @return {@see RequestState} object if state found otherwise null is returned.
   */
  RequestState getRequestStateForToken(final String pAuthToken);

  /**
   * Add new {@see RequestState} for the given authentication token.
   * @param pAuthToke authentication token.
   * @param pRequestState request state object.
   */
  void addRequestStateForToken(final String pAuthToke,
                               final RequestState pRequestState);

  /**
   * Remove request state for the given authentication token.
   * @param pAuthToken authentication token.
   */
  void removeRequestStateForToken(final String pAuthToken);

  /**
   * Generate request state token.
   * @return return the generated unique authentication token.
   */
  String generateRequestStateToken();

}
