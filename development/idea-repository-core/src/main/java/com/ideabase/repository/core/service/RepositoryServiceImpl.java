/* $Id:RepositoryServiceImpl.java 249 2007-12-02 08:32:47Z hasan $ */
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
package com.ideabase.repository.core.service;

import com.ideabase.repository.api.EventManager;
import com.ideabase.repository.api.RepositoryService;
import com.ideabase.repository.api.event.EventListener;
import com.ideabase.repository.common.Query;
import com.ideabase.repository.common.CommonConstants;
import static com.ideabase.repository.common.CommonConstants.INDEX_DEFAULT;
import com.ideabase.repository.common.exception.ServiceException;
import com.ideabase.repository.common.object.AbstractObjectBase;
import com.ideabase.repository.common.object.Hit;
import com.ideabase.repository.common.object.ObjectBase;
import com.ideabase.repository.common.object.PaginatedList;
import com.ideabase.repository.core.dao.Item;
import com.ideabase.repository.core.dao.ItemDAO;
import com.ideabase.repository.core.dao.ItemMapping;
import com.ideabase.repository.core.dao.ItemMappingDAO;
import com.ideabase.repository.core.helper.ObjectPopulateHelper;
import com.ideabase.repository.core.index.RepositoryItemIndex;
import com.ideabase.repository.core.search.RepositoryItemSearch;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.lucene.index.Term;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.*;

/**
 * Implementation of {@code RepositoryService}.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan
 *         (hasan)</a>
 */
public class RepositoryServiceImpl implements RepositoryService {

  /**
   * Logger instance.
   */
  private final Logger LOG = LogManager.getLogger(getClass());

  /**
   * Debug enable state
   */
  private final boolean DEBUG = LOG.isDebugEnabled();

  /**
   * Empty where caluse
   */
  private final Item EMPTY_WHERE = new Item() {{}};

  /**
   * {@see ItemDAO} instance.
   */
  private final ItemDAO mItemDAO;

  /**
   * {@see ItemMappingDAO} item mapping dao instance.
   */
  private final ItemMappingDAO mItemMappingDAO;

  /**
   * {@see RepositoryItemIndex} repository item index service.
   */
  private final Map<String, RepositoryItemIndex> mRepositoryItemIndexMap = new HashMap<String, RepositoryItemIndex>();

  /**
   * {@see RepositoryItemSearch} service.
   */
  private final Map<String, RepositoryItemSearch> mRepositoryItemSearchMap = new HashMap<String, RepositoryItemSearch>();

  /**
   * Event manager service.
   */
  private final EventManager mEventManager;

  /**
   * default key for index service.
   */
  private static final String KEY_DEFAULT = "default";

  /**
   * Default repository item index service instance.
   */
  private RepositoryItemIndex mDefaultRepositoryItemIndex;

  /**
   * Default repository item search service instance.
   */
  private RepositoryItemSearch mDefaultRepositoryItemSearch;

  private Item mEmptyItem = new Item();
  private ItemMapping mEmptyItemMapping = new ItemMapping();

  /**
   * Default constructor, it accepts required dependencies {@see ItemDAO},
   * {@see ItemMappingDAO}, {@see RepositoryItemIndex} and
   * {@see RepositoryItemSearch}.
   *
   * @param pItemDAO required dependency
   * @param pItemMappingDAO required dependency.
   * @param pEventManager required dependency.
   */
  public RepositoryServiceImpl(final ItemDAO pItemDAO,
                               final ItemMappingDAO pItemMappingDAO,
                               final EventManager pEventManager) {
    mItemDAO = pItemDAO;
    mItemMappingDAO = pItemMappingDAO;
    mEventManager = pEventManager;
  }

  /**
   * Set item indexing related service instance, with an associated key.
   *
   * @param pRepositoryItemIndexMap instance of repository item index service.
   */
  public void setRepositoryItemIndexServices(
      final Map<String, RepositoryItemIndex> pRepositoryItemIndexMap) {
    mRepositoryItemIndexMap.putAll(pRepositoryItemIndexMap);
    mDefaultRepositoryItemIndex = mRepositoryItemIndexMap.get(KEY_DEFAULT);
    if (mDefaultRepositoryItemIndex == null) {
      throw new RuntimeException("No default repository item index service is defined.");
    }
  }

  /**
   * Return the list of {@see RepositoryItemIndex} service instance with their
   * associated key in a {@see Map} object.
   * @return map of {@see RepositoryItemIndex} service instances.
   */
  public Map<String, RepositoryItemIndex> getRepositoryItemIndexMap() {
    return mRepositoryItemIndexMap;
  }

  /**
   * Set item search related service instance with an associated key.
   * @param pRepositoryItemSearchMap instance of repository item search service.
   */
  public void setRepositoryItemSearchServices(
      final Map<String, RepositoryItemSearch> pRepositoryItemSearchMap) {
    mRepositoryItemSearchMap.putAll(pRepositoryItemSearchMap);
    mDefaultRepositoryItemSearch = mRepositoryItemSearchMap.get(KEY_DEFAULT);
    if (mDefaultRepositoryItemSearch == null) {
      throw new RuntimeException("No default repository item search service is defined.");
    }
  }

  /**
   * Return the list of {@see RepositoryItemSearch} service instance with their
   * associated key in a {@see Map} object.
   * @return map of {@see RepositoryItemSearch} search instances.
   */
  public Map<String, RepositoryItemSearch> getRepositoryItemSearchMap() {
    return mRepositoryItemSearchMap;
  }

  /**
   * If the parameter {@see ObjectBase} object has no id, create a new
   * {@see Item} object in the persistent storage and send it to
   * perform indexing.<br>
   * if the parameter {@see ObjectBase} object has id, then update the existing
   * {@see Item} object and update the index.<br>
   * <br>
   * {@inheritDoc}
   */
  public Integer save(final ObjectBase pRepositoryItem) {
    if (DEBUG) {
      LOG.debug("Store item - " + pRepositoryItem);
    }
    final Integer itemId;
    if (pRepositoryItem.getId() == null) {
      itemId = createItem(pRepositoryItem);
      pRepositoryItem.setId(itemId);

      // index this item
      addIndex(pRepositoryItem);
    } else {
      // update on persistent storage
      itemId = updateItem(pRepositoryItem);
      // update on index
      updateIndex(pRepositoryItem);
    }
    return itemId;
  }

  private void addIndex(final ObjectBase pRepositoryItem) {
    if (DEBUG) {
      LOG.debug("Adding new item to the index (" +
                pRepositoryItem.getIndexRepository() + ").");
    }
    final String indexRepository = pRepositoryItem.getIndexRepository();
    if (indexRepository == null
        || indexRepository.equalsIgnoreCase(KEY_DEFAULT)) {
      synchronized (mDefaultRepositoryItemIndex) {
        mDefaultRepositoryItemIndex.addDocument(pRepositoryItem.getDocument());
        mDefaultRepositoryItemIndex.optimize();
      }
    } else {
      final RepositoryItemIndex indexer =
          mRepositoryItemIndexMap.get(indexRepository.toLowerCase());
      synchronized (indexer) {
        indexer.addDocument(pRepositoryItem.getDocument());
        indexer.optimize();
      }
    }
  }

  private void updateIndex(final ObjectBase pRepositoryItem) {
    if (DEBUG) {
      LOG.debug("Updating an item from index (" +
                pRepositoryItem.getIndexRepository() + "), object id - " +
                pRepositoryItem.getId());
    }
    final Term idTerm = new Term(ObjectBase.INDEX_FIELD_ID,
                                 String.valueOf(pRepositoryItem.getId()));
    final String indexRepository = pRepositoryItem.getIndexRepository();
    if (indexRepository == null
        || indexRepository.equalsIgnoreCase(KEY_DEFAULT)) {
      synchronized (mDefaultRepositoryItemIndex) {
        mDefaultRepositoryItemIndex.updateDocument(idTerm, pRepositoryItem.getDocument());
        mDefaultRepositoryItemIndex.optimize();
      }
    } else {
      final RepositoryItemIndex indexer =
          mRepositoryItemIndexMap.get(indexRepository.toLowerCase());
      synchronized (indexer) {
        indexer.updateDocument(idTerm, pRepositoryItem.getDocument());
        indexer.optimize();
      }
    }
  }

  private Integer updateItem(final ObjectBase pRepositoryItem) {
    Integer itemId = pRepositoryItem.getId();
    if (DEBUG) {
      LOG.debug("Update an existing object of id - " + itemId);
    }
    try {
      final Item item = new Item();
      // Populate Item object from user defined object.
      item.setCreatedOn(pRepositoryItem.getCreatedOn());
      if (pRepositoryItem.getLastUpdatedOn() != null) {
        item.setLastUpdatedOn(pRepositoryItem.getLastUpdatedOn());
      } else {
        item.setLastUpdatedOn(new Timestamp(System.currentTimeMillis()));
      }
      item.setId(pRepositoryItem.getId());
      item.setTitle(pRepositoryItem.getTitle());
      item.setDocument(pRepositoryItem.toXml());

      // update on persistent storage
      mItemDAO.updateItem(item);

      if (pRepositoryItem.isUpdateRelatedItemEnabled()) {
        LOG.debug("Related item update is enabled.");
        // Drop existing relations
        final ItemMapping where = new ItemMapping.Builder().
            leftId(itemId).build();
        mItemMappingDAO.deleteMapping(where);

        // Create new relation
        attachRelatedItems(pRepositoryItem);
      } else {
        LOG.debug("Related item update is not enabled.");
      }
    } catch (Exception e) {
      LOG.warn("Exception raised, rollback transaction.");
      throw ServiceException.
          aNew(pRepositoryItem, "Failed to update an existing item.", e);
    }
    return itemId;
  }

  private Integer createItem(final ObjectBase pRepositoryItem) {
    LOG.debug("Create new object.");
    LOG.debug("Begin transaction.");
    Integer itemId = null;
    try {
      itemId = saveItem(pRepositoryItem);

      // Set item id on the target object.
      pRepositoryItem.setId(itemId);
      if (DEBUG) {
        LOG.debug("Newly created item - " + itemId);
      }

      // create related object mapping.
      attachRelatedItems(pRepositoryItem);

      LOG.debug("Commit transaction.");
    } catch (Exception e) {
      LOG.warn("Exception raised, Rollback transaction.");
      throw ServiceException.
          aNew(pRepositoryItem, "Failed to create a new item.", e);
    }
    return itemId;
  }

  private void attachRelatedItems(final ObjectBase pRepositoryItem) {
    if (DEBUG) {
      LOG.debug("attach related items of types - " +
                pRepositoryItem.getRelationTypes());
    }
    // Iterate for each different type of relation.
    for (final String relationType : pRepositoryItem.getRelationTypes()) {
      // Iterate for each attached item.
      if (DEBUG) {
        LOG.debug("Relation type - " + relationType +
                  ", right side item list - " +
                  pRepositoryItem.getRelatedItemsByRelationType(relationType));
      }
      for (final Integer rightItemId
           : pRepositoryItem.getRelatedItemsByRelationType(relationType)) {
        // Create a new item mapping object
        final ItemMapping mapping = new ItemMapping.Builder().
            leftId(pRepositoryItem.getId()).rightId(rightItemId).
            relationType(relationType).build();

        if (DEBUG) {
          LOG.debug("Creating new item mapping - " + mapping);
        }
        // Store new mapping on storage.
        final Integer mappingId = mItemMappingDAO.createMapping(mapping);
        if (DEBUG) {
          LOG.debug("Newly created mapping id - " + mappingId);
        }
      }
    }
  }

  private Integer saveItem(final ObjectBase pRepositoryItem) {
    // Build request field digest
    final String fieldsXml = pRepositoryItem.toXml();

    // Find object title
    final String title = pRepositoryItem.getTitle();

    // set default indexRepositoryName if no indexRepositoryName name is defined.
    final String indexRepositoryName = (pRepositoryItem.getIndexRepository() == null)
        ? KEY_DEFAULT : pRepositoryItem.getIndexRepository();

    // Build new item.
    final Timestamp createdOn;
    if (pRepositoryItem.getCreatedOn() == null) {
      createdOn = new Timestamp(System.currentTimeMillis());
    } else {
      createdOn = pRepositoryItem.getCreatedOn();
    }

    final Timestamp lastUpdatedOn;
    if (pRepositoryItem.getLastUpdatedOn() == null) {
      lastUpdatedOn = new Timestamp(System.currentTimeMillis());
    } else {
      lastUpdatedOn = pRepositoryItem.getLastUpdatedOn();
    }

    final Item item = new Item.Builder() {{
      createdOn(createdOn);
      lastUpdatedOn(lastUpdatedOn);
      document(fieldsXml);
      title(title);
      indexRepository(indexRepositoryName);
    }}.build();

    // Store item on storage.
    return mItemDAO.createItem(item);
  }

  /**
   * {@inheritDoc}
   */
  public void delete(final Integer pRepositoryItemId) {
    final Item item = mItemDAO.findItem(pRepositoryItemId);
    // Remove item
    mItemDAO.deleteItem(item);
    deleteIndex(item.getIndexRepository(), pRepositoryItemId);
  }

  private void deleteIndex(final String pIndexRepository,
                           final Integer pRepositoryItemId) {
    if (DEBUG) {
      LOG.debug("delete index of item - " + pRepositoryItemId +
                " from repository - " + pIndexRepository);
    }
    final Term idTerm = new Term(ObjectBase.INDEX_FIELD_ID,
                                 String.valueOf(pRepositoryItemId));
    if (pIndexRepository == null
        || pIndexRepository.equalsIgnoreCase(KEY_DEFAULT)) {
      synchronized (mDefaultRepositoryItemIndex) {
        mDefaultRepositoryItemIndex.deleteDocument(idTerm);
      }
    } else {
      final RepositoryItemIndex indexer =
          mRepositoryItemIndexMap.get(pIndexRepository.toLowerCase());
      synchronized (indexer) {
        indexer.deleteDocument(idTerm);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  public <T extends ObjectBase> T getItem(final Integer pItemId,
                                          final Class<T> pTargetClass) {
    try {
      // Find item from storage.
      final Item item = mItemDAO.findItem(pItemId);
      return buildTargetObject(item, pTargetClass);
    } catch (Exception e) {
      throw ServiceException.aNew(pItemId, "Failed to retrieve.", e);
    }
  }

  private <T extends ObjectBase> T buildTargetObject(
      final Item pItem, final Class<T> pTargetClass)
      throws IllegalAccessException,
             InstantiationException,
             NoSuchMethodException,
             InvocationTargetException {
    // Return null if item is not exists on storage.
    if (pItem == null) {
      return null;
    }

    // Convert to target object.
    final T targetObject = pTargetClass.newInstance();
    if (DEBUG) {
      LOG.debug("Target object instance - " + targetObject);
    }

    // populate Target object from retrived item.
    ObjectPopulateHelper.populateObjectInstance(pItem, targetObject);

    final Integer itemId = pItem.getId();

    // Set id and title
    ObjectBase baseObject = (ObjectBase) targetObject;
    baseObject.setId(itemId);
    baseObject.setTitle(pItem.getTitle());

    // set created and last updated time stamp.
    baseObject.setCreatedOn(pItem.getCreatedOn());
    baseObject.setLastUpdatedOn(pItem.getLastUpdatedOn());

    // Related items are attached from aspect
    if (DEBUG) {
      LOG.debug("Populated object instance - " + targetObject);
    }
    return targetObject;
  }

  /**
   * {@inheritDoc}
   */
  public List<Integer> getRelatedItems(final Integer pParentItemId,
                                      final String pRelationType,
                                      final int pSkip, final int pMaxRow) {
    if (DEBUG) {
      LOG.debug("Retrieve items by type - " + pRelationType +
                " of parent item id - " + pParentItemId +
                " pagination setup(" + pSkip + ", " + pMaxRow + ")");
    }
    final List<ItemMapping> mappings = mItemMappingDAO.
        findRightSideItemsByType(pParentItemId, pRelationType, pSkip, pMaxRow);

    if (mappings != null && !mappings.isEmpty()) {
      final List<Integer> items = new ArrayList<Integer>();
      for (final ItemMapping mapping : mappings) {
        items.add(mapping.getRightId());
      }
      return items;
    } else {
      return Collections.emptyList();
    }
  }

  /**
   * {@inheritDoc}
   */
  public int getRelatedItemsCount(final Integer pLeftItemId,
                                  final String pRelationType) {
    return mItemMappingDAO.countRightSideItems(pLeftItemId, pRelationType);
  }

  /**
   * {@inheritDoc}
   */
  public Map<String, List<Integer>> getItems(
      final Integer pParentItemId, final int pSkip, final int pMax) {

    final List<ItemMapping> mappings =
        mItemMappingDAO.findRightSideItems(pParentItemId, pSkip, pMax);
    if (mappings != null && !mappings.isEmpty()) {
      final Map<String, List<Integer>> itemMap =
          new HashMap<String, List<Integer>>();
      for (final ItemMapping mapping : mappings) {
        List<Integer> itemList = itemMap.get(mapping.getRelationType());
        if (itemList != null) {
          itemList.add(mapping.getRightId());
        } else {
          itemList = new ArrayList<Integer>();
          itemList.add(mapping.getRightId());
          itemMap.put(mapping.getRelationType(), itemList);
        }
      }
      return itemMap;
    } else {
      return Collections.emptyMap();
    }
  }

  /**
   * {@inheritDoc}
   */
  public <T extends AbstractObjectBase> List<T> getItemObjects(
      final Integer pParentItemId, final String pRelationType,
      final int pSkip, final int pMaxRow, final Class<T> pType) {

    final List<ItemMapping> mappings = mItemMappingDAO.
        findRightSideItemsByType(pParentItemId, pRelationType, pSkip, pMaxRow);

    if (mappings != null && !mappings.isEmpty()) {
      final List<T> items = new ArrayList<T>();
      for (final ItemMapping mapping : mappings) {
        items.add(getItem(mapping.getRightId(), pType));
      }
      return items;
    } else {
      return Collections.emptyList();
    }
  }

  /**
   * {@inheritDoc}
   */
  public PaginatedList<Hit> getItemsByQuery(final Query pQuery) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Retrieve list of items by query - " + pQuery
                + " from index " + pQuery.getIndex());
    }
    if (pQuery.getIndex() == null
        || pQuery.getIndex().equalsIgnoreCase(INDEX_DEFAULT)) {
      return mDefaultRepositoryItemSearch.search(pQuery);
    } else {
      final RepositoryItemSearch searcher =
          mRepositoryItemSearchMap.get(pQuery.getIndex());
      if (searcher == null) {
        throw new RuntimeException("You have entered an invalid index " +
            "service name - '" + pQuery.getIndex() + "'");
      }
      return searcher.search(pQuery);
    }
  }

  public void subscribeEventListener(final EventListener pEventListener) {
    mEventManager.subscribe(pEventListener);
  }

  public void unsubscribeEventListener(final EventListener pEventListener) {
    mEventManager.unsubscribe(pEventListener);
  }

  /**
   * TODO: currently this service invoke optimize for every index repository. change it to on request basis.
   * <br>
   * {@inheritDoc}
   */
  public void optimizeIndex() {
    for (final Map.Entry<String, RepositoryItemIndex> entry
        : mRepositoryItemIndexMap.entrySet()) {
      LOG.info("Invoking optimizer for index repository - " + entry.getKey());
      entry.getValue().optimize();
    }
  }

  public int getAllItemsCount() {
    return mItemDAO.countItems(mEmptyItem);
  }

  public int getItemsCountByIndexRepository(final String pIndexRepository) {
    return mItemDAO.countItems(new Item.Builder().
        indexRepository(pIndexRepository).build());
  }

  /**
   * {@inheritDoc}
   */
  public Integer addRelatedItem(final String pRelationType,
                                final Integer pLeftItemId,
                                final Integer pRightItemId) {
    final ItemMapping itemMapping = new ItemMapping() {{
      setLeftId(pLeftItemId);
      setRightId(pRightItemId);
      setRelationType(pRelationType);
    }};
    return mItemMappingDAO.createMapping(itemMapping);
  }

  /**
   * {@inheritDoc}
   */
  public List<Integer> addRelatedItems(final String pRelationType,
                                       final Integer pLeftItemId,
                                       final List<Integer> pRightItemIds) {
    final List<Integer> mappingList = new ArrayList<Integer>();
    for (final Integer rightItemId : pRightItemIds) {
      mappingList.add(addRelatedItem(pRelationType, pLeftItemId, rightItemId));
    }
    return mappingList;
  }

  /**
   * {@inheritDoc}
   */
  public void deleteRelatedItems(final String pRelationType,
                                 final Integer pLeftItemId) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Delete related items - " + pRelationType + ", " + pLeftItemId);
    }
    final ItemMapping query = new ItemMapping.Builder() {{
      leftId(pLeftItemId);
      relationType(pRelationType);
    }}.build();
    mItemMappingDAO.deleteMapping(query);
  }

  /**
   * {@inheritDoc}
   */
  public void deleteRelatedItem(final String pRelationType,
                                final Integer pLeftItemId,
                                final Integer pRightItemId) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Delete related item - " + pRelationType + ", " + pLeftItemId +
                ", " + pRightItemId);
    }
    final ItemMapping query = new ItemMapping.Builder() {{
      leftId(pLeftItemId);
      rightId(pRightItemId);
      relationType(pRelationType);
    }}.build();
    mItemMappingDAO.deleteMapping(query);
  }

  /**
   * {@inheritDoc}
   */
  public List<Integer> getAllItems(final Integer pSkip, final Integer pMax) {
    final List<Item> items = mItemDAO.findItems(EMPTY_WHERE, pSkip, pMax);
    final List<Integer> itemIds = new ArrayList<Integer>();
    for (final Item item : items) {
      itemIds.add(item.getId());
    }
    return itemIds;
  }

  /**
   * {@inheritDoc}
   */
  public <T extends ObjectBase> T getItemByTitle(final String pTitle,
                                                 final Class<T> pType) {
    if (DEBUG) {
      LOG.debug("Retrieve item by title - " + pTitle);
    }
    final Item where = new Item() {{
      setTitle(pTitle);
    }};
    final List<Item> items = mItemDAO.findItems(where, 0, 1);
    if (items != null && !items.isEmpty()) {
      final Item item = items.get(0);
      try {
        return buildTargetObject(item, pType);
      } catch (Exception e) {
        throw ServiceException.aNew(
            item.getId(), "Failed to convert on target object.", e);
      }
    }
    return null;
  }

  /**
   * TODO: currently it destroyes index files from all repository, limit it to the specified repository.
   * {@inheritDoc}
   */
  public void destroyIndex(final boolean pConfirmation) throws IOException {
    for (Map.Entry<String, RepositoryItemIndex> entry
        : mRepositoryItemIndexMap.entrySet()) {
      synchronized (entry.getValue()) {
        LOG.info("Destroying index for repository - " + entry.getKey());
        entry.getValue().deleteIndexFiles(pConfirmation);
      }
    }
  }
}
