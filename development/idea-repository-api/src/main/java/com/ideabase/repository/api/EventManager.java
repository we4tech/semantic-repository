/* $Id: EventManager.java 250 2008-01-07 10:18:29Z hasan $ */
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
package com.ideabase.repository.api;

import com.ideabase.repository.api.event.Event;
import com.ideabase.repository.api.event.EventListener;

import java.util.Iterator;
import java.util.List;

/**
 * EventManager is responsible for subscribing and unsubscribing a list of
 * event consumer, who will be notified during various event over the
 * repository service.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan
 *        (hasan)</a>
 */
public interface EventManager {

  /**
   * Subscribe new event listener.
   * @param pEventListener subscribe new event listener.
   */
  void subscribe(final EventListener pEventListener);

  /**
   * Remove an existing subscribed {@see EventListener} .
   * @param pEventListener an existing subscribed event listener reference.
   */
  void unsubscribe(final EventListener pEventListener);

  /**
   * Return all available event listener.
   * @return iterator for all existing event listener.
   */
  Iterator<EventListener> eventListenerIterator();

  /**
   * Publish an event to all the subscriber in a new working thread.
   * @param pEvent all events packed with the iterator.
   */
  void publishEvent(final Event pEvent);

  /**
   * This method is useful to add some default event listeners.
   * @param pEventListeners list of event listeners.
   */
  void setEventListeners(final List<EventListener> pEventListeners);
}
