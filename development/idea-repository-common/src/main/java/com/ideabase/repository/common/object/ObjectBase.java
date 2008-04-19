/* $Id:ObjectBase.java 224 2007-07-14 10:39:36Z hasan $ */
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

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.Map;

import com.ideabase.repository.common.Query;

/**
 * All object will implement this interface. it holds all required methods.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public interface ObjectBase {

  public static final String INDEX_FIELD_ID = "id";
  public static final String INDEX_FIELD_TITLE = "title";
  public static final String DEFAULT_FIELD = "text";
  public static final String INDEX_FIELD_PREFIX = "idx_";
  public static final String STORE_FIELD_PREFIX = "sto_";
  public static final String INDEX_CREATED_ON = "createdOn";
  public static final String INDEX_LAST_UPDATED_ON = "lastUpdatedOn";

  /**
   * Object id is returned through this method.
   * @return object id.
   */
  Integer getId();

  /**
   * Set object id through this method.
   * @param pId object id.
   */
  void setId(final Integer pId);

  /**
   * Return item title string.
   * @return item title is returned.
   */
  String getTitle();

  /**
   * Set item default title.
   * @param pTitle item title string.
   */
  void setTitle(final String pTitle);

  /**
   * Set item creational date. when this item was created.
   * @param pCreatedOn created date of the item.
   */
  void setCreatedOn(final Timestamp pCreatedOn);

  /**
   * Return the date when this item was created.
   * @return the date of creation.
   */
  Timestamp getCreatedOn();

  /**
   * Set the date when this item was last modified.
   * @param pLastUpdatedOn the date of last modification.
   */
  void setLastUpdatedOn(final Timestamp pLastUpdatedOn);

  /**
   * Return the last modification date.
   * @return the last modification date.
   */
  Timestamp getLastUpdatedOn();

  /**
   * Set index repository name, <tt>i.e. aawaj</tt>
   * @param pIndex repository name.
   */
  void setIndexRepository(final String pIndex);

  /**
   * Return the index repository name, <tt>i.e. aawaj</tt>
   * @return the index repository name.
   */
  String getIndexRepository();

  /**
   * Add related item.
   * @param pRelationType the name of relation type.
   * @param pItemId the item id.
   */
  void addRelatedItem(final String pRelationType, final Integer pItemId);

  /**
   * Add a list of related items to a specific relation group.
   * @param pRelationType relation group name.
   * @param pItemIds list of related items.
   */
  void addRelatedItems(final String pRelationType,
                       final List<Integer> pItemIds);

  /**
   * Remove the related item.
   * @param pRelationType the name of relation type.
   * @param pItemId the item id.
   */
  void removeRelatedItem(final String pRelationType,
                                final Integer pItemId);

  /**
   * Set related items type and item id map.
   * @param pRelatedItemsMap related items type and item id map.
   */
  public void setRelatedItemsMap(
      final Map<String, List<Integer>> pRelatedItemsMap);

  /**
   * Return all related items with the type and item id in a map.
   * @return all related items with the type and item id in a map.
   */
  public Map<String, List<Integer>> getRelatedItemsMap();

  /**
   * The list of relation types, it is determined based on the related articles.
   * if different relation typed items are attached, both relation types are
   * returned through this method.
   *
   * @return list of relation types attached with this item.
   */
  Set<String> getRelationTypes();

  /**
   * List of related items of a specific type.
   * @param pRelationType relation type name.
   * @return list of related items of a specific type.
   */
  List<Integer> getRelatedItemsByRelationType(final String pRelationType);

  /**
   * Return {@code true} If related item has been removed or changed.
   * @return Return {@code true} If related item has been removed or changed.
   */
  boolean isUpdateRelatedItemEnabled();

  /**
   * Return information for indexer.
   * @return document is prepared for indexer.
   */
  Document getDocument();

  /**
   * Return all fields from this object.
   * @return all fields for this object.
   */
  Map<String, String> getFields();

  /**
   * Set all available fields for this object.
   * @param pName field name
   * @param pValue field value.
   */
  void addField(final String pName, final String pValue);

  /**
   * Remove field value.
   * @param pName remove field value.
   */
  void removeField(final String pName);

  /**
   * Generate request from the current object.
   * @return request from the current object.
   */
  String toXml();

  /**
   * Set fields to the current object.
   * @param pFields fields of this object.
   */
  void setFields(final Map<String, String> pFields);

  /**
   * Return the value of the field.
   * @param pName name of the field.
   * @return value of the field.
   */
  String getField(final String pName);

  /**
   * This query is made for testing the integrity of the object.<br>
   * for example - user object must be unique with user name or email address.
   * thats why this {@see Query} is returned with preferred integrity keys.<br>
   * if no verification is required, this method must return null<br>
   * {@see RepositoryService} will test the integrity first, if it fails it
   * will ended with an exception.
   * <br>
   * @return the query which reflects the uniquness of the object. if this
   *         query returns a list of items. that mean this object has already
   *         been created. for no required integirity this method must
   *         return null;
   */
  Query getIntegrityVerificationQuery();
}
