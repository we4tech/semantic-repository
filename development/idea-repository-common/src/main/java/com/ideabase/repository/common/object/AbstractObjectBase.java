/* $Id:AbstractObjectBase.java 224 2007-07-14 10:39:36Z hasan $ */
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
 * $LastChangedDate:2007-07-14 03:39:36 -0700 (Sat, 14 Jul 2007) $
 * $LastChangedRevision:224 $
 ******************************************************************************
*/
package com.ideabase.repository.common.object;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.DateTools;

import java.sql.Timestamp;
import java.util.*;
import static com.ideabase.repository.common.XmlConstants.*;
import com.ideabase.repository.common.Query;
import com.ideabase.repository.common.ObjectToString;

/**
 * Abstract implementation of {@see ObjectBase}
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public abstract class AbstractObjectBase implements ObjectBase {

  private Map<String, List<Integer>> mRelatedItemsMap =
      new HashMap<String, List<Integer>>();
  private Map<String, String> mFields = new HashMap<String, String>();
  private Integer mId;
  private transient String mTitle;
  private Timestamp mCreatedOn;
  private Timestamp mLastModifiedOn;
  private transient boolean mRelatedItemUpdateEnabled = false;
  private String mIndex;

  public Integer getId() {
    return mId;
  }

  public void setId(final Integer pId) {
    mId = pId;
  }

  public String getTitle() {
    if (mTitle == null) {
      return mFields.get(INDEX_FIELD_TITLE);
    }
    return mTitle;
  }

  public void setTitle(final String pTitle) {
    mTitle = pTitle;
    mFields.put(INDEX_FIELD_TITLE, pTitle);
  }

  public void setCreatedOn(final Timestamp pCreatedOn) {
    mCreatedOn = pCreatedOn;
  }

  public Timestamp getCreatedOn() {
    return mCreatedOn;
  }

  public void setLastUpdatedOn(final Timestamp pLastUpdatedOn) {
    mLastModifiedOn = pLastUpdatedOn;
  }

  public Timestamp getLastUpdatedOn() {
    return mLastModifiedOn;
  }

  public void setIndexRepository(final String pIndex) {
    mIndex = pIndex;
  }

  public String getIndexRepository() {
    return mIndex;
  }

  public void addRelatedItem(final String pRelationType,
                             final Integer pItemId) {
    List<Integer> relations = mRelatedItemsMap.get(pRelationType);
    if (relations == null) {
      relations = new ArrayList<Integer>();
      relations.add(pItemId);
      mRelatedItemsMap.put(pRelationType, relations);
    } else {
      relations.add(pItemId);
    }
    mRelatedItemUpdateEnabled = true;
  }

  public void addRelatedItems(final String pRelationType,
                              final List<Integer> pItemIds) {
    mRelatedItemsMap.put(pRelationType, pItemIds);
    mRelatedItemUpdateEnabled = true;
  }

  public void removeRelatedItem(final String pRelationType,
                                final Integer pItemId) {
    List<Integer> relations = mRelatedItemsMap.get(pRelationType);
    if (relations != null) {
      relations.remove(pItemId);
    }
    mRelatedItemUpdateEnabled = true;
  }

  public void setRelatedItemsMap(
      final Map<String, List<Integer>> pRelatedItemsMap) {
    mRelatedItemsMap = pRelatedItemsMap;
    mRelatedItemUpdateEnabled = true;
  }

  public Map<String, List<Integer>> getRelatedItemsMap() {
    return mRelatedItemsMap;
  }

  public Set<String> getRelationTypes() {
    Set<String> relationTypes = mRelatedItemsMap.keySet();
    if (relationTypes == null) {
      return Collections.emptySet();
    }
    return relationTypes;
  }

  public List<Integer> getRelatedItemsByRelationType(final String pRelationType)
  {
    List<Integer> items = mRelatedItemsMap.get(pRelationType);
    return items;
  }

  public boolean isUpdateRelatedItemEnabled() {
    return mRelatedItemUpdateEnabled;
  }

  public void enableRelatedItemUpdate() {
    mRelatedItemUpdateEnabled = true;
  }

  public Document getDocument() {
    final Document document = new Document();
    // Add id field
    document.add(new Field(ObjectBase.INDEX_FIELD_ID, String.valueOf(getId()),
                 Field.Store.YES, Field.Index.UN_TOKENIZED));
    // Add title field, tokenized index
    document.add(new Field(ObjectBase.INDEX_FIELD_TITLE, getTitle(),
                 Field.Store.YES, Field.Index.TOKENIZED));

    // set created on
    if (getCreatedOn() != null) {
      final Field indexFieldCreatedOn =
          new Field(
              ObjectBase.INDEX_CREATED_ON,
              DateTools.dateToString(getCreatedOn(),
                                     DateTools.Resolution.MILLISECOND),
              Field.Store.NO, Field.Index.UN_TOKENIZED);
      document.add(indexFieldCreatedOn);
    }

    // set updated on
    if (getLastUpdatedOn() != null) {
      final Field indexFieldCreatedOn =
          new Field(ObjectBase.INDEX_LAST_UPDATED_ON,
                    DateTools.dateToString(getLastUpdatedOn(),
                                           DateTools.Resolution.MILLISECOND),
                    Field.Store.NO, Field.Index.UN_TOKENIZED);
      document.add(indexFieldCreatedOn);
    }

    if (mFields != null && !mFields.isEmpty()) {
      for (final Map.Entry<String, String> entry : mFields.entrySet()) {
        if (document.getField(entry.getKey()) == null) {
          document.add(new Field(entry.getKey(), entry.getValue(),
                                 Field.Store.NO, Field.Index.TOKENIZED));
        }
      }
    }
    return document;
  }

  public Map<String, String> getFields() {
    return mFields;
  }

  public void addField(final String pName, final String pValue) {
    mFields.put(pName, pValue);
  }

  public void removeField(final String pName) {
    mFields.remove(pName);
  }

  public String toXml() {
    final StringBuilder buffer = new StringBuilder();
    // <fields>
    buffer.append("<").append(ELEMENT_FIELDS).append(">");
    for (final Map.Entry<String, String> entry : getFields().entrySet()) {
      // <field>
      buffer.append("<").append(ELEMENT_FIELD).append(" ").
          append(ATTR_NAME).append(QUOTE_START).append(entry.getKey()).
          append(QUOTE_END).append(">");

      // content
      buffer.append(BLOCK_CDATA_START).append(entry.getValue()).
             append(BLOCK_CDATA_END);

      buffer.append("</").append(ELEMENT_FIELD).append(">");
      // </field>
    }
    buffer.append("</").append(ELEMENT_FIELDS).append(">");
    // </fields>
    return buffer.toString();
  }

  public void setFields(final Map<String, String> pFields) {
    mFields = pFields;
  }

  public String getField(final String pName) {
    return mFields.get(pName);
  }

  /**
   * <strong><i>Return null, that mean no integrity verification is
   * enabled.</i></strong><br>
   * {@inheritDoc}
   */
  public Query getIntegrityVerificationQuery() {
    return null;
  }

  @Override
  public String toString() {
    return ObjectToString.toString(this);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    final AbstractObjectBase that = (AbstractObjectBase) o;
    return this.getId() != null && that.getId() != null
           && this.getId().hashCode() == that.getId().hashCode();
  }

  @Override
  public int hashCode() {
    return (mId != null ? mId.hashCode() : super.hashCode());
  }
}
