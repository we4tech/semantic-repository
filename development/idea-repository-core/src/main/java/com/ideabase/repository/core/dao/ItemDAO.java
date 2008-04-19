/*
 * $Id:ItemDAO.java 249 2007-12-02 08:32:47Z hasan $
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
 * Store object in a persistent storage.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public interface ItemDAO {

  /**
   * Create {@code Item} object in persistent storage.
   * @param pItem {@code Item} to store in
   * @return newly created object id.
   */
  Integer createItem(final Item pItem);

  /**
   * Update an existing {@code Item}.
   * @param pItem object that needs to store.
   */
  void updateItem(final Item pItem);

  /**
   * Delete object by creating query from parameter {@code Item} object.
   * @param pItem build query from this object.
   */
  void deleteItem(final Item pItem);

  /**
   * Delete {@code Item} object by specific id.
   * @param pId object id.
   */
  void deleteItemById(final Integer pId);

  /**
   * Retrieve a list of {@code Item} object.
   * @param pItem build query from this object.
   * @param pSkip page number.
   * @param pMax maximum rows .
   * @return a list of {@code Integer} id of {@code Item}.
   */
  List<Item> findItems(final Item pItem, final Integer pSkip,
                            final Integer pMax);

  /**
   * Retrieve {@code Item} by specified id.
   * @param pItemId item persistent refernce id
   * @return {@code Item} object.
   */
  Item findItem(final int pItemId);

  /**
   * Find total number of approximate {@code Item} objects.
   * @param pItem query from domain object attributes.
   * @return number of total approx rows.
   */
  int countItems(final Item pItem);

  /**
   * Being transaction .
   */
  void startTransaction();

  /**
   * Rollback transaction.
   */
  void endTransaction();

  /**
   * Commit transaction.
   */
  void commitTransaction();
}
