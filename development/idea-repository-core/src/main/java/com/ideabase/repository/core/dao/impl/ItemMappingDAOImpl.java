/*
 * $Id:ItemMappingDAOImpl.java 249 2007-12-02 08:32:47Z hasan $
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
package com.ideabase.repository.core.dao.impl;

import com.ideabase.repository.core.dao.ItemMappingDAO;
import com.ideabase.repository.core.dao.ItemMapping;
import com.ideabase.repository.common.exception.ServiceException;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.List;
import java.util.Collections;

/**
 * Implementation of {@code ItemMappingDAO}.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan
 *        (hasan)</a>
 */
public class ItemMappingDAOImpl extends SqlMapClientDaoSupport
    implements ItemMappingDAO {
  private static final String QUERY_INSERT = "insertMapping";
  private static final String QUERY_UPDATE = "updateMapping";
  private static final String QUERY_DELETE = "deleteMappings";

  private static final String QUERY_FIND_LEFT_ID = "findByLeftId";
  private static final String QUERY_FIND_RIGHT_ID = "findByRightId";
  private static final String QUERY_FIND_RELATION_TYPES =
      "findMappingRelationTypes";

  private static final String QUERY_FIND = "findMapping";
  private static final String QUERY_COUNT_LEFT = "countByLeftId";

  private static final String QUERY_COUNT_RIGHT = "countByRightId";
  private static final int AFFECTED_MINIMUM_ROW = 0;

  public Integer createMapping(final ItemMapping pItemMapping) {
    return (Integer) getSqlMapClientTemplate().
                     insert(QUERY_INSERT, pItemMapping);
  }

  public void updateMapping(final ItemMapping pItemMapping) {
    int affectedRow =
        getSqlMapClientTemplate().update(QUERY_UPDATE, pItemMapping);
    if (affectedRow < AFFECTED_MINIMUM_ROW) {
      throw new ServiceException(pItemMapping, "Failed to update " +
                                 "ItemMapping object");
    }
  }

  public void deleteMapping(final ItemMapping pItemMapping) {
    int affectedRow =
        getSqlMapClientTemplate().delete(QUERY_DELETE, pItemMapping);
    if (affectedRow < AFFECTED_MINIMUM_ROW) {
      throw new ServiceException(pItemMapping, "Failed to delete " +
                                 "ItemMapping object");
    }
  }

  public List<ItemMapping> findRightSideItemsByType(final Integer pLeftId,
                                                    final String pRelationType,
                                                    final Integer pSkip,
                                                    final Integer pMax) {
    final ItemMapping where = new ItemMapping.Builder() {{
      leftId(pLeftId);
      relationType(pRelationType);
    }}.build();
    return getSqlMapClientTemplate().
           queryForList(QUERY_FIND_LEFT_ID, where, pSkip, pMax);
  }

  public List<ItemMapping> findRightSideItems(final Integer pLeftId,
                                              final Integer pSkip,
                                              final Integer pMax) {
    final ItemMapping where = new ItemMapping.Builder() {{
      leftId(pLeftId);
    }}.build();
    return getSqlMapClientTemplate().
           queryForList(QUERY_FIND_LEFT_ID, where, pSkip, pMax);
  }

  public List<ItemMapping> findLeftSideItemsByType(final Integer pRightId,
                                                   final String pRelationType,
                                                   final Integer pSkip,
                                                   final Integer pMax) {
    final ItemMapping where = new ItemMapping.Builder() {{
      rightId(pRightId);
      relationType(pRelationType);
    }}.build();
    return getSqlMapClientTemplate().
           queryForList(QUERY_FIND_RIGHT_ID, where, pSkip, pMax);
  }

  public List<ItemMapping> findLeftSideItems(final Integer pRightId,
                                             final Integer pSkip,
                                             final Integer pMax) {
    final ItemMapping where = new ItemMapping.Builder() {{
      rightId(pRightId);
    }}.build();
    return getSqlMapClientTemplate().
           queryForList(QUERY_FIND_RIGHT_ID, where, pSkip, pMax);
  }

  public int countRightSideItems(final Integer pLeftId,
                                 final String pRelationType) {
    final ItemMapping where = new ItemMapping.Builder() {{
      leftId(pLeftId);
      relationType(pRelationType);
    }}.build();
    return (Integer) getSqlMapClientTemplate().
           queryForObject(QUERY_COUNT_LEFT, where);
  }

  public int countLeftSideItems(final Integer pRightId,
                                final String pRelationType) {
    final ItemMapping where = new ItemMapping.Builder() {{
      rightId(pRightId);
      relationType(pRelationType);
    }}.build();
    return (Integer) getSqlMapClientTemplate().
           queryForObject(QUERY_COUNT_RIGHT, where);
  }

  public ItemMapping findItemById(final int pMappingId) {
    final ItemMapping where = new ItemMapping.Builder().id(pMappingId).build();
    return (ItemMapping) getSqlMapClientTemplate().
        queryForObject(QUERY_FIND, where);
  }

  public void deleteMappingById(final int pMappingId) {
    final ItemMapping where = new ItemMapping.Builder().id(pMappingId).build();
    deleteMapping(where);
  }

  public List<String> findAllRelationTypesFromItemId(final Integer pLeftItemId)
  {
    final ItemMapping where =
        new ItemMapping.Builder().leftId(pLeftItemId).build();
    List<String> relationTypes = getSqlMapClientTemplate().
        queryForList(QUERY_FIND_RELATION_TYPES, where);
    if (relationTypes == null) {
      return Collections.emptyList();
    } else {
      return relationTypes;
    }
  }
}
