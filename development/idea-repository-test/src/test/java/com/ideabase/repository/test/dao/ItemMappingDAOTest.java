/*
 * $Id: ItemMappingDAOTest.java 250 2008-01-07 10:18:29Z hasan $
 ******************************************************************************
 * Copyright (C) 2007 somewhere in .Net ltd.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from somewhere in .Net LTD.  This notice must be included on
 * all copies, modifications and derivatives of this work.
 ******************************************************************************
 * $LastChangedBy: hasan $
 * $LastChangedDate: 2008-01-07 16:18:29 +0600 (Mon, 07 Jan 2008) $
 * $LastChangedRevision: 250 $
 ******************************************************************************
*/
package com.ideabase.repository.test.dao;

import com.ideabase.repository.test.BaseTestCase;
import com.ideabase.repository.core.dao.ItemMappingDAO;
import com.ideabase.repository.core.dao.ItemMapping;
import com.ideabase.repository.core.CoreServiceKey;

import java.util.List;

/**
 * Test {@code ItemMapping} domain object.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class ItemMappingDAOTest extends BaseTestCase {

  private ItemMappingDAO mItemMappingDAO;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mItemMappingDAO =
        (ItemMappingDAO) mContext.getBean(CoreServiceKey.ITEM_MAPPING_DAO);
    assertNotNull("Object mapping DAO is not configured.", mItemMappingDAO);
  }

  public void testCreate() {
    ItemMapping mapping =
        new ItemMapping.Builder().relationType("cat").
            leftId(107).rightId(108).build();
    Integer id = mItemMappingDAO.createMapping(mapping);
    assertNotNull("Mapping has not been created.", id);
  }

  public void testFindLeft() {
    List<ItemMapping> leftMappings =
        mItemMappingDAO.findLeftSideItemsByType(7, "cat", 0, 100);
    assertNotNull("No mapping could retrieve", leftMappings);
    assertTrue("Retrieved list doesn't match the number.",
              leftMappings.size() == 1);
  }

  public void testFindRight() {
    List<ItemMapping> rightMappings =
        mItemMappingDAO.findRightSideItemsByType(4, "cat", 0, 100);
    assertTrue("Retrieved list doesn't match the number of size",
              rightMappings.size() == 1);
  }

  public void testUpdate() {
    ItemMapping mapping = mItemMappingDAO.findItemById(1);
    LOG.debug(mapping);
    assertNotNull("number 1 map doesn't exist on database.", mapping);
    
  }

  public void testCountLeft() {
    Integer rightCount =
        mItemMappingDAO.countRightSideItems(4, "cat");
    assertTrue("Retrieved list doesn't match the number of size",
              rightCount == 1);
  }

  public void testDelete() {
    mItemMappingDAO.deleteMappingById(3);
    assertNull("Object not deleted", mItemMappingDAO.findItemById(3));
  }
}
