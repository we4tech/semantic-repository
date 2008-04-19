/* $Id: TestCaseRepositoryHelper.java 260 2008-03-19 05:38:06Z hasan $ */
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
 * $LastChangedDate: 2008-03-19 11:38:06 +0600 (Wed, 19 Mar 2008) $
 * $LastChangedRevision: 260 $
 ******************************************************************************
*/
package com.ideabase.repository.test;

import com.ideabase.repository.common.object.GenericItem;
import com.ideabase.repository.common.object.User;
import com.ideabase.repository.api.RepositoryService;
import com.ideabase.repository.api.UserService;

import java.util.List;
import java.util.ArrayList;
import java.sql.Timestamp;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * Assist test case classes
 *
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class TestCaseRepositoryHelper {

  public static final String FIELD_NAME = "name";
  public static final String FIELD_EMAIL = "email";
  public static final String FIELD_CURRENT_TIME = "currentTime";

  private static final Logger LOG =
      LogManager.getLogger(TestCaseRepositoryHelper.class);

  public static List<Integer> fixCreateItems(
      final RepositoryService pRepositoryService, final int pNumberOfItems) {
    final List<Integer> itemIds = new ArrayList<Integer>();
    final String dummyName = "the brown fox jump over the...";
    final String dummyEmail = "hasan@somewherein.net";
    for (int i = 0; i < pNumberOfItems; i++) {
      final GenericItem item = new GenericItem();
      item.addField("name", dummyName);
      item.addField("email", dummyEmail);
      item.addField("currentTime", String.valueOf(System.currentTimeMillis()));
      item.setTitle(dummyName);
      item.setCreatedOn(new Timestamp(System.currentTimeMillis()));
      item.setLastUpdatedOn(item.getCreatedOn());
      itemIds.add(pRepositoryService.save(item));
    }
    return itemIds;
  }

  public static List<Integer> fixCreateItemsWithRandomName(
      final RepositoryService pRepositoryService, final int pNumberOfItems) {
    final List<Integer> itemIds = new ArrayList<Integer>();
    final String dummyName = String.valueOf((System.currentTimeMillis() + Math.random())).replace(".", "");
    final String dummyEmail = String.valueOf((System.currentTimeMillis() + Math.random()) + "@somewherein.net").replace(".", "");
    for (int i = 0; i < pNumberOfItems; i++) {
      final GenericItem item = new GenericItem();
      item.addField("vendor", "test" + i);
      item.addField("name", i + dummyName);
      item.addField("email", i + dummyEmail);
      item.addField("currentTime", String.valueOf(System.currentTimeMillis()));
      item.setTitle(i + dummyName);
      item.setCreatedOn(new Timestamp(System.currentTimeMillis()));
      item.setLastUpdatedOn(item.getCreatedOn());
      itemIds.add(pRepositoryService.save(item));
    }
    return itemIds;
  }

  public static List<Integer> fixCreateItems(
      final RepositoryService pRepositoryService,
      final int pNumberOfItems, final String pRelationType,
      final List<Integer> pParentItemIds) {
    final List<Integer> itemIds = new ArrayList<Integer>();
    final String dummyName = "the brown fox jump over the...";
    final String dummyEmail = "hasan@somewherein.net";
    for (int i = 0; i < pNumberOfItems; i++) {
      final GenericItem item = new GenericItem();
      item.addField("name", dummyName);
      item.addField("email", dummyEmail);
      item.setTitle(dummyName);

      for (final Integer itemId : pParentItemIds) {
        item.addRelatedItem(pRelationType, itemId);
      }

      itemIds.add(pRepositoryService.save(item));
    }
    return itemIds;
  }

  public static void fixRemoveItems(final RepositoryService pRepositoryService,
                                    final List<Integer> pItemIds) {
    for (final Integer itemId : pItemIds) {
      pRepositoryService.delete(itemId);
    }
  }

  public static void fixRemoveAllItems(
      final RepositoryService pRepositoryService) {
    final List<Integer> items =
        pRepositoryService.getAllItems(0, Integer.MAX_VALUE);
    for (final Integer itemId : items) {
      LOG.info("Delete item - " + itemId);
      try {
        pRepositoryService.delete(itemId);
      } catch (Throwable t) {
        LOG.warn("Exception raised during remove item", t);
      }
    }
  }

  public static Integer fixCreateUser(
      final UserService pUserService, final String pUser,
      final String pPassword) {
    final User user = new User();
    user.setCreatedOn(new Timestamp(System.currentTimeMillis()));
    user.setLastUpdatedOn(new Timestamp(System.currentTimeMillis()));
    user.setUser(pUser);
    user.addField("someTest", "many things to write, but i don't know how i " +
        "gonna draw the line");
    user.addField("someBigText", "many stuffs are going here \n " +
        "something is not good\r very bad\r\n");
    user.setPassword(pPassword);
    user.setRole(User.FIELD_ROLE_ADMIN, true);
    user.setRole(User.FIELD_ROLE_READ, true);
    user.setRole(User.FIELD_ROLE_WRITE, true);
    return pUserService.registerUser(user);
  }

  public static Integer fixCreateUserHashed(
      final RepositoryService pRepsoirotyService, final String pUser,
      final String pPassword) {
    final User user = new User();
    user.setUser(pUser);
    user.setPassword(DigestUtils.shaHex(pPassword));
    user.setRole(User.FIELD_ROLE_ADMIN, true);
    user.setRole(User.FIELD_ROLE_READ, true);
    user.setRole(User.FIELD_ROLE_WRITE, true);
    return pRepsoirotyService.save(user);
  }
}
