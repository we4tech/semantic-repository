/* $Id: Hit.java 250 2008-01-07 10:18:29Z hasan $ */
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
 * $LastChangedDate: 2008-01-07 16:18:29 +0600 (Mon, 07 Jan 2008) $
 * $LastChangedRevision: 250 $
 ******************************************************************************
*/
package com.ideabase.repository.common.object;

import com.ideabase.repository.common.ObjectToString;

import java.util.Map;

/**
 * Store search result inside this object. including score and fields.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class Hit {
  private Integer mHitId;
  private Integer mId;
  private String mTitle;
  private Map<String, String> mFields;
  private float mScore;

  public Integer getHitId() {
    return mHitId;
  }

  public void setHitId(final Integer pHitId) {
    mHitId = pHitId;
  }

  public Integer getId() {
    return mId;
  }

  public void setId(final Integer pId) {
    mId = pId;
  }

  public String getTitle() {
    return mTitle;
  }

  public void setTitle(final String pTitle) {
    mTitle = pTitle;
  }

  public Map<String, String> getFields() {
    return mFields;
  }

  public void setFields(final Map<String,String> pFields) {
    mFields = pFields;
  }

  public float getScore() {
    return mScore;
  }

  public void setScore(final float pScore) {
    mScore = pScore;
  }


  @Override
  public String toString() {
    return ObjectToString.toString(this);
  }
}
