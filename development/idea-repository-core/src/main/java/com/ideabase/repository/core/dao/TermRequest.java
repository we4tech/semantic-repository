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

package com.ideabase.repository.core.dao;

import java.util.List;

/**
 * Build request object for retrieving list of terms from the storage.
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class TermRequest {
  private List<String> mFields;
  private List<Integer> mItemIds;
  private int mOffset;
  private int mMax;

  public List<String> getFields() {
    return mFields;
  }

  public void setFields(final List<String> pFields) {
    mFields = pFields;
  }

  public List<Integer> getItemIds() {
    return mItemIds;
  }

  public void setItemIds(final List<Integer> pItemIds) {
    mItemIds = pItemIds;
  }

  public int getOffset() {
    return mOffset;
  }

  public void setOffset(final int pOffset) {
    mOffset = pOffset;
  }

  public int getMax() {
    return mMax;
  }

  public void setMax(final int pMax) {
    mMax = pMax;
  }
}
