/* $Id:ItemDAOImpl.java 249 2007-12-02 08:32:47Z hasan $ */
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
package com.ideabase.repository.core.dao.impl;

import com.ideabase.repository.core.dao.ItemDAO;
import com.ideabase.repository.core.dao.Item;
import com.ideabase.repository.common.exception.ServiceException;

import java.util.List;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

/**
 * Implementation of {@code ItemDAO}.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class ItemDAOImpl extends SqlMapClientDaoSupport
    implements ItemDAO {

  private static final String QUERY_INSERT = "insertItem";
  private static final String QUERY_UPDATE = "updateItem";
  private static final String QUERY_DELETE = "deleteItems";
  private static final String QUERY_FIND = "findItems";
  private static final String QUERY_COUNT = "countItems";
  private static final int ROW_AFFECT_NORMAL = 0;

  public Integer createItem(final Item pItem) {
    return (Integer) getSqlMapClientTemplate().insert(QUERY_INSERT, pItem);
  }

  public void updateItem(final Item pItem) {
    int rowAffected = getSqlMapClientTemplate().update(QUERY_UPDATE, pItem);
    if (rowAffected < ROW_AFFECT_NORMAL) {
      throw new ServiceException(pItem,
          "Failed to updateItem an item on repository.");
    }
  }

  public void deleteItem(final Item pItem) {
    int rowAffected = getSqlMapClientTemplate().delete(QUERY_DELETE, pItem);
    if (rowAffected < ROW_AFFECT_NORMAL) {
      throw new ServiceException(pItem,
          "Failed to deleteItem object from repository.");
    }
  }

  public void deleteItemById(final Integer pId) {
    Item item = new Item.Builder().id(pId).build();
    int rowAffected = getSqlMapClientTemplate().delete(QUERY_DELETE, item);
    if (rowAffected < ROW_AFFECT_NORMAL) {
      throw new ServiceException(pId,
          "Failed to deleteItem object from repository.");
    }
  }

  public List<Item> findItems(final Item pItem, final Integer pSkip,
                                 final Integer pMax) {
    return getSqlMapClientTemplate().
           queryForList(QUERY_FIND, pItem, pSkip, pMax);
  }

  public Item findItem(final int pItemId) {
    List<Item> items = findItems(new Item.Builder().id(pItemId).build(), 0, 1);
    if (items != null && !items.isEmpty()) {
      return items.get(0);
    }
    return null;
  }

  public int countItems(final Item pItem) {
    return (Integer) getSqlMapClientTemplate().queryForObject(QUERY_COUNT);
  }

  public void startTransaction() {
    try {
      getSqlMapClientTemplate().getSqlMapClient().startTransaction();
    } catch (Exception e) {
      throw new ServiceException(null, "Failed to start transaction.", e);
    }
  }

  public void endTransaction() {
    try {
      getSqlMapClientTemplate().getSqlMapClient().endTransaction();
    } catch (Exception e) {
      throw new ServiceException(null, "Failed to start transaction.", e);
    }
  }

  public void commitTransaction() {
    try {
      getSqlMapClientTemplate().getSqlMapClient().commitTransaction();
    } catch (Exception e) {
      throw new ServiceException(null, "Failed to start transaction.", e);
    }
  }
}
