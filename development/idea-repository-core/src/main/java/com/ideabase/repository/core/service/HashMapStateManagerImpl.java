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

package com.ideabase.repository.core.service;

import com.ideabase.repository.api.StateManager;
import com.ideabase.repository.common.RequestState;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Map;
import java.util.HashMap;

/**
 * {@see HashMap} based {@see StateManager} implementation.
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class HashMapStateManagerImpl implements StateManager {

  private final Logger mLog = LogManager.getLogger(getClass());
  private final Map<String, RequestState> mRequestStateMap =
      new HashMap<String, RequestState>();
  private String mPrefix = "Hello";
  private String mSuffix = "World";
  private int mHitCount;
  private int mMissCount;

  public void setPrefix(final String pPrefix) {
    mPrefix = pPrefix;
  }

  public void setSuffix(final String pSuffix) {
    mSuffix = pSuffix;
  }

  public RequestState getRequestStateForToken(final String pAuthToken) {
    final RequestState requestState = mRequestStateMap.get(pAuthToken);
    if (requestState != null) {
      mHitCount++;
    } else {
      mMissCount++;
    }
    return requestState;
  }

  public void addRequestStateForToken(final String pAuthToke, final RequestState pRequestState) {
    mRequestStateMap.put(pAuthToke, pRequestState);
  }

  public void removeRequestStateForToken(final String pAuthToken) {
    mRequestStateMap.remove(pAuthToken);
  }

  public String generateRequestStateToken() {
    final StringBuilder builder = new StringBuilder();
    builder.append(mPrefix)
        .append(System.nanoTime())
        .append(Math.random() * 100)
        .append(mSuffix);
    return DigestUtils.shaHex(builder.toString());
  }

  public int getTotalHits() {
    return mHitCount;
  }

  public int getTotalMisses() {
    return mMissCount;
  }

  public int getHits(final String pKey) {
    throw new NoSuchMethodError("this method is not yet implemented");
  }

  public int getMisses(final String pKey) {
    throw new NoSuchMethodError("this method is not yet implemented");
  }
}
