/* $Id:GenericItem.java 224 2007-07-14 10:39:36Z hasan $ */
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
import org.apache.lucene.document.NumberTools;

import java.sql.Timestamp;
import java.util.Map;
import java.util.List;

import com.ideabase.repository.common.exception.ServiceException;

/**
 * Generic item holds all extra object inside a hash map. it is very useful
 * for using any kind of purpose.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class GenericItem extends AbstractObjectBase implements VisitableObject {

  private static final String FIELD_PREFIX_PRICE = "price";
  private static final String FIELD_SUFFIX_ID = "_id";
  private static final String FIELD_SUFFIX_DATE = "_date";
  private static final String FIELD_SUFFIX_NUMBER = "_number";

  private Integer mId;
  private transient String mTitle;
  private Timestamp mCreatedOn;
  private Timestamp mLastUpdatedOn;

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
    addField(INDEX_FIELD_TITLE, pTitle);
  }

  public void setCreatedOn(final Timestamp pCreatedOn) {
    mCreatedOn = pCreatedOn;
  }

  public Timestamp getCreatedOn() {
    return mCreatedOn;
  }

  public void setLastUpdatedOn(final Timestamp pLastUpdatedOn) {
    mLastUpdatedOn = pLastUpdatedOn;
  }

  public Timestamp getLastUpdatedOn() {
    return mLastUpdatedOn;
  }

  public Document getDocument() {
    final Document document = super.getDocument();
    // Add fields to indexable. (no store)
    acceptVisitor(new Visitor() {
      public void visitField(final String pName, final String pValue) {
        // TODO: hardcoded behavior for indexing price or number type field
        final String name = pName.toLowerCase();
        if (name.startsWith(FIELD_PREFIX_PRICE) ||
            name.endsWith(FIELD_SUFFIX_DATE) ||
            name.endsWith(FIELD_SUFFIX_NUMBER)) {
          document.add(new Field(
              pName, NumberTools.longToString(Integer.parseInt(pValue)),
              Field.Store.NO, Field.Index.UN_TOKENIZED));
        } else if (name.endsWith(FIELD_SUFFIX_ID)) {
          document.add(new Field(pName, pValue,
                       Field.Store.NO, Field.Index.UN_TOKENIZED));
        } else {
          document.add(new Field(pName, pValue,
                       Field.Store.NO, Field.Index.TOKENIZED,
                       Field.TermVector.WITH_POSITIONS_OFFSETS));
        }
      }
    });
    return document;
  }

  // ---------------- VisitableObject implementation

  /**
   * {@inheritDoc}
   */
  public void acceptVisitor(final Visitor pVisitor) {
    if (pVisitor == null) {
      throw new ServiceException(null, "Null visitor is not accepted.");
    }
    for (final Map.Entry<String, String> entry : getFields().entrySet()) {
      pVisitor.visitField(entry.getKey(), entry.getValue());
    }
  }
}
