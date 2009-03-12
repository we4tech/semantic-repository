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

/**
 * Domain model Term
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class Term {
  private int mId;
  private String mTerm;
  private int mCount;
  private String mField;
  private int mItemId;

  public int getId() {
    return mId;
  }

  public void setId(final int pId) {
    mId = pId;
  }

  public String getTerm() {
    return mTerm;
  }

  public void setTerm(final String pTerm) {
    mTerm = pTerm;
  }

  public int getCount() {
    return mCount;
  }

  public void setCount(final int pCount) {
    mCount = pCount;
  }

  public String getField() {
    return mField;
  }

  public void setField(final String pField) {
    mField = pField;
  }

  public int getItemId() {
    return mItemId;
  }

  public void setItemId(final int pItemId) {
    mItemId = pItemId;
  }

  public static class Builder {
    private Term mTerm = new Term();

    public Builder term(final String pTerm) {
      mTerm.setTerm(pTerm); return this;
    }

    public Builder count(final int pCount) {
      mTerm.setCount(pCount);
      return this;
    }

    public Builder id(final int pId) {
      mTerm.setId(pId);
      return this;
    }

    public Builder field(final String pField) {
      mTerm.setField(pField);
      return this;
    }

    public Builder itemId(final int pItemId) {
      mTerm.setItemId(pItemId);
      return this;
    }

    public Term build() { return mTerm; }
  }
}
