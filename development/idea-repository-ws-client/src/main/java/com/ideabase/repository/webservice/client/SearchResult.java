/*
 * $Id: SearchResult.java 250 2008-01-07 10:18:29Z hasan $
 ******************************************************************************
 * Copyright (C) 2007 somewhere in .Net ltd.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from somewhere in .Net LTD.  This notice must be included on
 * all copies, modifications and derivatives of this work.
 ******************************************************************************
 * $LastChangedBy: hasan $
 * $LastChangedDate: 2008-01-07 16:18:29 +0600 (Mon, 07 Jan 2008) $
 * $LastChangedRevision: 250 $
 ******************************************************************************
*/
package com.ideabase.repository.webservice.client;

import java.util.List;

import com.ideabase.repository.common.ObjectToString;

/**
 * Search result which holds the information about paging and number rows.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class SearchResult {
  private Integer maxRows;
  private Integer pageCount;
  private List<ItemRef> mItemRefs;

  public Integer getMaxRows() {
    return maxRows;
  }

  public void setMaxRows(final Integer pMaxRows) {
    maxRows = pMaxRows;
  }

  public Integer getPageCount() {
    return pageCount;
  }

  public void setPageCount(final Integer pPageCount) {
    pageCount = pPageCount;
  }

  public List<ItemRef> getItems() {
    return mItemRefs;
  }

  public void setItems(final List<ItemRef> pItemRefs) {
    mItemRefs = pItemRefs;
  }

  @Override
  public String toString() {
    return ObjectToString.toString(this);
  }
}
