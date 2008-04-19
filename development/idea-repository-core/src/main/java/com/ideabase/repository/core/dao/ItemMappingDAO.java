/*
 * $Id:ItemMappingDAO.java 249 2007-12-02 08:32:47Z hasan $
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

import java.util.List;

/**
 * CRUD functionalities for {@code ItemMapping} domain model.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public interface ItemMappingDAO {

  /**
   * Create {@code ItemMapping} object on persistent storage.
   * @param pItemMapping domain object.
   * @return return ID of newly created item object.
   */
  Integer createMapping(final ItemMapping pItemMapping);

  /**
   * Update {@code ItemMapping} object, parameter mapping object must contain
   * the id. without id number IllegalArgumentException is thrown.
   * @param pItemMapping domain object.
   */
  void updateMapping(final ItemMapping pItemMapping);

  /**
   * Delete mapping object, build query from specified {@code ItemMapping}.
   * @param pItemMapping query caluse is built from this parameter.
   */
  void deleteMapping(final ItemMapping pItemMapping);

  /**
   * Retrieve a list of {@code Integer} id of {@code Item} objects.
   * @return a list of {@code Integer} id of {@code Item} objects. @param pRightId built query with leftId parameter.
   * @param pSkip skip number of items.
   * @param pMax list total number of items.
   * @param pLeftId left side item.
   */
  List<ItemMapping> findRightSideItemsByType(final Integer pLeftId,
                                             final String pRelationType,
                                             final Integer pSkip,
                                             final Integer pMax);

  /**
   * Retrieve a list of {@code Integer} id of {@code Item} objects.
   * @return a list of {@code Integer} id of {@code Item} objects. @param pRightId built query with leftId parameter.
   * @param pSkip skip number of items.
   * @param pMax list total number of items.
   * @param pLeftId left side item.
   */
  List<ItemMapping> findRightSideItems(final Integer pLeftId,
                                       final Integer pSkip, final Integer pMax);

  /**
   * Retrieve a list of {@code Integer} id of {@code Item} objects.
   * @return a list of {@code Integer} id of {@code Item} objects. @param pLeftId built query with rightId parameter.
   * @param pSkip skip number of items.
   * @param pMax list total number of items.
   * @param pRightId right side item.
   */
  List<ItemMapping> findLeftSideItemsByType(final Integer pRightId,
                                            final String pRelationType,
                                            final Integer pSkip,
                                            final Integer pMax);

  /**
   * Retrieve a list of {@code Integer} id of {@code Item} objects.
   * @return a list of {@code Integer} id of {@code Item} objects. @param pLeftId built query with rightId parameter.
   * @param pSkip skip number of items.
   * @param pMax list total number of items.
   * @param pRightId right side item.
   */
  List<ItemMapping> findLeftSideItems(final Integer pRightId,
                                      final Integer pSkip, final Integer pMax);


  /**
   * Count total number of available right side attached objects.
   * @param pLeftId left attached object id
   * @return total number of items.
   * @param pRelationType relation type 
   */
  int countRightSideItems(final Integer pLeftId, final String pRelationType);

  /**
   * Count total number of available left side attached objects.
   * @param pRightId right side attached objects.
   * @return total number of items.
   * @param pRelationType relation type.
   */
  int countLeftSideItems(final Integer pRightId, final String pRelationType);

  /**
   * Retrieve a specific {@code ItemMapping} by given id.
   * @param pMappingId database reference id for {@code ItemMapping}
   * @return {@code ItemMapping} object instance is retruned. if nothing found
   *         null returned.
   */
  ItemMapping findItemById(final int pMappingId);

  /**
   * Delete {@code ItemMapping} by specified id.
   * @param pMappingId item mapping id.
   */
  void deleteMappingById(final int pMappingId);

  /**
   * Find all unique relation type from the item mapping.
   * @param pLeftItemId find by left side item id.
   * @return a list of unique relation type.
   */
  List<String> findAllRelationTypesFromItemId(final Integer pLeftItemId);
}
