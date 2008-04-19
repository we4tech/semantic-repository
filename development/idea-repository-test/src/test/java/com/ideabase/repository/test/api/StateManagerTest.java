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

package com.ideabase.repository.test.api;

import com.ideabase.repository.api.APIServiceKey;
import com.ideabase.repository.api.StateManager;
import com.ideabase.repository.common.RequestState;
import com.ideabase.repository.test.BaseTestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class StateManagerTest extends BaseTestCase {

  private StateManager mStateManager = null;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mStateManager = (StateManager) mContext.getBean(APIServiceKey.STATE_MANAGER);
  }

  public void testGenerateRequestStateToken() {
    final List<String> tokens = new ArrayList<String>();
    final ThreadGroup threadGroup = new ThreadGroup("test");
    for (int i = 0; i < 10; i++) {
      new Thread(threadGroup, new Runnable() {
        public void run() {
          tokens.add(mStateManager.generateRequestStateToken());
        }
      }).start();
    }

    for (final String token : tokens) {
      assertNotNull(token);

      tokens.remove(token);
      assertFalse(tokens.contains(token));
    }
  }

  public void testGetRequestStateForToken() {
    final String token = mStateManager.generateRequestStateToken();
    LOG.debug("Token - " + token);
    assertNotNull(token);

    assertNull(mStateManager.getRequestStateForToken(token));

    final RequestState requestState = new RequestState();
    requestState.setStateId(token);
    requestState.setSubject(null);
    requestState.setUserName("hasan");
    mStateManager.addRequestStateForToken(token, requestState);
    assertNotNull(mStateManager.getRequestStateForToken(token));

    mStateManager.removeRequestStateForToken(token);
    assertNull(mStateManager.getRequestStateForToken(token));
  }
}
