/* $Id: RepositoryServiceTest.java 253 2008-03-10 18:04:01Z hasan $ */
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
 * $LastChangedDate: 2008-03-10 23:34:01 +0530 (Mon, 10 Mar 2008) $
 * $LastChangedRevision: 253 $
 ******************************************************************************
*/
package com.ideabase.repository.test.api;

import java.util.List;
import java.util.Calendar;

import com.ideabase.repository.api.APIServiceKey;
import com.ideabase.repository.api.RepositoryService;
import com.ideabase.repository.api.UserService;
import com.ideabase.repository.common.Query;
import com.ideabase.repository.common.object.*;
import com.ideabase.repository.test.BaseTestCase;
import com.ideabase.repository.test.TestCaseRepositoryHelper;

/**
 * Test {@code RepositoryService}.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class RepositoryServiceTest extends BaseTestCase {

  private RepositoryService mRepositoryService;
  private UserService mUserService;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mRepositoryService =
        (RepositoryService) mContext.getBean(APIServiceKey.REPOSITORY_SERVICE);
    mUserService = (UserService) mContext.getBean(APIServiceKey.USER_SERVICE);
    TestCaseRepositoryHelper.fixRemoveAllItems(mRepositoryService);
  }

  /**
   * Store a new {@code RepostioryItem}
   */
  public void testSave() {
    LOG.debug("Starting store related test.");
    final long _initStartTime = System.currentTimeMillis();
    for (int k = 0; k < 10; k++) {
      final long __baseStartTime = System.currentTimeMillis();
      for (int i = 0; i < 10; i++) {
        try {
          LOG.debug("Creating new object # " + i);
          final long __startTime = System.currentTimeMillis();
          final GenericItem item = new GenericItem();
          item.setTitle("title - " + i);
          item.addField("category", "annual blup cow");
          item.addField("moderated", "false");
          if (i % 2 == 0) {
            item.setIndexRepository("aawaj");
          }

          // create new user object
          final Integer id = mRepositoryService.save(item);
          final long __endTime = System.currentTimeMillis();
          LOG.debug("Total time required - " + (__endTime - __startTime));
          // assert the value
          assertNotNull("No object created.", id);
          assertNotNull("Object id is not set.", item.getId());
          LOG.debug("Newly created object id - " + id);
        } catch (Throwable t) {
          LOG.warn("Save failed - " + (i * k), t);
        }
      }
      final long __baseEndTime = System.currentTimeMillis();
      LOG.debug("Average time required ("+ __baseStartTime + " - " +
               __baseEndTime + ") - " +
               ((__baseEndTime - __baseStartTime) / 10));
    }
    final long _initEndTime = System.currentTimeMillis();
    LOG.info("Average time - " + ((_initEndTime - _initStartTime) / 10));
  }

  public void testSaveWithRelations() {
    // create a new object
    final List<Integer> items =
        TestCaseRepositoryHelper.fixCreateItems(mRepositoryService, 1);

    // create new user object
    final User user = new User();
    final Integer relationId = items.get(0);
    user.setPassword("hasankhan");
    user.setUser("hasan");
    user.addRelatedItem("relatedItem", relationId);

    final Integer id = mRepositoryService.save(user);
    assertNotNull("No object created.", id);
    assertNotNull("Object id is not set.", user.getId());
    LOG.debug("Newly created object id - " + id);
  }

  public void testFind() {
    // create new object
    final Integer itemId =
        TestCaseRepositoryHelper.fixCreateItems(mRepositoryService, 1).get(0);

    // find out object
    final GenericItem item =
        mRepositoryService.getItem(itemId, GenericItem.class);
    
    // verify object
    LOG.debug("item object - " + item);
    assertNotNull("Object id # " + itemId + ", not found.", item);
    assertNotNull("Title value is null.", item.getTitle());
    assertNotNull("Object id is null.", item.getId());
  }

  public void testDelete() {
    // create new item
    final Integer itemId =
        TestCaseRepositoryHelper.fixCreateItems(mRepositoryService, 1).get(0);
    // delete item from repository
    mRepositoryService.delete(itemId);

    // verify whether item exists or not.
    assertNull("item # 104 not removed from repository.",
               mRepositoryService.getItem(itemId, GenericItem.class));
  }

  public void testUpdate() {

    // create new item.
    final Integer itemId =
        TestCaseRepositoryHelper.fixCreateItems(mRepositoryService, 1).get(0);
    // create related item
    final Integer relatedItemId =
        TestCaseRepositoryHelper.fixCreateItems(mRepositoryService, 1).get(0);
    final String relationType = "profile";

    // store new object
    final GenericItem item =
        mRepositoryService.getItem(itemId, GenericItem.class);

    // verify object existance
    assertNotNull("No such object #" + itemId + " found.", item);

    // modify object
    item.addRelatedItem(relationType, relatedItemId);

    // store modified object
    mRepositoryService.save(item);

    // vierify modified object.
    final List<Integer> relatedItems = mRepositoryService.
        getRelatedItems(itemId, relationType, 0, Integer.MAX_VALUE);

    // Verify
    LOG.debug("Related items - " + relatedItems);
    assertEquals("item title should be 'khan'.",
                 relatedItemId, relatedItems.get(0));
  }

  public void testFindItems() {
    // create new object
    final String relationType = "blog";

    // create related items
    final List<Integer> relatedItemIds =
        TestCaseRepositoryHelper.fixCreateItems(mRepositoryService, 2);

    // create a parent item object
    final Integer parentItemId = TestCaseRepositoryHelper.
    fixCreateItems(mRepositoryService, 1, relationType, relatedItemIds).get(0);

    final List<Integer> relatedItems = mRepositoryService.
        getRelatedItems(parentItemId, relationType, 0, Integer.MAX_VALUE);

    // verify
    assertNotNull("No related item found", relatedItems);
    assertFalse("No related item found.", relatedItems.isEmpty());
  }

  public void testFindItemsByQuery() {
    // create objects
    TestCaseRepositoryHelper.fixCreateItemsWithRandomName(mRepositoryService, 5);

    // build query
    final Query query = new Query()
        .andPrefix("currentTime", String.valueOf(System.currentTimeMillis()).substring(0, 4))
        .sortBy("currentTime", Boolean.TRUE);

    // perform search operation
    final PaginatedList<Hit> results =
        mRepositoryService.getItemsByQuery(query);

    // verify
    assertNotNull("No search result match.", results);
    assertFalse("No search result match.", results.isEmpty());
    LOG.debug("Search result - " + results);
  }

  public void testAddRelatedItem() {
    final Integer itemId =
        TestCaseRepositoryHelper.fixCreateItems(mRepositoryService, 1).get(0);
    final Integer item2Id =
        TestCaseRepositoryHelper.fixCreateItems(mRepositoryService, 1).get(0);

    // add relation with first item.
    final String relationType = "author";
    final Integer mappingId =
        mRepositoryService.addRelatedItem(relationType, item2Id, itemId);
    // verify
    assertNotNull("No item relation has been created.", mappingId);

    // load through getItemsByRelation
    final List<Integer> relatedItems = mRepositoryService.
        getRelatedItems(item2Id, relationType, 0, Integer.MAX_VALUE);
    assertFalse("No such item found",
                relatedItems == null || relatedItems.isEmpty());

    final Integer mapping2Id =
        mRepositoryService.addRelatedItem(relationType, itemId, item2Id);
    // verify
    assertNotNull("No item relation (reverse) has been created", mapping2Id);

    // load through getRelatedItems
    final List<Integer> relatedItems2 = mRepositoryService.
        getRelatedItems(itemId, relationType, 0, Integer.MAX_VALUE);
    assertFalse("No such item found",
                relatedItems2 == null || relatedItems2.isEmpty());
  }
  
}
