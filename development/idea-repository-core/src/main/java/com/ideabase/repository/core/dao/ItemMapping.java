/* $Id:ItemMapping.java 249 2007-12-02 08:32:47Z hasan $ */
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
 * $LastChangedBy:hasan $
 * $LastChangedDate:2007-12-02 14:32:47 +0600 (Sun, 02 Dec 2007) $
 * $LastChangedRevision:249 $
 ******************************************************************************
*/
package com.ideabase.repository.core.dao;

import com.ideabase.repository.common.ObjectToString;

/**
 * Mapping between two repository item.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class ItemMapping {

  private Integer id;
  private Integer leftId;
  private Integer rightId;
  private String relationType;

  public Integer getId() {
    return id;
  }

  public void setId(final Integer pId) {
    id = pId;
  }

  public Integer getLeftId() {
    return leftId;
  }

  public void setLeftId(final Integer pLeftId) {
    leftId = pLeftId;
  }

  public Integer getRightId() {
    return rightId;
  }

  public void setRightId(final Integer pRightId) {
    rightId = pRightId;
  }

  public String getRelationType() {
    return relationType;
  }

  public void setRelationType(final String pRelationType) {
    relationType = pRelationType;
  }

  public static class Builder {
    private final ItemMapping mMapping = new ItemMapping();

    public Builder id(final Integer pId) {
      mMapping.setId(pId);
      return this;
    }

    public Builder leftId(final Integer pLeftId) {
      mMapping.setLeftId(pLeftId);
      return this;
    }

    public Builder rightId(final Integer pRightId) {
      mMapping.setRightId(pRightId);
      return this;
    }

    public Builder relationType(final String pRelationType) {
      mMapping.setRelationType(pRelationType);
      return this;
    }

    public ItemMapping build() {
      return mMapping;
    }
  }

  @Override
  public String toString() {
    return ObjectToString.toString(this);
  }


  @Override
  public boolean equals(Object pObj) {
    if (pObj instanceof ItemMapping) {
      return pObj.hashCode() == hashCode();
    }
    return super.equals(pObj);
  }

  @Override
  public int hashCode() {
    return getId() != null ? getId() : super.hashCode();
  }
}
