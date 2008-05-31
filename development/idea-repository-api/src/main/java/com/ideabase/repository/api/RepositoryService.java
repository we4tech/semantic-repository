/*
 * $Id: RepositoryService.java 260 2008-03-19 05:38:06Z hasan $
 ******************************************************************************
 * Copyright (C) 2007 somewhere in .Net ltd.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from somewhere in .Net LTD.  This notice must be included on
 * all copies, modifications and derivatives of this work.
 ******************************************************************************
 * $LastChangedBy: hasan $
 * $LastChangedDate: 2008-03-19 11:38:06 +0600 (Wed, 19 Mar 2008) $
 * $LastChangedRevision: 260 $
 ******************************************************************************
*/
package com.ideabase.repository.api;

import com.ideabase.repository.common.Query;
import com.ideabase.repository.common.cache.annotation.Cache;
import com.ideabase.repository.common.cache.annotation.CacheType;
import com.ideabase.repository.common.object.ObjectBase;
import com.ideabase.repository.common.object.Hit;
import com.ideabase.repository.common.object.AbstractObjectBase;
import com.ideabase.repository.common.object.PaginatedList;
import com.ideabase.repository.api.event.EventListener;
import com.ideabase.repository.api.cache.CacheableObjectBaseValidator;

import java.util.List;
import java.util.Map;
import java.io.IOException;

/**
 * Repository access and manage related API.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public interface RepositoryService {

  /**
   * Store or update {@code RepositoryItem} on persistent storage.<br>
   * if {@code RepositoryItem} has already assigned id, it will send for
   * update request.
   *
   * @param pRepositoryItem object which properties are exposed through
   *        annotation.
   * @return Return the id of newly created item.
   */
  @Cache(
      cachableObjectValidator = CacheableObjectBaseValidator.class,
      cacheType = CacheType.CACHE_PARAMETER_AFTER_METHOD_RETURNS
  )
  Integer save(final ObjectBase pRepositoryItem);

  /**
   * Remove an item from persistent storage.
   *
   * @param pRepositoryItemId delete an item from repository by this
   *        specified id.
   */
  @Cache(
      cacheType = CacheType.REMOVE_CACHE,
      parameterType = Integer.class
  )
  void delete(final Integer pRepositoryItemId);

  /**
   * Retrieve an item from storage and covert it to target object.
   * @param pItemId the number of item.
   * @return {@code T} typed object is created on runtime and populated over
   *         reflection.
   * @param pTargetClass object is converted to the target class.
   */
  @Cache(
      cacheType = CacheType.CACHE_RETURNED_OBJECT
  )
  <T extends ObjectBase> T getItem(final Integer pItemId,
                                   final Class<T> pTargetClass);

  /**
   * Retrieve a list of {@code integer} item id which is attached on parent item.
   * @param pParentItemId parent item id. all items are attached with parent id.
   * @param pRelationType relation type for example : article, blog, post,
   *        category etc..
   * @param pSkip how many rows will be skiped.
   * @param pMaxRow maximum number of returable rows.
   * @return retrieve a list of {@code Integer} item id.
   */
  @Cache( cacheType = CacheType.CACHE_RETURNED_OBJECT )
  List<Integer> getRelatedItems(final Integer pParentItemId,
                               final String pRelationType,
                               final int pSkip, final int pMaxRow);

  /**
   * Find the number of available right side items.
   * @param pBaseItemId parent item id.
   * @param pRelationType related object relation type name.
   * @return the number of related items.
   */
  int getRelatedItemsCount(final Integer pBaseItemId,
                           final String pRelationType);

  /**
   * Retrieve a list of {@code integer} item id which is attached on parent item.
   * @param pParentItemId parent item id. all items are attached with parent id.
   * @param pSkip how many rows will be skiped.
   * @param pMaxRow maximum number of returable rows.
   * @return retrieve a list of {@code Integer} item id.
   */
  @Cache(
      cacheType = CacheType.CACHE_RETURNED_OBJECT
  )
  Map<String, List<Integer>> getItems(final Integer pParentItemId,
                                      final int pSkip, final int pMaxRow);

  /**
   * Retrieve a list of {@code T} objects, which are attached with parent item.
   * @param pParentItemId parent item id, all items are attached with it.
   * @param pRelationType item relation type.
   * @param pSkip how many rows will be skiped.
   * @param pMaxRow maximum number of returnable rows.
   * @param pType object cast type.
   * @return a list of {@code T} objects.
   */
  @Cache(
      cacheType = CacheType.CACHE_RETURNED_OBJECT
  )
  <T extends AbstractObjectBase> List<T> getItemObjects(
      final Integer pParentItemId, final String pRelationType,
      final int pSkip, final int pMaxRow, final Class<T> pType);

  /**
   * Search Item by {@code Query} {@see Query}
   * @param pQuery retrieval query.
   * @return a list of result. {@code Hit} object is used to encapsulate
   *         all result.
   */
  @Cache(
      cacheType = CacheType.CACHE_RETURNED_OBJECT
  )
  PaginatedList<Hit> getItemsByQuery(final Query pQuery);

  /**
   * This method is for testing purpose, though it is an option for end developer.
   * but we are not encouraging.
   * @param pSkip maximum number of skippable items.
   * @param pMax maximum number of items.
   * @return return a list of all items.
   */
  @Cache(cacheType = CacheType.CACHE_RETURNED_OBJECT)
  List<Integer> getAllItems(final Integer pSkip, final Integer pMax);

  /**
   * Retrieve item by title string, if nothing is found null is returned.
   * @param pTitle item title.
   * @return {@see T} item object , or null if nothing is found
   * @param pType object type.
   */
  @Cache(cacheType = CacheType.CACHE_RETURNED_OBJECT)
  <T extends ObjectBase> T getItemByTitle(final String pTitle,
                                          final Class<T> pType);

  /**
   * Subscribe a new {@see EventListener} to the internal event manager.
   * @param pEventListener event listener instance.
   */
  void subscribeEventListener(final EventListener pEventListener);

  /**
   * Remove an already subscribed {@see EventListener}.
   * @param pEventListener an already subscribed event listener.
   */
  void unsubscribeEventListener(final EventListener pEventListener);

  /**
   * Create item relation with left side item to right side item id.
   * @param pRelationType relation type, for example, author, post,
   *                      category etc..
   * @param pLeftItemId left side item id.
   * @param pRightItemId right side item id.
   * @return return newly created item mapping id.
   */
  Integer addRelatedItem(final String pRelationType,
                          final Integer pLeftItemId,
                          final Integer pRightItemId);

  /**
   * Attach a list of items to an item.
   * @param pRelationType relation type.
   * @param pLeftItemId left side item id.
   * @param pRightItemIds right side list of items.
   * @return a list of created item mapping id.
   */
  List<Integer> addRelatedItems(final String pRelationType, 
                                final Integer pLeftItemId,
                                final List<Integer> pRightItemIds);

  /**
   * Delete a list of related items, which are attached with the given left
   * side item id.
   * @param pRelationType relation type.
   * @param pLeftItemId left side item id.
   */
  void deleteRelatedItems(final String pRelationType, final Integer pLeftItemId);

  /**
   * Delete a single right side item.
   * @param pRelationType relation type.
   * @param pLeftItemId left side item id.
   * @param pRightItemId right side item id.
   */
  void deleteRelatedItem(final String pRelationType,
                         final Integer pLeftItemId,
                         final Integer pRightItemId);

  /**
   * This method harms the index manager, because it destroy the whole index
   * files.<br>
   * the {@code pConfirmation} boolean argument is kept intentionally to aware
   * developer before invoking this method.
   * @param pConfirmation confirm do you really want to remove index or not.
   * @throws java.io.IOException if exception raised during processing.
   */
  void destroyIndex(final boolean pConfirmation) throws IOException;

  /**
   * Optimize current indexes. this method will invoke internal index service.
   */
  void optimizeIndex();

  /**
   * Optimize a specific index
   * @param pIndexName index repository name.
   */
  void optimizeSpecificIndex(final String pIndexName);

  /**
   * Total number of available items on the repository
   * @return
   */
  int getAllItemsCount();

  int getItemsCountByIndexRepository(final String pIndexRepository);
}
