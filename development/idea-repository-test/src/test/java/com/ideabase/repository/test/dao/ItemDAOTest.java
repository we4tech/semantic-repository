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

import java.sql.*;
import java.util.List;
import java.io.ByteArrayInputStream;

import org.jmock.Mock;
import org.jmock.core.Stub;
import org.jmock.core.Invocation;

import javax.sql.DataSource;

/**
 * Test Repository Data access object.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class ItemDAOTest extends BaseTestCase {

  private static final String DOCUMENT_TITLE = "Test title";
  private static final String DOCUMENT_CONTENT = "Document content";
  private static final String DOCUMENT_NEW_TITLE = "Bang title";
  private ItemDAO mItemDAO;
  private boolean mDontCleanup = false;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mItemDAO =
        (ItemDAO) mContext.getBean(CoreServiceKey.ITEM_DAO);
    assertNotNull("ItemDAO is not configured properly, " +
                  "therefore returning null instance.", mItemDAO);
//    TestCaseDaoHelper.deleteAllItems(mItemDAO);
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

  /**
   * Test unicoded text
   */
  public void testUnicodedText() {
    final String unicodedText = "সাহায্য করুন কেউ";
    final Item newItem = new Item.Builder()
      .title(unicodedText)
      .document("<fields><field name='abc'>" + unicodedText + "</field></fields>")
      .build();
    final Integer itemId = mItemDAO.createItem(newItem);
    assertNotNull(itemId);

    // retrieve the newly created item
    final Item item = mItemDAO.findItem(itemId);
    assertNotNull(item);
    assertEquals(item.getTitle(), unicodedText);
    System.out.println("Item - " + item);

    // find newly created item by the unicode text
    final List<Item> items =
      mItemDAO.findItems(new Item.Builder().title(unicodedText).build(), 0, 1);
    mDontCleanup = true;
  }

  public void _testJdbcConnection() throws SQLException, ClassNotFoundException {
    mDontCleanup = true;
    final String unicodedText = "সাহায্য করুন কেউ";
    final DataSource dataSource = (DataSource) mContext.getBean("dataSource");
    final PreparedStatement statement = dataSource.getConnection().prepareStatement(
      new StringBuilder().append("INSERT INTO ").append("item")
      .append(" (title, document, createdOn, lastUpdatedOn, indexRepository)")
      .append(" VALUES(?, ?, ?, ?, ?)").toString());
//    statement.setBinaryStream(1, new ByteArrayInputStream(unicodedText.getBytes()), unicodedText.getBytes().length);
    statement.setString(1, unicodedText);
    statement.setString(2, "<fields><field name='word'><![CDATA[খান]]></field></fields>");
    statement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
    statement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
    statement.setString(5, "default");
    assertEquals(1, statement.executeUpdate());
  }

  public void _testUnicodeText2() throws ClassNotFoundException, SQLException {
    final String unicodedText = "সাহায্য করুন কেউ";
    Class.forName("com.mysql.jdbc.Driver");
    final Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ads_test_somewhereinads_net?jdbcCompliantTruncation=false", "root", "");
    final PreparedStatement statement = connection.prepareStatement("" +
      "INSERT INTO items" +
      "  (name, description, price, user_id, category_id, created_at, category_tree_ref, url, status, location_id)" +
      "VALUES " +
      "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
    statement.setString(1, unicodedText);
    statement.setBinaryStream(2, new ByteArrayInputStream(unicodedText.getBytes()), unicodedText.getBytes().length);
    statement.setDouble(3, 100d);
    statement.setInt(4, 52);
    statement.setInt(5, 126);
    statement.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
    statement.setString(7, "126");
    statement.setString(8, "bang_bang_kang");
    statement.setInt(9, 4);
    statement.setInt(10, 68);
    assertEquals(1, statement.executeUpdate());
    mDontCleanup = true;
  }

  @Override
  protected void tearDown() throws Exception {
    if (!mDontCleanup) {
      TestCaseDaoHelper.deleteAllItems(mItemDAO);
    }
  }
}
