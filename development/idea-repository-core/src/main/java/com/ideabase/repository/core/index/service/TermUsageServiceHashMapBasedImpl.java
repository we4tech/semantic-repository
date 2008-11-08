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

package com.ideabase.repository.core.index.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * To store and delete term usage count
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class TermUsageServiceHashMapBasedImpl implements TermUsageService {

  private Map<String, Integer> mTermUsages =
    new ConcurrentHashMap<String, Integer>();

  public void incrementTermCount(final String pTerm) {
    final String term = pTerm.trim();
    if (mTermUsages.containsKey(term)) {
      mTermUsages.put(term, mTermUsages.get(term) + 1);
    } else {
      mTermUsages.put(term, 1);
    }
  }

  public void incrementTermCount(final String pTerm, final String pField) {
    throw new NoSuchMethodError("this method is not yet implemented");
  }

  public void decrementTermCount(final String pTerm) {
    final String term = pTerm.trim();
    if (mTermUsages.containsKey(term)) {
      int count = mTermUsages.get(term);
      if (count > 0) {
        mTermUsages.put(term, count - 1);
      }
    }
  }

  public int length() {
    return mTermUsages.size();
  }

  public Map<String, Integer> map() {
    return mTermUsages;
  }

  public int getUsageCountOf(final String pTerm) {
    return mTermUsages.get(pTerm.trim());
  }
}
