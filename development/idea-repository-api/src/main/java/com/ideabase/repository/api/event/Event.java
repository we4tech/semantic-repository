/* $Id: Event.java 263 2008-04-08 11:42:34Z hasan $ */
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
 * $LastChangedDate: 2008-04-08 17:42:34 +0600 (Tue, 08 Apr 2008) $
 * $LastChangedRevision: 263 $
 ******************************************************************************
*/
package com.ideabase.repository.api.event;

import com.ideabase.repository.common.ObjectToString;

/**
 * Event source and type is defined here.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class Event {

  /**
   * Event type declares the functionalities of the event.
   */
  public static enum EventType {
    /**
     * When new item is created,
     */
    ITEM_CREATED,
    /**
     * When new item has sent for indexing.
     */
    ITEM_INDEX_ADDED,

    /**
     * When an existing item has been updated.
     */
    ITEM_UPDATED,
    /**
     * When an exiting item index has been updated.
     */
    ITEM_INDEX_UPDATED,

    /**
     * When an existing item has been deleted.
     */
    ITEM_DELETED,
    /**
     * When an exiting item idex has been removed.
     */
    ITEM_INDEX_DELETED,

    /**
     * Server exception
     */
    SERVER_EXCEPTION,
  }

  private final Object mItem;
  private final EventType mEventType;
  private final Class mSource;

  public Event(final Object pItem,
               final EventType pEventType,
               final Class pSource) {
    mItem = pItem;
    mEventType = pEventType;
    mSource = pSource;
  }

  public Object getItem() {
    return mItem;
  }

  public EventType getEventType() {
    return mEventType;
  }

  public Class getSource() {
    return mSource;
  }


  @Override
  public String toString() {
    return ObjectToString.toString(this);
  }
}
