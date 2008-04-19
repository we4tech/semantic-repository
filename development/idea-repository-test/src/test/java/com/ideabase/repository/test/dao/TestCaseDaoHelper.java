/* $Id: TestCaseDaoHelper.java 250 2008-01-07 10:18:29Z hasan $ */
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
package com.ideabase.repository.test.dao;

import java.util.List;
import java.util.ArrayList;
import java.sql.Timestamp;

import com.ideabase.repository.core.dao.ItemDAO;
import com.ideabase.repository.core.dao.Item;

/**
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class TestCaseDaoHelper {

  public static List<Integer> fixCreateItems(final ItemDAO pItemDAO,
                                             final Integer pMaxNumberOfItems) {
    final List<Integer> itemIds = new ArrayList<Integer>(pMaxNumberOfItems);
    for (int i = 0; i < pMaxNumberOfItems; i++) {
      final String title = "title_" + Math.random() + "_" + i;
      final Item item = new Item();
      item.setTitle(title);
      item.setCreatedOn(new Timestamp(System.currentTimeMillis()));
      item.setLastUpdatedOn(item.getCreatedOn());
      item.setDocument("<fields/>");

      itemIds.add(pItemDAO.createItem(item));
    }
    return itemIds;
  }
  public static List<Integer> fixCreateItems(final ItemDAO pItemDAO,
                                             final String pTitle,
                                             final Integer pMaxNumberOfItems) {
    final List<Integer> itemIds = new ArrayList<Integer>(pMaxNumberOfItems);
    for (int i = 0; i < pMaxNumberOfItems; i++) {
      final Item item = new Item();
      item.setTitle(pTitle);
      item.setCreatedOn(new Timestamp(System.currentTimeMillis()));
      item.setLastUpdatedOn(item.getCreatedOn());
      item.setDocument("<fields/>");

      itemIds.add(pItemDAO.createItem(item));
    }
    return itemIds;
  }

  public static void deleteAllItems(final ItemDAO pItemDAO) {
    final List<Item> items =
        pItemDAO.findItems(new Item(), 0, Integer.MAX_VALUE);
    for (final Item item : items) {
      pItemDAO.deleteItemById(item.getId());
    }
  }
}
