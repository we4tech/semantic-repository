/* $Id: ItemDAOTest.java 250 2008-01-07 10:18:29Z hasan $ */
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

import com.ideabase.repository.test.BaseTestCase;
import com.ideabase.repository.core.dao.ItemDAO;
import com.ideabase.repository.core.dao.Item;
import com.ideabase.repository.core.CoreServiceKey;

import java.sql.Timestamp;
import java.util.List;

import org.jmock.Mock;
import org.jmock.core.Stub;
import org.jmock.core.Invocation;

/**
 * Test Repository Data access object.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class ItemDAOTest extends BaseTestCase {

  private static final String DOCUMENT_TITLE = "Test title";
  private static final String DOCUMENT_CONTENT = "Document content";
  private static final String DOCUMENT_NEW_TITLE = "Bang title";
  private ItemDAO mItemDAO;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mItemDAO =
        (ItemDAO) mContext.getBean(CoreServiceKey.ITEM_DAO);
    assertNotNull("ItemDAO is not configured properly, " +
                  "therefore returning null instance.", mItemDAO);
    TestCaseDaoHelper.deleteAllItems(mItemDAO);
  }

  /**
   * Tested.
   */
  public void testCreateItem() {
    for (int i = 0; i < 10; i++) {
      Item item = new Item.Builder() {{
        title(DOCUMENT_TITLE);
        document(DOCUMENT_CONTENT);
        createdOn(new Timestamp(System.currentTimeMillis()));
        lastUpdatedOn(new Timestamp(System.currentTimeMillis()));
      }}.build();
      Integer id = mItemDAO.createItem(item);

      assertNotNull("Repository is returned back to null", item);
      assertNotNull("Repository item is not created.", id);
    }
  }

  /**
   * Tested.
   */
  public void testFindItem() {
    // create 10 items
    final String title = "my-title";
    TestCaseDaoHelper.fixCreateItems(mItemDAO, title, 10);
    final Item query = new Item.Builder().title(title).build();
    final List<Item> items = mItemDAO.findItems(query, 0, 10);

    assertNotNull("Object is not empty query - " + query, items);
    assertTrue("10 Items retrieved", items.size() == 10);
    LOG.debug("items - " + items);
  }

  public void testUpdate() {
    // create items
    final List<Integer> items = TestCaseDaoHelper.fixCreateItems(mItemDAO, 1);
    final Item item = mItemDAO.findItem(items.get(0));
    LOG.debug("Old item title - " + item.getTitle());
    item.setTitle(DOCUMENT_NEW_TITLE);

    // update item object
    mItemDAO.updateItem(item);
    assertNotNull("Object has been changed.", item);
    assertEquals("Newly assigned Object tite doesn't match.",
                 item.getTitle(), DOCUMENT_NEW_TITLE);
  }

  public void testDelete() {
    // create items
    final List<Integer> items = TestCaseDaoHelper.fixCreateItems(mItemDAO, 1);
    mItemDAO.deleteItemById(items.get(0));

    assertNull("Object exists, but it not suppose to",
               mItemDAO.findItem(items.get(0)));
  }

  public void testItemCount() {
    final String itemTitle = "new titlte";
    final int itemCount = 10;
    TestCaseDaoHelper.fixCreateItems(mItemDAO, itemTitle, itemCount);

    // create items
    int numItems = mItemDAO.
        countItems(new Item.Builder().title(itemTitle).build());

    // verify
    assertTrue("Total number of item doesn't match with our approximate number",
               numItems == itemCount);
  }

  @Override
  protected void tearDown() throws Exception {
    TestCaseDaoHelper.deleteAllItems(mItemDAO);
  }
}
