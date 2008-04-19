/* $Id: EventManagerTest.java 250 2008-01-07 10:18:29Z hasan $ */
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
package com.ideabase.repository.test.api;

import com.ideabase.repository.test.BaseTestCase;
import com.ideabase.repository.test.TestCaseRepositoryHelper;
import com.ideabase.repository.api.event.EventListener;
import com.ideabase.repository.api.event.Event;
import com.ideabase.repository.api.RepositoryService;
import com.ideabase.repository.api.APIServiceKey;
import com.ideabase.repository.api.EventManager;
import com.ideabase.repository.common.object.User;
import com.ideabase.repository.common.object.GenericItem;
import com.ideabase.repository.core.CoreServiceKey;

import java.util.List;
import java.util.ArrayList;

/**
 * Test {@see EventManagerImpl} functionalities.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class EventManagerTest extends BaseTestCase {

  private RepositoryService mRepositoryService;
  private EventManager mEventManager;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mRepositoryService =
        (RepositoryService) mContext.getBean(APIServiceKey.REPOSITORY_SERVICE);
    mEventManager =
        (EventManager) mContext.getBean(CoreServiceKey.EVENT_MANAGER);
    TestCaseRepositoryHelper.fixRemoveAllItems(mRepositoryService);
  }

  public void testSubscribe() throws InterruptedException {
    // subscribe for object creational event
    final List<Boolean> stateHolder = new ArrayList<Boolean>();
    final EventListener createEventListener = new EventListener() {
      public void onEvent(final Event pEvent) {
        assertTrue("Event type is not creational event.",
                   pEvent.getEventType() == Event.EventType.ITEM_CREATED);
        stateHolder.add(Boolean.TRUE);
      }
    };
    mEventManager.subscribe(createEventListener);

    // ------------------- Create object
    final Integer itemId =
        TestCaseRepositoryHelper.fixCreateItems(mRepositoryService, 1).get(0);
    assertNotNull("New user couldn't create successfully.", itemId);

    // verify event status
    assertTrue("No event received", !stateHolder.isEmpty()
               && stateHolder.get(0) == Boolean.TRUE);
    // clear state
    stateHolder.clear();
    mEventManager.unsubscribe(createEventListener);

    // -------------------- Update object
    final EventListener updateEventListener = new EventListener() {
      public void onEvent(final Event pEvent) {
        assertTrue("Event type is not update event",
                   pEvent.getEventType() == Event.EventType.ITEM_UPDATED);
        stateHolder.add(Boolean.TRUE);
      }
    };
    mEventManager.subscribe(updateEventListener);
    // update object
    final GenericItem item =
        mRepositoryService.getItem(itemId, GenericItem.class);
    item.setTitle("Brand new title");
    mRepositoryService.save(item);
    assertNotNull("Updated object id is not returned.", item);

    // verify event status
    assertTrue("No event received", !stateHolder.isEmpty()
               && stateHolder.get(0) == Boolean.TRUE);
    // clear state
    stateHolder.clear();
    mEventManager.unsubscribe(updateEventListener);

    // -------------------- Delete this user
    final EventListener deleteEventListener = new EventListener() {
      public void onEvent(final Event pEvent) {
        assertTrue("NO event type delete",
                   pEvent.getEventType() == Event.EventType.ITEM_DELETED);
        stateHolder.add(Boolean.TRUE);
      }
    };
    mEventManager.subscribe(deleteEventListener);

    // delete item
    mRepositoryService.delete(itemId);
    assertNull("Object wasn't removed proplery.", 
               mRepositoryService.getItem(itemId, User.class));
    // verify event status
    assertTrue("No event state found", !stateHolder.isEmpty() &&
               stateHolder.get(0) == Boolean.TRUE);
  }

  @Override
  protected void tearDown() throws Exception {
    TestCaseRepositoryHelper.fixRemoveAllItems(mRepositoryService);
  }
}
