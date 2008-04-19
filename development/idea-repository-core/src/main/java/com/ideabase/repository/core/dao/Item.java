/*
 * $Id:Item.java 249 2007-12-02 08:32:47Z hasan $
 ******************************************************************************
 * Copyright (C) 2007 somewhere in .Net ltd.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from somewhere in .Net LTD.  This notice must be included on
 * all copies, modifications and derivatives of this work.
 ******************************************************************************
 * $LastChangedBy:hasan $
 * $LastChangedDate:2007-12-02 14:32:47 +0600 (Sun, 02 Dec 2007) $
 * $LastChangedRevision:249 $
 ******************************************************************************
*/
package com.ideabase.repository.core.dao;

import java.sql.Timestamp;

/**
 * Domain model or repository item.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class Item {

  private Integer id;
  private String title;
  private String document;
  private Timestamp createdOn;
  private Timestamp lastUpdatedOn;
  private String indexRepository;

  public String getDocument() {
    return document;
  }

  public void setDocument(final String pDocument) {
    document = pDocument;
  }

  public Timestamp getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(final Timestamp pCreatedOn) {
    createdOn = pCreatedOn;
  }

  public Timestamp getLastUpdatedOn() {
    return lastUpdatedOn;
  }

  public void setLastUpdatedOn(final Timestamp pLastUpdatedOn) {
    lastUpdatedOn = pLastUpdatedOn;
  }

  public Integer getId() {
    return id;
  }

  public void setId(final Integer pId) {
    id = pId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(final String pTitle) {
    title = pTitle;
  }

  public String getIndexRepository() {
    return indexRepository;
  }

  public void setIndexRepository(final String pIndexRepository) {
    indexRepository = pIndexRepository;
  }

  public static class Builder {
    private Item mItem = new Item();

    public Builder title(final String pTitle) {
      mItem.setTitle(pTitle);
      return this;
    }

    public Builder id(final Integer pId) {
      mItem.setId(pId);
      return this;
    }

    public Builder document(final String pDocument) {
      mItem.setDocument(pDocument);
      return this;
    }

    public Builder createdOn(final Timestamp pCreatedOn) {
      mItem.setCreatedOn(pCreatedOn);
      return this;
    }

    public Builder lastUpdatedOn(final Timestamp pLastUpdatedOn) {
      mItem.setLastUpdatedOn(pLastUpdatedOn);
      return this;
    }

    public Builder indexRepository(final String pIndex) {
      mItem.setIndexRepository(pIndex);
      return this;
    }

    public Item build() {
      return mItem;
    }
  }

  @Override
  public String toString() {
    return new StringBuilder().append("{").append("id: ").append(id).
        append(", title: ").append(title).append(", document: ").
        append(document).append(", createdOn: ").append(createdOn).
        append(", lastUpdatedOn: ").append(lastUpdatedOn).
        append("}").toString();
  }


  @Override
  public boolean equals(Object pObj) {
    if (pObj instanceof Item) {
      return pObj.hashCode() == hashCode();
    }
    return super.equals(pObj);
  }

  @Override
  public int hashCode() {
    return getId() != null ? getId() : super.hashCode();
  }
}
